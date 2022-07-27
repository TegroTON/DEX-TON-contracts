import {SetWalletChoiceFunc} from "./index";

export function MainPart(params: {setter: SetWalletChoiceFunc}) {
    return (
        <>
            <a className="d-flex align-items-center hover border rounded-8 mb-3 px-4 py-12"
               style={{cursor: "pointer"}} onClick={() => params.setter('TonWallet')}>
                <div>
                    <img src="/images/wallets/tonwallet.svg" width={32} height={32} alt=""
                         className="wc-img"/>
                        <span className="ms-4">TON Wallet</span>
                </div>
                <div className="ms-auto">
                    <i className="fa-solid fa-angle-right"></i>
                </div>
            </a>
            {/* <a className="d-flex align-items-center hover border rounded-8 mb-3 px-4 py-12" */}
            {/*    style={{cursor: "pointer"}} onClick={() => params.setter('Tonhub')}> */}
            {/*     <div> */}
            {/*         <img src="/images/wallets/tonhub.svg" width={32} height={32} alt="" */}
            {/*              className="wc-img"/> */}
            {/*             <span className="ms-4">Tonhub (soon!)</span> */}
            {/*     </div> */}
            {/*     <div className="ms-auto"> */}
            {/*         <i className="fa-solid fa-angle-right"></i> */}
            {/*     </div> */}
            {/* </a> */}
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
        </>
    )
}
