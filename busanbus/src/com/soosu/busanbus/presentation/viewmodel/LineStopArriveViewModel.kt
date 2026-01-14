package com.soosu.busanbus.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soosu.busanbus.activity.ArrivalInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class LineStopArriveViewModel : ViewModel() {

    private val _arrivalInfo = MutableLiveData<ArrivalInfo?>()
    val arrivalInfo: LiveData<ArrivalInfo?> = _arrivalInfo

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadArrivalInfo(stopId: String, nosun: String, ord: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val html = fetchArrivalHtml(stopId, nosun)
                val info = parseArrivalInfo(html)
                _arrivalInfo.value = info
            } catch (e: Exception) {
                _errorMessage.value = "도착정보 없음."
                _arrivalInfo.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchArrivalHtml(stopId: String, lineId: String): String {
        return withContext(Dispatchers.IO) {
            val url = "http://121.174.75.12/01/011.html.asp?bstop_id=$stopId&line_id=$lineId"
            URL(url).readText()
        }
    }

    private fun parseArrivalInfo(html: String): ArrivalInfo? {
        if (!html.contains("차량이")) {
            return null
        }

        // Parse first bus info - extract as String values
        val arrivalMinutes = extractValue(html, "분후", 2)
        val remainingStops = extractValue(html, "번째", 2)
        val busNumber = extractValue(html, "번 차량이", 4)

        // Parse second bus info if available
        var secondArrivalMinutes: String? = null
        var secondRemainingStops: String? = null
        var secondBusNumber: String? = null

        if (html.contains(">2.")) {
            secondArrivalMinutes = extractLastValue(html, "분후", 2)
            secondRemainingStops = extractLastValue(html, "번째", 2)
            secondBusNumber = extractLastValue(html, "번 차량이", 4)
        }

        return ArrivalInfo(
            arrivalMinutes = arrivalMinutes,
            remainingStops = remainingStops,
            busNumber = busNumber,
            secondBusArrivalMinutes = secondArrivalMinutes,
            secondBusRemainingStops = secondRemainingStops,
            secondBusNumber = secondBusNumber
        )
    }

    private fun extractValue(html: String, marker: String, length: Int): String {
        val index = html.indexOf(marker)
        return if (index >= length) {
            html.substring(index - length, index).replace(" ", "")
        } else {
            ""
        }
    }

    private fun extractLastValue(html: String, marker: String, length: Int): String {
        val index = html.lastIndexOf(marker)
        return if (index >= length) {
            html.substring(index - length, index).replace(" ", "")
        } else {
            ""
        }
    }
}
