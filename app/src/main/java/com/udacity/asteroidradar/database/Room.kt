package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.api.AsteroidDTO
import com.udacity.asteroidradar.api.AsteroidPODDTO

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroids(vararg asteroids: DatabaseAsteroid)

    @Query("select * from databaseapod")
    fun getAsteroidPOD(): LiveData<AsteroidPODDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroidPOD(vararg asteroids: DatabaseAPOD)
}

@Database(entities = [DatabaseAsteroid::class, DatabaseAPOD::class], version = 1)
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
                "videos"
            ).build()
        }
    }
    return INSTANCE
}
