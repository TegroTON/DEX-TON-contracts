package money.tegro.dex.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.dex.model.PairModel
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import java.time.Instant

@JdbcRepository(dialect = Dialect.POSTGRES)
interface PairRepository : ReactorPageableRepository<PairModel, AddrStd> {
    fun findByBaseAndQuote(base: AddrStd, quote: AddrStd): Mono<PairModel>

    fun update(
        @Id address: AddrStd,
        baseReserve: BigInt,
        quoteReserve: BigInt,
        updated: Instant = Instant.now()
    )
}
