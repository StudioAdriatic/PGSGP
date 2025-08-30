package com.studioadriatic.gpgs.events

import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.event.Event
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class EventInfo(
    val id: String,
    val name: String,
    val value: Long,
    val description: String,
    val imgUrl: String
)

class EventsController(
    private val activity: Activity,
    private val eventsListener: EventsListener
) {

    private fun isSignedIn(): Boolean = GoogleSignIn.getLastSignedInAccount(activity) != null

    fun submitEvent(eventId: String, incrementBy: Int) {
        if (!isSignedIn()) {
            eventsListener.onEventSubmittingFailed(eventId)
            return
        }

        try {
            PlayGames.getEventsClient(activity).increment(eventId, incrementBy)
            eventsListener.onEventSubmitted(eventId)
        } catch (e: Exception) {
            Log.e("godot", "Failed to submit event: $eventId", e)
            eventsListener.onEventSubmittingFailed(eventId)
        }
    }

    fun loadEvents() {
        if (!isSignedIn()) {
            eventsListener.onEventsLoadingFailed()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = PlayGames.getEventsClient(activity).load(true).await()
                val events = result?.get()

                if (events != null && events.count > 0) {
                    val eventList = events.map { event ->
                        EventInfo(
                            id = event.eventId,
                            name = event.name,
                            value = event.value,
                            description = event.description,
                            imgUrl = event.iconImageUrl
                        )
                    }
                    eventsListener.onEventsLoaded(Gson().toJson(eventList))
                } else {
                    eventsListener.onEventsEmpty()
                }
            } catch (e: Exception) {
                Log.e("godot", "Failed to load events", e)
                eventsListener.onEventsLoadingFailed()
            }
        }
    }

    fun loadEventById(eventIds: Array<String>) {
        if (!isSignedIn()) {
            eventsListener.onEventsLoadingFailed()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = PlayGames.getEventsClient(activity).loadByIds(true, *eventIds).await()
                val events = result?.get()

                if (events != null && events.count > 0) {
                    val eventList = events.map { event ->
                        EventInfo(
                            id = event.eventId,
                            name = event.name,
                            value = event.value,
                            description = event.description,
                            imgUrl = event.iconImageUrl
                        )
                    }
                    eventsListener.onEventsLoaded(Gson().toJson(eventList))
                } else {
                    eventsListener.onEventsEmpty()
                }
            } catch (e: Exception) {
                Log.e("godot", "Failed to load events by ID", e)
                eventsListener.onEventsLoadingFailed()
            }
        }
    }
}
