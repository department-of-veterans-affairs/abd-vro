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
            "defaultFirstName defaultLastName"));
    collections.put(
        DEFAULT_SPEC,
        Arrays.asList(
            new ClaimSubmissionData(
                "defaultId",
                "defaultUserName",
                "defaultPii",
                "defaultFirstName",
                "defaultLastName",
                "defaultFirstName defaultLastName"),
            new ClaimSubmissionData(
                "defaultId2",
                "defaultUserName2",
                "defaultPii2",
                "defaultFirstName2",
                "defaultLastName2",
                "defaultFirstName2 defaultLastName2"),
            new ClaimSubmissionData(
                "defaultId3",
                "defaultUserName3",
                "defaultPii3",
                "defaultFirstName3",
                "defaultLastName3",
                "defaultFirstName3 defaultLastName3")));

    data.put(
        "bogus",
        new ClaimSubmissionData(
            "bogusId",
            "bogusUserName",
            "bogusPii",
            "bogusFirstName",
            "bogusLastName",
            "bogusFirstName bogusLastName"));
    collections.put(
        "bogus",
        Arrays.asList(
            new ClaimSubmissionData(
                "bogusId",
                "bogusUserName",
                "bogusPii",
                "bogusFirstName",
                "bogusLastName",
                "bogusFirstName bogusLastName"),
            new ClaimSubmissionData(
                "bogusId2",
                "bogusUserName2",
                "bogusPii2",
                "bogusFirstName2",
                "bogusLastName2",
                "bogusFirstName2 bogusLastName2"),
            new ClaimSubmissionData(
                "bogusId3",
                "bogusUserName3",
                "bogusPii3",
                "bogusFirstName3",
                "bogusLastName3",
                "bogusFirstName3 bogusLastName3")));

    data.put(
        "duplicateLastName",
        new ClaimSubmissionData(
            "dupId", "dupUserName", "dupPii", "dupFirstName", "Smith", "dupFirstName Smith"));
    collections.put(
        "duplicateLastName",
        Arrays.asList(
            new ClaimSubmissionData(
                "dupId", "dupUserName", "dupPii", "dupFirstName", "Smith", "dupFirstName Smith"),
            new ClaimSubmissionData(
                "dupId2",
                "dupUserName2",
                "dupPii2",
                "dupFirstName2",
                "Smith",
                "dupFirstName2 Smith"),
            new ClaimSubmissionData(
                "dupId3",
                "dupUserName3",
                "dupPii3",
                "dupFirstName3",
                "dupLastName3",
                "dupFirstName3 dupLastName3")));
  }
}
