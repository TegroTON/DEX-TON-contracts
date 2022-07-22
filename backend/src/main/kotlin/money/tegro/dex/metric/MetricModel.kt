package money.tegro.dex.metric

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import java.time.Instant

@MappedEntity("metrics")
data class MetricModel(
    val name: String,

    val value: Double,

    @field:TypeDef(type = DataType.JSON)
    val metadata: Map<String, String>,

    @field:Id
    val timestamp: Instant = Instant.now(),
)
