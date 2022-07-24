import {useContext, useRef, useState} from "react";
import {Jettons} from "../../static/jettons";
import {useForm} from "react-hook-form";
import {JettonsData} from "../../types";
import {DexContext, DexContextType} from "../../context";
import {Coins} from "ton3-core";
import {getValidPair} from "../../ton/dex/utils";

export function TokenModal(props: {side: "Left" | "Right"}) {
    const jettons = JSON.parse(JSON.stringify(Jettons))
    const {side} = props

    const {dexInfo, updateDexInfo} = useContext(DexContext) as DexContextType;
    const {walletInfo, swapInfo} = dexInfo;
    let {pair: {left: mySide, right: otherSide}} = swapInfo;
    if (side === "Right") [mySide, otherSide] = [otherSide, mySide]

    const {
        register,
        watch,
    } = useForm({ mode: 'onChange' });

    const search = useRef('');
    search.current = watch('search', 'value');

    const changeSelected = async (tokenAddress: string) => {
        if (tokenAddress === "EQCD39VS5jcptHL8vMjEXrzGaRcCVYto7HUn4bpAOg8xqB2N") {
            if (otherSide) {
                mySide = null
            } else {
                [mySide, otherSide] = [otherSide, mySide]
            }
        } else {
            if (otherSide?.jetton.address === tokenAddress) {
                [mySide, otherSide] = [otherSide, mySide]
            } else {
                const jetton = {address: tokenAddress, meta: Jettons[tokenAddress]}
                mySide = {jetton, wallet: {}, balance: new Coins(0)}
            }
        }
        if (side === "Left") {
            await updateDexInfo(await getValidPair(mySide, otherSide))
        } else {
            await updateDexInfo(await getValidPair(otherSide, mySide))
        }
    }

    return (
        <div className="modal fade" id={`TokenModal${side}`} tabIndex={-1} aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered mobile-modal-bottom">
                <div className="modal-content border-0 rounded p-40">
                    <div className="modal-header border-0 p-0 mb-40">
                        <h5 className="modal-title" id="ConnectModalLabel">Select a token</h5>
                        <button type="button" className="btn p-0" data-bs-dismiss="modal" aria-label="Close">
                            <i className="fa-solid fa-xmark fa-lg"/>
                        </button>
                    </div>
                    <div className="modal-body p-0">
                        <form action="" className="token-form">
                            <div className="input-group flex-nowrap mb-4">
                                <div className="input-group-text text-muted px-3">
                                    <i className="fa-solid fa-magnifying-glass fa-lg"/>
                                </div>
                                <input
                                    type="search"
                                    className="form-control"
                                    placeholder="Search name or paste address"
                                    {...register('search', {})}
                                />
                            </div>
                            <div className="token-form__btns row mb-5" style={{margin: "0 -4px"}}>
                                {Object.keys(jettons || {}).slice(0, 3).map((key) => {
                                    return (
                                        <label // type="button"
                                                className="col-4 btn btn-outline-secondary flex-fill px-3 py-2 m-1"
                                                style={{width: "120px"}} data-bs-dismiss="modal">
                                            <input
                                                type="checkbox"
                                                style={{ display: 'none' }}
                                                {...register(key, {
                                                    onChange: (event) => changeSelected(event.target.name)
                                                })}
                                            />
                                            <img className="token-form__img"
                                                 src={jettons[key].image} width="24px"
                                                 height="24px" alt={jettons[key].symbol}/>
                                            <span className="text-uppercase ms-2 fs-12">{jettons[key].symbol}</span>
                                        </label>
                                    )
                                })}
                            </div>
                            <h4 className="token-form__title fs-16 mb-4">Token Name</h4>
                            <div className="token-form__list overflow-auto"
                                 style={{maxHeight: "408px"}}>
                                {Object.keys(jettons || {}).map((key) => {
                                        if (!(jettons[key].name?.toLowerCase()
                                            .includes(search.current.toLowerCase()))
                                            && !(jettons[key].symbol.toLowerCase()
                                                .includes(search.current.toLowerCase()))) {
                                            return '';
                                        }
                                        return (
                                            <label className="token-form__item d-flex align-items-center rounded hover border-0 py-4 px-3"
                                                   data-bs-dismiss="modal"
                                            >
                                                <input
                                                    type="checkbox"
                                                    style={{ display: 'none' }}
                                                    {...register(key, {
                                                        onChange: (event) => changeSelected(event.target.name),
                                                    })}
                                                />
                                                <img className="token-form__img" src={jettons[key].image} width="24px" height="24px" alt={jettons[key].symbol}/>
                                                <span className="token-form__symbol text-uppercase fw-500 ms-3">{jettons[key].symbol}</span>
                                                <span className="token-form__name text-muted ms-auto">{jettons[key].name}</span>
                                            </label>
                                        )
                                    })}
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
