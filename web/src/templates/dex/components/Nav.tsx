import { Link, useLocation } from 'react-router-dom';

export function NavComponent() {
    const location = useLocation();

    return (
        <div className="nav__category d-flex rounded p-1 mt-5 mb-40 text-center">
            <Link
                className={`cat__link flex-fill ${location.pathname === '/' ? 'active' : ''}`}
                to="/"
            >
                <span>Swap</span>
            </Link>
            <Link
                className={`cat__link flex-fill ${location.pathname.slice(0, 10) === '/liquidity' ? 'active' : ''}`}
                to="/liquidity"
            >
                <span>Liquidity</span>
            </Link>
            <Link
                className={`cat__link flex-fill ${location.pathname === '/referral' ? 'active' : ''}`}
                to="/"
            >
                <span>Referral</span>
            </Link>
        </div>
    );
}
