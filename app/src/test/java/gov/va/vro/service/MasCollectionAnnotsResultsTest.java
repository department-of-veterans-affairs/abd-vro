package gov.va.vro.service;

import static gov.va.vro.model.mas.MasAnnotType.BLOOD_PRESSURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.va.vro.model.AbdEvidence;
import gov.va.vro.model.mas.MasAnnotation;
import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasDocument;
import gov.va.vro.service.provider.mas.service.mapper.MasCollectionAnnotsResults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author warren @Date 3/20/23 */
class MasCollectionAnnotsResultsTest {

  private static final int COLLECTION_ID = 350;

  private static final String[] BP_ANNOTATIONS = {
    "120/80", "150/82 mg/dL", "125/- mg/dL", "-/ - mg/dL", "32 mg/dL", "120/ mg/dL"
  };
  //  private static final String[] BAD_BP_ANNOTATIONS = {
  //    "-32/123 mg/dL", "120325 mg/dL", "blood pressure 120/80 mg/dL"
  //  };

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

    //    for (String annotation : BAD_BP_ANNOTATIONS) {
    //      annotationList.clear();
    //      MasAnnotation annot = new MasAnnotation();
    //      annot.setAnnotType(annotType);
    //      annot.setAnnotVal(annotation);
    //      annotationList.add(annot);
    //
    //      doc = new MasDocument();
    //      doc.setCondition(BP_CONDITION);
    //      doc.setAnnotations(annotationList);
    //      masCollectionAnnotation.setDocuments(Collections.singletonList(doc));
    //      try {
    //        evidence = results.mapAnnotationsToEvidence(masCollectionAnnotation);
    //        fail("Failed to catch bad blood pressure annotation. " + annotation);
    //      } catch (Exception e) {
    //        assertTrue(e instanceof MasException);
    //      }
    //    }
  }
}
