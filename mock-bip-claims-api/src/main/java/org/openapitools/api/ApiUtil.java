package org.openapitools.api;

import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

public class ApiUtil {
  /**
   * Example response.
   *
   * @param req request
   * @param contentType content type
   * @param example example
   */
  public static void setExampleResponse(NativeWebRequest req, String contentType, String example) {
    try {
      HttpServletResponse res = req.getNativeResponse(HttpServletResponse.class);
      res.setCharacterEncoding("UTF-8");
      res.addHeader("Content-Type", contentType);
      res.getWriter().print(example);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
