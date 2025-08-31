package com.studioadriatic.gpgs.events

import android.app.Activity
import android.util.Log
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.event.Event
import com.google.gson.Gson
import com.studioadriatic.gpgs.utils.AuthenticationHelper
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

    fun submitEvent(eventId: String, incrementBy: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                eventsListener.onEventSubmittingFailed(eventId)
                return@launch
            }

            try {
                PlayGames.getEventsClient(activity).increment(eventId, incrementBy)
                eventsListener.onEventSubmitted(eventId)
            } catch (e: Exception) {
                Log.e("godot", "Failed to submit event: $eventId", e)
                eventsListener.onEventSubmittingFailed(eventId)
            }
        }
    }

    fun loadEvents() {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                eventsListener.onEventsLoadingFailed()
                return@launch
            }

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
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                eventsListener.onEventsLoadingFailed()
                return@launch
            }

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
