package com.newagedavid.myride.presentation.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.newagedavid.myride.presentation.viewmodel.RideHistoryViewModel

@Composable
fun RideHistoryScreen(
    navController: NavHostController,
    viewModel: RideHistoryViewModel = hiltViewModel()
) {
    val rideHistory by viewModel.rides.collectAsState()
    var showDialog by remember { mutableStateOf(false) }


    Scaffold(
        floatingActionButton = {
            if (rideHistory.isNotEmpty()) {
                FloatingActionButton (
                    onClick = { showDialog = true },
                    containerColor = Color.Black,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear History")
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // remove default inset padding
        containerColor = Color(0xFFF6F6F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(
                text = "My Ride History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding( top = 45.dp, bottom = 8.dp),
                color = Color.Black
            )

            if (rideHistory.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No rides yet", color = Color.Black)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rideHistory) { ride ->
                        RideHistoryCard(ride)
                    }
                }
            }
        }

        // Confirmation Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearAll()
                        showDialog = false
                    }) {
                        Text("Yes", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = Color.Black)
                    }
                },
                title = { Text("Clear All Ride History", color = Color.Black) },
                text = { Text("Are you sure you want to delete all your ride history?", color = Color.Black) },
                containerColor = Color.White
            )
        }
    }
}


