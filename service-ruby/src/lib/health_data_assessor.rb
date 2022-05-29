require 'lighthouse_observation_data'

class HealthDataAssessor
  def assess(contention, json)
    case contention
    when 'hypertension'
      bp_observations = json['bp_observations']
      return {} if bp_observations.blank?

      relevant_readings = LighthouseObservationData.new(bp_observations).transform
      return { bp_readings: relevant_readings }
    when 'asthma'
      bp_observations = json['bp_observations']
      medications = json['medications']
      return {} if medications.blank?

      transformed_medications = LighthouseMedicationRequestData.new(medications).transform
      flagged_medications = transformed_medications.map do |medication|
        {
          **medication,
          flagged: ASTHMA_KEYWORDS.any? { |keyword| medication.to_s.downcase.include?(keyword) }
        }
      end
      medications = flagged_medications.sort_by { |medication| medication[:flagged] ? 0 : 1 }
      return { medications: medications }
    else
      return { error: "Unsupported contention: #{contention}" }
    end
  end
end
