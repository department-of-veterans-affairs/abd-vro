package gov.va.vro.service.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MasApiProps {

  private final String baseUrl;
  private final String collectionStatusPath;
  private final String collectionAnnotsPath;
  private final String createExamOrderPath;
}
