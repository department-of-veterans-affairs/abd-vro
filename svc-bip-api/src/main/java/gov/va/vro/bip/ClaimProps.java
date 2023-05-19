package gov.va.vro.bip;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** @author warren @Date 2/7/23 */
@Getter
@Setter
@ConfigurationProperties(prefix = "claim")
public class ClaimProps {
  private String specialIssue1;
  private String specialIssue2;
}
