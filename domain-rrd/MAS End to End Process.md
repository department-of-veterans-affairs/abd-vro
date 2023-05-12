1. Get evidence from MAS
2. Get evidence from Lighthouse
3. Combine evidence into one
4. Call Julian service using abdevidence and claimCondition.disabilityActionType
5. Julian service returns summary (flag)
6. Enhance response with Veteran info (for GeneratePDF)
7. Call generate PDF
8. --> If no evidence, call pcOrderExam
9. Call claimStatusUpdate
10. Upload PDF file (probably using BIP)
