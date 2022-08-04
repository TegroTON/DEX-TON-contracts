package money.tegro.dex.contract.op

import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor

data class OpSuccessfulSwap(
    val query_id: Long,
) : Op {
    companion object : TlbCodec<OpSuccessfulSwap> by OpSuccessfulSwapConstructor {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<OpSuccessfulSwap> = OpSuccessfulSwapConstructor
    }
}

private object OpSuccessfulSwapConstructor : TlbConstructor<OpSuccessfulSwap>(
    schema = "successful_swap#de6e0675 query_id:uint64 = InternalMsgBody;"
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: OpSuccessfulSwap) {
        cellBuilder.apply {
            storeUInt(value.query_id, 64)
        }
    }

    override fun loadTlb(cellSlice: CellSlice): OpSuccessfulSwap = cellSlice.run {
        OpSuccessfulSwap(
            query_id = loadUInt(64).toLong(),
        )
    }
}
