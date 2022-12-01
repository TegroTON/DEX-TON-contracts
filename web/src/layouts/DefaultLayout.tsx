import { Outlet } from 'react-router-dom';
import { DeLabModal } from '@delab-team/connect';
import usePrefersColorScheme from 'use-prefers-color-scheme';
import React, { useEffect } from 'react';
import { DefaultFooter } from '../templates/footers';
import { DefaultHeader } from '../templates/headers';
import { DeLabConnector } from '../deLabContext';

export default function DefaultLayout() {
    return (
        <>
            <div className="wrapper">
                <DefaultHeader />
                <Outlet />
            </div>
            <DefaultFooter />
        </>
    );
}
