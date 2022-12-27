package money.tegro.dex.metric

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.reactive.ReactorCrudRepository
import java.time.Instant

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MetricRepository : ReactorCrudRepository<MetricModel, Instant>
