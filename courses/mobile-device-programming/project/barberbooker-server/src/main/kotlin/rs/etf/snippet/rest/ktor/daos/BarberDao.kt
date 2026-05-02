package rs.etf.snippet.rest.ktor.daos

import rs.etf.snippet.rest.ktor.utils.DatabaseUtils
import rs.etf.snippet.rest.ktor.entities.structures.ExtendedBarberWithAverageGrade
import rs.etf.snippet.rest.ktor.entities.tables.Barber
import java.sql.Connection

object BarberDao {
    fun addNewBarber(barber: Barber) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    INSERT INTO barber (
                        email, 
                        password,
                        barbershopName,
                        price,
                        phone,
                        country,
                        city,
                        municipality,
                        address,
                        workingDays,
                        workingHours,
                        fcmToken
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            )
            statement.setString(1, barber.email)
            statement.setString(2, barber.password)
            statement.setString(3, barber.barbershopName)
            statement.setDouble(4, barber.price)
            statement.setString(5, barber.phone)
            statement.setString(6, barber.country)
            statement.setString(7, barber.city)
            statement.setString(8, barber.municipality)
            statement.setString(9, barber.address)
            statement.setString(10, barber.workingDays)
            statement.setString(11, barber.workingHours)
            statement.setString(12, barber.fcmToken)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun getBarberByEmail(email: String): Barber? {
        var connection: Connection? = null
        var barber: Barber? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT *
                    FROM barber
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, email)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                barber = Barber(
                    id = resultSet.getInt("id"),
                    email = resultSet.getString("email"),
                    password = resultSet.getString("password"),
                    barbershopName = resultSet.getString("barbershopName"),
                    price = resultSet.getDouble("price"),
                    phone = resultSet.getString("phone"),
                    country = resultSet.getString("country"),
                    city = resultSet.getString("city"),
                    municipality = resultSet.getString("municipality"),
                    address = resultSet.getString("address"),
                    workingDays = resultSet.getString("workingDays"),
                    workingHours = resultSet.getString("workingHours"),
                    fcmToken = resultSet.getString("fcmToken"),
                    googleAccessToken = resultSet.getString("googleAccessToken"),
                    googleRefreshToken = resultSet.getString("googleRefreshToken")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return barber
    }

    fun getBarberByEmailAndPassword(email: String, hashedPassword: String): Barber? {
        var connection: Connection? = null
        var barber: Barber? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT *
                    FROM barber
                    WHERE email = ? AND password = ?
                """.trimIndent()
            )
            statement.setString(1, email)
            statement.setString(2, hashedPassword)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                barber = Barber(
                    id = resultSet.getInt("id"),
                    email = resultSet.getString("email"),
                    password = resultSet.getString("password"),
                    barbershopName = resultSet.getString("barbershopName"),
                    price = resultSet.getDouble("price"),
                    phone = resultSet.getString("phone"),
                    country = resultSet.getString("country"),
                    city = resultSet.getString("city"),
                    municipality = resultSet.getString("municipality"),
                    address = resultSet.getString("address"),
                    workingDays = resultSet.getString("workingDays"),
                    workingHours = resultSet.getString("workingHours"),
                    fcmToken = resultSet.getString("fcmToken")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return barber
    }

    fun updateBarberProfile(
        email: String,
        barbershopName: String,
        price: Double,
        phone: String,
        country: String,
        city: String,
        municipality: String,
        address: String,
        workingDays: String,
        workingHours: String
    ) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE barber 
                    SET barbershopName = ?, 
                        price = ?, 
                        phone = ?, 
                        country = ?, 
                        city = ?, 
                        municipality = ?, 
                        address = ?, 
                        workingDays = ?, 
                        workingHours = ?
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, barbershopName)
            statement.setDouble(2, price)
            statement.setString(3, phone)
            statement.setString(4, country)
            statement.setString(5, city)
            statement.setString(6, municipality)
            statement.setString(7, address)
            statement.setString(8, workingDays)
            statement.setString(9, workingHours)
            statement.setString(10, email)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun getSearchResults(query: String): List<ExtendedBarberWithAverageGrade> {
        var connection: Connection? = null
        val searchResults = mutableListOf<ExtendedBarberWithAverageGrade>()
        val queryParameters = query.split(" ")
        try {
            connection = DatabaseUtils.dataSource.connection
            queryParameters.forEach {
                val statement = connection.prepareStatement(
                    """
                    SELECT b.*, COALESCE(AVG(r.grade), 0.00) AS averageGrade
                    FROM barber b
                    LEFT JOIN review r ON b.email = r.barberEmail
                    WHERE LOWER(b.barbershopName) LIKE LOWER(?)
                    OR LOWER(b.city) LIKE LOWER(?)
                    OR LOWER(b.municipality) LIKE LOWER(?)
                    GROUP BY b.email
                """.trimIndent()
                )
                statement.setString(1, "$it%")
                statement.setString(2, "$it%")
                statement.setString(3, "$it%")

                val resultSet = statement.executeQuery()
                while (resultSet.next()) {
                    val barber = ExtendedBarberWithAverageGrade(
                        id = resultSet.getInt("id"),
                        email = resultSet.getString("email"),
                        password = resultSet.getString("password"),
                        barbershopName = resultSet.getString("barbershopName"),
                        price = resultSet.getDouble("price"),
                        phone = resultSet.getString("phone"),
                        country = resultSet.getString("country"),
                        city = resultSet.getString("city"),
                        municipality = resultSet.getString("municipality"),
                        address = resultSet.getString("address"),
                        workingDays = resultSet.getString("workingDays"),
                        workingHours = resultSet.getString("workingHours"),
                        fcmToken = resultSet.getString("fcmToken"),
                        averageGrade = resultSet.getFloat("averageGrade")
                    )
                    searchResults.add(barber)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return searchResults.distinct()
    }

    fun updateFcmToken(
        email: String,
        fcmToken: String
    ) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE barber 
                    SET fcmToken = ?
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, fcmToken)
            statement.setString(2, email)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun updateGoogleTokens(
        email: String,
        accessToken: String,
        refreshToken: String
    ) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE barber 
                    SET googleAccessToken = ?, googleRefreshToken = ?
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, accessToken)
            statement.setString(2, refreshToken)
            statement.setString(3, email)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun isBarberConnectedToGoogle(email: String): Boolean {
        var connection: Connection? = null
        var isConnected = false
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT googleAccessToken, googleRefreshToken
                    FROM barber
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, email)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                val accessToken = resultSet.getString("googleAccessToken")
                val refreshToken = resultSet.getString("googleRefreshToken")
                isConnected = !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return isConnected
    }
}