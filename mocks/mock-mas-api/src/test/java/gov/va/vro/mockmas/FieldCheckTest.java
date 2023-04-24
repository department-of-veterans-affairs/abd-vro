package gov.va.vro.mockmas;

import gov.va.vro.mockmas.config.MasApiProperties;
import gov.va.vro.mockmas.config.MasApiService;
import gov.va.vro.mockmas.config.MasOauth2Properties;
import gov.va.vro.model.rrd.mas.MasAnnotation;
import gov.va.vro.model.rrd.mas.MasCollectionAnnotation;
import gov.va.vro.model.rrd.mas.MasDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * For now checks for incomplete blood pressure with plans to extends to other fields. To use change
 * mcp-mas properties in application-test.yml to point to your environment values (i.e. prod-test),
 * uncomment @Test, and update for collection ids you want to check. Once it is run search for
 * "Incomplete reading" tp see the incomplete reading.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
@EnableConfigurationProperties({MasApiProperties.class, MasOauth2Properties.class})
public class FieldCheckTest {
  private static final String BP_READING_REGEX = "^\\d{1,3}\\/\\d{1,3}$";

  @Autowired private MasApiService apiService;

  void checkBloodPressure(List<MasCollectionAnnotation> collections) {
    for (MasCollectionAnnotation collection : collections) {
      log.info(String.valueOf(collection.getCollectionsId()));
      log.info("===========================================");
      List<MasDocument> documents = collection.getDocuments();
      if (documents == null || documents.size() < 1) {
        log.info("No document found for {}", collection.getCollectionsId());
        log.info("");
        continue;
      }
      for (MasDocument document : documents) {
        log.info("Document: {}", document.getDocTypeDescription());
        log.info("===================");
        List<MasAnnotation> annotations = document.getAnnotations();
        if (annotations == null || annotations.size() < 1) {
          log.info("No annotations found for {}", document.getDocTypeDescription());
          log.info("");
          continue;
        }
        int bpAnnotationCount = 0;
        int bpIncompleteCount = 0;

        for (MasAnnotation annotation : annotations) {
          if (!annotation.getAnnotType().equals("blood_pressure")) {
            continue;
          }
          ++bpAnnotationCount;
          String value = annotation.getAnnotVal();
          if (value.matches(BP_READING_REGEX)) {
            continue;
          }
          log.info("Incomplete reading: {}", value);
          ++bpIncompleteCount;
        }
        log.info("");
        log.info(
            "Total BP Count: {}, incomplete BP Count: {}", bpAnnotationCount, bpIncompleteCount);
      }
    }
  }

  // @Test
  void extraordinaryBloodPressures() {
    int[] collectionIds = {5745, 5746, 5747, 5748, 5751, 5753, 5754};

    for (int collectionIndex = 0; collectionIndex < collectionIds.length; ++collectionIndex) {
      int collectionId = collectionIds[collectionIndex];
      List<MasCollectionAnnotation> collections = apiService.getAnnotation(collectionId);
      log.info("******************************************************");
      if (collections == null) {
        log.info("Collection {} cannot be found", collectionId);
      } else {
        checkBloodPressure(collections);
      }
      log.info("******************************************************");
    }
  }
}
