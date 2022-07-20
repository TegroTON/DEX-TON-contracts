import {Address, Coins, Providers} from "ton3";
import {tonClient} from "./index";
import GetMethodParser from "./getMethodParser";
import {RawGetMethodResult} from "./types";
import {JettonMasterContract} from "./jettons/contracts/JettonMasterContract";
import {JettonInfo, Pair} from "../types";
import {Jettons} from "../static/jettons";

export async function runGetMethod(
    client: Providers.ClientRESTV2, address: Address, method: string, stack: any[] | []
):
    Promise<{ exitCode: number, parsedResult: any }>
{
    const {data: {result: result}} = await client.runGetMethod(null, {address: address.toString(), method: method, stack: stack})
    // @ts-ignore
    const rawResult: RawGetMethodResult = result;
    const exitCode = rawResult.exit_code;
    const parsedResult = exitCode === 0 ? GetMethodParser.parseRawResult(rawResult) : null
    return {exitCode, parsedResult}
}


export async function getTonBalance(address: string): Promise<Coins> {
    const client = await tonClient
    const {data: {result: rawBalance}} = await client.getAddressBalance({address})
    return new Coins(rawBalance as string, true)
}

export function getDefaultJetton(): JettonInfo {
    const jettonsAddresses = Object.keys(Jettons || {})
    const jettonAddress = jettonsAddresses[1]
    const jettonMeta = Jettons[jettonAddress]
    return {jetton: {address: jettonAddress, meta: jettonMeta}, wallet: {}, balance: new Coins(0)}
}


export async function updateJettonWallet(jettonInfo: JettonInfo, address: string | null): Promise<JettonInfo> {
    if (address === null) return jettonInfo;
    const jettonMaster = new JettonMasterContract(new Address(jettonInfo.jetton.address));
    const jettonWallet = await jettonMaster.getJettonWallet(await tonClient, new Address(address));
    const jettonWalletAddress = jettonWallet.address.toString(undefined, {bounceable: true})
    const {balance} = await jettonWallet.getData(await tonClient);
    return {...jettonInfo, wallet: {address: jettonWalletAddress}, balance: balance}
}

export async function getJettonBalance(address: string, jettonAddress: string): Promise<Coins> {
    const jettonMaster = new JettonMasterContract(new Address(jettonAddress));
    const jettonWallet = await jettonMaster.getJettonWallet(await tonClient, new Address(address));
    const {balance} = await jettonWallet.getData(await tonClient);
    return balance;
}
