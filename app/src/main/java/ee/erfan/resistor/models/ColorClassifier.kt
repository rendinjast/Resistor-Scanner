/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:49 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/15/24, 4:15 PM
 *
 */

package ee.erfan.resistor.models

import android.graphics.Color

class ColorClassifier {
    fun classify(rgb: ColorBandDetector.RGB): Int {
        val hsv = FloatArray(3)
        Color.RGBToHSV(rgb.RED, rgb.GREEN, rgb.BLUE, hsv)
        val (hue, saturation, value) = hsv

        if (value < 0.12f) return 0

        if (saturation < 0.11f) {
            when {
                value in 0.4f..0.67f -> return 8
                value in 0.85f..1.0f -> return 9
            }
        }

        return when {
            hue in 0.0f..11.9f -> if (saturation <= 0.55f || value <= 0.55f) -1 else 2
            hue in 11.9f..16.0f -> when {
                saturation > 0.5f && value > 0.79f -> 3
                saturation > 0.5f && value in 0.55f..0.7f -> 2
                saturation <= 0.4f || value >= 0.4f -> -1
                else -> 1
            }

            hue in 16.0f..31.0f -> when {
                saturation > 0.65f && value > 0.65f -> 3
                saturation > 0.64f && value in 0.31f..0.4f -> 1
                else -> -1
            }

            hue in 32.0f..43.0f -> when {
                saturation > 0.64f && value > 0.63f -> 3
                saturation > 0.75f && value in 0.32f..0.44f -> 1
                else -> -1
            }

            hue in 47.0f..71.0f -> if (saturation <= 0.5f || value <= 0.5f) -1 else 4
            hue in 72.0f..180.0f -> if (saturation <= 0.2f || value <= 0.17f) -1 else 5
            hue in 180.0f..275.0f -> if (saturation <= 0.3f || value <= 0.25f) -1 else 6
            hue in 275.0f..315.0f -> if (saturation <= 0.3f || value <= 0.3f) -1 else 7
            hue in 325.0f..360.0f -> if (saturation <= 0.3f || value <= 0.3f) -1 else 2
            else -> -1
        }
    }

    fun getHue(rgb: ColorBandDetector.RGB): Int {
        val hsv = FloatArray(3)
        Color.RGBToHSV(rgb.RED, rgb.GREEN, rgb.BLUE, hsv)
        val (hue, saturation, value) = hsv

        if (value < 0.2f) return 0

        if (saturation < 0.2f) {
            return when {
                value in 0.0f..0.4f -> 0
                value in 0.4f..0.8f -> 8
                value in 0.8f..1.0f -> 9
                else -> -1
            }
        }

        return when {
            hue in 0.0f..24.9f -> 2
            hue in 24.9f..35.0f -> 3
            hue in 35.0f..49.0f -> 1
            hue in 49.0f..73.0f -> 4
            hue in 73.0f..180.0f -> 5
            hue in 180.0f..275.0f -> 6
            hue in 275.0f..325.0f -> 7
            hue in 325.0f..360.0f -> 2
            else -> -1
        }
    }
}