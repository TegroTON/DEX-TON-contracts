package money.tegro.dex.contract

import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.VmStackNumber
import org.ton.block.VmStackValue

fun VmStackValue.asBigInt(): BigInt = (this as VmStackNumber).toBigInt()

fun AddrStd.toSafeBounceable(): String = this.toString(userFriendly = true, urlSafe = true, bounceable = true)
