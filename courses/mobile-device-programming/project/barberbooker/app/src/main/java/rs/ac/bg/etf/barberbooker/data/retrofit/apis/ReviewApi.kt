package rs.ac.bg.etf.barberbooker.data.retrofit.apis

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReviewWithBarber
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.structures.ExtendedReviewWithClient
import rs.ac.bg.etf.barberbooker.data.retrofit.entities.tables.Review

const val REVIEW_URL = "http://172.20.10.4:8080/review/"

interface ReviewApi {

    @POST("submitReview")
    suspend fun submitReview(@Body review: Review)

    @GET("getClientReviewsForBarber")
    suspend fun getClientReviewsForBarber(
        @Query("clientEmail") clientEmail: String,
        @Query("barberEmail") barberEmail: String
    ): List<Review>

    @GET("getBarberReviews")
    suspend fun getBarberReviews(
        @Query("barberEmail") barberEmail: String
    ): List<ExtendedReviewWithClient>

    @GET("getClientReviews")
    suspend fun getClientReviews(
        @Query("clientEmail") clientEmail: String
    ): List<ExtendedReviewWithBarber>

    @GET("getBarberAverageGrade")
    suspend fun getBarberAverageGrade(
        @Query("barberEmail") barberEmail: String
    ): Float

}