package com.intech.holidayagency

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val returningFriends = Client(7)
        val clients = listOf(
            Client(30),
            Client(5),
            Client(1),
            returningFriends,
            Client(3),
            Client(12)
        )

        val destinations = mutableListOf(
            Destination("Brésil", 150f, 15),
            Destination("Tahiti", 300f, 32),
            Destination("Brest", 50f, 5),
            Destination("Ibiza", 100f, 7),
            Destination("Chengdu", 70f, 18),
            Destination("Londres", 400f, 40)
        )
        launch(Dispatchers.Main) {
            val v = awaitClick()
            Toast.makeText(v.context, "I have clicked", Toast.LENGTH_LONG).show()

            val totalSpots = countAvailableSpots(destinations)
            Log.d(TAG, "We have $totalSpots available")
            clients.forEach { client ->
                val chosenDestination = assignToDestination(destinations, client)
                chosenDestination?.let {
                    val indexToUpdate = destinations.indexOf(it)
                    destinations[indexToUpdate] = it
                }
            }
            val remainingSpots = countAvailableSpots(destinations)
            Log.d(TAG, "We have $remainingSpots remainings")

            clients.forEach {
                val nbPersonString = if (it.nbPersons > 1) "${it.nbPersons} persons" else "${it.nbPersons} person"
                Log.d(
                    TAG,
                    "Client with $nbPersonString has been sent to ${it.destinationHistory.last().name}"
                )
            }

            val destination = assignToDestination(destinations, returningFriends)
            // Update UI
        }


        Log.d(TAG, "Returning friends have been sent to ${returningFriends.destinationHistory}")
    }

    private suspend fun assignToDestination(
        destinations: List<Destination>,
        client: Client
    ): Destination? = withContext(Dispatchers.Default) {
        delay(2_000)
        yield()
        destinations
            .sortedByDescending { it.availableSpots }
            .filter { !client.destinationHistory.contains(it) }
            .firstOrNull { it.availableSpots >= client.nbPersons }?.let {
                client.goToDestination(it)
                it.copy(availableSpots = it.availableSpots - client.nbPersons)
            }
    }

    private suspend fun awaitClick() = suspendCoroutine<View> {
        textView.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                it.resume(v!!)
            }
        })
    }


    private fun countAvailableSpots(destinations: List<Destination>) = destinations.sumBy { it.availableSpots }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
    companion object {
        private const val TAG = "HolidayAgency"
    }
}
