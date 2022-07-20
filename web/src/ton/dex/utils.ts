import {Address, Coins} from "ton3";
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
        rightReserve: new Coins(0),
        direction: "normal"
    }
}

export const getPairAddress = async (
    leftSymbol: string, rightSymbol: string
): Promise<{address: string | null, direction: ("normal" | "reverse")}> => {
    let address
    let direction: ("normal" | "reverse") = "normal"
    try {
        const pairMeta = await getPair(leftSymbol, rightSymbol);
        address = pairMeta.address
    } catch {
        try {
            const pairMeta = await getPair(rightSymbol, leftSymbol);
            address = pairMeta.address
            direction = "reverse"
        } catch {
            // pass
        }
    }
    return {address: address ?? null, direction}
}

export const getValidPair = async (left: JettonInfo | null, right: JettonInfo | null): Promise<Pair> => {
    const {address, direction} = await getPairAddress(left ? left.jetton.meta.symbol : "TON", right ? right.jetton.meta.symbol : "TON")
    if (!address) throw Error("PAIR NOT VALID") // TODO запилить функцию выбора другой пары
    return {
        address,
        left: direction === "normal" ? left : right,
        right: direction === "normal" ? right : left,
        leftReserve: new Coins(0),
        rightReserve: new Coins(0),
        direction
    }
};
