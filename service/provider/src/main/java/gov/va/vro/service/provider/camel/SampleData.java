package gov.va.vro.service.provider.camel;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import gov.va.vro.service.spi.demo.model.AssessHealthData;
import gov.va.vro.service.spi.demo.model.GeneratePdfPayload;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.io.Charsets;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Slf4j
public class SampleData {

  AssessHealthData sampleAssessHealthPayload(AssessHealthData payload) {
    log.info("Using sample Lighthouse Observation Response string");
    if (payload == null) {
      payload = new AssessHealthData();
      payload.setContention("hypertension");
    }
    String sampleLighthouseObservationResponse = retrieveGist("lighthouse_observations_resp.json");
    payload.setBpObservations(sampleLighthouseObservationResponse);
    return payload;
  }

  GeneratePdfPayload sampleGeneratePdfPayload(GeneratePdfPayload body) {
    JSONObject patientInfo = new JSONObject();
    patientInfo.put("first", "Cat");
    patientInfo.put("middle", "Marie");
    patientInfo.put("last", "Power");
    patientInfo.put("suffix", "Jr.");
    patientInfo.put("birthdate", "10-10-1968");

    GeneratePdfPayload payload = new GeneratePdfPayload();
    payload.setDiagnosticCode("asthma");
    payload.setVeteranInfo(patientInfo.toJSONString());
    payload.setEvidence(retrieveGist("assessed_data_asthma.json"));
    return payload;
  }

  private JSONObject toJsonObject(String jsonString) {
    JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
    try {
      return (JSONObject) parser.parse(jsonString);
    } catch (ParseException | RuntimeException e) {
      log.error("", e);
      return null;
    }
  }

  private static String revision = "0d5ed1ce84d953e798782d3bba4aafd88d03d284";
  private static String baseUrl =
      "https://gist.githubusercontent.com/yoomlam/"
          + "0e22b8d01f6fd1bd51d6912dd051fda9/raw/"
          + revision
          + "/";

  // cache responses, so we don't hit the URL too often
  private static Map<String, String> gistCache = Maps.newHashMap();

  private String retrieveGist(String filename) {
    String responseString = gistCache.get(filename);
    if (responseString == null) {
      log.info("Retrieving gist file: " + filename);
      String urlString = baseUrl + filename;
      try {
        BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
        responseString = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
        gistCache.put(filename, responseString);
      } catch (IOException e) {
        log.error("", e);
        responseString = "";
      }
    }
    return responseString;
  }
}
