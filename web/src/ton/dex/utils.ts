import {Address, Coins} from "ton3-core";
import {JettonInfo, Pair} from "../../types";
import {getPair} from "./api/apiClient";
import {getDefaultJetton} from "../utils";


export function getDefaultPair(): Pair {
    const right = getDefaultJetton()
    return {
        address: "kQBpLTnl0mciLdS52V6-Eh7h5TX4ivz-jOzVQoXI9ibHy2Ro",
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

export const getOutAmount = (inAmount: Coins, inReserve: Coins, outReserve: Coins): Coins => {
    const inAmountWithFee = new Coins(inAmount).mul(9965)
    const numerator = new Coins(inAmountWithFee).mul(outReserve);
    const denominator = new Coins(inReserve).mul(10000).add(inAmountWithFee);
    return numerator.div(denominator);
}
