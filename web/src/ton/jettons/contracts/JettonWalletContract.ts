import { JettonOperation } from '../enums/JettonOperation';
import { parseInternalTransferTransaction } from './parsers/parseInternalTransferTransaction';
import { parseTransferTransaction } from './parsers/parseTransferTransaction';
import type { JettonTransaction } from '../types/JettonTransaction';
import {Coins, Address, Cell, Builder} from "ton3-core";
import {runGetMethod} from "../../utils";
import {TonClient} from "../../client/TonClient";

export class JettonWalletContract {
    constructor(
        public readonly address: Address,
    ) {
        this.address = address;
    }

    isDeployed(client: TonClient) {
        // return this.client.isContractDeployed(this.address);
    }

    async getData(client: TonClient): Promise<{ balance: Coins }> {
        const { exitCode, parsedResult } = await runGetMethod(client, this.address, "get_wallet_data", [])

        if (exitCode === -13) return { balance: new Coins(0) }; // not deployed
        if (exitCode !== 0) {
            throw new Error('Cannot retrieve jetton wallet data.');
        }

        const balance = new Coins(parsedResult[0], true)
        // balance
        // owner_address
        // jetton_master_address
        // jetton_wallet_code

        return {
            balance,
        };
    }

    // async getTransactions(limit = 5) {
    //     const transactions = await this.client.getTransactions(this.address, {
    //         limit,
    //     });
    //
    //     return transactions
    //         .map((transaction): JettonTransaction | null => {
    //             if (transaction.inMessage?.body?.type !== 'data') {
    //                 return null; // Not a jetton transaction
    //             }
    //
    //             const bodySlice = Cell.fromBoc(transaction.inMessage.body.data)[0].beginParse();
    //             const operation = bodySlice.readUint(32)
    //                 .toNumber();
    //             try {
    //                 switch (operation) {
    //                 case JettonOperation.TRANSFER:
    //                     return parseTransferTransaction(bodySlice, transaction);
    //
    //                 case JettonOperation.INTERNAL_TRANSFER:
    //                     return parseInternalTransferTransaction(bodySlice, transaction);
    //
    //                 default:
    //                     return null; // Unknown operation
    //                 }
    //             } catch {
    //                 return null;
    //             }
    //         })
    //         .filter((transaction) => !!transaction) as JettonTransaction[];
    // }
    //
    createTransferRequest({
        queryId = 0,
        amount,
        destination,
        responseDestination = null,
        forwardAmount = new Coins(0),
        forwardPayload = null,
    }: {
        queryId: number | bigint,
        amount: Coins;
        destination: Address;
        responseDestination?: Address | null;
        forwardAmount: Coins;
        forwardPayload: Cell | null;
    }): Cell {
        const builder = new Builder()
            .storeUint(0xf8a7ea5, 32)
            .storeUint(queryId, 64)
            .storeCoins(amount)
            .storeAddress(destination)
            .storeAddress(responseDestination)
            .storeBit(0)
            .storeCoins(forwardAmount)

        if (!forwardPayload || forwardPayload.bits.length <= builder.remainder) {
            builder.storeBit(0)
            if (forwardPayload) {
                builder.storeBits(forwardPayload.bits);
            }
        } else {
            builder.storeBit(1);
            builder.storeRef(forwardPayload);
        }

        return builder.cell();
    }
}
