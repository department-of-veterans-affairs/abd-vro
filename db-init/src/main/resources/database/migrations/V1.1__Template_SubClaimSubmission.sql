CREATE TABLE example.subclaimsubmission (
    id VARCHAR(128) NOT NULL PRIMARY KEY,
    userName VARCHAR NOT NULL,
    pii VARCHAR NOT NULL,
    firstName VARCHAR NOT NULL,
    lastName VARCHAR NOT NULL,
    claimsubmissionId VARCHAR
);
