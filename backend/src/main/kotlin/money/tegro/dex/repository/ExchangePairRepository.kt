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
abstract class ExchangePairRepository : ReactorPageableRepository<ExchangePairModel, AddrStd> {
    abstract fun findByLeftIsNull(): Flux<ExchangePairModel>

    abstract fun findByLeftIsNotNull(): Flux<ExchangePairModel>

    // This is guaranteed to only return one unique pair at most
    abstract fun findByLeftAndRight(left: AddrStd, right: AddrStd): Mono<ExchangePairModel>

    // This is guaranteed to only return one TON/XXX unique pair
    abstract fun findByLeftIsNullAndRight(right: AddrStd): Mono<ExchangePairModel>

    // Workaround of a silly null-related bytea handling issue
    @JvmName("findByLeftAndRight1")
    fun findByLeftAndRight(left: AddrStd?, right: AddrStd): Mono<ExchangePairModel> =
        left?.let { findByLeftAndRight(it, right) } ?: findByLeftIsNullAndRight(right)

    abstract fun update(
        @Id address: AddrStd,
        leftReserved: BigInt,
        rightReserved: BigInt,
        updated: Instant = Instant.now()
    )
}
