import {Address, Builder, Cell, Coins, Providers} from "ton3";
import {JettonWalletContract} from "../../jettons/contracts/JettonWalletContract";
import {JettonMasterContract} from "../../jettons/contracts/JettonMasterContract";
import {runGetMethod} from "../../utils";

export class DexBetaPairContract extends JettonMasterContract {
    createAddLiquidityRequest(tonAmount: Coins, jettonAmount: Coins, myAddress: Address, jettonWallet: JettonWalletContract): Cell {
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        const payload = new Builder()
            .storeUint(0x287e167a, 32) // sub-op
            .storeCoins(new Coins(0))
            .storeCoins(new Coins(0))
            .cell();
        return jettonWallet.createTransferRequest({
            queryId,
            amount: jettonAmount,
            destination: this.address,
            responseDestination: myAddress,
            forwardAmount: tonAmount,
            forwardPayload: payload
        })
    }

    async createInstallRequest(client: Providers.ClientRESTV2, jettonAddress: string): Promise<Cell> {
        const jettonMaster = new JettonMasterContract(new Address(jettonAddress))
        const jettonWallet = await jettonMaster.getJettonWallet(client, new Address(this.address))
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        return new Builder()
            .storeUint(0x3356dc14, 32)
            .storeUint(queryId, 64)
            .storeAddress(jettonWallet.address)
            .cell()
    }

    async getReserves(client: Providers.ClientRESTV2): Promise<[Coins, Coins]> {
        const {parsedResult} = await runGetMethod(client, this.address, "get_reserves", [])
        return parsedResult.map((item: bigint) => new Coins(item, true))
    }

    // createRemoveLiquidityRequest(lpAmount: Coins): Cell {
    //     // тут надо сделать запрос на сжигание этих токенов
    // }

    createTonSwapRequest(minReceived: Coins): Cell {
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        return new Builder()
            .storeUint(0x565c7311, 32)
            .storeUint(queryId, 64)
            .storeCoins(minReceived)
            .cell()
    }

    createJettonSwapRequest(jettonAmount: Coins, minReceived: Coins, myAddress: Address, jettonWallet: JettonWalletContract): Cell {
        const queryId = Math.round(Date.now() / Math.PI / Math.random());
        const payload = new Builder()
            .storeUint(0x5772112, 32) // sub-op
            .storeCoins(minReceived)
            .cell();
        return jettonWallet.createTransferRequest({
            queryId,
            amount: jettonAmount,
            destination: this.address,
            responseDestination: myAddress,
            forwardAmount: new Coins(0.1),
            forwardPayload: payload
        })
    }
}
