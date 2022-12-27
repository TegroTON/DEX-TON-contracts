package money.tegro.dex.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.dex.model.TokenModel
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import reactor.core.publisher.Mono
import java.time.Instant

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TokenRepository : ReactorPageableRepository<TokenModel, AddrStd> {
    // Addresses and symbols are both unique since they are used to look up for specific entries
    fun findBySymbol(symbol: String): Mono<TokenModel>

    fun update(
        @Id address: AddrStd,
        supply: BigInt,
        mintable: Boolean,
        admin: MsgAddressInt,
        updated: Instant = Instant.now()
    )
}
