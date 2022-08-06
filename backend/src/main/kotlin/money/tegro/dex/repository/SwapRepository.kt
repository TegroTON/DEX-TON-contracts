package money.tegro.dex.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.kotlin.CoroutinePageableCrudRepository
import money.tegro.dex.model.SwapModel

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface SwapRepository : CoroutinePageableCrudRepository<SwapModel, ByteArray>
