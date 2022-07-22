import type { Slice, TonTransaction } from 'ton';
import { JettonOperation } from '../../enums/JettonOperation';
import type { JettonIncomeTransaction } from '../../types/JettonIncomeTransaction';

/**
 internal_transfer  query_id:uint64 amount:(VarUInteger 16) from:MsgAddress
 response_address:MsgAddress
 forward_ton_amount:(VarUInteger 16)
 forward_payload:(Either Cell ^Cell)
 = InternalMsgBody;
 */

export function parseInternalTransferTransaction(bodySlice: Slice, transaction: TonTransaction): JettonIncomeTransaction {
    const queryId = bodySlice.readUint(64);
    const amount = bodySlice.readCoins();
    const from = bodySlice.readAddress();

    bodySlice.readAddress(); // response_address
    bodySlice.readCoins(); // forward_ton_amount

    const commentCell = bodySlice.readBit()
        ? bodySlice.readRef()
        : bodySlice;

    if (commentCell.remaining >= 32) {
        commentCell.skip(32);
    }

    const comment = commentCell.remaining % 8 === 0
        ? commentCell.readRemainingBytes().toString()
        : '';

    return {
        operation: JettonOperation.INTERNAL_TRANSFER,
        time: transaction.time,
        queryId: queryId.toString(10),
        amount: amount.toString(10),
        from: from?.toFriendly() ?? null,
        comment,
    };
}
