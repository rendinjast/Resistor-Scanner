/*
 * *
 *  * Created by Erfan Khadivar (hi@erfan.ee) on 7/16/24, 6:49 PM
 *  * Copyright (c) 2024 . All rights reserved.
 *  * Last modified 7/16/24, 6:34 PM
 *
 */

package ee.erfan.resistor.entity

import com.google.gson.annotations.SerializedName

data class ScannedResistor(
    @SerializedName("index")
    var index: Int? = null,
    @SerializedName("colors")
    val colors: List<Int>?,
    @SerializedName("value")
    val value: String,
)