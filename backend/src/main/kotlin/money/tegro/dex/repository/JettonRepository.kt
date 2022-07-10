package money.tegro.dex.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.dex.model.JettonModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface JettonRepository : ReactorPageableRepository<JettonModel, AddrStd> {
    // Addresses and symbols are both unique since they are used to look up for specific entries
    fun findBySymbol(symbol: String): Mono<JettonModel>
}
