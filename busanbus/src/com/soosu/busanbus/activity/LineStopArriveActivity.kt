package com.soosu.busanbus.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kmshack.BusanBus.R
import com.soosu.busanbus.presentation.viewmodel.LineStopArriveViewModel

class LineStopArriveActivity : AppCompatActivity() {

    private lateinit var viewModel: LineStopArriveViewModel
    private lateinit var arrivalTimeText: TextView
    private lateinit var remainingStopsText: TextView
    private lateinit var busNumberText: TextView

    private var nosun: String = ""
    private var stopId: String = ""
    private var stopName: String = ""
    private var ord: String = ""
    private var updown: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.busarrive)

        initViews()
        extractIntentExtras()
        setupViewModel()
        setupObservers()

        viewModel.loadArrivalInfo(stopId, nosun, ord)
    }

    private fun initViews() {
        arrivalTimeText = findViewById(R.id.busarrive_text4)
        remainingStopsText = findViewById(R.id.busarrive_text6)
    }

    private fun extractIntentExtras() {
        intent?.let {
            nosun = it.getStringExtra("NOSUN") ?: ""
            stopId = it.getStringExtra("UNIQUEID") ?: ""
            stopName = it.getStringExtra("BUSSTOPNAME") ?: ""
            ord = it.getStringExtra("ORD") ?: ""
            updown = it.getStringExtra("UPDOWN") ?: ""
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[LineStopArriveViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.arrivalInfo.observe(this) { info ->
            updateArrivalUI(info)
        }

        viewModel.errorMessage.observe(this) { error ->
            arrivalTimeText.text = error ?: getString(R.string.no_arrival_info)
        }
    }

    private fun updateArrivalUI(info: ArrivalInfo?) {
        if (info == null) {
            arrivalTimeText.text = getString(R.string.no_arrival_info)
            remainingStopsText.text = getString(R.string.no_arrival_info)
            return
        }

        // Fix: Use %s format specifier for String values, not %d
        // The arrival time and remaining stops are String values parsed from HTML
        arrivalTimeText.text = getString(
            R.string.arrival_info_format,
            info.arrivalMinutes,
            info.remainingStops,
            info.busNumber
        )

        if (info.secondBusArrivalMinutes != null) {
            remainingStopsText.text = getString(
                R.string.arrival_info_format,
                info.secondBusArrivalMinutes,
                info.secondBusRemainingStops,
                info.secondBusNumber
            )
        } else {
            remainingStopsText.text = getString(R.string.no_arrival_info)
        }
    }

    fun reload() {
        viewModel.loadArrivalInfo(stopId, nosun, ord)
    }
}

data class ArrivalInfo(
    val arrivalMinutes: String,
    val remainingStops: String,
    val busNumber: String,
    val secondBusArrivalMinutes: String? = null,
    val secondBusRemainingStops: String? = null,
    val secondBusNumber: String? = null
)
