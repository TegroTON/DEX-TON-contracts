import {
    Route, Routes, Navigate,
} from 'react-router-dom';
import { useContext, useEffect } from 'react';
import { DefaultLayout } from './layouts';
import { AddLiquidityPage, LiquidityPage, SwapPage } from './templates/dex';
import { ReferralPage } from './templates/dex/Referral';
import { DexContext, DexContextType } from './context';

export default function App() {
    return (
        <Routes>
            <Route path="/" element={<DefaultLayout />}>
                <Route index element={<SwapPage />} />
                <Route path="liquidity" element={<LiquidityPage />} />
                <Route path="liquidity-add" element={<AddLiquidityPage />} />
                <Route path="referral" element={<ReferralPage />} />
                <Route path="*" element={<Navigate to="/" replace />} />
            </Route>
        </Routes>
    );
}
