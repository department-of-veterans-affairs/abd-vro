package gov.va.vro.service.provider.bip.service.data;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class BipUpdateClaimStatusResponse {
    private List<BipUpdateClaimStatusMessage> messages;
}