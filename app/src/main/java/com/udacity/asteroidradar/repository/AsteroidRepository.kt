package com.udacity.asteroidradar.repository

import android.annotation.SuppressLint
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.config.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.AsteroidPOD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


enum class AsteroidFilter {
    saved,
    today,
    week
}

class AsteroidRepository(private val databaseAsteroid: AsteroidDatabase) {


    val pod: LiveData<AsteroidPOD> =
        Transformations.map(databaseAsteroid.asteroidDao.getAsteroidPOD()) {
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

            val asteroids = parseAsteroidsJsonResult(JSONObject(response)).toList()
            databaseAsteroid.asteroidDao.insertAsteroids(*asteroids.asDatabaseModel())
        }
    }


    fun getAsteroidsForToday(): LiveData<List<DatabaseAsteroid>> {
        val calendar = Calendar.getInstance()
        val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        }
        val currentDate = calendar.time
        val formattedDate: String = dateFormat.format(currentDate)

        return databaseAsteroid.asteroidDao.getAsteroidsToday(formattedDate)
    }

    fun getAsteroids(): LiveData<List<DatabaseAsteroid>> {
        return databaseAsteroid.asteroidDao.getAsteroids()
    }

    fun getAsteroidsForCurrentWeek(): LiveData<List<DatabaseAsteroid>> {
        val calendar = Calendar.getInstance()
        val dateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        }
        val currentDateTime = calendar.timeInMillis
        val weekStartDateTime = calendar.apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val startDateTime = dateFormat.format(Date(weekStartDateTime))
        val endDateTime = dateFormat.format(Date(currentDateTime))
        Timber.d("start $startDateTime end $endDateTime")
        return databaseAsteroid.asteroidDao.getAsteroidsThisWeek(startDateTime, endDateTime)

    }

    suspend fun refreshPOD() {
        withContext(Dispatchers.IO) {
            val response = AsteroidApi.retrofitService.getPictureOfTheDay(
                apiKey = Constants.API_KEY
            )
            if (response.mediaType == "image") {
                databaseAsteroid.asteroidDao.insertAsteroidPOD(response.asDatabaseModel())
            }
        }
    }
}