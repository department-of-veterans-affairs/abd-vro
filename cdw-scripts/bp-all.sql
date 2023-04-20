-- Finds blood pressure measurements for patients
-- Replace PatientSID's
SELECT
	PatientSID,
	Systolic,
	Diastolic,
	VitalSignTakenDateTime
FROM CDWWork.Vital.VitalSign
WHERE
	Systolic IS NOT NULL AND
	Diastolic is NOT NULL AND
	PatientSID IN (1234, 12345)
ORDER BY VitalSignTakenDateTime DESC
