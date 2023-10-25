package com.konigsoftware.decimal

import com.konigsoftware.decimal.KonigDecimalScale.Nanos
import java.math.RoundingMode.HALF_EVEN
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class KonigDecimalScaleTest {
    object CustomScale : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 11
    }

    object CustomScale2 : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 6
    }

    @Test
    fun `Given custom scale, when rounding a KonigDecimal to the custom scale, the rounding is correct`() {
        val arbitraryPrecision = KonigDecimal("123.1271282748129482912847")

        assertEquals("123.12712827481", arbitraryPrecision.roundToScale(CustomScale).toPlainString())
    }

    @Test
    fun `Given custom scale, when rounding to a built in scale and then a lower precision custom scale, the rounding is correct`() {
        val arbitraryPrecision = KonigDecimal("123.1271282748129482912847")

        assertEquals("123.127128", arbitraryPrecision.roundToScale(Nanos).roundToScale(CustomScale2).toPlainString())
    }
}