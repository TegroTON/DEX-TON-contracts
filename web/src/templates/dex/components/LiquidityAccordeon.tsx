export function LiquidityAccordeonComponent() {
    return (
        <div className="accordion" id="accordionLiquidity">
            <div className="accordion-item py-4" data-bs-toggle="collapse"
                 data-bs-target="#collapse1" aria-expanded="false" aria-controls="collapse1">
                <div className="d-flex align-items-center">
                    <img src="/images/token/Tether.svg" alt="" className="wc-img"
                         style={{width: "40px", height: "40px"}}/>
                        <div className="ms-4">
                            <span className="fs-16 fw-700">USTD / TGR</span>
                            <p className="mb-0 text-muted fs-12">Theather / Tegro Coin</p>
                        </div>
                        <div className="ms-auto">
                            <span className="me-4 fw-500 text-muted">11.98$</span><i
                            className="fa-solid fa-angle-right"></i>
                        </div>
                </div>
                <div id="collapse1" className="accordion-collapse collapse mt-4"
                     data-bs-parent="#accordionLiquidity">
                    <ul className="list-unstyled p-4 bg-soft-blue rounded-8">
                        <li className="list-item d-flex align-items-center mb-4">
                            <img src="/images/token/Tether.svg" alt="" className="wc-img"
                                 style={{width: "14px", height: "14px"}}/>
                                <span
                                    className="ms-2 me-auto fw-500">Added to pool USTD:</span><span
                                className="text-muted">1 USTD per 10 TGR</span>
                        </li>
                        <li className="list-item d-flex align-items-center mb-4">
                            <img src="/images/token/tgr.svg" alt="" className="wc-img"
                                 style={{width: "14px", height: "14px"}}/>
                                <span className="ms-2 me-auto fw-500">Added to pool TGR:</span><span
                                className="text-muted">3%</span>
                        </li>
                        <li className="list-item d-flex mb-3">
                            <span className="me-auto color-blue fw-500">BTCB per ALPACA</span><span
                            className="color-red fw-500">2.35%</span>
                        </li>
                        <li className="list-item d-flex mb-3">
                            <span className="me-auto fw-500">Share in the pool:</span><span
                            className="text-muted">98.11%</span>
                        </li>
                    </ul>
                    <div className="text-center mt-3">
                        <a href="#!" className="btn btn-sm btn-outline-danger"
                           data-bs-toggle="modal" data-bs-target="#RemoveLiquidity"><i
                            className="fa-regular fa-trash-can me-2"></i>Remove Liquidity</a>
                    </div>
                </div>
            </div>

            <div className="accordion-item py-4" data-bs-toggle="collapse"
                 data-bs-target="#collapse2" aria-expanded="false" aria-controls="collapse2">
                <div className="d-flex align-items-center">
                    <img src="/images/token/TRON.svg" alt="" className="wc-img"
                         style={{width: "40px", height: "40px"}}/>
                        <div className="ms-4">
                            <span className="fs-16 fw-700">TRON / TGR</span>
                            <p className="mb-0 text-muted fs-12">Tron / Tegro Coin</p>
                        </div>
                        <div className="ms-auto">
                            <span className="me-4 fw-500 text-muted">7.64$$</span><i
                            className="fa-solid fa-angle-right"></i>
                        </div>
                </div>
                <div id="collapse2" className="accordion-collapse collapse mt-4"
                     data-bs-parent="#accordionLiquidity">
                    <ul className="list-unstyled p-4 bg-soft-blue rounded-8">
                        <li className="list-item d-flex align-items-center mb-4">
                            <img src="/images/token/TRON.svg" alt="" className="wc-img"
                                 style={{width: "14px", height: "14px"}}/>
                                <span
                                    className="ms-2 me-auto fw-500">Added to pool TRON:</span><span
                                className="text-muted">1 TRON per 10 TGR</span>
                        </li>
                        <li className="list-item d-flex align-items-center mb-4">
                            <img src="/images/token/tgr.svg" alt="" className="wc-img"
                                 style={{width: "14px", height: "14px"}}/>
                                <span className="ms-2 me-auto fw-500">Added to pool TGR:</span><span
                                className="text-muted">3%</span>
                        </li>
                        <li className="list-item d-flex mb-3">
                            <span className="me-auto color-blue fw-500">BTCB per ALPACA</span><span
                            className="color-red fw-500">2.35%</span>
                        </li>
                        <li className="list-item d-flex mb-3">
                            <span className="me-auto fw-500">Share in the pool:</span><span
                            className="text-muted">98.11%</span>
                        </li>
                    </ul>
                    <div className="text-center mt-3">
                        <a href="#!" className="btn btn-sm btn-outline-danger"
                           data-bs-toggle="modal" data-bs-target="#RemoveLiquidity"><i
                            className="fa-regular fa-trash-can me-2"></i>Remove Liquidity</a>
                    </div>
                </div>
            </div>

            <div className="accordion-item py-4" data-bs-toggle="collapse"
                 data-bs-target="#collapse3" aria-expanded="false" aria-controls="collapse3">
                <div className="d-flex align-items-center">
                    <img src="/images/token/Coinbase.svg" alt="" className="wc-img"
                         style={{width: "40px", height: "40px"}}/>
                        <div className="ms-4">
                            <span className="fs-16 fw-700">COINBASE / TGR</span>
                            <p className="mb-0 text-muted fs-12">COINBASE / Tegro Coin</p>
                        </div>
                        <div className="ms-auto">
                            <span className="me-4 fw-500 text-muted">12.34$</span><i
                            className="fa-solid fa-angle-right"></i>
                        </div>
                </div>
                <div id="collapse3" className="accordion-collapse collapse mt-4"
                     data-bs-parent="#accordionLiquidity">
                    <ul className="list-unstyled p-4 bg-soft-blue rounded-8">
                        <li className="list-item d-flex align-items-center mb-4">
                            <img src="/images/token/Coinbase.svg" alt="" className="wc-img"
                                 style={{width: "14px", height: "14px"}}/>
                                <span className="ms-2 me-auto fw-500">Added to pool COINBASE:</span><span
                                className="text-muted">1 COINBASE per 10 TGR</span>
                        </li>
                        <li className="list-item d-flex align-items-center mb-4">
                            <img src="/images/token/tgr.svg" alt="" className="wc-img"
                                 style={{width: "14px", height: "14px"}}/>
                                <span className="ms-2 me-auto fw-500">Added to pool TGR:</span><span
                                className="text-muted">3%</span>
                        </li>
                        <li className="list-item d-flex mb-3">
                            <span className="me-auto color-blue fw-500">BTCB per ALPACA</span><span
                            className="color-red fw-500">2.35%</span>
                        </li>
                        <li className="list-item d-flex mb-3">
                            <span className="me-auto fw-500">Share in the pool:</span><span
                            className="text-muted">98.11%</span>
                        </li>
                    </ul>
                    <div className="text-center mt-3">
                        <a href="#!" className="btn btn-sm btn-outline-danger"
                           data-bs-toggle="modal" data-bs-target="#RemoveLiquidity"><i
                            className="fa-regular fa-trash-can me-2"></i>Remove Liquidity</a>
                    </div>
                </div>
            </div>

        </div>
    )
}
