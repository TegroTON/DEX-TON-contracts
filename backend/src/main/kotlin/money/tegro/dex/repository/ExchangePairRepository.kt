package money.tegro.dex.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.dex.model.ExchangePairModel
import org.ton.bigint.BigInt
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface ExchangePairRepository : ReactorPageableRepository<ExchangePairModel, AddrStd> {
    fun findByLeftIsNull(): Flux<ExchangePairModel>

    fun findByLeftIsNotNull(): Flux<ExchangePairModel>

    // This is guaranteed to only return one unique pair at most
    fun findByLeftAndRight(left: AddrStd?, right: AddrStd): Mono<ExchangePairModel>

    fun update(
        @Id address: AddrStd,
        leftReserved: BigInt,
        rightReserved: BigInt,
        updated: Instant = Instant.now()
    )
}
