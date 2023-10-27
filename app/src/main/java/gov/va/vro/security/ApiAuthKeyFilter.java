package gov.va.vro.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Slf4j
public class ApiAuthKeyFilter extends AbstractPreAuthenticatedProcessingFilter {
  private final String headerName;

  public ApiAuthKeyFilter(String headerName) {
    this.headerName = headerName;
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    return request.getHeader(headerName);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return null;
  }
}
