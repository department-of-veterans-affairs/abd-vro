package gov.va.vro.service.provider.mas.service;

import gov.va.vro.model.mas.MasCollectionAnnotation;
import gov.va.vro.model.mas.MasCollectionStatus;
import gov.va.vro.model.mas.request.MasOrderExamRequest;
import gov.va.vro.service.provider.mas.MasException;

import java.util.List;

public interface IMasApiService {
  List<MasCollectionStatus> getMasCollectionStatus(List<Integer> collectionIds) throws MasException;

  List<MasCollectionAnnotation> getCollectionAnnotations(Integer collectionId) throws MasException;

  String orderExam(MasOrderExamRequest masOrderExamRequest) throws MasException;
}
