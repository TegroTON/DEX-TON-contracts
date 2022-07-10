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

@MappedEntity("jettons")
data class JettonModel(
    @field:Id
    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = AddrStdAttributeConverter::class)
    val address: AddrStd,

    val totalSupply: BigInt,

    val mintable: Boolean,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val admin: MsgAddressInt,

    val name: String? = null,

    val description: String? = null,

    val symbol: String? = null,

    val decimals: Int = 9,

    val image: String? = null,

    // Cannot be nullable, idk why but the driver really doesn't like nullable byte arrays
    val imageData: ByteArray = byteArrayOf(),

    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
)
