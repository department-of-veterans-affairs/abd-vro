package gov.va.vro.service.provider.bip.service;

import gov.va.vro.service.provider.bip.BipApiProps;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author warren @Date 11/1/22
 */
class BipApiServiceTest {

    @Test
    public void testUpdateClaimStatus() {

        RestTemplate restTemplate = new RestTemplate();
        BipApiProps props = new BipApiProps();
        BipApiService service = new BipApiService(restTemplate, props);

    }
}