package gov.va.starter.example.persistence.model;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.starter.example.subclaimsubmission.factory.SubClaimSubmissionFactory;
import gov.va.starter.example.subclaimsubmission.model.SubClaimSubmissionData;
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
public class SubClaimSubmissionEntityRepositoryTest {

  @Autowired private SubClaimSubmissionEntityRepository modelEntityRepository;

  private SubClaimSubmissionEntity entity;

  private SubClaimSubmissionFactory subClaimSubmissionFactory = new SubClaimSubmissionFactory();
  private List<SubClaimSubmissionData> defaultSubClaimSubmissionDataCollection =
      subClaimSubmissionFactory.createCollectionBySpec("duplicateLastName");

  @BeforeEach
  public void setup() {
    entity =
        new SubClaimSubmissionEntity(
            defaultSubClaimSubmissionDataCollection.get(0).getUserName(),
            defaultSubClaimSubmissionDataCollection.get(0).getPii(),
            defaultSubClaimSubmissionDataCollection.get(0).getFirstName(),
            defaultSubClaimSubmissionDataCollection.get(0).getLastName());
  }

  /**
   * populate the tables with some tests data.
   *
   * @return one of the saved entities
   */
  public SubClaimSubmissionEntity populate() {
    SubClaimSubmissionEntity result = modelEntityRepository.save(entity);
    SubClaimSubmissionEntity agentSmith =
        new SubClaimSubmissionEntity(
            defaultSubClaimSubmissionDataCollection.get(1).getUserName(),
            defaultSubClaimSubmissionDataCollection.get(1).getPii(),
            defaultSubClaimSubmissionDataCollection.get(1).getFirstName(),
            defaultSubClaimSubmissionDataCollection.get(1).getLastName());
    modelEntityRepository.save(agentSmith);
    SubClaimSubmissionEntity maryQuiteContrary =
        new SubClaimSubmissionEntity(
            defaultSubClaimSubmissionDataCollection.get(2).getUserName(),
            defaultSubClaimSubmissionDataCollection.get(2).getPii(),
            defaultSubClaimSubmissionDataCollection.get(2).getFirstName(),
            defaultSubClaimSubmissionDataCollection.get(2).getLastName());
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

    Optional<SubClaimSubmissionEntity> retrievedEntity =
        modelEntityRepository.findByUserName(
            defaultSubClaimSubmissionDataCollection.get(0).getUserName());

    assertThat(retrievedEntity.isPresent());
    assertThat(retrievedEntity.get().getFirstName())
        .isEqualTo(defaultSubClaimSubmissionDataCollection.get(0).getFirstName());
  }

  @Test
  public void testFindByLastName() {
    populate();

    Page<SubClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultSubClaimSubmissionDataCollection.get(0).getLastName(), Pageable.unpaged());

    assertThat(retrievedSmiths.getContent().size()).isEqualTo(2);
  }

  @Test
  public void testFindByLastNamePaged() {
    populate();

    Pageable pageable = PageRequest.of(0, 1);
    Page<SubClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultSubClaimSubmissionDataCollection.get(0).getLastName(), pageable);

    assertThat(retrievedSmiths.getContent().size()).isEqualTo(1);
  }

  @Test
  public void testUpdateRecord() {
    final String newName = "Contrary";
    SubClaimSubmissionEntity saved = modelEntityRepository.save(entity);
    saved.setLastName(newName);

    SubClaimSubmissionEntity updated = modelEntityRepository.save(saved);

    Page<SubClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultSubClaimSubmissionDataCollection.get(0).getLastName(), Pageable.unpaged());
    assertThat(retrievedSmiths.getContent().size()).isEqualTo(0);
    Page<SubClaimSubmissionEntity> retrievedContrarians =
        modelEntityRepository.findByLastName(newName, Pageable.unpaged());
    assertThat(retrievedContrarians.getContent().size()).isEqualTo(1);
  }

  @Test
  public void testDeleteRecord() {
    SubClaimSubmissionEntity saved = populate();

    modelEntityRepository.deleteById(saved.getId());

    Page<SubClaimSubmissionEntity> retrievedSmiths =
        modelEntityRepository.findByLastName(
            defaultSubClaimSubmissionDataCollection.get(0).getLastName(), Pageable.unpaged());
    assertThat(retrievedSmiths.getContent().size()).isEqualTo(1);
  }

  @Test
  public void testFindAll() {
    populate();
    Page<SubClaimSubmissionEntity> retrieved = modelEntityRepository.findAll(Pageable.unpaged());
    assertThat(retrieved.getContent().size()).isEqualTo(3);
  }

  @Test
  public void testFindAllPaged() {
    populate();
    Pageable pageable = PageRequest.of(0, 2);
    Page<SubClaimSubmissionEntity> retrieved = modelEntityRepository.findAll(pageable);
    assertThat(retrieved.getContent().size()).isEqualTo(2);
  }
}
