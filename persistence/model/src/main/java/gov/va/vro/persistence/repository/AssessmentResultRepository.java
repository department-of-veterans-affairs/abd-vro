package gov.va.vro.persistence.repository;

import gov.va.vro.persistence.model.AssessmentResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResultEntity, UUID> {

  @Query(
      value =
          "SELECT SUM(CAST(getlatestcounts->>'medicationsCount' as integer)) FROM claims.GetLatestCounts()",
      nativeQuery = true)
  int getMedicationsCount();

  @Query(
      value =
          "SELECT SUM(CAST(getlatestcounts->>'totalBpReadings' as integer)) FROM claims.GetLatestCounts()",
      nativeQuery = true)
  int getTotalBpReadingsCount();

  @Query(
      value =
          "SELECT SUM(CAST(getlatestcounts->>'recentBpReadings' as integer)) FROM claims.GetLatestCounts()",
      nativeQuery = true)
  int getRecentBpReadingsCount();

  @Query(
      value =
          "SELECT SUM(CAST(getlatestcounts->>'proceduresCount' as integer)) FROM claims.GetLatestCounts()",
      nativeQuery = true)
  int getProceduresCount();
}
