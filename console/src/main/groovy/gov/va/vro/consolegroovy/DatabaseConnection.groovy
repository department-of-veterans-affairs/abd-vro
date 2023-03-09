package gov.va.vro.consolegroovy

import gov.va.vro.persistence.repository.ClaimRepository
import gov.va.vro.persistence.repository.VeteranRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Component

@Component
@EnableJpaRepositories("gov.va.vro.persistence.repository")
@EntityScan("gov.va.vro.persistence.model")
@groovy.transform.TupleConstructor
class DatabaseConnection {
  @Autowired
  final ClaimRepository claimRepository

  @Autowired
  final VeteranRepository veteranRepository
}
