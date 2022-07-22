import {JettonMasterContract} from "./contracts/JettonMasterContract";
import {Address} from "ton3";
import {JettonMeta} from "../../types";
import {tonClient} from "../index";
import {IPFS_GATEWAY_PREFIX} from "./utils/ipfs";

export async function getJettonData(jettonAddress: string): Promise<JettonMeta> {
    const contract = new JettonMasterContract(
        new Address(jettonAddress),
    );
    const { content } = await contract.getJettonData(await tonClient);
    return fetch(content.toString()
        .replace(/^ipfs:\/\//, IPFS_GATEWAY_PREFIX))
        .then((response) => response.json())
        .then((data) => data as JettonMeta);
}
