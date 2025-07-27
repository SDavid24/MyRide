package com.newagedavid.myride.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newagedavid.myride.data.local.dao.RideHistoryDao
import com.newagedavid.myride.data.local.entity.RideHistory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RideHistoryViewModel (
    private val dao: RideHistoryDao
) : ViewModel() {

    init {
        viewModelScope.launch {
            dao.getAllRides().collect { rides ->
                Log.d("RideHistoryViewModel", "Loaded ${rides.size} rides")
            }
        }
    }


    /**
     * Using Flow so changes to the database are reflected in real-time.
     */
    val rides: StateFlow<List<RideHistory>> = dao.getAllRides().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000), // Cancel when not observed (after 5s)
        emptyList()
    )

    fun clearAll() {
        viewModelScope.launch {
            dao.clearRideHistory()
        }
    }
}
