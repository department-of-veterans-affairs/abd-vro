package gov.va.vro.service.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MasDelays {

  private final long masProcessingInitialDelay;

  private final long masProcessingSubsequentDelay;
}
