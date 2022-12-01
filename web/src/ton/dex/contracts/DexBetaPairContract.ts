import {
    Address, Builder, Cell, Coins,
} from 'ton3-core';
import { TonClient } from '@tegro/ton3-client';
import { JettonWallet } from '@tegro/ton3-contracts';
import { tonClient } from '../../index';

export class DexBetaPairContract {
    constructor(
        public readonly address: Address,
    ) {
        this.address = address;
    }

    createAddLiquidityRequest(tonAmount: Coins, jettonAmount: Coins, myAddress: Address): Cell {
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        const payload = new Builder()
            .storeUint(0x287e167a, 32) // sub-op
            .storeCoins(new Coins(0))
            .storeCoins(new Coins(0))
            .cell();
        return JettonWallet.createTransferRequest({
            queryId,
            amount: jettonAmount,
            destination: this.address,
            responseDestination: myAddress,
            forwardAmount: tonAmount,
            forwardPayload: payload,
        });
    }

    async createInstallRequest(client: TonClient, jettonAddress: string): Promise<Cell> {
        const jettonWalletAddress = tonClient.Jetton.getWalletAddress(
            new Address(jettonAddress),
            new Address(this.address),
        );
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        return new Builder()
            .storeUint(0x3356dc14, 32)
            .storeUint(queryId, 64)
            .storeAddress(await jettonWalletAddress)
            .cell();
    }

    async getReserves(client: TonClient): Promise<[Coins, Coins]> {
        const { stack } = await client.callGetMethod(this.address, 'get_reserves', []);
        return stack.map((item: bigint) => new Coins(item, { isNano: true })) as [Coins, Coins];
    }

    // createRemoveLiquidityRequest(lpAmount: Coins): Cell {
    //     // тут надо сделать запрос на сжигание этих токенов
    // }

    static createTonSwapRequest(minReceived: Coins): Cell {
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        return new Builder()
            .storeUint(0x565c7311, 32)
            .storeUint(queryId, 64)
            .storeCoins(minReceived)
            .cell();
    }

    createJettonSwapRequest(jettonAmount: Coins, minReceived: Coins, myAddress: Address): Cell {
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        const payload = new Builder()
            .storeUint(0x5772112, 32) // sub-op
            .storeCoins(minReceived)
            .cell();
        return JettonWallet.createTransferRequest({
            queryId,
            amount: jettonAmount,
            destination: this.address,
            responseDestination: myAddress,
            forwardAmount: new Coins(0.1),
            forwardPayload: payload,
        });
    }

    async getLPShare(client: TonClient, LPAmount: Coins): Promise<[Coins, Coins]> {
        const { stack } = await client.callGetMethod(this.address, 'get_lp_share', [['num', LPAmount.toNano()]]);
        return stack.map((item: bigint) => new Coins(item, { isNano: true })) as [Coins, Coins];
    }
}
