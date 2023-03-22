# frozen_string_literal: true

class BgsClient
  attr_reader :bgs

  def initialize
    @bgs = BGS::Services.new(external_uid: nil, external_key: nil)
  end

  def vro_participant_id
    @vro_participant_id ||= begin
      cfg = BGS.configuration
      @bgs.security.find_participant_id(station_id: cfg.client_station_id, css_id: cfg.client_username)
    end
  end

  def create_note(claim_id:, participant_id: nil, txt:)
    participant_id ||= begin
      claim_details = @bgs.benefit_claims.find_claim_details_by_claim_id(claim_id: claim_id)
      claim_details[:bnft_claim_detail][:ptcpnt_clmant_id]
    end
    @bgs.notes.create_note(claim_id: claim_id, participant_id: participant_id, txt: txt, user_id: vro_participant_id)
  end
end
