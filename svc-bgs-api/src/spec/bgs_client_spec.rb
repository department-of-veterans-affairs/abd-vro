require "bgs_client"

describe BgsClient do
  let(:client) { BgsClient.new }
  let(:metrics) { double("metrics") }
  let(:notes_service) { double("notes_service") }

  before do
    allow(MetricLogger).to receive(:new).and_return(metrics)
    allow(metrics).to receive(:submit_count_with_default_value).and_return(202)
    allow(metrics).to receive(:submit_request_duration).and_return(202)

    allow(BGS::Services).to receive(:new).and_return(double("bgs_services", notes: notes_service))
    allow(client).to receive(:vro_participant_id).and_return("12345")
  end

  describe ".handle_request" do
    subject { client.handle_request(req) }

    context "when given claimNotes but no vbmsClaimId" do
      let(:req) { { "claimNotes" => ["a claim note"] } }

      it "raises an ArgumentError" do
        expect { subject }.to raise_error(ArgumentError, /vbmsClaimId is required/)
      end
    end

    context "when given veteranNote but no veteranParticipantId or vbmsClaimId" do
      let(:req) { { "veteranNote" => "a note" } }

      it "raises an ArgumentError" do
        expect { subject }.to raise_error(ArgumentError, /veteranParticipantId is required/)
      end
    end

    context "when given neither claimNotes or veteranNote" do
      let(:req) { { "vbmsClaimId" => "1111", "veteranParticipantId" => "2222" } }

      it "raises an ArgumentError" do
        expect { subject }.to raise_error(ArgumentError, /missing claimNotes or veteranNote/)
      end
    end

    context "when called to create claim notes" do
      let(:req) { { "vbmsClaimId" => "1111", "claimNotes" => ["note 1", "note 2"] } }

      before { allow(notes_service).to receive(:create_notes) }

      it "calls notes.create_notes with the correct hashes" do
        subject
        expect(notes_service).to have_received(:create_notes).with([
          { claim_id: "1111", txt: "note 1", user_id: "12345" },
          { claim_id: "1111", txt: "note 2", user_id: "12345" }
        ])
      end
    end

    context "when called to create a veteran note" do
      let(:req) { { "veteranParticipantId" => "2222", "veteranNote" => "another note" } }

      before { allow(notes_service).to receive(:create_note) }

      it "calls notes.create_note with the correct hash" do
        subject
        expect(notes_service).to have_received(:create_note).with(
          { participant_id: "2222", txt: "another note", user_id: "12345" }
        )
      end

      context "when given empty claimNotes and non-null veteranNote" do
        let(:req) { { "claimNotes": [], "veteranParticipantId" => "2222", "veteranNote" => "another note" } }

        it "interprets the request as a veteranNote accordingly" do
          subject
          expect(notes_service).to have_received(:create_note).with(
            { participant_id: "2222", txt: "another note", user_id: "12345" }
          )
        end
      end
    end
  end
end
