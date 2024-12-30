package com.example.dhbw_raumsuche.location

// This is a mapping of buildings to their coordinates.
// The height above sea level is also stored for each level.
data class DHBWBuilding(
    val building: Building,
    val latitude: Double,
    val longitude: Double,
    val floors: Map<Floor, Double>
) {
    companion object {
        val buildings = setOf(
            DHBWBuilding(
                Building.A,
                49.4736747,
                8.5346100,
                mapOf(Floor.FirstFloor to 147.10000610351562)
            ),
            DHBWBuilding(
                Building.B,
                49.4741747,
                8.534918,
                mapOf(
                    Floor.FirstFloor to 147.40000915527344,
                    Floor.SecondFloor to 150.1000061035136,
                    Floor.ThirdFloor to 154.40000305175,
                    Floor.FourthFloor to 157.60000610351562
                )
            ),
            DHBWBuilding(
                Building.C,
                49.4746708,
                8.5349309,
                mapOf(
                    Floor.FirstFloor to 147.10000610351562,
                    Floor.SecondFloor to 149.90000915527344,
                    Floor.ThirdFloor to 153.8000030517578,
                    Floor.FourthFloor to 157.60000610351562
                )
            ),
            DHBWBuilding(
                Building.D,
                49.4733223,
                8.5346464,
                mapOf(
                    Floor.FirstFloor to 144.6000610351562,
                    Floor.SecondFloor to 148.3000030517578,
                    Floor.ThirdFloor to 151.60000610351562,
                    Floor.FourthFloor to 155.20001220703125
                )
            )
        )
    }

}
