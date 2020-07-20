package com.whocooler.app.Common.Utilities

import android.app.Activity
import android.view.View
import kotlin.math.roundToInt

fun View.dip(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()
fun View.dip(value: Float): Int = (value * resources.displayMetrics.density).roundToInt()

fun Activity.dip(value: Int) : Int = (value * resources.displayMetrics.density).roundToInt()