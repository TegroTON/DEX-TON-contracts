import axios from "axios";
import {Pair, Pairs} from "./types";

const endpoint = "https://api-testnet.tegro.finance"
// const endpoint = "http://5.188.119.227:8081"

const getPairs = async (): Promise<Pairs> => {
    const res = await axios.get(`${endpoint}/pairs`)
    if (res.status !== 200) {
        throw Error('Received error: ' + JSON.stringify(res.data || {}));
    }
    return res.data as Pairs
}

const getPair = async (leftSymbol: string, rightSymbol: string): Promise<Pair> => {
    const res = await axios.get(`${endpoint}/pairs/${leftSymbol}/${rightSymbol}`)
    if (res.status !== 200 || !res.data.updated) {
        throw Error('Received error: ' + JSON.stringify(res.data || {}));
    }
    return res.data as Pair
}


export {
    getPairs,
    getPair
}
