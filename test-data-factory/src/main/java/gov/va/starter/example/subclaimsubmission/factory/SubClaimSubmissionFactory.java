package gov.va.starter.example.subclaimsubmission.factory;

import gov.va.starter.boot.test.data.provider.NamedDataFactory;
import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;

import java.util.Arrays;

public class SubClaimSubmissionFactory extends NamedDataFactory<SubClaimSubmissionData> {
  public SubClaimSubmissionFactory() {
    data.put(
        DEFAULT_SPEC,
        new SubClaimSubmissionData(
            "defaultSubId",
            "defaultSubUserName",
            "defaultSubPii",
            "defaultSubFirstName",
            "defaultSubLastName",
            "defaultSubFirstName defaultSubLastName"));
    collections.put(
        DEFAULT_SPEC,
        Arrays.asList(
            new SubClaimSubmissionData(
                "defaultSubId",
                "defaultSubUserName",
                "defaultSubPii",
                "defaultSubFirstName",
                "defaultSubLastName",
                "defaultSubFirstName defaultSubLastName"),
            new SubClaimSubmissionData(
                "defaultSubId2",
                "defaultSubUserName2",
                "defaultSubPii2",
                "defaultSubFirstName2",
                "defaultSubLastName2",
                "defaultSubFirstName2 defaultSubLastName2"),
            new SubClaimSubmissionData(
                "defaultSubId3",
                "defaultSubUserName3",
                "defaultSubPii3",
                "defaultSubFirstName3",
                "defaultSubLastName3",
                "defaultSubFirstName3 defaultSubLastName3")));

    data.put(
        "bogus",
        new SubClaimSubmissionData(
            "bogusSubId",
            "bogusSubUserName",
            "bogusSubPii",
            "bogusSubFirstName",
            "bogusSubLastName",
            "bogusSubFirstName bogusSubLastName"));
    collections.put(
        "bogus",
        Arrays.asList(
            new SubClaimSubmissionData(
                "bogusSubId",
                "bogusSubUserName",
                "bogusSubPii",
                "bogusSubFirstName",
                "bogusSubLastName",
                "bogusSubFirstName bogusSubLastName"),
            new SubClaimSubmissionData(
                "bogusSubId2",
                "bogusSubUserName2",
                "bogusSubPii2",
                "bogusSubFirstName2",
                "bogusSubLastName2",
                "bogusSubFirstName2 bogusSubLastName2"),
            new SubClaimSubmissionData(
                "bogusSubId3",
                "bogusSubUserName3",
                "bogusSubPii3",
                "bogusSubFirstName3",
                "bogusSubLastName3",
                "bogusSubFirstName3 bogusSubLastName3")));

    data.put(
        "duplicateLastName",
        new SubClaimSubmissionData(
            "dupSubId",
            "dupSubUserName",
            "dupSubPii",
            "dupSubFirstName",
            "Smith",
            "dupSubFirstName Smith"));
    collections.put(
        "duplicateLastName",
        Arrays.asList(
            new SubClaimSubmissionData(
                "dupSubId",
                "dupSubUserName",
                "dupSubPii",
                "dupSubFirstName",
                "Smith",
                "dupSubFirstName Smith"),
            new SubClaimSubmissionData(
                "dupSubId2",
                "dupSubUserName2",
                "dupSubPii2",
                "dupSubFirstName2",
                "Smith",
                "dupSubFirstName2 Smith"),
            new SubClaimSubmissionData(
                "dupSubId3",
                "dupSubUserName3",
                "dupSubPii3",
                "dupSubFirstName3",
                "dupSubLastName3",
                "dupSubFirstName3 dupSubLastName3")));
  }
}
