package money.tegro.dex.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import money.tegro.dex.converter.AddrStdAttributeConverter
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("exchange_pairs")
data class ExchangePairModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    /** Address of the left jetton, null denotes Toncoin */
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val left: AddrStd?,

    /** Address of the right jetton */
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val right: AddrStd,

    /** Amount reserved in the liquidity pool of the left currency */
    val leftReserve: BigInt,

    /** Amount reserved in the liquidity pool of the right jetton */
    val rightReserve: BigInt,

    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
)
