import { TON_WALLET_EXTENSION_URL, TonWalletClient } from './TonWalletClient';
import {Wallet, WalletAdapter} from "../types";
import {TonClient} from "../../client/TonClient";
import {timeout as timeoutFc} from "../utils";

export function delay(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

export class TonWalletWalletAdapter implements WalletAdapter<boolean> {

  constructor(
    private readonly tonClient: TonClient,
    private readonly tonWalletClient: TonWalletClient,
  ) {
  }

  async createSession(): Promise<boolean> {
    try {
      await this.tonWalletClient.ready(150);
      return true;
    } catch (error) {
      window.open(TON_WALLET_EXTENSION_URL, '_blank');
      throw error;
    }
  }

  async awaitReadiness(session: boolean): Promise<Wallet> {
    await this.tonWalletClient.ready();

    const [[wallet]] = await Promise.all([
      this.tonWalletClient.requestWallets(),
      delay(150),
    ]);

    if (!wallet) {
      throw new Error('TON Wallet is not configured.');
    }

    return wallet;
  }

  getWallet(session: boolean): Promise<Wallet> {
    return this.awaitReadiness(session);
  }

  isAvailable(): boolean {
    return !!window.ton?.isTonWallet;
  }

  async requestTransfer(
    session: boolean,
    destination: string,
    amount: string,
    payload: string,
    timeout: number,
  ): Promise<void> {
    await Promise.race([
      this.tonWalletClient.sendTransaction({
        to: destination,
        value: amount,
        dataType: 'boc',
        data: payload,
      }),
      timeoutFc(timeout, 'Transaction request exceeded timeout.'),
    ]);
  }
}
