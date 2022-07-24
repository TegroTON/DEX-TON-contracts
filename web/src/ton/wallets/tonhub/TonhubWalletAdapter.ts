import { Address, Cell, ConfigStore, toNano } from 'ton'; // TODO migrate to ton3
import { TonhubConnector } from 'ton-x';
import { TonhubCreatedSession, TonhubTransactionRequest } from 'ton-x/dist/connector/TonhubConnector';
import {Wallet, WalletAdapter} from "../types";

const TONHUB_TIMEOUT = 5 * 60 * 1000;

export type TonhubSession = TonhubCreatedSession;

export class TonhubWalletAdapter implements WalletAdapter<TonhubSession> {

  constructor(
    private readonly tonhubConnector: TonhubConnector,
  ) {
  }

  async createSession(): Promise<TonhubSession> {
    const { location } = document;

    const session = await this.tonhubConnector.createNewSession({
      name: 'Tegro Finance',
      url: 'https://vk.com',//`${location.protocol}//${location.host}`,
    });

    const sessionLink = session.link
      .replace('ton-test://', 'https://test.tonhub.com/')
      .replace('ton://', 'https://tonhub.com/');

    return {
      id: session.id,
      seed: session.seed,
      link: sessionLink,
    };
  }

  async awaitReadiness(session: TonhubSession): Promise<Wallet> {
    const state = await this.tonhubConnector.awaitSessionReady(session.id, TONHUB_TIMEOUT);

    if (state.state === 'revoked') {
      throw new Error('Connection was cancelled.');
    }

    if (state.state === 'expired') {
      throw new Error('Connection was not confirmed.');
    }

    const walletConfig = new ConfigStore(state.wallet.walletConfig);

    return {
      address: state.wallet.address,
      publicKey: walletConfig.getString('pk'),
      walletVersion: state.wallet.walletType,
    };
  }

  getWallet(session: TonhubSession): Promise<Wallet> {
    return this.awaitReadiness(session);
  }

  private async requestTransaction(session: TonhubSession, request: Omit<TonhubTransactionRequest, 'seed' | 'appPublicKey'>): Promise<void> {
    const state = await this.tonhubConnector.getSessionState(session.id);

    if (state.state !== 'ready') return;

    const response = await this.tonhubConnector.requestTransaction({
      ...request,
      seed: session.seed,
      appPublicKey: state.wallet.appPublicKey,
    });

    if (response.type === 'rejected') {
      throw new Error('Transaction was rejected.');
    }

    if (response.type === 'expired') {
      throw new Error('Transaction was expired.');
    }

    if (response.type === 'invalid_session') {
      throw new Error('Something went wrong. Refresh the page and try again.');
    }

    if (response.type === 'success') {
      // Handle successful transaction
      // const externalMessage = response.response; // Signed external message that was sent to the network
    }
  }

  isAvailable(): boolean {
    return true;
  }

  async requestTransfer(
    session: TonhubSession,
    destination: string,
    amount: string,
    payload: string,
    timeout: number,
  ): Promise<void> {
    await this.requestTransaction(
      session,
      {
        to: destination,
        value: amount,
        payload: payload,
        timeout,
      },
    );
  }
}
