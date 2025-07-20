package com.newagedavid.myride.presentation.ui.utils

import androidx.compose.ui.platform.LocalContext

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.maps.MapView

/**
 * Composable helper to remember a MapView instance
 * and tie it to the Compose lifecycle properly.
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current

    // Remember a single MapView instance across recompositions
    val mapView = remember {
        MapView(context).apply {
            id = View.generateViewId()
        }
    }


    // Get the current Lifecycle (of the composable)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // Create a lifecycle observer that manages the MapView lifecycle
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)

    // Attach and detach the observer as needed
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

/**
 * Returns a LifecycleEventObserver that mirrors the lifecycle events
 * to the given MapView instance — ensuring the MapView works as expected
 * inside a Jetpack Compose environment.
 */
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver {
    return LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> {}
        }
    }
}
