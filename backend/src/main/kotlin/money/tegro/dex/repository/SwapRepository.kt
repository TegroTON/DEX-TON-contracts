package money.tegro.dex.repository

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import money.tegro.dex.model.SwapModel
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface SwapRepository : ReactorPageableRepository<SwapModel, Instant>
