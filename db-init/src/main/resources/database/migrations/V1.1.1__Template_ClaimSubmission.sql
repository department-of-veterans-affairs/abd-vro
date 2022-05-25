CREATE TABLE example.claimsubmission (
    id VARCHAR(128) NOT NULL PRIMARY KEY,
    createdAt TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    userName VARCHAR NOT NULL,
    pii VARCHAR NOT NULL,
    firstName VARCHAR NOT NULL,
    lastName VARCHAR NOT NULL,
    submissionId VARCHAR(128) NOT NULL,
    claimantId VARCHAR(128) NOT NULL,
    contentionType VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
);
