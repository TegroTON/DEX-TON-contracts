package money.tegro.dex.contract

import org.ton.block.AddrStd

fun AddrStd.toSafeBounceable(): String = this.toString(userFriendly = true, urlSafe = true, bounceable = true)
