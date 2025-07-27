package com.newagedavid.myride.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.newagedavid.myride.data.repository.PlacesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the RideInputSheet.
 * Handles business logic and updates UI state.
 */
class RideInputViewModel (
    private val placesRepository: PlacesRepository
) : ViewModel() {

    // Holds the current list of suggestions to show in the UI
    private val _suggestions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val suggestions: StateFlow<List<AutocompletePrediction>> = _suggestions

    /**
     * Fetch place suggestions for user input.
     * Called whenever the user types in pickup/destination field.
     */
    fun fetchSuggestions(query: String) {
        viewModelScope.launch {
            if (query.length >= 2) {
                val results = placesRepository.getAutocompleteSuggestions(query)
                _suggestions.value = results
            } else {
                _suggestions.value = emptyList()
            }
        }
    }

    /**
     * Clears the suggestions when the input is no longer focused or user selects one.
     */
    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}
