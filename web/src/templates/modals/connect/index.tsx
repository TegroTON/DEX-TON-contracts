import {useState} from "react";
import {MainPart} from "./Main";
import {TonkeeperPart} from "./Tonkeeper";
import {TonhubPart} from "./Tonhub";

export type WalletChoice = ("Tonkeeper" | "Tonhub" | "Main")
export type SetWalletChoiceFunc = (walletChoice: WalletChoice) => void

export function ConnectModal() {
    const [walletChoice, setWalletChoice] = useState<WalletChoice>("Main")
    const parts = {"Main": <MainPart setter={setWalletChoice}/>, "Tonkeeper": <TonkeeperPart/>, "Tonhub": <TonhubPart/>}

    return (
        <div className="modal fade" id="ConnectModal" tabIndex={-1} aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered mobile-modal-bottom">
                <div className="modal-content border-0 rounded p-40">
                    <div className="modal-header border-0 p-0 mb-5">
                        <h5 className="modal-title" id="ConnectModalLabel">{`Connect ${walletChoice === "Main" ? "a wallet" : walletChoice}`}</h5>
                        {walletChoice === "Main" ? (
                            <button type="button" className="btn p-0" data-bs-dismiss="modal"
                                aria-label="Close"><i className="fa-solid fa-xmark fa-lg"/>
                            </button>
                        ) : (
                            <button type="button" className="btn p-0" aria-label="Back" onClick={() => setWalletChoice("Main")}>
                                <i className="fa-solid fa-angle-left fa-lg"/>
                            </button>
                        )}
                    </div>
                    <div className="modal-body p-0">
                        {parts[walletChoice]}
                    </div>
                </div>
            </div>
        </div>
    )
}
