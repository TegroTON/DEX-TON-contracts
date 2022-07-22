import {useContext, useEffect, useState} from "react";
import { isAndroid, isIOS } from 'react-device-detect';
import { QRCode } from 'react-qrcode-logo';
import {TonhubSession} from "../../../ton/wallets/tonhub/TonhubWalletAdapter";
import {saveWalletSession, walletService} from "../../../ton/wallets/WalletService";
import {DexContext, DexContextType} from "../../../context";

export function TonhubPart() {
    const isMobileAppSupported = isIOS || isAndroid;

    const [session, setSession] = useState<TonhubSession | null>(null);
    const {updateDexInfo} = useContext(DexContext) as DexContextType;

    // TODO прокинуть вот сюда контекст и записывать новый кошель в контекст
    // также после подключания кошелька можно сделать перезагруз страницы
    // или просто сделать перерендер (или оно само сделается)
    // по идее надо просто балансы обновить

    const createSession = async () => {
        const session = await walletService.createSession('tonhub')
        setSession(session as TonhubSession);
        const wallet = await saveWalletSession('tonhub', session)
        console.log('i get session, i can write it to context', wallet)
        await updateDexInfo()
        location.reload()
    }
    useEffect(() => {
        createSession().then()
    }, []);

    return (
        <>
            {isMobileAppSupported ? (
                <ol>
                    <li className="fs-18 fw-normal">Open <a href="#!"><span className="fw-700">Tonhub</span></a> application</li>
                    <li className="fs-18 fw-normal">Confirm the authentication request</li>
                </ol>
            ) : (
                <>
                <ol>
                    <li className="fs-18 fw-normal">Open <span className="fw-700">Tonhub</span> application</li>
                    <li className="fs-18 fw-normal">Touch <i className="fa-regular fa-qrcode fa-xl"/> icon in the top right corner</li>
                    <li className="fs-18 fw-normal">Scan the next QR code:</li>
                </ol>
                <div className="d-flex justify-content-center">
                    <QRCode
                        value={session?.link}
                        size={256}
                        quietZone={0}
                        logoImage="/images/wallets/tonhub.svg"
                        ecLevel="H"
                        removeQrCodeBehindLogo
                        eyeRadius={10}
                    />
                </div>
                </>
            )}
        </>
    )
}
