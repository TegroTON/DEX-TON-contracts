import {SetWalletChoiceFunc} from "./index";

export function MainPart(params: {setter: SetWalletChoiceFunc}) {
    return (
        <>
            <a className="d-flex align-items-center hover border rounded-8 mb-3 px-4 py-12"
               style={{cursor: "pointer"}} onClick={() => params.setter('Tonhub')}>
                <div>
                    <img src="/images/wallets/tonhub.svg" width={32} height={32} alt=""
                         className="wc-img"/>
                        <span className="ms-4">Tonhub</span>
                </div>
                <div className="ms-auto">
                    <i className="fa-solid fa-angle-right"></i>
                </div>
            </a>
            <a className="d-flex align-items-center hover border rounded-8 mb-3 px-4 py-12"
               style={{cursor: "pointer"}}>
                <div>
                    <img src="/images/wallets/tonwallet.svg" width={32} height={32} alt=""
                         className="wc-img"/>
                        <span className="ms-4">TON Wallet (soon!)</span>
                </div>
                <div className="ms-auto">
                    <i className="fa-solid fa-angle-right"></i>
                </div>
            </a>
            <a className="d-flex align-items-center hover border rounded-8 mb-3 px-4 py-12"
               style={{cursor: "pointer"}}>
                <div>
                    <img src="/images/wallets/tonhold.svg" width={32} height={32} alt=""
                         className="wc-img"/>
                        <span className="ms-4">Tonhold (soon!)</span>
                </div>
                <div className="ms-auto">
                    <i className="fa-solid fa-angle-right"></i>
                </div>
            </a>
            <a className="d-flex align-items-center hover border rounded-8 mb-3 px-4 py-12"
               style={{cursor: "pointer"}} >
               {/* onClick={() => params.setter('Tonkeeper')}> */}
                <div>
                    <img src="/images/wallets/tonkeeper.svg" width={32} height={32} alt=""
                         className="wc-img"/>
                        <span className="ms-4">Tonkeeper (soon!)</span>
                </div>
                <div className="ms-auto">
                    <i className="fa-solid fa-angle-right"></i>
                </div>
            </a>
            <div className="card-alert bg-soft-blue mt-5 p-3 rounded-8 small">
                By connecting a wallet, you agree to Uniswap Labs’ <a href="#!"
                                                                      className="link">Terms
                of Service</a> and acknowledge that you have read and understand the
                Uniswap Protocol Disclaimer.
            </div>
        </>
    )
}
