import {Wallet, WalletSession} from "./ton/wallets/types";
import {Coins} from "ton3";
import {JettonWalletContract} from "./ton/jettons/contracts/JettonWalletContract";

export interface LocationParams {
    from: string;
    noBack?: boolean;
    noLang?: boolean;
    data: {
    };
}

export type DexInfo = {
    walletInfo: WalletInfo | null
    swapInfo: SwapInfo
}


export type WalletInfo = {
    session: WalletSession
    adapterId: string
    meta: Wallet
    balance: Coins
}

export type JettonInfo = {
    jetton: Jetton;
    wallet: { address?: string };
    balance: Coins;
}

export type Jetton = {
    address: string;
    meta: JettonMeta;
}

export type Pair = {
    address: string;
    left: (JettonInfo | null);
    right: (JettonInfo | null);
    leftReserve: Coins;
    rightReserve: Coins;
    direction: ("normal" | "reverse")
}

export type JettonsData = {
    [key: string]: JettonMeta;
}

export type JettonMeta = {
    name: string;
    symbol: string;
    description?: string;
    image?: string;
    image_data?: string;
    decimal?: number;
}

export type SwapParams = {
    slippage: number
    inAmount: Coins
    outAmount: Coins
}

export type SwapInfo = {
    // slippage: number
    swapParams: SwapParams
    pair: Pair
    // selected: {left: (JettonInfo | null), right: (JettonInfo | null)}
    // pairAddress: string
    // direction: ("normal" | "reverse")
}

export type LiquidityInfo = {

}

export type ReferralInfo = {

}
