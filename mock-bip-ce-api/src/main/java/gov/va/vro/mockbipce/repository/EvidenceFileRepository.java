package gov.va.vro.mockbipce.repository;

import gov.va.vro.mockbipce.model.EvidenceFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvidenceFileRepository extends CrudRepository<EvidenceFile, String> {
}
