package de.bund.digitalservice.ris.norms.domain.entity

import java.util.UUID

data class Norm(val guid: UUID, val longTitle: String, val articles: List<Article> = listOf())