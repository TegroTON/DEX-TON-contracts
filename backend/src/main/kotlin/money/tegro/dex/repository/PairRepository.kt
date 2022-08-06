package money.tegro.dex.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import kotlinx.coroutines.flow.Flow
import money.tegro.dex.model.PairModel
import org.ton.bigint.BigInt
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface PairRepository : CoroutinePageableCrudRepository<PairModel, MsgAddressInt> {
    suspend fun findByBaseAndQuoteAndEnabledTrue(base: MsgAddress, quote: MsgAddressInt): PairModel?

    suspend fun findByEnabledTrue(): Flow<PairModel>

    suspend fun update(
        @Id address: MsgAddressInt,
        baseReserve: BigInt,
        quoteReserve: BigInt,
        updated: Instant = Instant.now()
    ): Long
}
