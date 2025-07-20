package com.newagedavid.myride.presentation.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.newagedavid.myride.data.local.entity.RideHistory
import java.text.DateFormat
import java.util.Date

@Composable
fun RideHistoryCard(ride: RideHistory) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column (modifier = Modifier.padding(16.dp)) {
            Text("Driver: ${ride.driverName}", style = MaterialTheme.typography.titleMedium, color = Color.Black)
            AnnotatedText("Car: ", ride.car)
            AnnotatedText("Plate: ", ride.plateNumber)
            Spacer(modifier = Modifier.height(5.dp))
            AnnotatedText("Fare: ", "$${"%.2f".format(ride.fare)}")
            AnnotatedText("Timestamp: ",  "${DateFormat.getDateTimeInstance().format(Date(ride.timestamp))}")

        }
    }
}

@Composable
fun AnnotatedText(label: String, text: String, textColor: Color = Color.Black){
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(label)
            }
            append(text)
        },
        color = textColor
    )
}
