# frozen_string_literal: true

require 'active_support/time'
require 'active_support/core_ext/object/blank'
require 'bgs'
require 'metric_logger'

# patch bgs_ext with a createNote implementation that follows our spec better
BGS::DevelopmentNotesService.class_eval do
  # Takes a hash with keys txt, user_id, and at least one of participant_id or claim_id
  def create_note(note)
    response = request(:create_note, 'note' => format_note(**note))
    response.body[:create_note_response]
  end

  # Takes an array of hashes that are suitable for create_note (singular)
  # This will result in success but incorrect behavior if called with multiple notes that include
  # at least one veteran-level note
  def create_notes(notes)
    response = request(:create_notes, 'notes' => notes.map { |note| format_note(**note) })
    response.body[:create_notes_response]
  end

  def format_note(claim_id: nil, participant_id: nil, txt:, user_id:)
    note = if claim_id.nil?
             { 'ptcpntNoteTc' => "CLMNTCONTACT", 'noteOutTn' => "Contact with Claimant" }
           else
             { 'clmId' => claim_id, 'bnftClmNoteTc' => "CLMDVLNOTE", 'noteOutTn' => "Claim Development Note" }
           end
    note['ptcpntId'] = participant_id unless participant_id.nil?
    note.merge(
      'createDt' => Time.current.iso8601,
      'txt' => txt,
      'userId' => user_id
    )
  end
end


class BgsClient
  attr_reader :bgs
  attr_reader :metrics

  def initialize
    @bgs = BGS::Services.new(external_uid: nil, external_key: nil)
    @metrics = MetricLogger.new
  end

  def handle_request(req)
    claim_id = req["vbmsClaimId"]
    begin
        start_time = Time.now
        metric_custom_tag = req.has_key?("claimNotes") && req["claimNotes"].any? ? 'bgsNoteType:claim' : 'bgsNoteType:veteran'
        @metrics.submit_count_with_default_value(METRIC[:REQUEST_START], [metric_custom_tag])
        if req.has_key?("claimNotes") && req["claimNotes"].any?
          raise ArgumentError.new("vbmsClaimId is required for claimNotes") unless claim_id
          create_claim_notes(claim_id: claim_id, notes: req["claimNotes"])
        elsif req.has_key?("veteranNote")
          participant_id = req["veteranParticipantId"]
          note = req["veteranNote"]
          raise ArgumentError.new("at least one of vbmsClaimId and veteranParticipantId is required") unless claim_id || participant_id
          raise ArgumentError.new("invalid veteranNote value") unless note.is_a?(String) && note.length > 0
          create_veteran_note(claim_id: claim_id, participant_id: participant_id, note: note)
        else
          raise ArgumentError.new("missing claimNotes or veteranNote")
        end
        @metrics.submit_request_duration(start_time, Time.now, [metric_custom_tag])
        @metrics.submit_count_with_default_value(METRIC[:REQUEST_COMPLETE], [metric_custom_tag])
    rescue Exception => e
        @metric_logger.submit_count(METRIC[:RESPONSE_ERROR])
        $logger.error "Exception submitting metric RESPONSE_ERROR #{e.message}"

        raise e.class, "Message: #{e.message}"
    end
  end

  def vro_participant_id
    @vro_participant_id ||= begin
      if ENVIRONMENT != 'local'
        cfg = BGS.configuration
        bgs.security.find_participant_id(station_id: cfg.client_station_id, css_id: cfg.client_username)
      end
    end
  end

  def create_claim_notes(claim_id:, notes:)
    note_hashes = notes.map do |note|
      { claim_id: claim_id, txt: note, user_id: vro_participant_id }
    end
    bgs.notes.create_notes(note_hashes)
  end

  def create_veteran_note(claim_id: nil, participant_id: nil, note:)
    participant_id ||= bgs.benefit_claims.find_bnft_claim(claim_id: claim_id)[:bnft_claim_dto][:ptcpnt_vet_id]
    bgs.notes.create_note(participant_id: participant_id, txt: note, user_id: vro_participant_id)
  end
end
