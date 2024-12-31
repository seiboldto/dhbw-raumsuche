package com.example.dhbw_raumsuche.ui.rooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.dhbw_raumsuche.ui.viewmodel.RoomSortType
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel

@Composable
fun RoomFilters(roomViewModel: RoomViewModel) {
    val filterSettings by roomViewModel.filterSettings.collectAsState()
    val selectedSortType by roomViewModel.sortType.collectAsState()

    var sortMenuExpanded by remember { mutableStateOf(false) }
    var buildingMenuExpanded by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
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
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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