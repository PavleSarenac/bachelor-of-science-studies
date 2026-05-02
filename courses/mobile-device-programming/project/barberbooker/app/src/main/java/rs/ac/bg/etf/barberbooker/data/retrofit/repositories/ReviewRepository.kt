package rs.ac.bg.etf.barberbooker.data.retrofit.repositories

import android.util.Log
import retrofit2.HttpException
import rs.ac.bg.etf.barberbooker.data.retrofit.apis.ReviewApi
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReviewWithBarber
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReviewWithClient
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Review
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val reviewApi: ReviewApi
) {
    private val httpExceptionLogTag = "ReviewRepository"

    suspend fun submitReview(
        clientEmail: String,
        barberEmail: String,
        grade: Int,
        text: String,
        date: String
    ) {
        try {
            reviewApi.submitReview(
                Review(
                    id = 0,
                    clientEmail = clientEmail,
                    barberEmail = barberEmail,
                    grade = grade,
                    text = text,
                    date = date
                )
            )
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
    }

    suspend fun getClientReviewsForBarber(
        clientEmail: String,
        barberEmail: String
    ): List<Review> {
        try {
            return reviewApi.getClientReviewsForBarber(clientEmail, barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getBarberReviews(barberEmail: String): List<ExtendedReviewWithClient> {
        try {
            return reviewApi.getBarberReviews(barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getClientReviews(clientEmail: String): List<ExtendedReviewWithBarber> {
        try {
            return reviewApi.getClientReviews(clientEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return listOf()
    }

    suspend fun getBarberAverageGrade(barberEmail: String): Float {
        try {
            return reviewApi.getBarberAverageGrade(barberEmail)
        } catch (exception: HttpException){
            Log.e(httpExceptionLogTag, exception.message())
        }
        return 0.00f
    }

}