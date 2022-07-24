import {useContext, useEffect} from "react";
import {DexContext, DexContextType} from "../../../context";
import {saveWalletSession, walletService} from "../../../ton/wallets/WalletService";

export function TonWalletPart() {
    const {updateDexInfo} = useContext(DexContext) as DexContextType;


    const createSession = async () => {
        const session = await walletService.createSession('ton-wallet')
        const wallet = await saveWalletSession('ton-wallet', session)
        await updateDexInfo()
        location.reload()
    }
    useEffect(() => {
        createSession().then()
    }, []);
    return (
        <>
        </>
    )
}
