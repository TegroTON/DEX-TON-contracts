import {NavComponent} from "./components/Nav";
import {Link} from "react-router-dom";
import {LiquidityAccordeonComponent} from "./components/LiquidityAccordeon";
import {useContext, useEffect} from "react";
import {DexContext, DexContextType} from "../../context";
import axios from "axios";
import {walletService} from "../../ton/wallets/WalletService";
import {JettonWalletContract} from "../../ton/jettons/contracts/JettonWalletContract";
import {Address, BOC, Coins} from "ton3";
import {DexBetaPairContract} from "../../ton/dex/contracts/DexBetaPairContract";

export function LiquidityPage() {
    const {
        dexInfo,
    } = useContext(DexContext) as DexContextType;
    const {walletInfo, swapInfo} = dexInfo;

    const testFunc = async () => {
        // const adapter = walletService.getWalletAdapter(walletInfo?.adapterId as string)
        // const jettonWallet = new JettonWalletContract(new Address(swapInfo.pair.right?.wallet.address as string))
        // const dexPair = new DexBetaPairContract(new Address("kQDkozv1Jg2qUvbTCov75pO1olRHMVoB6gKblB0vGAgSu_Jg"))
        // const payload = dexPair.createAddLiquidityRequest(new Coins(500), new Coins(9000), new Address(walletInfo?.meta.address as string), jettonWallet)
        // await adapter.requestTransfer(walletInfo?.session, jettonWallet.address.toString(), "500500000000", BOC.toBase64Standard(payload), 300000)
        // const res = await axios.get('http://localhost:8081/pairs')
        // console.log('а что тут у нас?', res.data);
    }

    useEffect(() => {
        testFunc().then()
    }, [])

    return (
        <div className="container">
            <div className="row">
                <div className="col-lg-7 col-xl-5 mx-auto">
                    <NavComponent/>
                    <div className="card rounded shadow border-0">
                        <form className="card-body p-40" action="">
                            <div className="d-flex mb-40">
                                <div>
                                    <h2 className="card-title fs-24 fw-700 me-auto mb-2">
                                        Your Liquidity
                                    </h2>
                                    <p className="mb-0 text-muted">
                                        Remove liquidity to receive tokens back
                                    </p>
                                </div>
                                <a href="#!" className="ms-auto" data-bs-toggle="modal"
                                   data-bs-target="#SettingsModal">
                                    <i className="fa-regular fa-gear fa-lg"/>
                                </a>
                            </div>
                            {walletInfo ? (
                                <>
                                    <LiquidityAccordeonComponent/>
                                    <div className="mt-40 text-center">
                                        <Link to="/liquidity-add" className="btn btn-danger">
                                            Add Liquidity</Link>
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div className="card-alert p-5 bg-soft-blue text-center rounded-8">
                                        <i className="fa-regular fa-cloud-arrow-down fa-3x mb-4 color-blue"/>
                                        <p className="text-muted mb-0">
                                            Your active liquidity positions <br/> will appear here.
                                        </p>
                                    </div>
                                    <div className="text-center mt-40">
                                        <button type="button" className="btn btn-danger"
                                                data-bs-toggle="modal"
                                                data-bs-target="#ConnectModal">Connect Wallet
                                        </button>
                                    </div>
                                </>
                            )}
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
