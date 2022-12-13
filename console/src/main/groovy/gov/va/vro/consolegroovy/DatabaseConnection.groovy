package gov.va.vro.consolegroovy

import gov.va.vro.persistence.repository.ClaimRepository
import gov.va.vro.persistence.repository.VeteranRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@groovy.transform.TupleConstructor
class DatabaseConnection {
  @Autowired
  final ClaimRepository claimRepository

  @Autowired
  final VeteranRepository veteranRepository
}
