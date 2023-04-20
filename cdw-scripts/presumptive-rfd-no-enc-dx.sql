-- This finds test patients (ICN) for presumptive rfd case:
-- Patients will not have an encounter diagnosis of
--    "I10." (ICD10), "401.0", "401.1", "401.9" (ICD9)
-- 3 blood pressures: 1 elevated 2 normal within two years.
WITH Measurements (PatientSID, Elevated) AS (
	SELECT
		PatientSID,
		IIF(Systolic >= 160 AND Diastolic >= 100, 1, 0) As Elevated
	FROM CDWWork.Vital.VitalSign
	WHERE
		Systolic IS NOT NULL AND
		Diastolic is NOT NULL AND
		VitalSignTakenDateTime > DATEADD(year, -2, GETDATE())
), ICNMeasurements (PatientICN, PatientSSN, Elevated, Cnt, RowNumber) AS (
	SELECT p.PatientICN, p.PatientSSN, v.Elevated,
	COUNT(*) OVER (PARTITION BY p.PatientICN, v.Elevated),
	ROW_NUMBER() OVER (PARTITION BY p.PatientICN, v.Elevated  ORDER BY p.PatientSID)
	FROM Measurements v
	INNER JOIN CDWWork.SPatient.SPatient p ON p.PatientSID = v.PatientSID
), CountedICNMeasurements (PatientICN, PatientSSN, Cnt) AS (
	SELECT PatientICN, PatientSSN,
	COUNT(*) OVER (PARTITION BY PatientICN)
	FROM ICNMeasurements
	WHERE RowNumber = 1 AND ((Cnt = 2 AND Elevated = 0) OR (Cnt = 1 AND Elevated = 1))
), ICNs (PatientICN) AS (
	SELECT DISTINCT PatientICN FROM CountedICNMeasurements WHERE Cnt > 1
), Dx10s (ICD10SID) AS (
	SELECT ICD10SID FROM CDWWork.Dim.ICD10 WHERE ICD10Code = 'I10.'
), Dx9s (ICD9SID) AS (
	SELECT ICD9SID FROM CDWWork.Dim.ICD9
	WHERE ICD9Code = '401.1' OR ICD9Code = '401.0' OR ICD9Code = '401.9'
), FilterICNs (PatientICN) AS (
	SELECT DISTINCT icns.PatientICN
	FROM ICNs icns
	INNER JOIN CDWWork.SPatient.SPatient p ON p.PatientICN = icns.PatientICN
	INNER JOIN CDWWork.Outpat.Visit v ON v.PatientSID = p.PatientSID
	INNER JOIN CDWWork.Outpat.VDiagnosis dx
		ON dx.VisitSID = v.VisitSID AND
			(ICD10SID IN (SELECT ICD10SID FROM Dx10s) OR
			ICD9SID IN (SELECT ICD9SID FROM Dx9s)) AND
			dx.PrimarySecondary = 'P'
)
SELECT PatientICN FROM ICNs
EXCEPT
SELECT PatientICN FROM FilterICNs
