package gov.va.starter.example.claimsubmission.factory;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ClaimSubmissionFactoryTest {
  private ClaimSubmissionFactory dataFactory;

  @BeforeEach
  public void setup() {
    dataFactory = new ClaimSubmissionFactory();
  }

  @Test
  public void testClaimSubmissionFactoryDefaultRecords() {
    ClaimSubmissionData defaultClaimSubmission = dataFactory.create();

    assertThat(defaultClaimSubmission.getId()).isEqualTo("defaultId");
    assertThat(defaultClaimSubmission.getUserName()).isEqualTo("defaultUserName");
    assertThat(defaultClaimSubmission.getPii()).isEqualTo("defaultPii");
    assertThat(defaultClaimSubmission.getFirstName()).isEqualTo("defaultFirstName");
    assertThat(defaultClaimSubmission.getLastName()).isEqualTo("defaultLastName");
    assertThat(defaultClaimSubmission.getFullName()).isEqualTo("defaultFirstName defaultLastName");
  }

  @Test
  public void testClaimSubmissionFactoryDefaultCollection() {
    List<ClaimSubmissionData> defaultClaimSubmissionCollection = dataFactory.createCollection();

    assertThat(defaultClaimSubmissionCollection.size()).isEqualTo(3);
    assertThat(defaultClaimSubmissionCollection.get(0).getId()).isEqualTo("defaultId");
    assertThat(defaultClaimSubmissionCollection.get(0).getUserName()).isEqualTo("defaultUserName");
    assertThat(defaultClaimSubmissionCollection.get(0).getPii()).isEqualTo("defaultPii");
    assertThat(defaultClaimSubmissionCollection.get(0).getFirstName())
        .isEqualTo("defaultFirstName");
    assertThat(defaultClaimSubmissionCollection.get(0).getLastName()).isEqualTo("defaultLastName");
    assertThat(defaultClaimSubmissionCollection.get(0).getFullName())
        .isEqualTo("defaultFirstName defaultLastName");
  }

  @Test
  public void testClaimSubmissionFactoryBogusRecords() {
    ClaimSubmissionData defaultClaimSubmission = dataFactory.createBySpec("bogus");

    assertThat(defaultClaimSubmission.getId()).isEqualTo("bogusId");
    assertThat(defaultClaimSubmission.getUserName()).isEqualTo("bogusUserName");
    assertThat(defaultClaimSubmission.getPii()).isEqualTo("bogusPii");
    assertThat(defaultClaimSubmission.getFirstName()).isEqualTo("bogusFirstName");
    assertThat(defaultClaimSubmission.getLastName()).isEqualTo("bogusLastName");
    assertThat(defaultClaimSubmission.getFullName()).isEqualTo("bogusFirstName bogusLastName");
  }

  @Test
  public void testClaimSubmissionFactoryBogusCollection() {
    List<ClaimSubmissionData> defaultClaimSubmissionCollection =
        dataFactory.createCollectionBySpec("bogus");

    assertThat(defaultClaimSubmissionCollection.size()).isEqualTo(3);
    assertThat(defaultClaimSubmissionCollection.get(0).getId()).isEqualTo("bogusId");
    assertThat(defaultClaimSubmissionCollection.get(0).getUserName()).isEqualTo("bogusUserName");
    assertThat(defaultClaimSubmissionCollection.get(0).getPii()).isEqualTo("bogusPii");
    assertThat(defaultClaimSubmissionCollection.get(0).getFirstName()).isEqualTo("bogusFirstName");
    assertThat(defaultClaimSubmissionCollection.get(0).getLastName()).isEqualTo("bogusLastName");
    assertThat(defaultClaimSubmissionCollection.get(0).getFullName())
        .isEqualTo("bogusFirstName bogusLastName");
  }

  @Test
  public void testClaimSubmissionFactoryDupLastNameRecords() {
    ClaimSubmissionData defaultClaimSubmission = dataFactory.createBySpec("duplicateLastName");

    assertThat(defaultClaimSubmission.getId()).isEqualTo("dupId");
    assertThat(defaultClaimSubmission.getUserName()).isEqualTo("dupUserName");
    assertThat(defaultClaimSubmission.getPii()).isEqualTo("dupPii");
    assertThat(defaultClaimSubmission.getFirstName()).isEqualTo("dupFirstName");
    assertThat(defaultClaimSubmission.getLastName()).isEqualTo("Smith");
    assertThat(defaultClaimSubmission.getFullName()).isEqualTo("dupFirstName Smith");
  }

  @Test
  public void testClaimSubmissionFactoryDupLastNameCollection() {
    List<ClaimSubmissionData> defaultClaimSubmissionCollection =
        dataFactory.createCollectionBySpec("duplicateLastName");

    assertThat(defaultClaimSubmissionCollection.size()).isEqualTo(3);
    assertThat(defaultClaimSubmissionCollection.get(0).getId()).isEqualTo("dupId");
    assertThat(defaultClaimSubmissionCollection.get(0).getUserName()).isEqualTo("dupUserName");
    assertThat(defaultClaimSubmissionCollection.get(0).getPii()).isEqualTo("dupPii");
    assertThat(defaultClaimSubmissionCollection.get(0).getFirstName()).isEqualTo("dupFirstName");
    assertThat(defaultClaimSubmissionCollection.get(0).getLastName()).isEqualTo("Smith");
    assertThat(defaultClaimSubmissionCollection.get(0).getFullName())
        .isEqualTo("dupFirstName Smith");
  }
}
