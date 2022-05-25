package gov.va.starter.example.claimsubmission.factory;

import gov.va.starter.boot.test.data.provider.NamedDataFactory;
import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;

import java.util.Arrays;

public class ClaimSubmissionFactory extends NamedDataFactory<ClaimSubmissionData> {
  public ClaimSubmissionFactory() {
    data.put(
        DEFAULT_SPEC,
        new ClaimSubmissionData(
            "defaultId",
            "defaultUserName",
            "defaultPii",
            "defaultFirstName",
            "defaultLastName",
            "defaultFirstName defaultLastName",
            "submissionId",
            "claimantId",
            "A"));
    collections.put(
        DEFAULT_SPEC,
        Arrays.asList(
            new ClaimSubmissionData(
                "defaultId",
                "defaultUserName",
                "defaultPii",
                "defaultFirstName",
                "defaultLastName",
                "defaultFirstName defaultLastName",
                "submissionId",
                "claimantId",
                "A"),
            new ClaimSubmissionData(
                "defaultId2",
                "defaultUserName2",
                "defaultPii2",
                "defaultFirstName2",
                "defaultLastName2",
                "defaultFirstName2 defaultLastName2",
                "submissionId2",
                "claimantId2",
                "B"),
            new ClaimSubmissionData(
                "defaultId3",
                "defaultUserName3",
                "defaultPii3",
                "defaultFirstName3",
                "defaultLastName3",
                "defaultFirstName3 defaultLastName3",
                "submissionId3",
                "claimantId3",
                "A")));

    data.put(
        "bogus",
        new ClaimSubmissionData(
            "bogusId",
            "bogusUserName",
            "bogusPii",
            "bogusFirstName",
            "bogusLastName",
            "bogusFirstName bogusLastName",
            "bogusSubmissionId",
            "bogusClaimantId",
            "bogusContentionType"));
    collections.put(
        "bogus",
        Arrays.asList(
            new ClaimSubmissionData(
                "bogusId",
                "bogusUserName",
                "bogusPii",
                "bogusFirstName",
                "bogusLastName",
                "bogusFirstName bogusLastName",
                "bogusSubmissionId",
                "bogusClaimantId",
                "bogusContentionType"),
            new ClaimSubmissionData(
                "bogusId2",
                "bogusUserName2",
                "bogusPii2",
                "bogusFirstName2",
                "bogusLastName2",
                "bogusFirstName2 bogusLastName2",
                "bogusSubmissionId2",
                "bogusClaimantId2",
                "bogusContentionType"),
            new ClaimSubmissionData(
                "bogusId3",
                "bogusUserName3",
                "bogusPii3",
                "bogusFirstName3",
                "bogusLastName3",
                "bogusFirstName3 bogusLastName3",
                "bogusSubmissionId3",
                "bogusClaimantId3",
                "bogusContentionType")));

    data.put(
        "duplicateLastName",
        new ClaimSubmissionData(
            "dupId",
            "dupUserName",
            "dupPii",
            "dupFirstName",
            "Smith",
            "dupFirstName Smith",
            "dupSubmissionId",
            "dupClaimantId",
            "A"));
    collections.put(
        "duplicateLastName",
        Arrays.asList(
            new ClaimSubmissionData(
                "dupId",
                "dupUserName",
                "dupPii",
                "dupFirstName",
                "Smith",
                "dupFirstName Smith",
                "dupSubmissionId",
                "dupClaimantId",
                "A"),
            new ClaimSubmissionData(
                "dupId2",
                "dupUserName2",
                "dupPii2",
                "dupFirstName2",
                "Smith",
                "dupFirstName2 Smith",
                "dupSubmissionId2",
                "dupClaimantId2",
                "A"),
            new ClaimSubmissionData(
                "dupId3",
                "dupUserName3",
                "dupPii3",
                "dupFirstName3",
                "dupLastName3",
                "dupFirstName3 dupLastName3",
                "dupSubmissionId3",
                "dupClaimantId3",
                "A")));
  }
}
