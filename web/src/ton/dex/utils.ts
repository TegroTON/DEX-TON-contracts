import {Address, Coins} from "ton3-core";
import {JettonInfo, Pair} from "../../types";
import {getPair} from "./api/apiClient";
import {getDefaultJetton} from "../utils";


export function getDefaultPair(): Pair {
    const right = getDefaultJetton()
    return {
        address: "kQDkozv1Jg2qUvbTCov75pO1olRHMVoB6gKblB0vGAgSu_Jg",
        left: null,
        right,
        leftReserve: new Coins(0),
        rightReserve: new Coins(0)
    }
}

export const getPairInfo = async (
    leftSymbol: string, rightSymbol: string
): Promise<{address: string | null, leftReserve: Coins, rightReserve: Coins}> => {
    let address
    let leftReserve
    let rightReserve
    try {
        const pairMeta = await getPair(leftSymbol, rightSymbol);
        address = pairMeta.address
        leftReserve = new Coins(pairMeta.leftReserved, true)
        rightReserve = new Coins(pairMeta.rightReserved, true)
    } catch {
        // pass
    }
    return {address: address ?? null, leftReserve: leftReserve ?? new Coins(0), rightReserve: rightReserve ?? new Coins(0)}
}

export const getValidPair = async (left: JettonInfo | null, right: JettonInfo | null): Promise<Pair> => {
    const {address, leftReserve, rightReserve} = await getPairInfo(left ? left.jetton.meta.symbol : "TON", right ? right.jetton.meta.symbol : "TON")
    if (!address) throw Error("PAIR NOT VALID") // TODO запилить функцию выбора другой пары
    return {
        address,
        left,
        right,
        leftReserve,
        rightReserve,
    }
};
