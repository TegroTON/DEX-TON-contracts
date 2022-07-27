import {NavComponent} from "./components/Nav";
import {useContext, useEffect, useState} from "react";
import {DexContext, DexContextType} from "../../context";
import {Coins} from "ton3-core";
import {useForm} from "react-hook-form";
import axios from "axios";
import {getOutAmount} from "../../ton/dex/utils";

export function SwapPage() {
    const {dexInfo, updateSwapParams, updateDexInfo} = useContext(DexContext) as DexContextType;
    const {walletInfo, swapInfo} = dexInfo;
    const {swapParams, pair} = swapInfo;
    let {slippage: slippage, inAmount: inAmount, outAmount: outAmount} = swapParams
    const {left: left, right: right, leftReserve: leftReserve, rightReserve: rightReserve} = pair;
    const tonBalance = walletInfo ? walletInfo.balance : new Coins(0);
    const leftBalance = left ? left.balance ?? new Coins(0) : tonBalance;
    const rightBalance = right ? right.balance ?? new Coins(0) : tonBalance;

    const from = {
        symbol: left ? left.jetton.meta.symbol : "TON",
        balance: leftBalance,
        images: left ? left.jetton.meta.image : "/images/ton.png",
        name: left ? left.jetton.meta.name : "Toncoin",
    }

    const to = {
        symbol: right ? right.jetton.meta.symbol : "TON",
        balance: rightBalance,
        images: right ? right.jetton.meta.image : "/images/ton.png",
        name: right ? right.jetton.meta.name : "Toncoin",
    }

    const price = leftReserve.isZero() ? new Coins(0) : new Coins(rightReserve).div(leftReserve);
    const priceImpact = inAmount.isZero() ? new Coins(0) : new Coins(0).sub(new Coins(outAmount).div(new Coins(inAmount).mul(price))).add(1).mul(100).mul(0.9965)

    const realPrice = inAmount.isZero() ? new Coins(1).div(price) : new Coins(inAmount).div(outAmount)
    const minReceived = new Coins(outAmount).mul(1-slippage/100)

    const updateAmount = (side: ("left" | "right")) => {
        const [lReserve, rReserve] = [leftReserve, rightReserve]
        if (side === "left") {
            const leftValue = getValues(side)
            if (leftValue) {
                inAmount = new Coins(leftValue)
                outAmount = getOutAmount(inAmount, lReserve, rReserve)
                updateSwapParams({...swapParams, inAmount, outAmount})
                // setValue("right", outAmount.toString())
            } else {
                // setValue("right", leftValue)
                updateSwapParams({...swapParams, inAmount: new Coins(0), outAmount: new Coins(0)})
            }
        } else {
            // pass
        }
    }

    const {
        register,
        setValue,
        getValues,
        formState: { isValid }
    } = useForm({ mode: 'onChange' });

    const fieldHandler = (fieldName: string, fieldValue: string) => {
        let normValue = fieldValue
        normValue = normValue.replace(/[^0-9\.]+/g, '')
        normValue = normValue.replace(/\b0+/, '0')
        normValue = normValue.replace(/\.+/, '.')
        normValue = normValue.replace(/^\./, '0.')
        normValue = normValue.replace(/\b0(\d+)/, '$1')
        setValue(fieldName, normValue)
    }

    const updater = async () => {
        // await updateDexInfo()
        // await updateAmount('left')
    }

    useEffect(() => {
        // const interval = setInterval(() => updater(), 5000);
        //
        // return () => clearInterval(interval)
    }, [])

    setValue("right", outAmount.toString())

    return (
        <div className="container">
            <div className="row">
                <div className="col-lg-7 col-xl-5 mx-auto">
                    <NavComponent/>
                    <div className="card rounded shadow border-0">
                        <form className="card-body p-40" action="">
                            <div className="d-flex mb-4">
                                <h2 className="card-title fs-24 fw-700 me-auto">Swap</h2>
                                <a href="#!" data-bs-toggle="modal" data-bs-target="#SettingsModal">
                                    <i className="fa-regular fa-gear fa-lg"/>
                                </a>
                            </div>
                            <div className="d-flex justify-content-between mb-3 px-2">
                                <label htmlFor="">You pay:</label>
                                {walletInfo ? (
                                    <div className="fw-500 color-grey">
                                    Balance: <span>{`${from.balance.toString()} ${from.symbol}`}</span>
                                    </div>
                                ) : ("")}
                            </div>
                            <div className="input-group mb-4">
                                <input className="form-control fw-500 fs-18 px-3"
                                       placeholder="0"
                                       {...register('left', {
                                           onChange: (event) => {fieldHandler('left', event.target.value); updateAmount('left')},
                                           validate: (value) => value && parseFloat(value) > 0
                                       })}
                                />
                                    <div className="input-group-text p-1">
                                        <a className="btn btn-sm bg-soft-blue rounded-8 d-flex align-items-center justify-content-center px-4"
                                           style={{minWidth: "164px"}} href="#!" data-bs-toggle="modal"
                                           data-bs-target="#TokenModalLeft">
                                            <img src={from.images} width="24" height="24"
                                                 alt={from.name}/>
                                                <span
                                                    className="mx-3 fw-500 text-uppercase">{from.symbol}</span>
                                                <i className="fa-solid fa-ellipsis-vertical"/>
                                        </a>
                                    </div>
                            </div>

                            <div className="d-flex justify-content-between mb-3 px-2">
                                <label htmlFor="">You receive:</label>
                                {walletInfo ? (
                                    <div className="fw-500 color-grey">
                                    Balance: <span>{`${to.balance.toString()} ${to.symbol}`}</span>
                                    </div>
                                ) : ("")}
                            </div>
                            <div className="input-group mb-4">
                                <input className="form-control fw-500 fs-18 px-3"
                                       placeholder="0"
                                       defaultValue={outAmount.toString()}
                                       disabled
                                       {...register('right', {
                                           onChange: (event) => fieldHandler('right', event.target.value),
                                           validate: (value) => value && parseFloat(value) > 0
                                       })}
                                />
                                    <div className="input-group-text p-1">
                                        <a className="btn btn-sm bg-soft-blue rounded-8 d-flex align-items-center justify-content-center px-4"
                                           style={{minWidth: "164px"}} href="#!" data-bs-toggle="modal"
                                           data-bs-target="#TokenModalRight">
                                            <img src={to.images} width="24" height="24"
                                                 alt={to.name}/>
                                                <span
                                                    className="mx-3 fw-500 text-uppercase">{to.symbol}</span>
                                                <i className="fa-solid fa-ellipsis-vertical"></i>
                                        </a>
                                    </div>
                            </div>
                            <div className="card-alert p-4 bg-soft-blue rounded-8">
                                <ul className="list-unstyled">
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">Price:</span><span
                                        className="text-muted">{`${realPrice.toString()} ${from.symbol} per 1 ${to.symbol}`}</span>
                                    </li>
                                    <li className="list-item d-flex mb-4">
                                        <span
                                            className="me-auto fw-500">Slippage Tolarance:</span><span
                                        className="text-muted">{`${slippage}%`}</span>
                                    </li>
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">Minimum received:</span><span
                                        className="text-muted">{`${minReceived.toString()} ${to.symbol}`}</span>
                                    </li>
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">Price Impact:</span><span
                                        className="text-muted">{`${parseFloat(priceImpact.toString()).toFixed(2)}%`}</span>
                                    </li>
                                </ul>
                            </div>
                            <div className="text-center mt-40">
                                {walletInfo ? (
                                    isValid ? (
                                        leftBalance.gte(inAmount) ? (
                                                <button type="button" className="btn btn-primary"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#ConfirmOffer">
                                                    Swap
                                                </button>
                                            ) : (
                                                <button type="button" className="btn btn-outline-primary" style={{cursor: "not-allowed"}}>
                                                    {`Insufficient ${from.symbol} balance`}
                                                </button>
                                        )
                                        ) : (
                                            <button type="button" className="btn btn-outline-primary" style={{cursor: "not-allowed"}}>
                                                Enter an amount
                                            </button>
                                        )
                                ) : (
                                    <button type="button" className="btn btn-success"
                                            data-bs-toggle="modal"
                                            data-bs-target="#ConnectModal">
                                        Connect Wallet
                                    </button>
                                )}
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
