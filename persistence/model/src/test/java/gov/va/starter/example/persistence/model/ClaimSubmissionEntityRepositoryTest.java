package gov.va.starter.example.persistence.model;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.claimsubmission.factory.ClaimSubmissionFactory;
import gov.va.starter.example.claimsubmission.model.ClaimSubmissionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ClaimSubmissionEntityRepositoryTest {

  @Autowired private ClaimSubmissionEntityRepository modelEntityRepository;

  private ClaimSubmissionEntity entity;

  private ClaimSubmissionFactory claimSubmissionFactory = new ClaimSubmissionFactory();
  private List<ClaimSubmissionData> defaultClaimSubmissionDataCollection =
      claimSubmissionFactory.createCollectionBySpec("duplicateLastName");

  @BeforeEach
  public void setup() {
    entity =
        new ClaimSubmissionEntity(
            defaultClaimSubmissionDataCollection.get(0).getUserName(),
            defaultClaimSubmissionDataCollection.get(0).getPii(),
            defaultClaimSubmissionDataCollection.get(0).getFirstName(),
            defaultClaimSubmissionDataCollection.get(0).getLastName());
  }

  /**
   * populate the tables with some tests data.
   *
   * @return one of the saved entities
   */
  public ClaimSubmissionEntity populate() {
    ClaimSubmissionEntity result = modelEntityRepository.save(entity);
    ClaimSubmissionEntity agentSmith =
        new ClaimSubmissionEntity(
            defaultClaimSubmissionDataCollection.get(1).getUserName(),
            defaultClaimSubmissionDataCollection.get(1).getPii(),
            defaultClaimSubmissionDataCollection.get(1).getFirstName(),
            defaultClaimSubmissionDataCollection.get(1).getLastName());
    modelEntityRepository.save(agentSmith);
    ClaimSubmissionEntity maryQuiteContrary =
        new ClaimSubmissionEntity(
            defaultClaimSubmissionDataCollection.get(2).getUserName(),
            defaultClaimSubmissionDataCollection.get(2).getPii(),
            defaultClaimSubmissionDataCollection.get(2).getFirstName(),
            defaultClaimSubmissionDataCollection.get(2).getLastName());
    modelEntityRepository.save(maryQuiteContrary);

    return result;
  }

  @Test
  public void setupValid() {
    assertThat(modelEntityRepository != null);
  }

  @Test
  public void createAndGetTest() {
    modelEntityRepository.save(entity);

    Optional<ClaimSubmissionEntity> retrievedEntity =
        modelEntityRepository.findByUserName(
            defaultClaimSubmissionDataCollection.get(0).getUserName());

    assertThat(retrievedEntity.isPresent());
    assertThat(retrievedEntity.get().getFirstName())
        .isEqualTo(defaultClaimSubmissionDataCollection.get(0).getFirstName());
  }

  @Test
  public void testFindByLastName() {
    populate();

    Page<ClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultClaimSubmissionDataCollection.get(0).getLastName(), Pageable.unpaged());

    assertThat(retrievedSmiths.getContent().size()).isEqualTo(2);
  }

  @Test
  public void testFindByLastNamePaged() {
    populate();

    Pageable pageable = PageRequest.of(0, 1);
    Page<ClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultClaimSubmissionDataCollection.get(0).getLastName(), pageable);

    assertThat(retrievedSmiths.getContent().size()).isEqualTo(1);
  }

  @Test
  public void testUpdateRecord() {
    final String newName = "Contrary";
    ClaimSubmissionEntity saved = modelEntityRepository.save(entity);
    saved.setLastName(newName);

    ClaimSubmissionEntity updated = modelEntityRepository.save(saved);

    Page<ClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultClaimSubmissionDataCollection.get(0).getLastName(), Pageable.unpaged());
    assertThat(retrievedSmiths.getContent().size()).isEqualTo(0);
    Page<ClaimSubmissionEntity> retrievedContrarians =
        modelEntityRepository.findByLastName(newName, Pageable.unpaged());
    assertThat(retrievedContrarians.getContent().size()).isEqualTo(1);
  }

  @Test
  public void testDeleteRecord() {
    ClaimSubmissionEntity saved = populate();

    modelEntityRepository.deleteById(saved.getId());

    Page<ClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultClaimSubmissionDataCollection.get(0).getLastName(), Pageable.unpaged());
    assertThat(retrievedSmiths.getContent().size()).isEqualTo(1);
  }

  @Test
  public void testFindAll() {
    populate();
    Page<ClaimSubmissionEntity> retrieved = modelEntityRepository.findAll(Pageable.unpaged());
    assertThat(retrieved.getContent().size()).isEqualTo(3);
  }

  @Test
  public void testFindAllPaged() {
    populate();
    Pageable pageable = PageRequest.of(0, 2);
    Page<ClaimSubmissionEntity> retrieved = modelEntityRepository.findAll(pageable);
    assertThat(retrieved.getContent().size()).isEqualTo(2);
  }
}
