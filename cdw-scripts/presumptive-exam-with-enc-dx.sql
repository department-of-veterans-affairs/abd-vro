-- This finds a test patient for presumptive exam ordering case.
-- Patient will have an encounter diagnosis "I10." (ICD10) last year and
-- single blood pressure last year as well.
WITH Measurements (PatientSID, Diastolic, Systolic) AS (
	SELECT PatientSID, Diastolic, Systolic
	FROM CDWWork.Vital.VitalSign
	WHERE Systolic IS NOT NULL AND Diastolic is NOT NULL AND VitalSignTakenDateTime > DATEADD(year, -1, GETDATE())
), ICNMeasurement (PatientICN, PatientSID, PatientSSN, Diastolic, Systolic, Cnt) AS (
	SELECT p.PatientICN, p.PatientSID, p.PatientSSN, v.Diastolic, v.Systolic,
	COUNT(1) OVER (PARTITION BY p.PatientICN)
	FROM Measurements v
	INNER JOIN CDWWork.SPatient.SPatient p ON p.PatientSID = v.PatientSID
), SIDs (PatientSID, PatientSSN) AS (
	SELECT DISTINCT PatientSID, PatientSSN FROM ICNMeasurement WHERE Cnt = 1
), Dxs (ICD10SID) AS (
	SELECT ICD10SID
	FROM CDWWork.Dim.ICD10
	WHERE ICD10Code = 'I10.'
)
SELECT s.PatientSID, s.PatientSSN
FROM CDWWork.Outpat.Visit v
INNER JOIN SIDs s ON s.PatientSID = v.PatientSID
INNER JOIN CDWWork.Outpat.VDiagnosis dx ON dx.VisitSID = v.VisitSID AND
    ICD10SID IN (SELECT ICD10SID FROM Dxs) AND dx.PrimarySecondary = 'P'
WHERE v.EncounterDateTime >= DATEADD(year, -1, GETDATE())
ORDER BY s.PatientSSN