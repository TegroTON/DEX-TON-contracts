package money.tegro.dex.contract

import mu.KLogging
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.VmStackValue
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient

interface PairContract {
    companion object : KLogging() {
        @JvmStatic
        suspend fun isInitialized(address: AddrStd, liteClient: LiteClient): Boolean =
            liteClient.runSmcMethod(LiteServerAccountId(address), "initialized").toMutableVmStack().let {
                it.popNumber().toLong() != 0L
            }

        @JvmStatic
        suspend fun getReserves(address: AddrStd, liteClient: LiteClient): Pair<BigInt, BigInt> =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_reserves").toMutableVmStack().let {
                it.popNumber().toBigInt() to it.popNumber().toBigInt()
            }

        @JvmStatic
        suspend fun getLpShare(address: AddrStd, amount: BigInt, liteClient: LiteClient): Pair<BigInt, BigInt> =
            liteClient.runSmcMethod(LiteServerAccountId(address), "get_lp_share", VmStackValue.of(amount))
                .toMutableVmStack().let {
                    it.popNumber().toBigInt() to it.popNumber().toBigInt()
                }
    }
}
