package com.intech.holidayagency


data class Client(val nbPersons: Int) {
    val destinationHistory = mutableListOf<Destination>()

    fun goToDestination(destination: Destination) {
        destinationHistory.add(destination)
    }
}