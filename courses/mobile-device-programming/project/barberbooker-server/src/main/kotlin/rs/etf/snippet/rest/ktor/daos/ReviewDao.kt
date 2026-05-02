package rs.etf.snippet.rest.ktor.daos

import rs.etf.snippet.rest.ktor.utils.DatabaseUtils
import rs.etf.snippet.rest.ktor.entities.structures.ExtendedReviewWithBarber
import rs.etf.snippet.rest.ktor.entities.structures.ExtendedReviewWithClient
import rs.etf.snippet.rest.ktor.entities.tables.Review
import java.sql.Connection

object ReviewDao {
    fun submitReview(review: Review) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    INSERT INTO review (
                        clientEmail,
                        barberEmail,
                        grade,
                        text,
                        date
                    )
                    VALUES (?, ?, ?, ?, ?)
                """.trimIndent()
            )
            statement.setString(1, review.clientEmail)
            statement.setString(2, review.barberEmail)
            statement.setInt(3, review.grade)
            statement.setString(4, review.text)
            statement.setString(5, review.date)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun getClientReviewsForBarber(
        clientEmail: String,
        barberEmail: String
    ): List<Review> {
        var connection: Connection? = null
        val reviews = mutableListOf<Review>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT *
                    FROM review
                    WHERE clientEmail = ? AND barberEmail = ?
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            statement.setString(2, barberEmail)

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                reviews.add(
                    Review(
                        id = resultSet.getInt("id"),
                        clientEmail = resultSet.getString("clientEmail"),
                        barberEmail = resultSet.getString("barberEmail"),
                        grade = resultSet.getInt("grade"),
                        text = resultSet.getString("text"),
                        date = resultSet.getString("date")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return reviews
    }

    fun getBarberReviews(
        barberEmail: String
    ): List<ExtendedReviewWithClient> {
        var connection: Connection? = null
        val reviews = mutableListOf<ExtendedReviewWithClient>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reviewId,
                        r.clientEmail,
                        r.barberEmail,
                        r.grade,
                        r.text,
                        r.date,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM review r
                    INNER JOIN client c ON c.email = r.clientEmail
                    WHERE barberEmail = ?
                """.trimIndent()
            )
            statement.setString(1, barberEmail)

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                reviews.add(
                    ExtendedReviewWithClient(
                        reviewId = resultSet.getInt("reviewId"),
                        clientEmail = resultSet.getString("clientEmail"),
                        barberEmail = resultSet.getString("barberEmail"),
                        grade = resultSet.getInt("grade"),
                        text = resultSet.getString("text"),
                        date = resultSet.getString("date"),
                        clientId = resultSet.getInt("clientId"),
                        clientName = resultSet.getString("clientName"),
                        clientSurname = resultSet.getString("clientSurname")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return reviews
    }

    fun getClientReviews(
        clientEmail: String
    ): List<ExtendedReviewWithBarber> {
        var connection: Connection? = null
        val reviews = mutableListOf<ExtendedReviewWithBarber>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reviewId,
                        r.clientEmail,
                        r.barberEmail,
                        r.grade,
                        r.text,
                        r.date,
                        b.id AS barberId,
                        b.barbershopName
                    FROM review r
                    INNER JOIN barber b ON b.email = r.barberEmail
                    WHERE clientEmail = ?
                """.trimIndent()
            )
            statement.setString(1, clientEmail)

            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                reviews.add(
                    ExtendedReviewWithBarber(
                        reviewId = resultSet.getInt("reviewId"),
                        clientEmail = resultSet.getString("clientEmail"),
                        barberEmail = resultSet.getString("barberEmail"),
                        grade = resultSet.getInt("grade"),
                        text = resultSet.getString("text"),
                        date = resultSet.getString("date"),
                        barberId = resultSet.getInt("barberId"),
                        barbershopName = resultSet.getString("barbershopName")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return reviews
    }

    fun getBarberAverageGrade(
        barberEmail: String
    ): Float {
        var connection: Connection? = null
        var averageGrade = 0.00f
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT COALESCE(AVG(r.grade), 0.00) AS averageGrade
                    FROM review r
                    WHERE r.barberEmail = ?
                """.trimIndent()
            )
            statement.setString(1, barberEmail)

            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                averageGrade = resultSet.getFloat("averageGrade")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return averageGrade
    }
}