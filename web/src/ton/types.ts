export type RawGetMethodResult = {
    exit_code: number,
    gas_used: number,
    stack: any,
}


export type GetMethodResult = {
    data: {
        result: RawGetMethodResult
    }
}
