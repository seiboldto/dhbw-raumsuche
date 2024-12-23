package com.example.dhbw_raumsuche.location

data class UserLocation(val building: Building, val floor: Floor)

enum class Building {
    A, B, C, D
}

enum class Floor {

    FirstFloor,
    SecondFloor,
    ThirdFloor,
    FourthFloor,

}
