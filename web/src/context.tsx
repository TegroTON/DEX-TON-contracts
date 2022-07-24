import React from 'react';
import {DexInfo, JettonInfo, Pair, SwapInfo, SwapParams, WalletInfo} from "./types";
import {restoreSession} from "./ton/wallets/WalletService";
import {loadSwapInfo, saveSwapInfo} from "./utils";
import {getDefaultJetton, updateJettonWallet} from "./ton/utils";
import {DexBetaPairContract} from "./ton/dex/contracts/DexBetaPairContract";
import {Address} from "ton3-core";
import {tonClient} from "./ton";
import {getPairInfo, getValidPair} from "./ton/dex/utils";



export type DexContextType = {
    dexInfo: DexInfo;
    updateDexInfo: (newPair?: Pair) => Promise<void>;
    updateSlippage: (newSlippage: number) => void;
    updateSwapParams: (newSwapParams: SwapParams) => void;
};

export const DexContext = React.createContext<DexContextType | null>(null);

interface Props {
    children: React.ReactNode;
}

export const DexContextProvider: React.FC<Props> = ({ children }) => {
    const [dexInfo, setDexInfo] = React.useState<DexInfo>({walletInfo: null, swapInfo: loadSwapInfo()});

    const updateDexInfo = async (newPair?: Pair) => {
        let walletInfo = dexInfo.walletInfo
        let swapInfo = dexInfo.swapInfo
        if (newPair) swapInfo = {...swapInfo, pair: {...swapInfo.pair, left: newPair.left, right: newPair.right, address: newPair.address}}
        let {pair: {left: left, right: right, address: pairAddress}} = swapInfo
        try {
            const [adapterId, session, wallet] = await restoreSession();
            const balance = await tonClient.getBalance(new Address(wallet.address))
            walletInfo =  {...walletInfo, adapterId, session, meta: wallet, balance}
        } catch {
            // pass
        }

        // const selectedLeft = typeof newSelectedLeft !== "undefined" ? newSelectedLeft : swapInfo.selected.left
        // const selectedRight = typeof newSelectedRight !== "undefined" ? newSelectedRight : swapInfo.selected.right
        const newLeft = left ? await updateJettonWallet(left, walletInfo ? walletInfo.meta.address : null) : null;
        const newRight = right ? await updateJettonWallet(right, walletInfo ? walletInfo.meta.address : null) : null;
        // const pairContract = new DexBetaPairContract(new Address(pairAddress));
        const {leftReserve, rightReserve} = await getValidPair(left, right)
        console.log('left', leftReserve.toString(), 'right', rightReserve.toString())

        swapInfo = {
            ...swapInfo,
            pair: {...swapInfo.pair, left: newLeft, right: newRight, leftReserve, rightReserve}
        };
        saveSwapInfo(swapInfo);
        setDexInfo({...dexInfo, walletInfo, swapInfo})
    }

    const updateSlippage = (newSlippage: number) => {
        let correctSlippage = newSlippage;
        if (correctSlippage > 99.7) correctSlippage = 99.7;
        if (correctSlippage < 0.1) correctSlippage = 0.1;
        let swapInfo = dexInfo.swapInfo
        swapInfo = {...swapInfo, swapParams: {...swapInfo.swapParams, slippage: correctSlippage}}
        saveSwapInfo(swapInfo);
        setDexInfo({...dexInfo, swapInfo})
    }

    const updateSwapParams = (newSwapParams: SwapParams) => {
        setDexInfo({...dexInfo, swapInfo: {...dexInfo.swapInfo, swapParams: newSwapParams}})
    }


    return (
        <DexContext.Provider value={{
            dexInfo,
            updateDexInfo,
            updateSlippage,
            updateSwapParams,
        }}
        >
            {children}
        </DexContext.Provider>
    );
};
