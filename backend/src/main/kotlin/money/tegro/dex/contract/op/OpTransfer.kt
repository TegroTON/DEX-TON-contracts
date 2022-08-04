package money.tegro.dex.contract.op

import org.ton.block.Either
import org.ton.block.Maybe
import org.ton.block.MsgAddress
import org.ton.block.VarUInteger
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbConstructor
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.constructor.tlbCodec
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class OpTransfer(
    val query_id: Long,
    val amount: VarUInteger,
    val destination: MsgAddress,
    val response_destination: MsgAddress,
    val custom_payload: Maybe<Cell>,
    val forward_ton_amount: VarUInteger,
    val forward_payload: Either<Cell, Cell>,
) : Op {
    companion object : TlbCodec<OpTransfer> by OpTransferConstructor {
        @JvmStatic
        fun tlbCodec(): TlbConstructor<OpTransfer> = OpTransferConstructor
    }
}

private object OpTransferConstructor : TlbConstructor<OpTransfer>(
    schema = "transfer#0f8a7ea5 query_id:uint64 amount:(VarUInteger 16) destination:MsgAddress\n" +
            "                 response_destination:MsgAddress custom_payload:(Maybe ^Cell)\n" +
            "                 forward_ton_amount:(VarUInteger 16) forward_payload:(Either Cell ^Cell)\n" +
            "                 = InternalMsgBody;"
) {
    override fun storeTlb(cellBuilder: CellBuilder, value: OpTransfer) {
        cellBuilder.apply {
            storeUInt(value.query_id, 64)
            storeTlb(VarUInteger.tlbCodec(16), value.amount)
            storeTlb(MsgAddress, value.destination)
            storeTlb(MsgAddress, value.response_destination)
            storeTlb(Maybe.tlbCodec(AnyTlbConstructor), value.custom_payload)
            storeTlb(VarUInteger.tlbCodec(16), value.forward_ton_amount)
            storeTlb(Either.tlbCodec(AnyTlbConstructor, Cell.tlbCodec()), value.forward_payload)
        }
    }

    override fun loadTlb(cellSlice: CellSlice): OpTransfer = cellSlice.run {
        OpTransfer(
            query_id = loadUInt(64).toLong(),
            amount = loadTlb(VarUInteger.tlbCodec(16)),
            destination = loadTlb(MsgAddress),
            response_destination = loadTlb(MsgAddress),
            custom_payload = loadTlb(Maybe.tlbCodec(AnyTlbConstructor)),
            forward_ton_amount = loadTlb(VarUInteger.tlbCodec(16)),
            forward_payload = loadTlb(Either.tlbCodec(AnyTlbConstructor, Cell.tlbCodec()))
        )
    }
}
