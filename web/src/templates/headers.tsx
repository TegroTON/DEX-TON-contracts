import {Link, useLocation, useNavigate, useSearchParams} from 'react-router-dom';
import { LocationParams } from '../types';
import {useContext} from "react";
import {DexContext, DexContextType} from "../context";

export function DefaultHeader() {
    const navigate = useNavigate();
    const location = useLocation();
    const {dexInfo} = useContext(DexContext) as DexContextType;

    const disconnect = async () => {
        localStorage.clear()
        window.location.reload()
    }
    const go_back = () => navigate(-1);

    return (
        <header className="header">
            <nav className="navbar navbar-expand-lg">
                <div className="container">
                    <Link to="/" className="header__logo">
                        <img src="/images/logo.png" alt="" className="header__logo-img"/>
                    </Link>
                    <button className="navbar-toggler btn p-2" type="button"
                            data-bs-toggle="collapse" data-bs-target="#navbarDexContent"
                            aria-controls="navbarDexContent" aria-expanded="false"
                            aria-label="Toggle navigation">
                        <i className="fa-solid fa-bars fs-24"/>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarDexContent">
                        <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                            <li className="nav-item">
                                <a className="nav-link" href="#">Trade</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link" href="#">About</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link" href="#">Help Center</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link" href="#">Request Features</a>
                            </li>
                            <li className="nav-item">
                                <a className="nav-link" href="#">Discord</a>
                            </li>
                            <li className="nav-item dropdown">
                                <a className="nav-link dropdown-toggle" href="#" id="navbarDropdown"
                                   role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i className="fa-regular fa-ellipsis fa-xl"></i>
                                </a>
                                <ul className="dropdown-menu border-0"
                                    aria-labelledby="navbarDropdown">
                                    <li><a className="dropdown-item" href="https://tegro.io" target="_blank">Token TGR</a></li>
                                    <li><a className="dropdown-item" href="https://tonhold.com" target="_blank">TON Wallet</a></li>
                                    <li><a className="dropdown-item" href="https://tegro.money" target="_blank">Payments</a></li>
                                </ul>
                            </li>
                        </ul>
                        {dexInfo.walletInfo ? (
                            <div className="dropdown">
                                <a className="btn btn-sm btn-secondary d-flex align-items-center ps-3 pe-2 py-2 dropdown-toggle"
                                   href="#" role="button" id="dropdownMenuLink"
                                   data-bs-toggle="dropdown" aria-expanded="false">
                                {`${dexInfo.walletInfo.balance ? dexInfo.walletInfo.balance.toString() : "Load..."} TON`}
                                    <span className="wallet-address">
                                        {`${dexInfo.walletInfo.meta.address.slice(0, 4)}...${dexInfo.walletInfo.meta.address.slice(-4)}`}
                                    </span>
                                </a>
                                <ul className="dropdown-menu shadow border-0 mt-3"
                                    aria-labelledby="dropdownMenuLink" style={{minWidth: "252px"}}>
                                    <li>
                                        <a className="dropdown-item" href="#" onClick={() => disconnect()}>
                                            <i className="fa-regular fa-link-simple-slash me-3"/>
                                            Disconnect wallet
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        ) : (
                            <a href="#!" className="btn btn-sm btn-secondary" data-bs-toggle="modal"
                               data-bs-target="#ConnectModal">
                                Connect Wallet
                                <i className="fa-solid fa-arrow-right-to-arc ms-3"/>
                            </a>
                        )}

                    </div>
                </div>
            </nav>
        </header>
    );
}
