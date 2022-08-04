package money.tegro.dex.contract.op

import org.ton.tlb.TlbCodec
import org.ton.tlb.TlbCombinator
import org.ton.tlb.TlbConstructor

sealed interface Op {
    companion object : TlbCodec<Op> by OpCombinator
}

private object OpCombinator : TlbCombinator<Op>() {
    override val constructors: List<TlbConstructor<out Op>> = listOf(OpSuccessfulSwap.tlbCodec(), OpTransfer.tlbCodec())
}
