package com.konigsoftware.decimal

import com.konigsoftware.decimal.KonigDecimalScale.Nanos
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LongUnitTest {

    object CustomLong : LongUnit {
        override val oneInLongUnit = KonigDecimal(10_000)
    }
    @Test
    fun `Give custom LongUnit, when converting a KonigDecimal to a long of the custom unit, then the long is correct`() {
        val noUnitAmount = KonigDecimal("123.123")

        assertEquals(1231230L, noUnitAmount.roundToScale(Nanos).toLong(CustomLong))
    }

    @Test
    fun `Give custom LongUnit, when converting a long of the custom unit to a KonigDecimal, then the KonigDecimal is correct`() {
        assertEquals(KonigDecimal("123.123"), KonigDecimal.fromLong(1231230, CustomLong))
    }
}