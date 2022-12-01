import {
    Link, useLocation, useNavigate, useSearchParams,
} from 'react-router-dom';
import { useContext } from 'react';
import { DeLabButton } from '@delab-team/connect';
import usePrefersColorScheme from 'use-prefers-color-scheme';
import { LocationParams } from '../types';
import { DexContext, DexContextType } from '../context';
import { DeLabButtonLabel, DeLabConnector } from '../deLabContext';

export function DefaultHeader() {
    const navigate = useNavigate();
    const location = useLocation();
    const { walletInfo } = useContext(DexContext) as DexContextType;

    const disconnect = async () => {
        localStorage.clear();
        window.location.reload();
    };
    const go_back = () => navigate(-1);

    return (
        <header className="header">
            <nav className="navbar navbar-expand-lg">
                <div className="container">
                    <Link to="/" className="header__logo">
                        <img src="/images/logo.png" alt="" className="header__logo-img" />
                    </Link>
                    <button
                        className="navbar-toggler btn p-2"
                        type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#navbarDexContent"
                        aria-controls="navbarDexContent"
                        aria-expanded="false"
                        aria-label="Toggle navigation"
                    >
                        <i className="fa-solid fa-bars fs-24" />
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
                                <a
                                    className="nav-link dropdown-toggle"
                                    href="#"
                                    id="navbarDropdown"
                                    role="button"
                                    data-bs-toggle="dropdown"
                                    aria-expanded="false"
                                >
                                    <i className="fa-regular fa-ellipsis fa-xl" />
                                </a>
                                <ul
                                    className="dropdown-menu border-0"
                                    aria-labelledby="navbarDropdown"
                                >
                                    <li><a className="dropdown-item" href="https://tegro.io" target="_blank" rel="noreferrer">Token TGR</a></li>
                                    <li><a className="dropdown-item" href="https://tonhold.com" target="_blank" rel="noreferrer">TON Wallet</a></li>
                                    <li><a className="dropdown-item" href="https://tegro.money" target="_blank" rel="noreferrer">Payments</a></li>
                                </ul>
                            </li>
                        </ul>
                        {walletInfo?.isConnected ? (
                            <div className="dropdown">
                                <a
                                    className="btn btn-sm btn-secondary d-flex align-items-center ps-3 pe-2 py-2 dropdown-toggle"
                                    href="#"
                                    role="button"
                                    id="dropdownMenuLink"
                                    data-bs-toggle="dropdown"
                                    aria-expanded="false"
                                >
                                    {`${walletInfo.balance ? walletInfo.balance.toString() : 'Load...'} TON`}
                                    <span className="wallet-address">
                                        {`${walletInfo.address.toString().slice(0, 4)}...${walletInfo.address.toString().slice(-4)}`}
                                    </span>
                                </a>
                                <ul
                                    className="dropdown-menu shadow border-0 mt-3"
                                    aria-labelledby="dropdownMenuLink"
                                    style={{ minWidth: '252px' }}
                                >
                                    <li>
                                        <a className="dropdown-item" href="#" onClick={() => DeLabConnector.disconnect()}>
                                            <i className="fa-regular fa-link-simple-slash me-3" />
                                            Disconnect wallet
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        ) : (
                            <a
                                href="#!"
                                className="btn btn-sm btn-secondary"
                                onClick={() => DeLabConnector.openModal()}
                            >
                                <DeLabButtonLabel />
                                <i className="fa-solid fa-arrow-right-to-arc ms-3" />
                            </a>
                        )}
                    </div>
                </div>
            </nav>
        </header>
    );
}
