package com.newagedavid.myride.presentation.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.newagedavid.myride.data.common.utils.isCurrentTimePeakHour
import com.newagedavid.myride.data.common.utils.minutesAway
import com.newagedavid.myride.data.common.utils.simulateTrafficLevel
import com.newagedavid.myride.data.repository.PlacesRepository
import com.newagedavid.myride.data.local.entity.RideHistory
import com.newagedavid.myride.presentation.ui.history.AnnotatedText
import com.newagedavid.myride.presentation.ui.utils.getAddressFromLocation
import com.newagedavid.myride.presentation.ui.utils.getCurrentLocation
import com.newagedavid.myride.presentation.viewmodel.MapViewModel
import com.newagedavid.myride.presentation.viewmodel.RideViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
) {


        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        val mapViewModel: MapViewModel = hiltViewModel()
        val rideViewModel: RideViewModel = hiltViewModel()

        val pickupLocation by mapViewModel.pickupLocation.collectAsState()
        val destinationLocation by mapViewModel.destinationLocation.collectAsState()
        val polylinePoints by mapViewModel.routePolyline.collectAsState()

        val fareEstimate by rideViewModel.fareEstimate.collectAsState()

        var uiState by remember { mutableStateOf(FareUiState.INPUT) }

        val cameraPositionState = rememberCameraPositionState()
        val sheetState = rememberBottomSheetScaffoldState()

        // Track focused field for dynamic UI (pickup or destination)
        var focusedField by remember { mutableStateOf<String?>(null) }
        var minutesAway by remember { mutableStateOf<Int?>(null) }

        //  Text inputs for pickup and destination
        var pickupInput by remember { mutableStateOf("") }
        var destinationInput by remember { mutableStateOf("") }

        // Suggestions from Google Places API
        var suggestions by remember { mutableStateOf(listOf<AutocompletePrediction>()) }
        val placesRepository = remember { PlacesRepository(context) }

        //  Your API key (move to secure config in real apps)
        val apiKey = "AIzaSyC_RlomlIQMdPks0efQ89IyjXBJoeaM5Ek"

        // Location permission handling
        var hasLocationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        var showBookingDialog by remember { mutableStateOf(false) }
        var showRideDetails by remember { mutableStateOf(false) }
        var confirmedRide by remember { mutableStateOf<RideHistory?>(null) }


        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted -> hasLocationPermission = granted }
        )

        // Request location and set pickup address on first launch
        LaunchedEffect(Unit) {
            if (!hasLocationPermission) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                val location = getCurrentLocation(context)
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    mapViewModel.setPickupLocation(latLng)
                    pickupInput = getAddressFromLocation(context, it)
                }
            }
        }


        // Move camera to pickup location
        LaunchedEffect(pickupLocation) {
            if (pickupLocation != null && mapViewModel.routePolyline.value.isEmpty()) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(pickupLocation!!, 16f),
                    durationMs = 1000
                )
            }
        }


        LaunchedEffect(mapViewModel.routePolyline.value) {
            val pickup = mapViewModel.pickupLocation.value
            val destination = mapViewModel.destinationLocation.value
            val polyline = mapViewModel.routePolyline.value

            Log.d("ZOOM", "Zoom triggered with ${polyline.size} polyline points")

            if (pickup != null && destination != null && polyline.isNotEmpty()) {
                zoomToFitRoute(pickup, destination, cameraPositionState)
            }
        }

        LaunchedEffect(pickupLocation, destinationLocation, polylinePoints) {
            if (
                pickupLocation != null &&
                destinationLocation != null &&
                polylinePoints.isNotEmpty()
            ) {
                uiState = FareUiState.LOADING

                minutesAway = minutesAway()  //calculate minutes away

                Log.d("HomeScreen", "minutesAway is: $minutesAway" )

                coroutineScope.launch {
                    rideViewModel.calculateFareEstimate(
                        polyline = polylinePoints,
                        isPeakHour = isCurrentTimePeakHour(),
                        trafficMultiplier = simulateTrafficLevel()
                    )

                    delay(1000) // simulate backend call
                    uiState = FareUiState.RESULT
                }
            }
        }


        Log.d("HomeScreen", "rideViewModel.fareEstimate: ${fareEstimate?.totalFare}")

        // Animate bottom sheet on focus
        LaunchedEffect(focusedField) {
            coroutineScope.launch {
                if (focusedField != null) {
                    sheetState.bottomSheetState.expand()
                } else {
                    sheetState.bottomSheetState.partialExpand()
                }
            }
        }

        //  Scaffold with bottom sheet for ride input
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 280.dp,
            sheetContainerColor = Color.White,
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                ) {
                    RideInputSheet(
                        pickupLocationText = pickupInput,
                        destinationLocationText = destinationInput,

                        // 🚕 Text input handlers (fetch suggestions)
                        onPickupChange = {
                            pickupInput = it
                            if (focusedField == "pickup") {
                                coroutineScope.launch {
                                    suggestions = placesRepository.getAutocompleteSuggestions(it)
                                }
                            }
                        },
                        onDestinationChange = {
                            destinationInput = it
                            if (focusedField == "destination") {
                                coroutineScope.launch {
                                    suggestions = placesRepository.getAutocompleteSuggestions(it)
                                }
                            }
                        },

                        // 🔄 Track focus and clear suggestions if needed
                        onFieldFocus = { field ->
                            focusedField = field
                            if (field == null) suggestions = emptyList()
                        },

                        suggestions = suggestions,

                        minutesAway = minutesAway ?: 0,

                        // Handle suggestion click (set pickup/destination)
                        onSuggestionClick = { suggestion ->
                            val fullText = suggestion.getFullText(null).toString()
                            val placeId = suggestion.placeId

                            when (focusedField) {
                                "pickup" -> {
                                    pickupInput = fullText
                                    coroutineScope.launch {
                                        val latLng =
                                            placesRepository.getLatLngFromPlaceId(placeId, context)
                                        latLng?.let { mapViewModel.setPickupLocation(it) }
                                    }
                                }

                                "destination" -> {
                                    destinationInput = fullText
                                    coroutineScope.launch {
                                        val latLng =
                                            placesRepository.getLatLngFromPlaceId(placeId, context)
                                        latLng?.let {
                                            mapViewModel.setDestinationLocation(
                                                it,
                                                apiKey
                                            )
                                        }
                                    }
                                }
                            }

                            focusedField = null
                            suggestions = emptyList()

                            coroutineScope.launch {
                                sheetState.bottomSheetState.partialExpand()
                            }
                        },

                        uiState = uiState,

                        fareEstimate = rideViewModel.fareEstimate.value,

                        onEstimateFareClick = {
                            if (pickupLocation != null && destinationLocation != null) {
                                showBookingDialog = true

                                rideViewModel.requestRide { confirmation -> //request the ride
                                    val fare = rideViewModel.fareEstimate.value?.totalFare ?: 0.0

                                    rideViewModel.saveRideToHistory( //on response, ssave to local db
                                        pickup = pickupLocation!!,
                                        destination = destinationLocation!!,
                                        fare = fare,
                                        driver = confirmation.driver
                                    )

                                    confirmedRide =
                                        RideHistory( //show user the response in a dialog
                                            pickupLat = pickupLocation!!.latitude,
                                            pickupLng = pickupLocation!!.longitude,
                                            destinationLat = destinationLocation!!.latitude,
                                            destinationLng = destinationLocation!!.longitude,
                                            fare = fare,
                                            timestamp = System.currentTimeMillis(),
                                            driverName = confirmation.driver.name,
                                            car = confirmation.driver.car,
                                            plateNumber = confirmation.driver.plateNumber
                                        )

                                    showRideDetails = true
                                }
                                showBookingDialog = true

                            } else {
                                Toast.makeText(
                                    context,
                                    "Please select both pickup and destination",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    )
                }
            },

            //  Map content under bottom sheet
            content = {
                Box (
                    modifier = Modifier
                        .fillMaxSize()
                        // Draw behind system bars for edge-to-edge
                        .padding(WindowInsets(0, 0, 0, 0).asPaddingValues())
                ){
                    GoogleMapView(
                        currentLocation = pickupLocation,
                        cameraPositionState = cameraPositionState,
                        routePolyline = polylinePoints,
                    )
                }

            },
        )

        if (showBookingDialog) {
            AlertDialog(
                onDismissRequest = {
                    uiState = FareUiState.INPUT
                },
                confirmButton = {},
                title = {
                    if (!showRideDetails) {
                        Text("Booking Ride...")
                    } else {
                        Text("Booking Successful!")
                    }
                },
                text = {
                    if (!showRideDetails) {
                        CircularProgressIndicator()
                    } else {
                        confirmedRide?.let { ride ->
                            Column {
                                AnnotatedText("Driver:  ", ride.driverName, Color.White)
                                AnnotatedText("Car:  ", ride.car, Color.White)
                                AnnotatedText("Plate:  ", ride.plateNumber, Color.White)
                                AnnotatedText("Fare:  ", "$${"%.2f".format(ride.fare)}", Color.White)
                                AnnotatedText("ETA:" , "$minutesAway mins", Color.White)
                            }
                        }
                    }
                },
                dismissButton = {
                    if (showRideDetails) {
                        // Reset destination so user must add it again
                        mapViewModel.setDestinationLocation(null, "")

                        TextButton(onClick = {
                            showBookingDialog = false
                            showRideDetails = false

                            uiState = FareUiState.INPUT

                        }) {
                            Text("Done")
                        }
                    }
                }
            )
        }

}


suspend fun zoomToFitRoute(
    pickup: LatLng,
    destination: LatLng,
    cameraPositionState: CameraPositionState
) {
    val boundsBuilder = LatLngBounds.builder()
    boundsBuilder.include(pickup)
    boundsBuilder.include(destination)

    val bounds = boundsBuilder.build()
    val padding = 150 // pixels

    cameraPositionState.animate(
        update = CameraUpdateFactory.newLatLngBounds(bounds, padding),
        durationMs = 1000
    )
}



