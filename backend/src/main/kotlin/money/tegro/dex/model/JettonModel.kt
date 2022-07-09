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

@MappedEntity("jettons")
data class JettonModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    val totalSupply: BigInt,

    val mintable: Boolean,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val admin: MsgAddressInt,

    val name: String?,

    val description: String?,

    val symbol: String?,

    val decimals: Int = 9,

    val image: String?,

    // Cannot be nullable, idk why but the driver really doesn't like nullable byte arrays
    val imageData: ByteArray,
)
