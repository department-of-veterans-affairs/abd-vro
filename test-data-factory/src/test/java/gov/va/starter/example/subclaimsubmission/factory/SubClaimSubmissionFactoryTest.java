package gov.va.starter.example.subclaimsubmission.factory;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SubClaimSubmissionFactoryTest {
  private SubClaimSubmissionFactory dataFactory;

  @BeforeEach
  public void setup() {
    dataFactory = new SubClaimSubmissionFactory();
  }

  @Test
  public void testSubClaimSubmissionFactoryDefaultRecords() {
    SubClaimSubmissionData defaultSubClaimSubmission = dataFactory.create();

    assertThat(defaultSubClaimSubmission.getId()).isEqualTo("defaultSubId");
    assertThat(defaultSubClaimSubmission.getUserName()).isEqualTo("defaultSubUserName");
    assertThat(defaultSubClaimSubmission.getPii()).isEqualTo("defaultSubPii");
    assertThat(defaultSubClaimSubmission.getFirstName()).isEqualTo("defaultSubFirstName");
    assertThat(defaultSubClaimSubmission.getLastName()).isEqualTo("defaultSubLastName");
    assertThat(defaultSubClaimSubmission.getFullName())
        .isEqualTo("defaultSubFirstName defaultSubLastName");
  }

  @Test
  public void testSubClaimSubmissionFactoryDefaultCollection() {
    List<SubClaimSubmissionData> defaultSubClaimSubmissionCollection =
        dataFactory.createCollection();

    assertThat(defaultSubClaimSubmissionCollection.size()).isEqualTo(3);
    assertThat(defaultSubClaimSubmissionCollection.get(0).getId()).isEqualTo("defaultSubId");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getUserName())
        .isEqualTo("defaultSubUserName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getPii()).isEqualTo("defaultSubPii");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getFirstName())
        .isEqualTo("defaultSubFirstName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getLastName())
        .isEqualTo("defaultSubLastName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getFullName())
        .isEqualTo("defaultSubFirstName defaultSubLastName");
  }

  @Test
  public void testSubClaimSubmissionFactoryBogusRecords() {
    SubClaimSubmissionData defaultSubClaimSubmission = dataFactory.createBySpec("bogus");

    assertThat(defaultSubClaimSubmission.getId()).isEqualTo("bogusSubId");
    assertThat(defaultSubClaimSubmission.getUserName()).isEqualTo("bogusSubUserName");
    assertThat(defaultSubClaimSubmission.getPii()).isEqualTo("bogusSubPii");
    assertThat(defaultSubClaimSubmission.getFirstName()).isEqualTo("bogusSubFirstName");
    assertThat(defaultSubClaimSubmission.getLastName()).isEqualTo("bogusSubLastName");
    assertThat(defaultSubClaimSubmission.getFullName())
        .isEqualTo("bogusSubFirstName bogusSubLastName");
  }

  @Test
  public void testSubClaimSubmissionFactoryBogusCollection() {
    List<SubClaimSubmissionData> defaultSubClaimSubmissionCollection =
        dataFactory.createCollectionBySpec("bogus");

    assertThat(defaultSubClaimSubmissionCollection.size()).isEqualTo(3);
    assertThat(defaultSubClaimSubmissionCollection.get(0).getId()).isEqualTo("bogusSubId");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getUserName())
        .isEqualTo("bogusSubUserName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getPii()).isEqualTo("bogusSubPii");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getFirstName())
        .isEqualTo("bogusSubFirstName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getLastName())
        .isEqualTo("bogusSubLastName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getFullName())
        .isEqualTo("bogusSubFirstName bogusSubLastName");
  }

  @Test
  public void testSubClaimSubmissionFactoryDupLastNameRecords() {
    SubClaimSubmissionData defaultSubClaimSubmission =
        dataFactory.createBySpec("duplicateLastName");

    assertThat(defaultSubClaimSubmission.getId()).isEqualTo("dupSubId");
    assertThat(defaultSubClaimSubmission.getUserName()).isEqualTo("dupSubUserName");
    assertThat(defaultSubClaimSubmission.getPii()).isEqualTo("dupSubPii");
    assertThat(defaultSubClaimSubmission.getFirstName()).isEqualTo("dupSubFirstName");
    assertThat(defaultSubClaimSubmission.getLastName()).isEqualTo("Smith");
    assertThat(defaultSubClaimSubmission.getFullName()).isEqualTo("dupSubFirstName Smith");
  }

  @Test
  public void testSubClaimSubmissionFactoryDupLastNameCollection() {
    List<SubClaimSubmissionData> defaultSubClaimSubmissionCollection =
        dataFactory.createCollectionBySpec("duplicateLastName");

    assertThat(defaultSubClaimSubmissionCollection.size()).isEqualTo(3);
    assertThat(defaultSubClaimSubmissionCollection.get(0).getId()).isEqualTo("dupSubId");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getUserName())
        .isEqualTo("dupSubUserName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getPii()).isEqualTo("dupSubPii");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getFirstName())
        .isEqualTo("dupSubFirstName");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getLastName()).isEqualTo("Smith");
    assertThat(defaultSubClaimSubmissionCollection.get(0).getFullName())
        .isEqualTo("dupSubFirstName Smith");
  }
}
