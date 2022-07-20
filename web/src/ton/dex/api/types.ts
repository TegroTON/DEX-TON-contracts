export type Jetton = {
    updated: number,
    name: string,
    symbol: string,
    address: string
}

export type Jettons = Jetton[]

export type Pair = {
    updated: number,
    address: string,
    leftName: string,
    leftSymbol: string,
    leftAddress?: string,
    leftReserved: string,
    rightName: string,
    rightSymbol: string,
    rightAddress: string,
    rightReserved: string
}

export type Pairs = Pair[]

