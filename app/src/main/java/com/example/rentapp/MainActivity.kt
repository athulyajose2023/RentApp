package com.example.rentapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

object DeepLinkHelper {
    private const val AFFILIATE_ID = "your_affiliate_id"
    private const val ENCODER = "27_1"

    fun getCheapflightsCarRentalUrl(
        clickId: String,
        locationId: String,
        experimentId: String,
        iataOrigin: String,
        iataDestination: String,
        departureDate: String,
        returnDate: String,
        numberOfAdults: Int
    ): String {
        return "https://www.cheapflights.com/in?a=$AFFILIATE_ID&encoder=$ENCODER" +
                "&enc_cid=$clickId&enc_lid=$locationId&enc_eid=$experimentId&enc_pid=deeplinks" +
                "&url=/s/horizon/cars/search/brands/cheapflights/Redirect?url=book-car-hire/$iataOrigin-$iataDestination/$departureDate/$returnDate/${numberOfAdults}adults/"
    }

    fun openCheapflightsCarRental(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}

class CarRentalViewModel : ViewModel() {
    private val _pickupLocation = MutableStateFlow("")
    val pickupLocation: StateFlow<String> = _pickupLocation

    private val _dropoffLocation = MutableStateFlow("")
    val dropoffLocation: StateFlow<String> = _dropoffLocation

    @RequiresApi(Build.VERSION_CODES.O)
    private val _pickupDate = MutableStateFlow(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val pickupDate: StateFlow<LocalDate> = _pickupDate

    @RequiresApi(Build.VERSION_CODES.O)
    private val _dropoffDate = MutableStateFlow(LocalDate.now().plusDays(1))
    @RequiresApi(Build.VERSION_CODES.O)
    val dropoffDate: StateFlow<LocalDate> = _dropoffDate

    fun updatePickupLocation(location: String) {
        _pickupLocation.value = location
    }

    fun updateDropoffLocation(location: String) {
        _dropoffLocation.value = location
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePickupDate(date: LocalDate) {
        _pickupDate.value = date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDropoffDate(date: LocalDate) {
        _dropoffDate.value = date
    }
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarRentalBookingScreen { url ->
                DeepLinkHelper.openCheapflightsCarRental(this, url)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CarRentalBookingScreen(viewModel: CarRentalViewModel = viewModel(), onSearch: (String) -> Unit) {
    val pickupLocation by viewModel.pickupLocation.collectAsState()
    val dropoffLocation by viewModel.dropoffLocation.collectAsState()
    val pickupDate by viewModel.pickupDate.collectAsState()
    val dropoffDate by viewModel.dropoffDate.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val context = LocalContext.current

    fun showDatePickerDialog(onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = pickupLocation,
            onValueChange = { viewModel.updatePickupLocation(it) },
            label = { Text("Pickup Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dropoffLocation,
            onValueChange = { viewModel.updateDropoffLocation(it) },
            label = { Text("Drop-off Location (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { showDatePickerDialog { viewModel.updatePickupDate(it) } }) {
            Text("Select Pickup Date: ${pickupDate.format(dateFormatter)}")
        }
        Button(onClick = { showDatePickerDialog { viewModel.updateDropoffDate(it) } }) {
            Text("Select Drop-off Date: ${dropoffDate.format(dateFormatter)}")
        }

        Button(onClick = {
            if (pickupLocation.isNotBlank() && pickupDate.isBefore(dropoffDate)) {
                val url = DeepLinkHelper.getCheapflightsCarRentalUrl(
                    clickId = "123abc",
                    locationId = "home_page",
                    experimentId = "0",
                    iataOrigin = pickupLocation,
                    iataDestination = dropoffLocation.ifBlank { pickupLocation },
                    departureDate = pickupDate.format(dateFormatter),
                    returnDate = dropoffDate.format(dateFormatter),
                    numberOfAdults = 1
                )
                onSearch(url)
            }
        }) {
            Text("Search on Cheapflights")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun PreviewCarRentalBookingScreen() {
    CarRentalBookingScreen { }
}
