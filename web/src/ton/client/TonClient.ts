import {Address, Coins} from "ton3-core";
import {HttpApi} from "./api/HttpApi";

export type TonClientParameters = {
    endpoint: string;
    timeout?: number;
    apiKey?: string;
}

export type TonClientResolvedParameters = {
    endpoint: string;
}


export class TonClient {
    readonly parameters: TonClientResolvedParameters;

    #api: HttpApi;

    constructor(parameters: TonClientParameters) {
        this.parameters = {
            endpoint: parameters.endpoint
        };
        this.#api = new HttpApi(this.parameters.endpoint, {
            timeout: parameters.timeout,
            apiKey: parameters.apiKey
        });
    }

    async callGetMethodWithError(address: Address, name: string, params: any[] = []): Promise<{ gasUsed: number, stack: any[], exitCode: number }> {
        let res = await this.#api.callGetMethod(address, name, params);
        return { gasUsed: res.gas_used, stack: res.stack, exitCode: res.exit_code };
    }

    async getBalance(address: Address): Promise<Coins> {
        return (await this.getContractState(address)).balance;
    }

    async getContractState(address: Address) {
        let info = await this.#api.getAddressInformation(address);
        let balance = new Coins(info.balance, true);
        let state = info.state as 'frozen' | 'active' | 'uninitialized';
        return {
            balance,
            state,
            // code: info.code !== '' ? Buffer.from(info.code, 'base64') : null,
            // data: info.data !== '' ? Buffer.from(info.data, 'base64') : null,
            lastTransaction: info.last_transaction_id.lt !== '0' ? {
                lt: info.last_transaction_id.lt,
                hash: info.last_transaction_id.hash,
            } : null,
            blockId: {
                workchain: info.block_id.workchain,
                shard: info.block_id.shard,
                seqno: info.block_id.seqno
            },
            timestampt: info.sync_utime
        };
    }

}
