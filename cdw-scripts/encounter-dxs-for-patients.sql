-- Finds all encounter diagnoses for patients
-- Replace PatientSID's
SELECT v.PatientSID, v.EncounterDateTime, icd9.ICD9Code, icd10.ICD10Code
FROM CDWWork.Outpat.Visit v
INNER JOIN CDWWork.Outpat.VDiagnosis dx ON dx.VisitSID = v.VisitSID AND dx.PrimarySecondary = 'P'
LEFT JOIN CDWWork.DIM.ICD10 icd10 ON icd10.ICD10SID = dx.ICD10SID
LEFT JOIN CDWWork.DIM.ICD9 icd9 ON icd9.ICD9SID = dx.ICD9SID
WHERE v.PatientSID IN (123, 124, 125)
ORDER BY v.PatientSID, v.EncounterDateTime Desc
