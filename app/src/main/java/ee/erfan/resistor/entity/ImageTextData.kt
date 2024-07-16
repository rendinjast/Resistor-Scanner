/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:49 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/15/24, 8:24 PM
 *
 */

package ee.erfan.resistor.entity

import android.graphics.Rect
import androidx.compose.ui.geometry.Size


data class ImageTextData(
    val id: Int,
    val imageSize: Size,
    val rect: Rect,
    val text: String,
    val size: Int,
    val blocNumber: Int,
    val lineNumber: Int,
    val confidence: Float,
)