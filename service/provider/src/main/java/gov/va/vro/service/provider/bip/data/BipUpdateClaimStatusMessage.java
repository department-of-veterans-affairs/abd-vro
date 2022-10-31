package gov.va.vro.service.provider.bip.data;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class BipUpdateClaimStatusMessage {
    private String severity;
    private String test;
    private String key;
    private int status;
    private String timestamp;
}