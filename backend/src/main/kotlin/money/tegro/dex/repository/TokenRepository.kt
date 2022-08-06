package money.tegro.dex.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.dex.model.TokenModel
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface TokenRepository : CoroutinePageableCrudRepository<TokenModel, MsgAddressInt> {
    // Addresses and symbols are both unique since they are used to look up for specific entries
    suspend fun findBySymbol(symbol: String): TokenModel?

    suspend fun update(
        @Id address: MsgAddressInt,
        supply: BigInt,
        mintable: Boolean,
        admin: MsgAddressInt,
        updated: Instant = Instant.now()
    ): Long
}
