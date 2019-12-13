package com.utsman.kemana.base

import android.content.Context
import android.util.TypedValue
import java.text.NumberFormat
import java.util.Formatter
import kotlin.math.floor
import kotlin.math.roundToInt

fun Context.dp(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).roundToInt()
}

fun Context.dpFloat(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).roundToInt().toFloat()
}

fun Double.calculatePricing(): String {
    val pricePerKm = 2500
    logi("origin meter --> ${this.toInt()}")
    val bloatedNumber = (this * pricePerKm) / 1000

    logi("bloated number --> $bloatedNumber")
    val formatter = Formatter().format("%,d", bloatedNumber.toInt())
    val doubleFormat = formatter.toString().replace(",", ".").toDouble()

    val originPrice = floor(doubleFormat) * 1000

    val finalPrice = Formatter().format("%,d", originPrice.toInt())
    return "Rp. $finalPrice".replace(",", ".")
}

fun Double.calculateDistanceKm(): String {
    val nf = NumberFormat.getInstance()
    nf.maximumFractionDigits = 1

    val bloatedNumber = (this) / 1000
    val result = nf.format(bloatedNumber).toDouble()
    return "$result Km"
}