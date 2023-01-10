package gov.va.vro.controller.advice;

import gov.va.vro.controller.exception.DisallowedPatternException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
@ComponentScan
public class InputSanitizerAdvice implements RequestBodyAdvice {

  // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
  private static final Pattern NON_PRINTABLE = Pattern.compile("\\P{Print}&&[^{}]");
  private static final Pattern NUL_CHARACTER = Pattern.compile("(\\u0000|%00)");

  @Override
  public boolean supports(
      MethodParameter methodParameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    // Return true to indicate that this advice should be applied to all request
    // bodies
    return true;
  }

  @Override
  public HttpInputMessage beforeBodyRead(
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType)
      throws IOException {
    // Create a custom HttpInputMessage that sanitizes the request body before it is
    // read
    return new HttpInputMessage() {
      @Override
      public InputStream getBody() throws IOException {
        // Read the request body and remove non-printable and NUL characters from it
        String requestBody =
            new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8);

        Matcher nonPrintableMatcher = NON_PRINTABLE.matcher(requestBody);
        boolean nonPrintableMatches = nonPrintableMatcher.find();

        Matcher nullCharacterMatcher = NUL_CHARACTER.matcher(requestBody);
        boolean nullCharacterMatches = nullCharacterMatcher.find();

        if (nonPrintableMatches || nullCharacterMatches) {
          throw new DisallowedPatternException();
        } else {
          return new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        }
      }

      @Override
      public HttpHeaders getHeaders() {
        return inputMessage.getHeaders();
      }
    };
  }

  // The following methods are not used in this implementation and can be left
  // blank
  @Override
  public Object afterBodyRead(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  @Override
  public Object handleEmptyBody(
      Object body,
      HttpInputMessage inputMessage,
      MethodParameter parameter,
      Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }
}
