/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:48 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/16/24, 6:42 PM
 *
 */

package ee.erfan.resistor.models

import androidx.compose.ui.graphics.Color


class ColorBandDetector {
    var mBuckets: Array<Bucket> = Array(10) { Bucket(-1) }
    private val mColorClassifier = ColorClassifier()
    private var type: Type = Type.FourBands

    init {
        for (i in 0 until 10) {
            mBuckets[i] = Bucket(i)
        }
    }

    fun placeOnBuckets(rgb: RGB, xAxis: Int, yAxis: Int, includeGray: Boolean) {
        val classify = mColorClassifier.classify(rgb)
        if (classify != -1) {
            mBuckets[classify].add(xAxis, yAxis, includeGray)
        }
    }


    fun findColorBands(): List<Int> {
        val colorBands = ArrayList<Int>()
        mBuckets.forEach { it.clean() }

        val pixelCounts = ArrayList<Int>()
        val colorMap = HashMap<Int, Int>()
        val xAxisColorMap = HashMap<Int, Int>()
        val sortedXAxis = ArrayList<Int>()

        for (i in 0 until 10) {
            val size = mBuckets[i].size()
            for (j in 0 until size) {
                xAxisColorMap[mBuckets[i].mXAxis[j]] = mBuckets[i].mPixelCount[j]
                colorMap[mBuckets[i].mXAxis[j]] = i
                pixelCounts.add(mBuckets[i].mPixelCount[j])
                sortedXAxis.add(mBuckets[i].mXAxis[j])
            }
        }

        sortedXAxis.sortDescending()
        pixelCounts.sortDescending()

        when (type) {
            Type.FourBands -> {
                for (xAxis in sortedXAxis) {
                    if (xAxisColorMap[xAxis]!! > COUNT_THRESHOLD) {
                        for (i in 0 until 3) {
                            if (pixelCounts.size > i) {
                                val count = xAxisColorMap[xAxis]
                                if (count == pixelCounts[i]) {
                                    colorBands.add(colorMap[xAxis]!!)
                                }
                            }
                        }
                    }
                }
            }

            else -> {} // Handle other types if needed
        }

        return colorBands
    }

    fun setResistorType(numberOfBands: Int) {
        type = when (numberOfBands) {
            4 -> Type.FourBands
            5 -> Type.FiveBands
            6 -> Type.SixBands
            else -> type
        }
    }

    fun getFilledBucketsCount(): Int {
        return mBuckets.count { it.totalPixelCount() > 15 }
    }

    fun resetBuckets() {
        mBuckets.forEach { it.reset() }
        yAxisMAX = 0
        yAxisMIN = 0
        xAxisMAX = 0
        xAxisMIN = 0
    }

    enum class Type {
        FourBands, FiveBands, SixBands
    }

    data class RGB(var RED: Int, var GREEN: Int, var BLUE: Int)

    class Bucket(private val mColor: Int) {
        var mPixelCount: MutableList<Int> = ArrayList()
        var mXAxis: MutableList<Int> = ArrayList()

        fun size() = mPixelCount.size

        fun add(xAxis: Int, yAxis: Int, includeGray: Boolean) {
            if (mColor == 8 || mColor == 9) return

            val existingIndex =
                mXAxis.indexOfFirst { Math.abs(xAxis - it) <= COLOR_BAND_WIDTH_IN_PIXELS }
            if (existingIndex != -1) {
                mPixelCount[existingIndex]++
            } else {
                mPixelCount.add(1)
                mXAxis.add(xAxis)
            }
        }

        fun totalPixelCount() = mPixelCount.sum()

        fun reset() {
            mPixelCount.clear()
            mXAxis.clear()
        }

        fun clean() {
            val toRemove = mutableListOf<Int>()
            for (i in mXAxis.indices) {
                if (mPixelCount[i] < PIXEL_THRESHOLD) {
                    toRemove.add(i)
                }
            }
            toRemove.reversed().forEach { index ->
                mPixelCount.removeAt(index)
                mXAxis.removeAt(index)
            }
        }
    }

    companion object {
        const val BUCKET_SIZE = 10
        var COLOR_BAND_WIDTH_IN_PIXELS = 60
        var COUNT_THRESHOLD = 10
        var PIXEL_THRESHOLD = 5
        var yAxisMAX = 0
        var yAxisMIN = 0
        var xAxisMAX = Int.MIN_VALUE
        var xAxisMIN = Int.MAX_VALUE
        var colorNames: MutableList<String> = mutableListOf(
            "Black",
            "Brown",
            "Red",
            "Orange",
            "Yellow",
            "Green",
            "Blue",
            "Violet",
            "Gray",
            "White"
        )
        val allColors = arrayOf(
            Color(0xFF000000),
            Color(0xFF996633),
            Color(0xFFFF0000),
            Color(0xFFFF9900),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF0060FF),
            Color(0xFFFF00FF),
            Color(0xFF808080),
            Color(0xFFFFFFFF),
            Color(0xFFDAA520),
            Color(0xFFCCCCCC)
        )
        private val COLOR_CODES = arrayOf(
            RGB(0, 0, 0),
            RGB(153, 102, 51),
            RGB(255, 0, 0),
            RGB(255, 153, 0),
            RGB(255, 255, 0),
            RGB(0, 255, 0),
            RGB(0, 0, 255),
            RGB(255, 0, 255),
            RGB(204, 204, 204),
            RGB(255, 255, 255)
        )
    }
}