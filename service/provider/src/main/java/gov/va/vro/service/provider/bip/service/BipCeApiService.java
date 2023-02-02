package gov.va.vro.service.provider.bip.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.vro.model.bip.FileIdType;
import gov.va.vro.model.bipevidence.BipFileUploadPayload;
import gov.va.vro.model.bipevidence.BipFileUploadResp;
import gov.va.vro.service.provider.BipApiProps;
import gov.va.vro.service.provider.bip.BipException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;

/**
 * BIP claim API service.
 *
 * @author warren @Date 10/31/22
 */
@Service
@Conditional(BipConditions.NonLocalEnvCeCondition.class)
@RequiredArgsConstructor
@Slf4j
public class BipCeApiService implements IBipCeApiService {
  private static final String X_FOLDER_URI = "VETERAN:%s:%s";
  private static final String HTTPS = "https://";

  private static final String UPLOAD_FILE = "/files";

  @Qualifier("bipCERestTemplate")
  @NonNull
  private final RestTemplate ceRestTemplate;

  private final BipApiProps bipApiProps;

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public BipFileUploadResp uploadEvidenceFile(
      FileIdType idtype, String fileId, BipFileUploadPayload payload, byte[] fileContent)
      throws BipException {
    try {
      String url = HTTPS + bipApiProps.getEvidenceBaseUrl() + UPLOAD_FILE;
      log.info("Call {} to uploadEvidenceFile for {} : {}", url, idtype.name(), fileId);

      HttpHeaders headers = getBipHeader();
      String headerFolderUri = String.format(X_FOLDER_URI, idtype.name(), fileId);
      headers.set("X-Folder-URI", headerFolderUri);
      log.info("X-Folder-URI header is set: {}", headerFolderUri);

      String filename = payload.getContentName();
      MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
      body.add("payload", mapper.writeValueAsString(payload));

      ByteArrayResource contentsAsResource =
          new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
              return filename; // Filename has to be returned in order to be able to post.
            }
          };

      body.add("file", contentsAsResource);
      HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> bipResponse =
          ceRestTemplate.postForEntity(url, httpEntity, String.class);

      BipFileUploadResp resp = new BipFileUploadResp();
      log.info(
          "bip response for upload: status: {}, message: {}",
          bipResponse.getStatusCode(),
          bipResponse.getBody());
      resp.setStatus(bipResponse.getStatusCode());
      resp.setMessage(mapper.writeValueAsString(bipResponse.getBody()));
      return resp;
    } catch (RestClientException | IOException e) {
      log.error("failed to upload file.", e);
      throw new BipException(e.getMessage(), e);
    }
  }

  private HttpHeaders getBipHeader() throws BipException {
    try {
      HttpHeaders bipHttpHeaders = new HttpHeaders();
      bipHttpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

      String jwt = createJwt();
      bipHttpHeaders.add("Authorization", "Bearer " + jwt);
      return bipHttpHeaders;
    } catch (Exception e) {
      log.error("Failed to build BIP HTTP Headers.", e);
      throw new BipException(e.getMessage(), e);
    }
  }

  private String createJwt() throws BipException {
    Claims claims = bipApiProps.toCommonJwtClaims();

    Map<String, Object> headerType = new HashMap<>();
    headerType.put("typ", Header.JWT_TYPE);

    byte[] signSecretBytes = bipApiProps.getEvidenceSecret().getBytes(StandardCharsets.UTF_8);
    SecretKeySpec signingKey =
        new SecretKeySpec(signSecretBytes, SignatureAlgorithm.HS256.getJcaName());
    claims.put("iss", bipApiProps.getEvidenceIssuer());
    return Jwts.builder()
        .setSubject("Evidence")
        .setIssuedAt(Calendar.getInstance().getTime())
        .setExpiration(claims.getExpiration())
        .setClaims(claims)
        .signWith(SignatureAlgorithm.HS256, signingKey)
        .setHeaderParams(headerType)
        .compact();
  }
}
