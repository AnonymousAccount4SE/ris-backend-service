package de.bund.digitalservice.ris.norms.domain.entity

import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParagraphTest {
  @Test
  fun `can create a simple paragraph instance`() {
    val guid = UUID.randomUUID()
    val paragraph = Paragraph(guid, "marker", "text")

    assertThat(paragraph.guid).isEqualTo(guid)
    assertThat(paragraph.marker).isEqualTo("marker")
    assertThat(paragraph.text).isEqualTo("text")
  }
}
