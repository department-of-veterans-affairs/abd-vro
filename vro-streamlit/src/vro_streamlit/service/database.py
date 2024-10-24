# mypy: ignore-errors
# Ignore until this class is implemented

import pandas


class EventsRepo:
    def get_contention_events(self):
        return pandas.DataFrame(
            {
                'EventType': ['Associated', 'Updated', 'Classified'],
                'EventTime': ['2021-01-01T01:01:01', '2021-02-02T02:02:02', '2021-03-03T03:03:03'],
                'NotifiedAt': ['2021-01-01T01:01:01', '2021-02-02T02:02:02', '2021-03-03T03:03:03'],
                'OccurredAt': ['2021-01-01T01:01:01', '2021-02-02T02:02:02', '2021-03-03T03:03:03'],
                'ActionName': ['Action1', 'Action2', 'Action3'],
                'ActionResultName': ['Result1', 'Result2', 'Result3'],
                'ActorStation': ['Station1', 'Station2', 'Station3'],
                'ActorUserId': ['UserId1', 'UserId2', 'UserId3'],
                'AutomationIndicator': [True, True, False],
                'BenefitClaimTypeCode': ['ClaimType1', 'ClaimType2', 'ClaimType3'],
                'ClaimId': [1111, 2222, 3333],
                'ContentionClassificationName': ['Classification1', 'Classification2', 'Classification3'],
                'ContentionId': [1234, 5678, 9012],
                'ContentionStatusTypeCode': ['StatusCode1', 'StatusCode2', 'StatusCode3'],
                'ContentionTypeCode': ['TypeCode1', 'TypeCode2', 'TypeCode3'],
                'CurrentLifecycleStatus': ['Status1', 'Status2', 'Status3'],
                'DateAdded': ['2021-01-01', '2021-02-02', '2021-03-03'],
                'DateCompleted': ['2021-01-01', '2021-02-02', '2021-03-03'],
                'DateUpdated': ['2021-01-01', '2021-02-02', '2021-03-03'],
                'Details': ['Details1', 'Details2', 'Details3'],
                'DiagnosticTypeCode': ['DiagnosticType1', 'DiagnosticType2', 'DiagnosticType3'],
                'VeteranParticipantId': ['ParticipantId1', 'ParticipantId2', 'ParticipantId3'],
                'Description': ['****', '****', '****'],
                'JournalStatusTypeCode': ['JournalStatusCode1', 'JournalStatusCode2', 'JournalStatusCode3'],
            }
        )

    def get_claim_events(self):
        return pandas.DataFrame(
            {
                'EventType': ['Created', 'Updated', 'Closed'],
                'EventTime': ['2021-01-01T01:01:01', '2021-02-02T02:02:02', '2021-03-03T03:03:03'],
                'NotifiedAt': ['2021-01-01T01:01:01', '2021-02-02T02:02:02', '2021-03-03T03:03:03'],
                'OccurredAt': ['2021-01-01T01:01:01', '2021-02-02T02:02:02', '2021-03-03T03:03:03'],
                'ActorApplicationId': ['ApplicationId1', 'ApplicationId2', 'ApplicationId3'],
                'ActorStation': ['Station1', 'Station2', 'Station3'],
                'ActorUserId': ['UserId1', 'UserId2', 'UserId3'],
                'BenefitClaimId': [1111, 2222, 3333],
                'ClaimantParticipantId': [1234, 5678, 9012],
                'BenefitClaimTypeCode': ['StatusCode1', 'StatusCode2', 'StatusCode3'],
                'BirthDate': ['********', '********', '********'],
                'ClaimReceivedDate': ['2021-01-01', '2021-02-02', '2021-03-03'],
                'DeathDate': ['2021-01-01', '2021-02-02', '2021-03-03'],
                'PayeeTypeCode': ['PayeeType1', 'PayeeType2', 'PayeeType3'],
                'ProgramTypeCode': ['ProgramType1', 'ProgramType2', 'ProgramType3'],
                'DiagnosticTypeCode': ['DiagnosticType1', 'DiagnosticType2', 'DiagnosticType3'],
                'VeteranParticipantId': ['ParticipantId1', 'ParticipantId2', 'ParticipantId3'],
                'JournalStatusTypeCode': ['JournalStatusCode1', 'JournalStatusCode2', 'JournalStatusCode3'],
            }
        )


EVENTS_REPO = EventsRepo()
