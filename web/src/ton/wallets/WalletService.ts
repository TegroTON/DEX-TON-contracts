import {Wallet, WalletAdapter, WalletSession} from "./types";

export class WalletService {
  private readonly adapters: Map<string, WalletAdapter<any>> = new Map();

  registerAdapter(adapterId: string, adapter: WalletAdapter<any>) {
    this.adapters.set(adapterId, adapter);
  }

  createSession<S>(adapterId: string): Promise<S> {
    const adapter = this.adapters.get(adapterId) as WalletAdapter<S>;
    return adapter.createSession();
  }

  async awaitReadiness<S>(adapterId: string, session: S): Promise<Wallet> {
    const adapter = this.adapters.get(adapterId) as WalletAdapter<S>;
    return adapter.awaitReadiness(session);
  }

  async getWallet<S>(adapterId: string, session: S): Promise<Wallet> {
    const adapter = this.adapters.get(adapterId) as WalletAdapter<S>;
    return adapter.getWallet(session);
  }

  getWalletAdapter<S>(adapterId: string): WalletAdapter<S> {
    const adapter = this.adapters.get(adapterId) as WalletAdapter<S>;

    if (!adapter) {
      throw new Error('Wallet adapter is not registered.');
    }

    return adapter;
  }
}


export const walletService = new WalletService();


export const restoreSession = async (): Promise<[string, WalletSession, Wallet]> => {
    const adapterId = localStorage.getItem('wallet:adapter-id');
    const session = localStorage.getItem('wallet:session');

    if (!adapterId || !session) {
      throw new Error('Nothing to restore.');
    }

    try {
      const wallet = await walletService.awaitReadiness(
        adapterId,
        JSON.parse(session),
      );
      return [
          adapterId,
          JSON.parse(session),
          wallet
      ];
    } catch {
      deleteSession()
      throw Error("Restore wallet error. Session deleted")
    }
}



export const saveWalletSession = async <S>(adapterId: string, session: S): Promise<Wallet> => {
    try {
      const wallet = await walletService.awaitReadiness(adapterId, session);

      localStorage.setItem('wallet:adapter-id', adapterId);
      localStorage.setItem('wallet:session', JSON.stringify(session));
      return wallet;
    } catch {
      throw Error("Save wallet error")
    }
};

export const deleteSession = () => {
  localStorage.removeItem('wallet:adapter-id');
  localStorage.removeItem('wallet:session');
}
