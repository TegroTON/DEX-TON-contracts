import {TonhubSession, TonhubWalletAdapter} from "./tonhub/TonhubWalletAdapter";

export interface Wallet {
  address: string;
  publicKey: string;
  walletVersion: string;
}

export interface WalletAdapter<S> {
  isAvailable(): boolean;
  createSession(): Promise<S>;
  awaitReadiness(session: S): Promise<Wallet>;
  getWallet(session: S): Promise<Wallet>;

  requestTransfer(
    session: S,
    destination: string,
    amount: string,
    payload: string,
    timeout: number,
  ): Promise<void>;

  // requestJettonTransfer(
  //   session: S,
  //   contractAddress: string,
  //   destination: string,
  //   amount: string,
  //   forwardPayload: string,
  //   requestTimeout: number,
  //   forwardAmount?: number,
  //   gasFee?: number,
  // ): Promise<void>;
}

export type WalletSession = (TonhubSession)
