package rs.etf.snippet.rest.ktor.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DatabaseUtils {
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = System.getenv("BARBERBOOKER_DATABASE_URL") ?: error("Missing BARBERBOOKER_DATABASE_URL environment variable")
        username = System.getenv("BARBERBOOKER_DATABASE_USERNAME") ?: error("Missing BARBERBOOKER_DATABASE_USERNAME environment variable")
        password = System.getenv("BARBERBOOKER_DATABASE_PASSWORD") ?: error("Missing BARBERBOOKER_DATABASE_PASSWORD environment variable")
        driverClassName = "com.mysql.cj.jdbc.Driver"
        maximumPoolSize = 10
        isAutoCommit = true
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }
    val dataSource: DataSource = HikariDataSource(hikariConfig)
}