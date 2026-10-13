package com.example

import com.example.model.BigNum
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBigNumFormatting() {
        assertEquals("0", BigNum.format(0.0))
        assertEquals("500", BigNum.format(500.0))
        assertEquals("1.50 K", BigNum.format(1500.0))
        assertEquals("2.45 M", BigNum.format(2450000.0))
        assertEquals("7.89 B", BigNum.format(7890000000.0))
        assertEquals("1.00 T", BigNum.format(1000000000000.0))
    }

    @Test
    fun testGeometricBulkCostCalculation() {
        // baseCost = 10, multiplier = 1.15, level = 0, count = 1
        val cost1 = BigNum.calculateTotalCost(10.0, 1.15, 0, 1)
        assertEquals(10.0, cost1, 0.001)

        // count = 10
        val cost10 = BigNum.calculateTotalCost(10.0, 1.15, 0, 10)
        assertTrue(cost10 > 100.0)

        // max affordable
        val maxAffordable = BigNum.calculateMaxAffordable(10.0, 1.15, 0, cost10)
        assertEquals(10, maxAffordable)
    }
}
