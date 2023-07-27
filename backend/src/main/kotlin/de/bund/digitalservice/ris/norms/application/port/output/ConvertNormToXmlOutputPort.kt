package de.bund.digitalservice.ris.norms.application.port.output

import de.bund.digitalservice.ris.norms.domain.entity.Norm
import reactor.core.publisher.Mono

fun interface ConvertNormToXmlOutputPort {
  fun convertNormToXml(command: Command): Mono<String>

  data class Command(val norm: Norm)
}
