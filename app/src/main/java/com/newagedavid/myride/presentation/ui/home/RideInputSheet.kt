package com.newagedavid.myride.presentation.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.newagedavid.myride.R
import com.newagedavid.myride.data.common.model.FareEstimate

enum class FareUiState {
    INPUT, LOADING, RESULT
}

@Composable
fun RideInputSheet(
    pickupLocationText: String,
    destinationLocationText: String,
    onPickupChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onFieldFocus: (String?) -> Unit, // pass "pickup" or "destination"
    suggestions: List<AutocompletePrediction>,
    onSuggestionClick: (AutocompletePrediction) -> Unit, // Pass the entire prediction object
    fareEstimate: FareEstimate?, //️ Add this to show final price
    uiState: FareUiState,
    minutesAway: Int,
    onEstimateFareClick: () -> Unit,

    ) {
    var isPickupFocused by remember { mutableStateOf(false) }
    var isDestinationFocused by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .heightIn(min = 250.dp) // ensure min height for partial sheet

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            when (uiState) {
                FareUiState.INPUT -> {
                    // Define your custom text selection colors
                    val customTextSelectionColors = TextSelectionColors(
                        handleColor = Color.Black,
                        backgroundColor = Color.Black.copy(alpha = 0.4f) // selection background (optional)
                    )


                    // Pickup location field (now fully controlled)
                    CompositionLocalProvider (LocalTextSelectionColors provides customTextSelectionColors) {
                        OutlinedTextField(
                            value = pickupLocationText,
                            onValueChange = onPickupChange,
                            label = { Text("Pickup Location") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged {
                                    isPickupFocused = it.isFocused
                                    isDestinationFocused = false
                                    onFieldFocus(if (it.isFocused) "pickup" else null)
                                },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            },
                            trailingIcon = {
                                if (pickupLocationText.isNotEmpty() && isPickupFocused) {
                                    IconButton(onClick = { onPickupChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear text",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                            )
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Destination input
                    CompositionLocalProvider (LocalTextSelectionColors provides customTextSelectionColors) {

                        OutlinedTextField(
                            value = destinationLocationText,
                            onValueChange = { onDestinationChange(it) },
                            label = { Text("Destination") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged {
                                    isDestinationFocused = it.isFocused
                                    isPickupFocused = false

                                    onFieldFocus(if (it.isFocused) "destination" else null)
                                },
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null,
                                    tint = Color.Black
                                )
                            },
                            trailingIcon = {
                                if (pickupLocationText.isNotEmpty() && isDestinationFocused) {
                                    IconButton(onClick = { onDestinationChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear text",
                                            tint = Color.Black
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Black,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Suggestion list
                    if (suggestions.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight().padding(bottom = 30.dp)
                        ) {
                            items(suggestions) { suggestion ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSuggestionClick(suggestion) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Fancy trailing icon
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF0F0F0)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Place,
                                                contentDescription = "Location",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(15.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = suggestion.getPrimaryText(null).toString(),
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Black
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = suggestion.getSecondaryText(null).toString(),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color.Black
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Divider
                                            Divider(
                                                color = Color(0xFFE0E0E0),
                                                thickness = 0.6.dp,
                                                modifier = Modifier.padding(end = 10.dp)
                                            )
                                        }

                                    }

                                }
                            }
                        }

                    }

                }

                FareUiState.LOADING -> {
                    Spacer(modifier = Modifier.height(100.dp))
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                FareUiState.RESULT -> {

                    val cornerRadius = 6.dp // You can adjust this value

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(cornerRadius)) // Clip the content
                            .border(
                                border = BorderStroke(1.dp, Color.Black),
                                shape = RoundedCornerShape(cornerRadius) // Apply rounded border
                            )
                    ) {

                        Row (
                            modifier = Modifier.height(90.dp)
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.fast_car), // replace with your image resource
                                contentDescription = "Your image",
                                modifier = Modifier
                                    .padding(start = 12.dp, top = 12.dp, bottom = 12.dp, )
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Column(
                                modifier = Modifier.fillMaxHeight().padding(8.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.SpaceEvenly

                            ) {

                                Text(
                                    text = "$${"%.2f".format(fareEstimate?.totalFare ?: 0.0)}",
                                    style = TextStyle(
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    ),
                                )

                                Text(
                                    text = "Driver is $minutesAway mins away",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black
                                    ),
                                )

                                Text(
                                    text = "Mid-sized car",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black
                                    ),
                                )

                            }


                        }

                    }
                }
            }
        }


        if(suggestions.isEmpty()) {
            Button(
                onClick = onEstimateFareClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),

                // enabled = fareEstimate != null // Disable if fare not ready
            ) {
                Text("Book my ride", color = Color.White)
            }
        }
    }
}
