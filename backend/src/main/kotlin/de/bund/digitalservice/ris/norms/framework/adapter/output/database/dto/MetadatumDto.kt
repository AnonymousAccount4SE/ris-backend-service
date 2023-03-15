package de.bund.digitalservice.ris.norms.framework.adapter.output.database.dto

import de.bund.digitalservice.ris.norms.domain.entity.MetadatumType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "metadata")
data class MetadatumDto(
    @Id
    val id: Int,
    val value: String,
    val type: MetadatumType,
    @Column("order_number") // Because `order` is a reserved keyword in SQL.
    val order: Int,
    @Column("norm_id")
    val normId: Int,
)