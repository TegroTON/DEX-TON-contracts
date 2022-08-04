package money.tegro.dex.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import money.tegro.dex.converter.MsgAddressIntAttributeConverter
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import java.time.Instant

@MappedEntity("tokens")
data class TokenModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val address: MsgAddressInt,

    val supply: BigInt,

    val mintable: Boolean,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val admin: MsgAddressInt,

    val name: String,

    val description: String,

    val symbol: String,

    val decimals: Int = 9,

    val image: String,

    val updated: Instant = Instant.now(),
)
