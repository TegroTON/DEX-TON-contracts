import {Address, Cell, TonClient} from "ton";
import {fromCode} from "tvm-disassembler";

const main = async () => {
    let client = new TonClient({
    endpoint: 'https://scalable-api.tonwhales.com/jsonRPC'
    });
    let address = Address.parseFriendly('EQBRrTk63wHpvreMs7_cDKWh6zrYmQcSBOjKz1i6GcbRTLZX').address;
    let { code } = await client.getContractState(address);
    console.log(code)
    let codeCell = Cell.fromBoc(code ?? "")[0];

    // @ts-ignore
    let source = fromCode(codeCell);
    return source
}

console.log(main())
