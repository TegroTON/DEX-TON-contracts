package money.tegro.dex.measurement

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.Instant

@MappedEntity("measurements")
data class MeasurementModel(
    val name: String,

    @field:TypeDef(type = DataType.JSON)
    val dimensions: Map<String, String>,

    @field:TypeDef(type = DataType.JSON)
    val metadata: Map<String, String>,

    val value: Double,

    @field:Id
    val timestamp: Instant = Instant.now(),
)
