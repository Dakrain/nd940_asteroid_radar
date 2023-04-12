package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.api.AsteroidDTO
import com.udacity.asteroidradar.api.AsteroidPODDTO

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid ORDER BY closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate BETWEEN :startOfWeek AND :endOfWeek ORDER BY closeApproachDate DESC")
    fun getAsteroidsThisWeek(startOfWeek: String, endOfWeek: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = :today ORDER BY closeApproachDate DESC")
    fun getAsteroidsToday(today: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroids(vararg asteroids: DatabaseAsteroid)

    @Query("select * from databaseapod")
    fun getAsteroidPOD(): LiveData<DatabaseAPOD>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroidPOD( asteroids: DatabaseAPOD)
}

@Database(entities = [DatabaseAsteroid::class, DatabaseAPOD::class], version = 2, exportSchema = false)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroids"
            ).build()
        }
    }
    return INSTANCE
}
