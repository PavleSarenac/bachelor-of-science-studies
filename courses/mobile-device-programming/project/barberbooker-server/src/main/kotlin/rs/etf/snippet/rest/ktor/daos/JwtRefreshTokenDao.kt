package rs.etf.snippet.rest.ktor.daos

import rs.etf.snippet.rest.ktor.entities.tables.JwtRefreshToken
import rs.etf.snippet.rest.ktor.utils.DatabaseUtils
import java.sql.Connection
import java.sql.Timestamp

object JwtRefreshTokenDao {
    fun addNewRefreshToken(jwtRefreshToken: JwtRefreshToken) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    INSERT INTO jwtrefreshtoken (
                        tokenHash,
                        audience,
                        issuer,
                        subject,
                        userType,
                        issuedAt,
                        expiresAt,
                        isRevoked
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            )
            statement.setString(1, jwtRefreshToken.tokenHash)
            statement.setString(2, jwtRefreshToken.audience)
            statement.setString(3, jwtRefreshToken.issuer)
            statement.setString(4, jwtRefreshToken.subject)
            statement.setString(5, jwtRefreshToken.userType)
            statement.setTimestamp(6, Timestamp.valueOf(jwtRefreshToken.issuedAt))
            statement.setTimestamp(7, Timestamp.valueOf(jwtRefreshToken.expiresAt))
            statement.setBoolean(8, false)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun revokeRefreshToken(tokenHash: String) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE jwtrefreshtoken
                    SET isRevoked = ?
                    WHERE tokenHash = ?
                """.trimIndent()
            )
            statement.setBoolean(1, true)
            statement.setString(2, tokenHash)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun isRefreshTokenRevoked(tokenHash: String): Boolean {
        var isRevoked = true
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT isRevoked
                    FROM jwtrefreshtoken
                    WHERE tokenHash = ?
                """.trimIndent()
            )
            statement.setString(1, tokenHash)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                isRevoked = resultSet.getBoolean("isRevoked")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return isRevoked
    }

    fun deleteExpiredTokens(): Int {
        var numberOfDeletedRows = 0
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    DELETE FROM jwtrefreshtoken
                    WHERE expiresAt < ?
                """.trimIndent()
            )
            statement.setTimestamp(1, Timestamp(System.currentTimeMillis()))
            numberOfDeletedRows = statement.executeUpdate()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return numberOfDeletedRows
    }
}