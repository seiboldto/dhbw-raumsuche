package com.example.dhbw_raumsuche.ui.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.dhbw_raumsuche.data.local.dao.RoomDao
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.location.Building
import com.example.dhbw_raumsuche.location.Floor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val FAVORITES_DELIMITER = "|"

class RoomViewModel(
    private val dataStore: DataStore<Preferences>,
    private val roomDao: RoomDao,
    private val getRoomData: () -> Unit,
) : ViewModel() {
    // Directly collect the flow of rooms with events from the DAO
    private val _rooms = MutableStateFlow<List<RoomWithEvents>>(emptyList())

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    // State flows for sort types and filters
    private val _filterSettings = MutableStateFlow(RoomFilterSettings())
    val filterSettings: StateFlow<RoomFilterSettings> = _filterSettings

    private val _sortType = MutableStateFlow(RoomSortType.ROOM_ID)
    val sortType: StateFlow<RoomSortType> = _sortType

    val roomList: StateFlow<List<RoomWithEvents>> =
        combine(_rooms, _filterSettings, _sortType) { rooms, filterSettings, sortType ->
            val filteredRooms = filterRooms(rooms, filterSettings)
            when (sortType) {
                RoomSortType.ROOM_ID -> filteredRooms.sortedBy { it.room.roomId }
                RoomSortType.BUILDING -> filteredRooms.sortedBy { it.room.building }
                RoomSortType.FLOOR -> filteredRooms.sortedBy { it.room.floor }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun filterRooms(
        rooms: List<RoomWithEvents>,
        filterSettings: RoomFilterSettings
    ): List<RoomWithEvents> {
        return rooms.filter {
            // Location filter overrides the selected buildings
            if (filterSettings.locationBuilding != null && filterSettings.locationFloor != null) {
                it.building == filterSettings.locationBuilding.toString() && it.room.floor == when (filterSettings.locationFloor) {
                    Floor.FirstFloor -> "1"
                    Floor.SecondFloor -> "2"
                    Floor.ThirdFloor -> "3"
                    Floor.FourthFloor -> "4"
                }
            } else (filterSettings.selectedBuildings.isEmpty() || it.building.isNotEmpty() && filterSettings.selectedBuildings.contains(
                Building.valueOf(it.building)
            ))
        }.filter { !filterSettings.free || it.isFree }
            .filter { !filterSettings.favorites || favorites.value.contains(it.room.roomId) }
    }

    init {
        loadRooms()
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences[FavoritesKeys.FAVORITES]?.split(FAVORITES_DELIMITER)?.toSet()
                    ?: emptySet()
            }.collect { _favorites.value = it }
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            getRoomData()
            CoroutineScope(Dispatchers.IO).launch {
                _rooms.emit(roomDao.getRoomsWithEvents())

            }
        }
    }

    fun toggleFavorite(name: String) {
        val updatedSet = _favorites.value.toMutableSet()
        if (updatedSet.contains(name)) updatedSet.remove(name)
        else updatedSet.add(name)

        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[FavoritesKeys.FAVORITES] = updatedSet.joinToString(FAVORITES_DELIMITER)
            }
        }
        _favorites.value = updatedSet
    }

    fun setSortType(sortType: RoomSortType) {
        viewModelScope.launch { _sortType.emit(sortType) }
    }

    fun setBuildingFilter(building: Building) {
        viewModelScope.launch {
            _filterSettings.value = _filterSettings.value.copy(
                selectedBuildings = _filterSettings.value.updateBuildings(building)
            )
        }
    }

    fun setLocationFilter(building: Building?, floor: Floor?) {
        viewModelScope.launch {
            _filterSettings.value =
                _filterSettings.value.copy(locationBuilding = building, locationFloor = floor)
        }
    }

    fun setFavoritesFilter(favorites: Boolean) {
        viewModelScope.launch {
            _filterSettings.value =
                _filterSettings.value.copy(favorites = favorites)
        }
    }

    fun setFreeFilter(free: Boolean) {
        viewModelScope.launch {
            _filterSettings.value = _filterSettings.value.copy(free = free)
        }
    }
}

val Context.favoritesStore by preferencesDataStore(name = "favorites")

object FavoritesKeys {
    val FAVORITES = stringPreferencesKey("favorites")
}