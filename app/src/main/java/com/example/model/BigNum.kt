package com.example.model

import java.util.Locale
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow

object BigNum {
    private val SUFFIXES = arrayOf(
        "", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No",
        "Dc", "Ud", "Dd", "Td", "QaD", "QiD", "SxD", "SpD", "OcD", "NoD",
        "Vg", "UVg", "DVg", "TVg", "QaVg", "QiVg", "SxVg", "SpVg", "OcVg", "NoVg"
    )

    fun format(value: Double, scientific: Boolean = false): String {
        if (value.isNaN() || value.isInfinite()) return "∞"
        if (value < 0.0) return "-" + format(-value, scientific)
        if (value < 1000.0) {
            return if (value == floor(value)) {
                String.format(Locale.US, "%.0f", value)
            } else {
                String.format(Locale.US, "%.1f", value)
            }
        }

        if (scientific) {
            val exp = floor(log10(value)).toInt()
            val mantissa = value / 10.0.pow(exp.toDouble())
            return String.format(Locale.US, "%.2fe%d", mantissa, exp)
        }

        val tier = (floor(log10(value)) / 3).toInt()
        if (tier < SUFFIXES.size) {
            val scaled = value / 10.0.pow((tier * 3).toDouble())
            return String.format(Locale.US, "%.2f %s", scaled, SUFFIXES[tier])
        }

        val exp = floor(log10(value)).toInt()
        val mantissa = value / 10.0.pow(exp.toDouble())
        return String.format(Locale.US, "%.2fe%d", mantissa, exp)
    }

    /**
     * Calculates the price for buying [count] levels starting from [currentLevel].
     * Formula: baseCost * r^currentLevel * (r^count - 1) / (r - 1)
     */
    fun calculateTotalCost(
        baseCost: Double,
        multiplier: Double,
        currentLevel: Int,
        count: Int
    ): Double {
        if (count <= 0) return 0.0
        val startingCost = baseCost * multiplier.pow(currentLevel.toDouble())
        if (multiplier == 1.0) return startingCost * count
        return startingCost * (multiplier.pow(count.toDouble()) - 1.0) / (multiplier - 1.0)
    }

    /**
     * Calculates maximum levels that can be bought with [availableCurrency].
     */
    fun calculateMaxAffordable(
        baseCost: Double,
        multiplier: Double,
        currentLevel: Int,
        availableCurrency: Double
    ): Int {
        if (availableCurrency <= 0.0) return 0
        val startingCost = baseCost * multiplier.pow(currentLevel.toDouble())
        if (availableCurrency < startingCost) return 0
        if (multiplier == 1.0) return (availableCurrency / startingCost).toInt()

        val ratio = 1.0 + (availableCurrency * (multiplier - 1.0)) / startingCost
        if (ratio <= 0.0) return 0
        val maxCount = floor(ln(ratio) / ln(multiplier)).toInt()
        return maxCount.coerceAtLeast(0)
    }
}
