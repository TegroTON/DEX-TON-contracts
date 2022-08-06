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

@MappedEntity("wallets")
data class WalletModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val address: MsgAddressInt,

    val balance: BigInt,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val owner: MsgAddress,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressAttributeConverter::class)
    val master: MsgAddress,
    
    val updated: Instant = Instant.now(),
)
