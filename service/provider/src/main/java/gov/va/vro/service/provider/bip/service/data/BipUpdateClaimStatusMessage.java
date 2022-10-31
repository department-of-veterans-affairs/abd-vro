package gov.va.vro.service.provider.bip.service.data;

@Component
@RequiredArgsConstructor
@Slf4j
@Data
public class BipUpdateClaimStatusMessage {
    private String serverity;
    private String test;
    private String key;
    private int status;
    private String timestamp;
}