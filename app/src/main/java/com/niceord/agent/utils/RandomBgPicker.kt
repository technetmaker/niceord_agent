package com.niceord.agent.utils

import com.niceord.agent.R

object RandomBgPicker {
    val bgDrawable = arrayOf(R.drawable.light_dark_green_shape,
        R.drawable.light_dark_orange_shape,
        R.drawable.light_dark_red_shape,
        R.drawable.light_dark_purple_shape,
        R.drawable.light_dark_yellow_shape,
        R.drawable.light_dark_blue_shape,
        R.drawable.light_dark_spark_shape)

    var currentDrawable = 0

    fun getBgDrawable():Int{
        currentDrawable = (currentDrawable + 1) % bgDrawable.size
        return bgDrawable[currentDrawable]
    }
}