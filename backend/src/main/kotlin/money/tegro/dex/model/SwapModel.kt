package money.tegro.dex.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import money.tegro.dex.converter.AddrStdAttributeConverter
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("swaps")
data class SwapModel(
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val pair: AddrStd,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val token: AddrStd,

    val amount: BigInt,

    val lt: Long,

    @field:Id
    val timestamp: Instant = Instant.now(),
)
