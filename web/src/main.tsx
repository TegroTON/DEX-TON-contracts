import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter, MemoryRouter } from 'react-router-dom';
import App from './App';
import './index.css';
import { ScrollToTop } from './ScrollToTop';
import { DexContextProvider } from './context';
import { DeLabContextProvider } from './deLabContext';

console.log('test');
// walletService.registerAdapter('ton-wallet', new TonWalletWalletAdapter(tonClient, new TonWalletClient(window)));
// walletService.registerAdapter('tonhub', new TonhubWalletAdapter(new TonhubConnector({network: 'sandbox'})));

ReactDOM.createRoot(document.getElementById('root')!)
    .render(
        <React.StrictMode>
            <DeLabContextProvider>
                <DexContextProvider>
                    {/* <MemoryRouter> */}
                    <BrowserRouter>
                        <ScrollToTop>
                            <App />
                        </ScrollToTop>
                    </BrowserRouter>
                    {/* </MemoryRouter> */}
                </DexContextProvider>
            </DeLabContextProvider>
        </React.StrictMode>,
    );
