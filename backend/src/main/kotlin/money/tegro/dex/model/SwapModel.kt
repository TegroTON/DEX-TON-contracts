package money.tegro.dex.model

import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.model.DataType
import money.tegro.dex.converter.MsgAddressIntAttributeConverter
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import java.time.Instant

@MappedEntity("swaps")
data class SwapModel(
    @field:Id
    val hash: ByteArray,

    val lt: Long,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val src: MsgAddressInt,

    @field:TypeDef(type = DataType.BYTE_ARRAY, converter = MsgAddressIntAttributeConverter::class)
    val dest: MsgAddressInt,

    val amount: BigInt,

    val timestamp: Instant = Instant.now(),
)
