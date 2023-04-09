package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

interface AsteroidService {
    @GET("/neo/rest/v1/feed")
    suspend fun getListAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String
    ): JSONObject

    @GET("/planetary/apod")
    suspend fun getPictureOfTheDay(@Query("api_key") apiKey: String): AsteroidPODDTO
}

object AsteroidApi {
    val retrofitService: AsteroidService by lazy { retrofit.create(AsteroidService::class.java) }
}