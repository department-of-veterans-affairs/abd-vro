package gov.va.vro.service;

import static gov.va.vro.model.rrd.mas.MasAnnotType.BLOOD_PRESSURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import gov.va.vro.model.rrd.AbdEvidence;
import gov.va.vro.model.rrd.mas.MasAnnotation;
import gov.va.vro.model.rrd.mas.MasCollectionAnnotation;
import gov.va.vro.model.rrd.mas.MasDocument;
import gov.va.vro.service.provider.mas.service.mapper.MasCollectionAnnotsResults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author warren @Date 3/20/23 */
@Slf4j
class MasCollectionAnnotsResultsTest {

  private static final int COLLECTION_ID = 350;

  private static final String[] BP_ANNOTATIONS = {"120/80", "150/ 82", "125/-", "32 ", "-/80"};
  private static final String[] BAD_BP_ANNOTATIONS = {"-/-", "120325 mg/dL", "-/3245 mg/dL"};

  private static final String BP_CONDITION = "Hypertension";

  @BeforeEach
  void setUp() {}

  @Test
  public void mapAnnotationsToEvidenceTest() {
    MasCollectionAnnotation masCollectionAnnotation = new MasCollectionAnnotation();
    masCollectionAnnotation.setCollectionsId(COLLECTION_ID);
    List<MasAnnotation> annotationList = new ArrayList<>();
    String annotType = BLOOD_PRESSURE.getMasAnnotTypeText();
    for (String annotation : BP_ANNOTATIONS) {
      MasAnnotation annot = new MasAnnotation();
      annot.setAnnotType(annotType);
      annot.setAnnotVal(annotation);
      annotationList.add(annot);
    }

    MasDocument doc = new MasDocument();
    doc.setCondition(BP_CONDITION);
    doc.setAnnotations(annotationList);
    masCollectionAnnotation.setDocuments(Collections.singletonList(doc));

    MasCollectionAnnotsResults results = new MasCollectionAnnotsResults();
    AbdEvidence evidence = results.mapAnnotationsToEvidence(masCollectionAnnotation);
    assertNotNull(evidence);
    assertEquals(doc.getAnnotations().size(), evidence.getBloodPressures().size());

    // Negative tests

    for (String annotation : BAD_BP_ANNOTATIONS) {
      annotationList.clear();
      MasAnnotation annot = new MasAnnotation();
      annot.setAnnotType(annotType);
      annot.setAnnotVal(annotation);
      annotationList.add(annot);

      doc = new MasDocument();
      doc.setCondition(BP_CONDITION);
      doc.setAnnotations(annotationList);
      masCollectionAnnotation.setDocuments(Collections.singletonList(doc));
      try {
        evidence = results.mapAnnotationsToEvidence(masCollectionAnnotation);
        assertTrue(evidence.getBloodPressures().isEmpty());
      } catch (Exception e) {
        fail("Failed to catch bad blood pressure annotation. " + annotation);
      }
    }
  }
}
