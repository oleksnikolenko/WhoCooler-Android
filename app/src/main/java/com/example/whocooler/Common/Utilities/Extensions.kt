package com.example.whocooler.Common.Utilities

import android.view.View
import kotlin.math.roundToInt

fun View.dip(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()
fun View.dip(value: Float): Int = (value * resources.displayMetrics.density).roundToInt()