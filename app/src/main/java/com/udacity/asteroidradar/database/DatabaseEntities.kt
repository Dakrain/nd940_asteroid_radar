package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.AsteroidPOD

@Entity
data class DatabaseAsteroid(
    @PrimaryKey
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

@Entity
data class DatabaseAPOD(
    @PrimaryKey
    val url: String,
    val title: String,
    val mediaType: String,
)


fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun DatabaseAPOD.asDomainModel(): AsteroidPOD {
    return AsteroidPOD(
        url = url,
        title = title,
        mediaType = mediaType
    )
}