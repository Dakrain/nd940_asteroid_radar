package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.AsteroidFilter
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)


    private val _filter = MutableLiveData(AsteroidFilter.saved)


    val asteroids: LiveData<List<Asteroid>> = Transformations.switchMap(_filter) { filter ->
        Transformations.map(
            when (filter) {
                AsteroidFilter.saved -> asteroidRepository.getAsteroids()
                AsteroidFilter.today -> asteroidRepository.getAsteroidsForToday()
                AsteroidFilter.week -> asteroidRepository.getAsteroidsForCurrentWeek()
            }
        ) {
            it.asDomainModel()
        }
    }

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            asteroidRepository.refreshPOD()
        }
    }

    val asteroidPOD = asteroidRepository.pod


    fun onMenuSelected(menuId: Int) {
        when (menuId) {
            R.id.show_week_menu -> _filter.value = AsteroidFilter.week
            R.id.show_today_menu -> _filter.value = AsteroidFilter.today
            R.id.show_all_menu -> _filter.value = AsteroidFilter.saved
            else -> _filter.value = AsteroidFilter.saved
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}