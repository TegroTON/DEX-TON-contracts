package money.tegro.dex.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.dex.model.WalletModel
import org.ton.bigint.BigInt
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface WalletRepository : CoroutinePageableCrudRepository<WalletModel, MsgAddressInt> {
    suspend fun update(
        @Id address: MsgAddressInt,
        balance: BigInt,
        owner: MsgAddress,
        master: MsgAddress,
        updated: Instant = Instant.now()
    ): Long

    suspend fun update(
        @Id address: MsgAddressInt,
        balance: BigInt,
        updated: Instant = Instant.now()
    ): Long
}
