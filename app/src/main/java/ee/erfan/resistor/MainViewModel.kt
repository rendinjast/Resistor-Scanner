/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:51 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/16/24, 6:39 PM
 *
 */

package ee.erfan.resistor


import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ee.erfan.resistor.entity.ImageTextData
import ee.erfan.resistor.entity.ScannedResistor
import ee.erfan.resistor.models.ColorBandDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = getApplication<Application>()
        .getSharedPreferences("main_prefs", Context.MODE_PRIVATE)

    var bmv by mutableStateOf<String?>(null)

    var foundColors by mutableStateOf<ImageTextData?>(null)

    var priceTagContour by mutableStateOf<Rect?>(null)

    var foundValue by mutableStateOf<ImageTextData?>(null)

    var isScanning by mutableStateOf(false)

    var lastScanResult by mutableStateOf<Pair<Double, String?>?>(null)

    var cameraFocusPoint by mutableStateOf<Offset?>(null)

    var isFlashOn by mutableStateOf(false)

    var isScanButtonExtended by mutableStateOf(false)


    var showMultiplierPicker by mutableStateOf(false)
    var multiplierValue by mutableStateOf(1)

    private var scannedResistor = mutableStateListOf<ScannedResistor>()

    var clearScannedTagsConfirmDialog by mutableStateOf(false)

    fun addResistor(resistor: ScannedResistor) {
        scannedResistor.add(0, resistor)
        scannedResistor.mapIndexed { index, scannedProduct -> scannedProduct.index = index }
        val gson = Gson()
        val jsonData = gson.toJson(scannedResistor)
        sharedPreferences.edit().putString("SAVED_RESISTORS", jsonData).apply()
    }

    fun getResistors() {
        scannedResistor.clear()
        val gson = Gson()
        val listType = object : TypeToken<ArrayList<ScannedResistor>>() {}.type
        val scannedProducts = sharedPreferences.getString("SAVED_RESISTORS", "[]")
        val listObjects = gson.fromJson<ArrayList<ScannedResistor>>(scannedProducts, listType)
        scannedResistor.addAll(listObjects.sortedBy { it.index })
    }

    fun clearResistors() {
        scannedResistor.clear()
        sharedPreferences.edit().putString("SAVED_RESISTORS", "[]").apply()
    }

    private val colorBandDetector = ColorBandDetector()
    private var _detectedBands = mutableStateOf<List<Int>>(emptyList())
    val detectedBands: State<List<Int>> = _detectedBands

    fun processImageProxy(imageProxy: ImageProxy) {
        viewModelScope.launch(Dispatchers.Default) {
            val bitmap = imageProxy.toBitmap()
            colorBandDetector.resetBuckets()


            for (x in 0 until bitmap.width) {
                for (y in 0 until bitmap.height) {
                    val pixel = bitmap.getPixel(x, y)

                    val rgb = ColorBandDetector.RGB(
                        pixel.red,
                        pixel.green,
                        pixel.blue
                    )
                    colorBandDetector.placeOnBuckets(rgb, x, y, true)
                }
            }

            val bands = colorBandDetector.findColorBands()

            val counts = mutableMapOf<Int, Int>()
            for (band in bands) {
                counts[band] = counts.getOrDefault(band, 0) + 1
            }
            if (bands.size == 3 && counts[0] != 3) {

                _detectedBands.value = bands
            } else {
                _detectedBands.value = emptyList()
            }

            imageProxy.close()
        }
    }

    fun ImageProxy.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}