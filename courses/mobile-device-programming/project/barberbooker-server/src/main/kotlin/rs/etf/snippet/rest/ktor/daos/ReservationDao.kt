package rs.etf.snippet.rest.ktor.daos

import rs.etf.snippet.rest.ktor.utils.DatabaseUtils
import rs.etf.snippet.rest.ktor.entities.structures.ExtendedReservationWithBarber
import rs.etf.snippet.rest.ktor.entities.structures.ExtendedReservationWithClient
import rs.etf.snippet.rest.ktor.entities.tables.Reservation
import java.sql.Connection
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ReservationDao {
    fun addNewReservation(reservation: Reservation) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    INSERT INTO reservation (
                        clientEmail,
                        barberEmail,
                        date,
                        startTime,
                        endTime,
                        status
                    )
                    VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()
            )
            statement.setString(1, reservation.clientEmail)
            statement.setString(2, reservation.barberEmail)
            statement.setString(3, reservation.date)
            statement.setString(4, reservation.startTime)
            statement.setString(5, reservation.endTime)
            statement.setString(6, reservation.status)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun getAllValidTimeSlots(
        clientEmail: String,
        barberEmail: String,
        date: String
    ) : List<String> {
        var connection: Connection? = null

        val allValidTimeSlots = mutableListOf<String>()
        val barberWorkingTimeSlots = getBarberWorkingTimeSlots(barberEmail)
        allValidTimeSlots.addAll(barberWorkingTimeSlots)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val requestedDate = LocalDate.parse(date, formatter)
        val today = LocalDate.now()

        if (requestedDate.equals(today)) {
            val currentTime = LocalTime.now()
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            allValidTimeSlots.removeIf { slot ->
                val slotTime = LocalTime.parse(slot.split(" - ")[0], timeFormatter)
                slotTime.isBefore(currentTime)
            }
        }

        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT * FROM reservation
                    WHERE (clientEmail = ? AND date = ?) OR (barberEmail = ? AND date = ? AND status = 'ACCEPTED')
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            statement.setString(2, date)
            statement.setString(3, barberEmail)
            statement.setString(4, date)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val startTime = resultSet.getString("startTime")
                val endTime = resultSet.getString("endTime")
                val invalidTimeSlot = "$startTime - $endTime"
                allValidTimeSlots.remove(invalidTimeSlot)
            }
        } catch (e: Exception) {
            allValidTimeSlots.clear()
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return allValidTimeSlots
    }

    private fun getBarberWorkingTimeSlots(
        barberEmail: String
    ): List<String> {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT * FROM barber
                    WHERE email = ?
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                val workingHours = resultSet.getString("workingHours")
                val workingTimeSlots = generateTimeSlots(workingHours)
                return workingTimeSlots
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return listOf()
    }

    private fun generateTimeSlots(workingHours: String): List<String> {
        val splittedWorkingHours = workingHours.split(" - ")
        val startString = splittedWorkingHours[0]
        val endString = splittedWorkingHours[1]

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTime = LocalTime.parse(startString, formatter)
        val endTime = LocalTime.parse(endString, formatter)

        return generateSequence(startTime) { previousSlotStartTime ->
            val nextSlotStartTime = previousSlotStartTime.plusMinutes(30)
            val isNextSlotStartTimeValid = !nextSlotStartTime.equals(LocalTime.MIDNIGHT) && (endTime.equals(LocalTime.MIDNIGHT) || nextSlotStartTime < endTime)
            if (isNextSlotStartTimeValid) nextSlotStartTime else null
        }.map { currentSlotStartTime ->
            val formattedCurrentSlotStartTime = currentSlotStartTime.format(formatter)
            val formattedCurrentSlotEndTime = currentSlotStartTime.plusMinutes(30).format(formatter)
            "$formattedCurrentSlotStartTime - $formattedCurrentSlotEndTime"
        }.toList()
    }

    fun updateReservationStatuses(
        currentDate: String,
        currentTime: String
    ) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation 
                    SET status = 'WAITING_CONFIRMATION'
                    WHERE date <= ? AND ? >= endTime AND status = 'ACCEPTED'
                """.trimIndent()
            )
            statement.setString(1, currentDate)
            statement.setString(2, currentTime)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun updatePendingRequests(
        currentDate: String,
        currentTime: String
    ) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation 
                    SET status = 'REJECTED'
                    WHERE date <= ? AND ? >= endTime AND status = 'PENDING'
                """.trimIndent()
            )
            statement.setString(1, currentDate)
            statement.setString(2, currentTime)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun getClientPendingReservationRequests(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName,
                        b.price AS barberPrice,
                        b.city AS barberCity,
                        b.municipality AS barberMunicipality
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND status = 'PENDING'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName"),
                    barberCity = resultSet.getString("barberCity"),
                    barberMunicipality = resultSet.getString("barberMunicipality"),
                    barberPrice = resultSet.getDouble("barberPrice")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getClientAppointments(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName,
                        b.price AS barberPrice,
                        b.city AS barberCity,
                        b.municipality AS barberMunicipality
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND status = 'ACCEPTED'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName"),
                    barberCity = resultSet.getString("barberCity"),
                    barberMunicipality = resultSet.getString("barberMunicipality"),
                    barberPrice = resultSet.getDouble("barberPrice")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getClientRejections(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName,
                        b.price AS barberPrice,
                        b.city AS barberCity,
                        b.municipality AS barberMunicipality
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND status = 'REJECTED'
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName"),
                    barberCity = resultSet.getString("barberCity"),
                    barberMunicipality = resultSet.getString("barberMunicipality"),
                    barberPrice = resultSet.getDouble("barberPrice")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getClientArchive(
        clientEmail: String
    ): List<ExtendedReservationWithBarber> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithBarber>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        b.id AS barberId,
                        b.barbershopName,
                        b.price AS barberPrice,
                        b.city AS barberCity,
                        b.municipality AS barberMunicipality
                    FROM reservation r
                    INNER JOIN barber b ON r.barberEmail = b.email
                    WHERE clientEmail = ? AND (status = 'DONE_SUCCESS' OR status = 'DONE_FAILURE')
                """.trimIndent()
            )
            statement.setString(1, clientEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithBarber(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    barberId = resultSet.getInt("barberId"),
                    barbershopName = resultSet.getString("barbershopName"),
                    barberCity = resultSet.getString("barberCity"),
                    barberMunicipality = resultSet.getString("barberMunicipality"),
                    barberPrice = resultSet.getDouble("barberPrice")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberPendingReservationRequests(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname,
                        c.fcmToken AS fcmToken
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'PENDING'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname"),
                    fcmToken = resultSet.getString("fcmToken")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberAppointments(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'ACCEPTED'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberArchive(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND (status = 'DONE_SUCCESS' OR status = 'DONE_FAILURE')
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberRejections(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'REJECTED'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun getBarberConfirmations(
        barberEmail: String
    ): List<ExtendedReservationWithClient> {
        var connection: Connection? = null
        val requests = mutableListOf<ExtendedReservationWithClient>()
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    SELECT 
                        r.id AS reservationId,
                        r.clientEmail,
                        r.barberEmail,
                        r.date,
                        r.startTime,
                        r.endTime,
                        r.status,
                        c.id AS clientId,
                        c.name AS clientName,
                        c.surname AS clientSurname
                    FROM reservation r
                    INNER JOIN client c ON r.clientEmail = c.email
                    WHERE barberEmail = ? AND status = 'WAITING_CONFIRMATION'
                """.trimIndent()
            )
            statement.setString(1, barberEmail)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                requests.add(ExtendedReservationWithClient(
                    reservationId = resultSet.getInt("reservationId"),
                    clientEmail = resultSet.getString("clientEmail"),
                    barberEmail = resultSet.getString("barberEmail"),
                    date = resultSet.getString("date"),
                    startTime = resultSet.getString("startTime"),
                    endTime = resultSet.getString("endTime"),
                    status = resultSet.getString("status"),
                    clientId = resultSet.getInt("clientId"),
                    clientName = resultSet.getString("clientName"),
                    clientSurname = resultSet.getString("clientSurname")
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return requests
    }

    fun acceptReservationRequest(reservationId: Int) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation
                    SET status = 'ACCEPTED'
                    WHERE id = ?
                """.trimIndent()
            )
            statement.setInt(1, reservationId)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun rejectReservationRequest(reservationId: Int) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation
                    SET status = 'REJECTED'
                    WHERE id = ?
                """.trimIndent()
            )
            statement.setInt(1, reservationId)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }

    fun updateDoneReservationStatus(reservationId: Int, status: String) {
        var connection: Connection? = null
        try {
            connection = DatabaseUtils.dataSource.connection
            val statement = connection.prepareStatement(
                """
                    UPDATE reservation
                    SET status = ?
                    WHERE id = ?
                """.trimIndent()
            )
            statement.setString(1, status)
            statement.setInt(2, reservationId)
            statement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }
    }
}