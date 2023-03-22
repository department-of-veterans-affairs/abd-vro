# frozen_string_literal: true

Rails.application.reloader.to_prepare do
  BGS.configure do |config|
    config.application = "VRO"
    config.client_ip = Socket.ip_address_list.reject(&:ipv4_loopback?).first.ip_address
    config.client_station_id = 281
    config.client_username = "VROSYSACCT"
    config.env = "beplinktest"
    config.log = true
    config.logger = Logger.new(STDOUT)
    config.ssl_verify_mode = :none
  end
end

# patch bgs_ext with a createNote implementation that follows our spec better
BGS::DevelopmentNotesService.class_eval do
  def create_note(claim_id:, participant_id:, txt:, user_id:)
    response = request(
      :create_note, {
        'note' => {
          'bnftClmNoteTc' => "CLMDVLNOTE",
          'clmId' => claim_id,
          'createDt' => Time.current.iso8601,
          'noteOutTn' => "Claim Development Note",
          'ptcpntId' => participant_id,
          'txt' => txt,
          'userId' => user_id
        }
      }
    )
    response.body[:create_note_response]
  end

  def find_development_notes(claim_id:)
    response = request(:find_development_notes, claimid: claim_id)
    response.body[:find_development_notes_response]
  end
end
