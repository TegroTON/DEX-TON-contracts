import {TonClient} from "./client/TonClient";


// testnet
const url = 'https://testnet.toncenter.com/api/v2/jsonRPC'
const apiKey = '09f1e024cbb6af1b0f608631c42b1427313407b7aa385009195e3f5c09d51fb8'

// mainnet
// const url = 'https://toncenter.com/api/v2'
// const apiKey = '1048eba2377df542264d2e25589a36b9608d3c746d82b8e99284bc59845b041b'

// sandbox
// const url = 'https://sandbox.tonhubapi.com/jsonRPC'
// const apiKey = ''

// const provider = new Providers.ProviderRESTV2(url, {apiKey})
// export const tonClient = provider.client()

export const tonClient = new TonClient({endpoint: url, apiKey: apiKey})
