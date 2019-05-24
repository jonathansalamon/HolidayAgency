package com.intech.holidayagency

data class Destination(val name: String, val pricePerPerson: Float, val availableSpots: Int = 0) {
    override operator fun equals(other: Any?): Boolean {
        if(other is Destination) {
            return name == other.name
        }
        return super.equals(other)
    }
}