package money.tegro.dex.contract

import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.VmStackValue

fun VmStackValue.asBigInt(): BigInt = when (this) {
    is VmStackValue.TinyInt -> BigInt(this.value)
    is VmStackValue.Int -> this.value
    else -> throw Exception("cannot cast given VmStackValue to any known integer type")
}

fun AddrStd.toSafeBounceable(): String = this.toString(userFriendly = true, urlSafe = true, bounceable = true)
