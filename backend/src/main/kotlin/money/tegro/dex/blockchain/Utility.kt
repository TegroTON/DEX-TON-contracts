package money.tegro.dex.blockchain

import org.ton.bigint.BigInt
import org.ton.block.VmStackValue

fun VmStackValue.asBigInt() : BigInt = when(this) {
    is VmStackValue.TinyInt -> BigInt(this.value)
    is VmStackValue.Int -> this.value
    else -> throw Exception("cannot cast given VmStackValue to any known integer type")
}

