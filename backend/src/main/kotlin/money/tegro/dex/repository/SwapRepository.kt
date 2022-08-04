package money.tegro.dex.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.dex.model.SwapModel

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SwapRepository : CoroutinePageableCrudRepository<SwapModel, ByteArray>
