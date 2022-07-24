import {Address, Coins} from "ton3-core";
import {tonClient} from "./index";
import GetMethodParser from "./getMethodParser";
import {JettonMasterContract} from "./jettons/contracts/JettonMasterContract";
import {JettonInfo, Pair} from "../types";
import {Jettons} from "../static/jettons";
import {TonClient} from "./client/TonClient";


export async function runGetMethod(
    client: TonClient, address: Address, method: string, stack: any[] | []
):
    Promise<{ exitCode: number, parsedResult: any }>
{
    const {stack: resultStack, exitCode} = await client.callGetMethodWithError(address, method, stack)
    const parsedResult = exitCode === 0 ? GetMethodParser.parseStack(resultStack) : null
    return {exitCode, parsedResult}
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
    const jettonWallet = await jettonMaster.getJettonWallet(tonClient, new Address(address));
    const jettonWalletAddress = jettonWallet.address.toString(undefined, {bounceable: true})
    const {balance} = await jettonWallet.getData(tonClient);
    return {...jettonInfo, wallet: {address: jettonWalletAddress}, balance: balance}
}


export async function getJettonBalance(address: string, jettonAddress: string): Promise<Coins> {
    const jettonMaster = new JettonMasterContract(new Address(jettonAddress));
    const jettonWallet = await jettonMaster.getJettonWallet(tonClient, new Address(address));
    const {balance} = await jettonWallet.getData(tonClient);
    return balance;
}
