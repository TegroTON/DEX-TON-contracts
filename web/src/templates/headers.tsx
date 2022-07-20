import {Link, useLocation, useNavigate, useSearchParams} from 'react-router-dom';
import { LocationParams } from '../types';
import {useContext} from "react";
import {DexContext, DexContextType} from "../context";

export function DefaultHeader() {
    const navigate = useNavigate();
    const location = useLocation();
    const {dexInfo} = useContext(DexContext) as DexContextType;


    const go_back = () => navigate(-1);

    return (
        <header className="header">
            <nav className="navbar navbar-expand-lg">
                <div className="container">
                    <a href="/" className="header__logo"><img src="/images/logo.png" alt=""
                                                              className="header__logo-img"/></a>
                    <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                            data-bs-target="#navbarDexContent" aria-controls="navbarDexContent"
                            aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"></span>
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
                                    <li><a className="dropdown-item" href="#" onClick={() => {localStorage.clear(); window.location.reload()}}>Link</a></li>
                                    <li><a className="dropdown-item" href="#">Link</a></li>
                                    <li><a className="dropdown-item" href="#">Link</a></li>
                                </ul>
                            </li>
                        </ul>
                        {dexInfo.walletInfo ? (
                            <a href={`https://tonmoon.org/explorer/${dexInfo.walletInfo.meta.address}`} target="_blank"
                               className="btn btn-sm btn-secondary d-flex align-items-center ps-3 pe-2 py-2">
                                {`${dexInfo.walletInfo.balance ? dexInfo.walletInfo.balance.toString() : "Load..."} TON`}
                                <span className="wallet-address">
                                    {`${dexInfo.walletInfo.meta.address.slice(0, 6)}...${dexInfo.walletInfo.meta.address.slice(-6)}`}
                                </span>
                            </a>
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
