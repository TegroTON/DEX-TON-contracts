import {SwapInfo, WalletInfo} from "./types";
import {restoreSession} from "./ton/wallets/WalletService";
import {Coins} from "ton3";
import {getDefaultPair} from "./ton/dex/utils";

export function loadSwapInfo(): SwapInfo {
    const slippage = localStorage.getItem('slippage')
    const parseSlippage = slippage && slippage !== "NaN" ? parseFloat(slippage) : 0.5
    const normSlippage = parseSlippage > 99.7 ? 99.7 : parseSlippage < 0.1 ? 0.1 : parseSlippage
    const pair = localStorage.getItem('pair')
    const parsePair = pair ? JSON.parse(pair) : getDefaultPair()
    if (parsePair.left) parsePair.left.balance = new Coins(0)
    if (parsePair.right) parsePair.right.balance = new Coins(0)
    parsePair.leftReserve = new Coins(0)
    parsePair.leftReserve = new Coins(0)

    const swapInfo = {swapParams: {slippage: normSlippage, inAmount: new Coins(0), outAmount: new Coins(0)}, pair: parsePair} as SwapInfo;
    saveSwapInfo(swapInfo)
    return swapInfo
}

export function saveSwapInfo(swapInfo: SwapInfo) {
    const pair: any = (JSON.parse(JSON.stringify(swapInfo.pair)));
    if (pair.left !== null) delete pair.left.balance
    if (pair.right !== null) delete pair.right.balance
    if (pair.leftReserve) delete pair.leftReserve
    if (pair.rightReserve) delete pair.rightReserve
    localStorage.setItem('slippage', JSON.stringify(swapInfo.swapParams.slippage))
    localStorage.setItem('pair', JSON.stringify(pair))
}

export async function loadWalletInfo(): Promise<WalletInfo | null> {
    try {
        const [adapterId, session, wallet] = await restoreSession();
        return {adapterId, session, meta: wallet, balance: new Coins(0)}
    } catch {
        return null
    }
}
