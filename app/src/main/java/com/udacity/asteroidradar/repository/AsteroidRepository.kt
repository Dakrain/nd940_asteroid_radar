package com.udacity.asteroidradar.repository

import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val databaseAsteroid: AsteroidDatabase) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(databaseAsteroid.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }


    @SuppressLint("WeekBasedYear")
    suspend fun refreshAsteroids() {
        val calendar = Calendar.getInstance()

        // Get the current date and time
        val currentDate = calendar.time


        // Set the date format
        val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        }

        // Format the date
        val formattedDate: String = dateFormat.format(currentDate)

        withContext(Dispatchers.IO) {
            val response = AsteroidApi.retrofitService.getListAsteroids(
                startDate = formattedDate,
                endDate = formattedDate,
                apiKey = Constants.API_KEY
            )

            val asteroids = parseAsteroidsJsonResult(response).toList()
            databaseAsteroid.asteroidDao.insertAsteroids(*asteroids.asDatabaseModel())
        }
    }
}