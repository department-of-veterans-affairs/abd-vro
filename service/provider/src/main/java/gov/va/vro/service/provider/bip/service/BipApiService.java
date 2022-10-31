package gov.va.vro.service.provider.bip.service;

@Component
@RequiredArgsConstructor
@Slf4j
public class BipApiService {
    private final RestTemplate restTemplate;

    public boolean updateClaimStatus(String claimId, String statusCode) throws BipException {
        return false;
    }
}