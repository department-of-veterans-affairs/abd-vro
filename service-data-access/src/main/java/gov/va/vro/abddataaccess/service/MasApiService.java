package gov.va.vro.abddataaccess.service;

import gov.va.vro.abddataaccess.exception.MasException;
import gov.va.vro.abddataaccess.model.MasCollectionAnnotation;
import gov.va.vro.abddataaccess.model.MasCollectionStatus;
import gov.va.vro.abddataaccess.model.MasOrderExam;

import java.util.List;

/**
 * MAS API access service.
 *
 * @author warren @Date 10/5/22
 */
public interface MasApiService {

  List<MasCollectionStatus> getMasCollectionStatus(List<Integer> collectionIds) throws MasException;

  List<MasCollectionAnnotation> queryCollectionAnnots(int collectionIds) throws MasException;

  MasOrderExam orderExam(int collectionId) throws MasException;
}
