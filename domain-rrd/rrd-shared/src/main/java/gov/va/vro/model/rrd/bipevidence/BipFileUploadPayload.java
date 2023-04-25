package gov.va.vro.model.rrd.bipevidence;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Payload data is used for BIP Evidence File Upload API.
 *
 * @author warren @Date 11/10/22
 */
@Builder
@Getter
@Setter
public class BipFileUploadPayload {
  private String contentName; // e.g., "filename.pdf",
  private BipFileProviderData providerData;
}
