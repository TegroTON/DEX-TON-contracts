import {NavComponent} from "./components/Nav";
import {useNavigate} from "react-router-dom";

export function AddLiquidityPage() {
    const navigate = useNavigate();
    const go_back = () => navigate(-1);

    return (
        <div className="container">
            <div className="row">
                <div className="col-lg-7 col-xl-5 mx-auto">
                    <NavComponent/>
                    <div className="card rounded shadow border-0">
                        <form className="card-body p-40" action="">
                            <div className="d-flex align-items-center mb-4">
                                <h2 className="card-title fs-24 fw-700 me-auto">
                                    <a onClick={go_back} className="me-3">
                                        <i className="fa-regular fa-arrow-left"/>
                                    </a>
                                    Add Liquidity
                                </h2>
                                <a href="#!" data-bs-toggle="modal" data-bs-target="#SettingsModal">
                                    <i className="fa-regular fa-gear fa-lg"/>
                                </a>
                            </div>

                            <div className="d-flex justify-content-between mb-3 px-2">
                                <label htmlFor="">You pay:</label>
                                <div className="fw-500 color-grey">Balance: <span>245.000 TON</span>
                                </div>
                            </div>
                            <div className="input-group mb-4">
                                <input type="number" className="form-control fw-500 fs-18 px-3"
                                       placeholder="0"/>
                                    <div className="input-group-text p-1">
                                        <a className="btn btn-sm bg-soft-blue rounded-8 d-flex align-items-center justify-content-center px-4"
                                           style={{minWidth: "164px"}} href="#!" data-bs-toggle="modal"
                                           data-bs-target="#TokenModal">
                                            <img src="/images/ton.png" width="24" height="24"
                                                 alt="Ton Coin"/>
                                                <span
                                                    className="mx-3 fw-500 text-uppercase">Ton</span>
                                                <i className="fa-solid fa-ellipsis-vertical"/>
                                        </a>
                                    </div>
                            </div>

                            <div className="d-flex justify-content-between mb-3 px-2">
                                <label htmlFor="">You receive:</label>
                                <div
                                    className="fw-500 color-grey">Balance: <span>1485.000 TGR</span>
                                </div>
                            </div>
                            <div className="input-group mb-4">
                                <input type="number" className="form-control fw-500 fs-18 px-3"
                                       placeholder="0"/>
                                    <div className="input-group-text p-1">
                                        <a className="btn btn-sm bg-soft-blue rounded-8 d-flex align-items-center justify-content-center px-4"
                                           style={{minWidth: "164px"}} href="#!" data-bs-toggle="modal"
                                           data-bs-target="#TokenModal">
                                            <img src="/images/tgr.png" width="24" height="24"
                                                 alt="Tegro Coin"/>
                                                <span
                                                    className="mx-3 fw-500 text-uppercase">Tgr</span>
                                                <i className="fa-solid fa-ellipsis-vertical"/>
                                        </a>
                                    </div>
                            </div>
                            <div className="d-flex align-items-center mb-4 px-2">
                                <span
                                    className="me-auto fw-500 color-blue">BTCB per ALPACA</span><span
                                className="fw-500 color-red">0.11%</span>
                            </div>
                            <div className="card-alert p-4 bg-soft-blue rounded-8">
                                <ul className="list-unstyled">
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">BTCB per ALPACA</span><span
                                        className="text-muted">0.0000107444</span>
                                    </li>
                                    <li className="list-item d-flex mb-4">
                                        <span className="me-auto fw-500">ALPACA per BTCB</span><span
                                        className="text-muted">93071.5</span>
                                    </li>
                                    <li className="list-item d-flex">
                                        <span className="me-auto fw-500">Share of Pool</span><span
                                        className="text-muted">0%</span>
                                    </li>
                                </ul>
                            </div>
                            <div className="text-center mt-40">
                                <button type="button" className="btn btn-danger"
                                        data-bs-toggle="modal"
                                        data-bs-target="#ConfirmOffer">Suggest
                                </button>
                            </div>
                        </form>
                    </div>
                    <div
                        className="alert alert-dismissible bg-white rounded shadow border-0 fade show mt-40 p-4"
                        role="alert">
                        <div className="d-flex">
                            <i className="fa-regular fa-circle-info fa-2x color-red mt-1"></i>
                            <p className="ms-3 mb-0 pe-3 text-muted">
                                By adding liquidity you'll earn 0.17% of all trades on this pair
                                proportional to your share of the pool. Fees are added to the pool,
                                accrue in real time and can be claimed by withdrawing
                                your liquidity.
                            </p>
                        </div>
                        <button type="button" className="btn-close" data-bs-dismiss="alert"
                                aria-label="Close"></button>
                    </div>
                </div>
            </div>
        </div>
    )
}
