# frozen_string_literal: true

require 'bgs'

# patch bgs_ext with a createNote implementation that follows our spec better
BGS::DevelopmentNotesService.class_eval do
  def create_note(claim_id: nil, participant_id: nil, txt:, user_id:)
    note = if claim_id.nil?
             { 'ptcpntNoteTc' => "CLMNTCONTACT", 'noteOutTn' => "Contact with Claimant" }
           else
             { 'clmId' => claim_id, 'bnftClmNoteTc' => "CLMDVLNOTE", 'noteOutTn' => "Claim Development Note" }
           end
    note['ptcpntId'] = participant_id unless participant_id.nil?
    note.merge!(
      'createDt' => Time.now.iso8601,
      'txt' => txt,
      'userId' => user_id
    )
    response = request(:create_note, 'note' => note)
    response.body[:create_note_response]
  end

  def find_development_notes(claim_id:)
    response = request(:find_development_notes, claimid: claim_id)
    response.body[:find_development_notes_response]
  end
end


class BgsClient
  attr_reader :bgs

  def initialize
    @bgs = BGS::Services.new(external_uid: nil, external_key: nil)
  end

  def vro_participant_id
    @vro_participant_id ||= begin
      cfg = BGS.configuration
      bgs.security.find_participant_id(station_id: cfg.client_station_id, css_id: cfg.client_username)
    end
  end

  def create_note(claim_id:, participant_id: nil, txt:)
    participant_id ||= begin
      claim_details = bgs.benefit_claims.find_claim_details_by_claim_id(claim_id: claim_id)
      claim_details[:bnft_claim_detail][:ptcpnt_clmant_id]
    end
    bgs.notes.create_note(claim_id: claim_id, participant_id: participant_id, txt: txt, user_id: vro_participant_id)
  end
end
