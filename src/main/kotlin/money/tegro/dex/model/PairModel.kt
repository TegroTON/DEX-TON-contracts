package money.tegro.dex.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import money.tegro.dex.converter.AddrStdAttributeConverter
import money.tegro.dex.converter.MsgAddressIntAttributeConverter
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import java.time.Instant

@MappedEntity("pairs")
data class PairModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val base: AddrStd,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val quote: AddrStd,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val baseWallet: MsgAddressInt,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val quoteWallet: MsgAddressInt,

    val baseReserve: BigInt,

    val quoteReserve: BigInt,

    val updated: Instant = Instant.now(),
)
