import { Outlet } from 'react-router-dom';
import { DefaultFooter } from '../templates/footers';
import { DefaultHeader } from '../templates/headers';

export function DefaultLayout() {
    return (
        <>
            <div className="wrapper">
                <DefaultHeader />
                <Outlet />
                <div className="text-center mt-5">
                    Uniswap available in: <a href="#!" className="link">русский</a>
                </div>
            </div>
            <DefaultFooter />
        </>
    );
}
