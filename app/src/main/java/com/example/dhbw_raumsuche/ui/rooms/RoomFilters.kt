package com.example.dhbw_raumsuche.ui.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dhbw_raumsuche.R
import com.example.dhbw_raumsuche.location.Building
import com.example.dhbw_raumsuche.location.Floor
import com.example.dhbw_raumsuche.location.LocalLocationModel
import com.example.dhbw_raumsuche.ui.viewmodel.RoomSortType
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomFilters(roomViewModel: RoomViewModel) {
    val filterSettings by roomViewModel.filterSettings.collectAsState()
    val selectedSortType by roomViewModel.sortType.collectAsState()

    val location = LocalLocationModel.current
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var buildingMenuExpanded by remember { mutableStateOf(false) }

    val building = when (location.building) {
        Building.A -> "A"
        Building.B -> "B"
        Building.C -> "C"
        Building.D -> "D"
        else -> null
    }

    val floor = when (location.floor) {
        Floor.FirstFloor -> "1"
        Floor.SecondFloor -> "2"
        Floor.ThirdFloor -> "3"
        Floor.FourthFloor -> "4"
        else -> null
    }

    SideEffect {
        roomViewModel.setLocationFilter(location.building, location.floor)
    }

    val isLocationActive = floor != null && building != null
    FlowRow(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InputChip(
            onClick = {
                if (!isLocationActive) location.requestLocation() else {
                    location.clearLocation()
                }
            },
            selected = isLocationActive,
            label = {
                Text(
                    // Unfortunately, this cannot be replaced with `isLocationActive`
                    // If it is, building and floor cannot be guaranteed to be non-null anymore by the type checker
                    text = if (floor != null && building != null) stringResource(
                        R.string.location_description, building, floor

                    ) else stringResource(R.string.location)
                )
            },
            leadingIcon = {
                if (isLocationActive) Icon(
                    Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear)
                ) else Icon(Icons.Default.LocationOn, contentDescription = null)
            })
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            InputChip(
                onClick = { buildingMenuExpanded = true },
                label = {
                    Text(
                        text = if (filterSettings.selectedBuildings.isNotEmpty()) filterSettings.selectedBuildings.joinToString() else stringResource(
                            R.string.buildings
                        )
                    )
                },
                selected = filterSettings.selectedBuildings.isNotEmpty(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null
                    )
                })
            DropdownMenu(
                expanded = buildingMenuExpanded,
                onDismissRequest = { buildingMenuExpanded = false }) {
                Building.entries.forEach { building ->
                    DropdownMenuItem(
                        text = { Text(text = building.name) },
                        onClick = {
                            roomViewModel.setBuildingFilter(building)
                        },
                        leadingIcon = {
                            if (filterSettings.selectedBuildings.contains(
                                    building
                                )
                            ) Icon(
                                Icons.Default.Check,
                                contentDescription = null
                            )
                        })
                }
            }
        }
        InputChip(
            selected = filterSettings.favorites,
            onClick = { roomViewModel.setFavoritesFilter(!filterSettings.favorites) },
            label = { Text(text = stringResource(R.string.favorites)) },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) }
        )
    }
        FlowRow(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
        InputChip(
            selected = filterSettings.free,
            onClick = { roomViewModel.setFreeFilter(!filterSettings.free) },
            label = { Text(text = stringResource(R.string.free)) },
            leadingIcon = { Icon(Icons.Default.ThumbUp, contentDescription = null) }
        )
        Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
            IconButton(onClick = { sortMenuExpanded = true }) {
                Icon(
                    Icons.AutoMirrored.Default.List,
                    contentDescription = stringResource(R.string.sort)
                )
            }
            DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }) {
                DropdownMenuItem(text = {
                    Text(
                        text = stringResource(R.string.sort),
                        style = MaterialTheme.typography.labelMedium
                    )
                }, onClick = {})
                RoomSortType.entries.forEach { sortType ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(
                                    when (sortType) {
                                        RoomSortType.ROOM_ID -> R.string.room_id
                                        RoomSortType.FLOOR -> R.string.floor
                                        RoomSortType.BUILDING -> R.string.building
                                        RoomSortType.FREE_TIME -> R.string.free_time
                                    }
                                )
                            )
                        },
                        onClick = {
                            roomViewModel.setSortType(sortType)
                            sortMenuExpanded = false
                        },
                        leadingIcon = {
                            if (selectedSortType == sortType) Icon(
                                Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    )
                }

            }
        }
    }
}