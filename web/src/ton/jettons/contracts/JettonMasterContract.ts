import {Address, BOC, Builder, Cell, Slice} from 'ton3-core';
import {JettonWalletContract} from './JettonWalletContract';
import {runGetMethod} from "../../utils";
import {TonClient} from "../../client/TonClient";

export class JettonMasterContract {
    constructor(
        public readonly address: Address,
    ) {
        this.address = address;
    }

    createChangeAdminRequest(newAdminAddress: Address): Cell {
        return new Builder()
            .storeUint(3, 32)
            .storeUint(0, 64)
            .storeAddress(newAdminAddress)
            .cell();
    }

    async getJettonData(client: TonClient) {
        const { parsedResult } = await runGetMethod(client, this.address, "get_jetton_data", [])

        const totalSupply = parsedResult[0]

        const adminAddress = Slice.parse(parsedResult[2]).preloadAddress();

        const content = parsedResult[3]
        const jettonWalletCode = parsedResult[4];

        return {
            totalSupply,
            adminAddress,
            content,
            jettonWalletCode,
        };
    }

    private async resolveWalletAddress(client: TonClient, owner: Address): Promise<Address> {
        const ownerAddressCell = new Builder()
            .storeAddress(owner)
            .cell()

        const { parsedResult } = await runGetMethod(client, this.address, "get_wallet_address", [
            [
                'tvm.Slice',
                BOC.toBase64([ownerAddressCell], {has_index: false})
            ]
        ])
        return Slice.parse(parsedResult)
            .preloadAddress();
    }

    async getJettonWallet(client: TonClient, ownerAddress: Address): Promise<JettonWalletContract> {
        return new JettonWalletContract(await this.resolveWalletAddress(client, ownerAddress));
    }
}
