package money.tegro.dex.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import money.tegro.dex.converter.MsgAddressAttributeConverter
import money.tegro.dex.converter.MsgAddressIntAttributeConverter
import org.ton.bigint.BigInt
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@MappedEntity("pairs")
data class PairModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val address: MsgAddressInt,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val base: MsgAddress,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val quote: MsgAddressInt,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val baseWallet: MsgAddress,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val quoteWallet: MsgAddressInt,

    val baseReserve: BigInt,

    val quoteReserve: BigInt,

    val updated: Instant = Instant.now(),
)
