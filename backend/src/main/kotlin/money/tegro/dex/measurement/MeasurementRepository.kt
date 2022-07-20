package money.tegro.dex.measurement

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorCrudRepository
import java.time.Instant

@R2dbcRepository(dialect = Dialect.POSTGRES)
interface MeasurementRepository : ReactorCrudRepository<MeasurementModel, Instant>
