package gov.va.vro.service.provider.bip.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class BipUpdateClaimStatusResponse {
    private List<BipUpdateClaimStatusMessage> messages;
}