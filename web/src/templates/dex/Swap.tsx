import React, { useContext, useEffect, useState } from 'react';
import { Coins } from 'ton3-core';
import { useForm } from 'react-hook-form';
import { DeLabButton, DeLabModal } from '@delab-team/connect';
import usePrefersColorScheme from 'use-prefers-color-scheme';
import { DexContext, DexContextType } from '../../context';
import { NavComponent } from './components/Nav';
import { getOutAmount } from '../../ton/dex/utils';
import { Token } from '../../ton/dex/api/types';
import { PairData } from '../../types';
import { DeLabButtonLabel, DeLabConnector } from '../../deLabContext';
import { fieldNormalizer } from '../../utils';

export default function SwapPage() {
    let colorScheme = usePrefersColorScheme();
    colorScheme = colorScheme === 'no-preference' ? 'dark' : colorScheme;
    const {
        swapPair, swapParams, updateSwapParams, walletInfo, tokens,
    } = useContext(DexContext) as DexContextType;
    let { slippage, inAmount, outAmount } = swapParams;
    const {
        leftWallet, rightWallet, leftReserved, rightReserved, rightBalance, leftBalance, leftToken, rightToken,
    } = swapPair as PairData;


    const tonBalance = walletInfo?.balance ?? new Coins(0);

    const from = tokens?.find((t) => t.address.eq(leftToken)) as Token;

    const to = tokens?.find((t) => t.address.eq(rightToken)) as Token;

    const price = leftReserved.isZero()
        ? new Coins(0)
        : new Coins(rightReserved).div(leftReserved.toString());
    const priceImpact = inAmount.isZero()
        ? new Coins(0)
        : new Coins(0).sub(new Coins(outAmount).div(new Coins(inAmount).mul(price.toString()).toString())).add(new Coins(1)).mul(100)
            .mul(0.9965);

    const realPrice = inAmount.isZero()
        ? new Coins(1).div(price.toString())
        : new Coins(inAmount).div(outAmount.toString());
    const minReceived = new Coins(outAmount).mul(1 - slippage / 100);

    const {
        register,
        setValue,
        getValues,
        formState: { isValid },
    } = useForm({ mode: 'onChange' });

    const updateAmount = (side: ('left' | 'right')) => {
        const [lReserve, rReserve] = side === 'left' ? [leftReserved, rightReserved] : [rightReserved, leftReserved];
        const value = getValues(side);
        if (value) {
            inAmount = new Coins(value);
            outAmount = getOutAmount(inAmount, lReserve, rReserve);
            if (side === 'left') {
                updateSwapParams({ ...swapParams, inAmount, outAmount });
            } else {
                updateSwapParams({ ...swapParams, inAmount: outAmount, outAmount: inAmount });
            }
        } else {
            updateSwapParams({
                ...swapParams,
                inAmount: new Coins(0),
                outAmount: new Coins(0),
            });
        }
    };

    // const updater = async () => {
    //     // await updateDexInfo()
    //     // await updateAmount('left')
    // };

    useEffect(() => {
        // const interval = setInterval(() => updater(), 5000);
        //
        // return () => clearInterval(interval)
    }, []);

    // useEffect(() => {
    // }, [outAmount]);

    setValue('right', outAmount.toString());

    return (
        <div className="container">
            <div className="row">
                <div className="col-lg-7 col-xl-5 mx-auto">
                    <NavComponent />
                    <div className="card rounded shadow border-0">
                        <form className="card-body p-40" action="">
                            <div className="d-flex mb-4">
                                <h2 className="card-title fs-24 fw-700 me-auto">Swap</h2>
                                <a href="#!" data-bs-toggle="modal" data-bs-target="#SettingsModal">
                                    <i className="fa-regular fa-gear fa-lg" />
                                </a>
                            </div>
                            <div className="d-flex justify-content-between mb-3 px-2">
                                <label htmlFor="">You pay:</label>
                                {walletInfo ? (
                                    <div className="fw-500 color-grey">
                                        {'Balance: '}
                                        <span>{`${(leftBalance ?? tonBalance).toString()} ${from.symbol}`}</span>
                                    </div>
                                ) : ('')}
                            </div>
                            <div className="input-group mb-4">
                                <input
                                    className="form-control fw-500 fs-18 px-3"
                                    placeholder="0"
                                    type="text"
                                    inputMode="decimal"
                                    aria-invalid="false"
                                    {...register('left', {
                                        onChange: (event) => { fieldNormalizer('left', event.target.value, setValue); updateAmount('left'); },
                                        validate: (value) => value && parseFloat(value) > 0,
                                    })}
                                />
                                <div className="input-group-text p-1">
                                    <a
                                        className="btn btn-sm bg-soft-blue rounded-8 d-flex align-items-center justify-content-center px-4"
                                        style={{ minWidth: '164px' }}
                                        href="#!"
                                        data-bs-toggle="modal"
                                        data-bs-target="#TokenModalLeft"
                                    >
                                        <img
                                            src={from.image}
                                            width="24"
                                            height="24"
                                            alt={from.name}
                                        />
                                        <span
                                            className="mx-3 fw-500 text-uppercase"
                                        >
                                            {from.symbol}
                                        </span>
                                        <i className="fa-solid fa-ellipsis-vertical" />
                                    </a>
                                </div>
                            </div>

                            <div className="d-flex justify-content-between mb-3 px-2">
                                <label htmlFor="">You receive:</label>
                                {walletInfo ? (
                                    <div className="fw-500 color-grey">
                                        Balance:
                                        {' '}
                                        <span>{`${(rightBalance ?? tonBalance).toString()} ${to.symbol}`}</span>
                                    </div>
                                ) : ('')}
                            </div>
                            <div className="input-group mb-4">
                                <input
                                    className="form-control fw-500 fs-18 px-3"
                                    placeholder="0"
                                    defaultValue={outAmount.toString()}
                                    disabled
                                    {...register('right', {
                                        onChange: (event) => fieldNormalizer('right', event.target.value, setValue),
                                        validate: (value) => value && parseFloat(value) > 0,
                                    })}
                                />
                                <div className="input-group-text p-1">
                                    <a
                                        className="btn btn-sm bg-soft-blue rounded-8 d-flex align-items-center justify-content-center px-4"
                                        style={{ minWidth: '164px' }}
                                        href="#!"
                                        data-bs-toggle="modal"
                                        data-bs-target="#TokenModalRight"
                                    >
                                        <img
                                            src={to.image}
                                            width="24"
                                            height="24"
                                            alt={to.name}
                                        />
                                        <span className="mx-3 fw-500 text-uppercase">
                                            {to.symbol}
                                        </span>
                                        <i className="fa-solid fa-ellipsis-vertical" />
                                    </a>
                                </div>
                            </div>
                            <div className="card-alert p-4 bg-soft-blue rounded-8">
                                <ul className="list-unstyled">
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">Price:</span>
                                        <span
                                            className="text-muted"
                                        >
                                            {`${(realPrice ?? '0').toString()} ${from.symbol} per 1 ${to.symbol}`}
                                        </span>
                                    </li>
                                    <li className="list-item d-flex mb-4">
                                        <span
                                            className="me-auto fw-500"
                                        >
                                            Slippage Tolerance:
                                        </span>
                                        <span
                                            className="text-muted"
                                        >
                                            {`${slippage}%`}
                                        </span>
                                    </li>
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">Minimum received:</span>
                                        <span
                                            className="text-muted"
                                        >
                                            {`${(minReceived ?? '0').toString()} ${to.symbol}`}
                                        </span>
                                    </li>
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">Price Impact:</span>
                                        <span
                                            className="text-muted"
                                        >
                                            {`${parseFloat(priceImpact.toString()).toFixed(2)}%`}
                                        </span>
                                    </li>
                                </ul>
                            </div>
                            <div className="text-center mt-40 d-flex justify-content-center">
                                {walletInfo?.isConnected ? (
                                    isValid ? (
                                        ((leftBalance && leftBalance.gte(inAmount)) || (!leftBalance && tonBalance.gte(inAmount))) ? (
                                            <button
                                                type="button"
                                                className="btn btn-primary"
                                                data-bs-toggle="modal"
                                                data-bs-target="#ConfirmSwap"
                                            >
                                                Swap
                                            </button>
                                        ) : (
                                            <button type="button" className="btn btn-outline-primary" style={{ cursor: 'not-allowed' }}>
                                                {`Insufficient ${from.symbol} balance`}
                                            </button>
                                        )
                                    ) : (
                                        <button type="button" className="btn btn-outline-primary" style={{ cursor: 'not-allowed' }}>
                                            Enter an amount
                                        </button>
                                    )
                                ) : (
                                    <button
                                        type="button"
                                        className="btn btn-success"
                                        onClick={() => DeLabConnector.openModal()}
                                    >
                                        <DeLabButtonLabel />
                                    </button>
                                )}
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}
