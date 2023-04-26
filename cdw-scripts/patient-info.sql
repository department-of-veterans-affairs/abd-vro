-- Gets patient information from ICN
-- Replace ICN's
SELECT PatientSID, PatientICN, PatientSSN, PatientName, PatientLastName
FROM CDWWork.SPatient.SPatient
WHERE PatientICN IN ('123', '124', '125')
ORDER BY PatientICN
