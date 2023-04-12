package com.udacity.asteroidradar.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.database.DatabaseAPOD
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.AsteroidPOD


data class AsteroidDTO(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

@JsonClass(generateAdapter = true)
data class AsteroidPODDTO(
    val url: String,
    val title: String,
    @Json(name = "media_type") val mediaType: String,
)


fun AsteroidDTO.asDomainModel(): Asteroid {
    return Asteroid(
        id = id,
        codename = codename,
        closeApproachDate = closeApproachDate,
        absoluteMagnitude = absoluteMagnitude,
        estimatedDiameter = estimatedDiameter,
        relativeVelocity = relativeVelocity,
        distanceFromEarth = distanceFromEarth,
        isPotentiallyHazardous = isPotentiallyHazardous
    )
}


fun List<AsteroidDTO>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

fun AsteroidPODDTO.asDomainModel(): AsteroidPOD {
    return AsteroidPOD(
        url = url,
        title = title,
        mediaType = mediaType
    )
}

fun AsteroidPODDTO.asDatabaseModel(): DatabaseAPOD {
    return DatabaseAPOD(
        url = url,
        title = title,
        mediaType = mediaType
    )
}