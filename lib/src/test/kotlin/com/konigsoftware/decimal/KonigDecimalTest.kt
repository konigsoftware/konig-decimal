package com.konigsoftware.decimal

import com.konigsoftware.decimal.KonigDecimalScale.Attos
import com.konigsoftware.decimal.KonigDecimalScale.Centis
import com.konigsoftware.decimal.KonigDecimalScale.Micros
import com.konigsoftware.decimal.KonigDecimalScale.Nanos
import com.konigsoftware.decimal.KonigDecimalScale.Octos
import com.konigsoftware.decimal.KonigDecimalScale.Quatros
import com.konigsoftware.decimal.LongUnit.LongCentis
import com.konigsoftware.decimal.LongUnit.LongMicros
import com.konigsoftware.decimal.LongUnit.LongNanos
import com.konigsoftware.decimal.LongUnit.LongOctos
import com.konigsoftware.decimal.LongUnit.LongQuatros
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.pow
import kotlin.test.assertEquals
import org.junit.jupiter.api.Nested

internal class KonigDecimalTest {
    object CustomLongUnit : LongUnit {
        // 10^-3
        override val oneInLongUnit = KonigDecimal(1_000)
    }

    @Test
    fun `fromLong tests`() {
        // Long Nanos
        assertEquals(KonigDecimal.fromLong(101992823098, LongNanos), KonigDecimal("101.992823098"))
        assertEquals("1", KonigDecimal.fromLong(1_000_000_000, LongNanos).toString())
        assertEquals("10", KonigDecimal.fromLong(10_000_000_000, LongNanos).toString())
        assertEquals("0.000000001", KonigDecimal.fromLong(1, LongNanos).toString())

        // Long Octos
        assertEquals(KonigDecimal.fromLong(1283801, LongOctos), KonigDecimal("0.01283801"))
        assertEquals("1", KonigDecimal.fromLong(100_000_000, LongOctos).toString())
        assertEquals("10", KonigDecimal.fromLong(1_000_000_000, LongOctos).toString())

        // Long Micros
        assertEquals(KonigDecimal.fromLong(1248012, LongMicros), KonigDecimal("1.248012"))

        // Long Quatros
        assertEquals(KonigDecimal.fromLong(120921, LongQuatros), KonigDecimal("12.0921"))

        // Long Centis
        assertEquals(KonigDecimal.fromLong(10199, LongCentis), KonigDecimal("101.99"))
        assertEquals("0.01", KonigDecimal.fromLong(1, LongCentis).toString())
        assertEquals("12.34", KonigDecimal.fromLong(1234, LongCentis).toString())
        assertEquals(1000, KonigDecimal.fromLong(1000, LongCentis).roundToScale(Centis).toLong(LongCentis))
        assertEquals(10000000000, KonigDecimal.fromLong(1000, LongCentis).roundToScale(Centis).toLong(LongNanos))

        // Custom Long Unit
        assertEquals(KonigDecimal.fromLong(1242, CustomLongUnit), KonigDecimal("1.242"))
    }

    @Test
    fun `roundToScale tests`() {
        // Attos scale
        assertEquals("1.012345678909876544", KonigDecimal("1.012345678909876543690").roundToScale(Attos).toString())
        assertEquals("12.234150000000000000", KonigDecimal("12.23415").roundToScale(Attos).toString())
        assertEquals("0.123901124442904209", KonigDecimal("0.12390112444290420939012").roundToScale(Attos).toString())
        assertEquals("1.742160278745644598", KonigDecimal("1.742160278745644597803135888501742").roundToScale(Attos).toString())

        // Nanos scale
        assertEquals("1.012345679", KonigDecimal("1.01234567890").roundToScale(Nanos).toString())

        // Octos scale
        assertEquals("1.01234568", KonigDecimal("1.01234567890").roundToScale(Octos).toString())

        // Micros scale
        assertEquals("1.012346", KonigDecimal("1.01234567890").roundToScale(Micros).toString())

        // Quatros scale
        assertEquals("1.0123", KonigDecimal("1.01234567890").roundToScale(Quatros).toString())

        // Centis scale
        assertEquals("1.01", KonigDecimal("1.01234567890").roundToScale(Centis).toString())
    }

    /* ARITHMETIC FUNCTIONS */

    // Addition

    @Test
    fun `Addition tests`() {
        assertEquals(KonigDecimal("532352.05012"), KonigDecimal("1.9210") + KonigDecimal("532350.12912"))
        assertEquals(KonigDecimal(8), KonigDecimal(5) + KonigDecimal(3))
        assertEquals(KonigDecimal("-4.39"), KonigDecimal("-12.59") + KonigDecimal("8.2"))
        assertEquals(KonigDecimal("5.05"), KonigDecimal("5.05") + KonigDecimal.ZERO)
    }

    // Subtraction

    @Test
    fun `Subtraction tests`() {
        assertEquals(KonigDecimal("707.21"), KonigDecimal("921.21") - KonigDecimal(214))
        assertEquals(KonigDecimal("-8.318"), KonigDecimal("192.212") - KonigDecimal("200.53"))
        assertEquals(KonigDecimal("-2822.11"), KonigDecimal("-2924.21") - KonigDecimal("-102.1"))
        assertEquals(KonigDecimal("5.05"), KonigDecimal("5.05") - KonigDecimal.ZERO)
    }

    // Multiplication

    @Test
    fun `multiplication tests`() {
        assertEquals(KonigDecimal("1106.460"), KonigDecimal("12.294") * KonigDecimal("90"))
        assertEquals(KonigDecimal("-1244.644"), KonigDecimal("102.02") * KonigDecimal("-12.2"))
        assertEquals(KonigDecimal("0.00"), KonigDecimal("102.02") * KonigDecimal.ZERO)
    }

    // Division

    @Test
    fun `division tests`() {
        assertEquals(KonigDecimal("0.1366"), KonigDecimal("12.294") / KonigDecimal("90"))
        assertEquals(
            KonigDecimal("-8.362295081967213114754098360655738"),
            KonigDecimal("102.02") / KonigDecimal("-12.2")
        )
        val error = assertThrows<ArithmeticException> {
            KonigDecimal("102.02") / KonigDecimal.ZERO
        }

        assertEquals("Division by zero", error.message)
    }

    @Test
    fun `Given two KonigDecimals, when dividing them and the quotient is a non-terminating decimal, then the quotient is truncated`() {
        assertEquals(
            "0.3333333333333333333333333333333333",
            (KonigDecimal(1) / KonigDecimal(3)).toString()
        )

        assertEquals(
            "0.6666666666666666666666666666666667",
            (KonigDecimal(2) / KonigDecimal(3)).toString()

        )
    }

    @Test
    fun `Given two KonigDecimals, when dividing them and the quotient is a terminating decimal, then the quotient is not truncated`() {
        assertEquals(
            "0.000000000000000000000000000000000000000000000000000000001",
            (
                    KonigDecimal(1) /
                            KonigDecimal("1000000000000000000000000000000000000000000000000000000000")
                    ).toString()
        )
    }

    /* COMPARISON TESTS */

    @Test
    fun `compareTo tests`() {
        assertEquals(-1, KonigDecimal(1).compareTo(KonigDecimal(2)))
        assertEquals(0, KonigDecimal(2).compareTo(KonigDecimal(2)))
        assertEquals(0, KonigDecimal("2.00").compareTo(KonigDecimal("2.0")))
        assertEquals(1, KonigDecimal(2).compareTo(KonigDecimal(1)))

        assert(KonigDecimal(1) < KonigDecimal(2))
        assert(KonigDecimal(1) <= KonigDecimal(2))
        assert(KonigDecimal(2) > KonigDecimal(1))
        assert(KonigDecimal(2) >= KonigDecimal(1))
    }

    @Test
    fun `equals tests`() {
        assert(KonigDecimal("1.0123") == KonigDecimal("1.0123"))
        assert(KonigDecimal("1.01231") != KonigDecimal("1.0123"))
        assert(KonigDecimal("2.0") != KonigDecimal("2.00"))
    }

    @Test
    fun `equalsIgnoreScale tests`() {
        assert(KonigDecimal("1.0123").equalsIgnoreScale(KonigDecimal("1.0123")))
        assert(!KonigDecimal("1.01231").equalsIgnoreScale(KonigDecimal("1.0123")))
        assert(KonigDecimal("2.0").equalsIgnoreScale(KonigDecimal("2.00")))
    }

    /* UTILITY TESTS */

    @Test
    fun `toString tests`() {
        assertEquals("1.902142", KonigDecimal("1.902142").toString())
        assertEquals("100219210921.902142", KonigDecimal("100219210921.902142").toString())
        assertEquals("-902190.81240", KonigDecimal("-902190.81240").toString())
    }

    @Test
    fun `toDouble tests`() {
        assertEquals(129.90124, KonigDecimal("129.90124").toDouble())
        assertEquals(-2015.9201, KonigDecimal("-2015.9201").toDouble())
    }

    @Nested
    inner class RoundToNanosAsLongNanosTest {
        @Test
        fun `simple conversions`() {
            // 1 Gwei = 1 nano-ETH
            assertEquals(1, KonigDecimal("0.000000001").roundToNanosAsLongNanos())

            // 1 unit = 10^9 nano-units
            assertEquals(10.0.pow(9).toLong(), KonigDecimal(1).roundToNanosAsLongNanos())
            // 10 units = 10 billion nano-units
            assertEquals(10_000_000_000, KonigDecimal(10).roundToNanosAsLongNanos())

            // Convert to nanos and back again
            assertEquals("10", KonigDecimal.fromLong(KonigDecimal(10).roundToNanosAsLongNanos(), LongNanos).toString())
        }

        @Test
        fun `nanos conversion works for fractional amounts`() {
            assertEquals("0.000000001", KonigDecimal.fromLong(1, LongNanos).toString())
            assertEquals(1, KonigDecimal("0.000000001").roundToNanosAsLongNanos())
        }

        @Test
        fun `numbers less than 1 nano are truncated`() {
            assertEquals(2, KonigDecimal("0.00000000210000000000000000432000000001").roundToNanosAsLongNanos())
            assertEquals(1, KonigDecimal("0.00000000100000000000000000000400000001").roundToNanosAsLongNanos())
        }

        @Test
        fun `Given a KonigDecimal that overflows a Long, when converting it to nanos, then an exception is thrown`() {
            assertThrows<ArithmeticException> {
                KonigDecimal("1000000000000000000000000000000000000000000000.0").roundToNanosAsLongNanos()
            }
        }

        @Test
        fun `Given a KonigDecimal that requires decimal truncation, when converting it to nanos, then the value just has less precision`() {
            assertEquals(
                KonigDecimal("123.12421412412414124321423412341512535235135124124325235253").roundToNanosAsLongNanos(),
                123124214124L
            )
        }

        @Test
        fun `Given a KonigDecimal that requires truncations and has a last precision number greater than or equal to 5, when converting it to nanos, then the value has less precision and is rounded up,`() {
            assertEquals(KonigDecimal("23.85019243259231230912049104").roundToNanosAsLongNanos(), 23850192433L)
        }

        @Test
        fun `Given a KonigDecimal that requires truncations and has a last precision number less than 5, when converting it to nanos, then the value has less precision and is rounded down,`() {
            assertEquals(
                KonigDecimal("120.1312124402124014102901124092104909124").roundToNanosAsLongNanos(),
                120131212440L
            )
        }

        @Test
        fun `Given a high precision amount with a last precision number greater than or equal to 5, when rounding to nanos scale, then the result is truncated and rounded up`() {
            val highPrecisionAmount = KonigDecimal("1.742160278745644599303135888501742")

            assertEquals("1.742160279", highPrecisionAmount.roundToScale(Nanos).toString())
        }

        @Test
        fun `Given a high precision amount with a last precision number less than 5, when rounding to nanos scale, then the result is truncated and rounded down`() {
            val highPrecisionAmount = KonigDecimal("0.12390112444290420939012")

            assertEquals("0.123901124", highPrecisionAmount.roundToScale(Nanos).toString())
        }

        @Test
        fun `Given a low precision amount, when truncating to nanos scale, then the result does not loose any precision`() {
            val lowPrecisionAmount = KonigDecimal("12.23415")

            assertEquals("12.234150000", lowPrecisionAmount.roundToScale(Nanos).toString())
        }
    }

    @Nested
    inner class RoundToOctosAsLongOctosTest {
        @Test
        fun `simple conversions`() {
            // 1 Gwei = 1 octo-ETH
            assertEquals(1, KonigDecimal("0.00000001").roundToOctosAsLongOctos())

            // 1 unit = 10^8 octo-units
            assertEquals(10.0.pow(8).toLong(), KonigDecimal(1).roundToOctosAsLongOctos())
            // 10 units = 1 billion octo-units
            assertEquals(1_000_000_000, KonigDecimal(10).roundToOctosAsLongOctos())

            // Convert to octos and back again
            assertEquals("10", KonigDecimal.fromLong(KonigDecimal(10).roundToOctosAsLongOctos(), LongOctos).toString())
        }

        @Test
        fun `octos conversion works for fractional amounts`() {
            assertEquals("0.00000001", KonigDecimal.fromLong(1, LongOctos).toString())
            assertEquals(1, KonigDecimal("0.00000001").roundToOctosAsLongOctos())
        }

        @Test
        fun `numbers less than 1 octos are truncated`() {
            assertEquals(2, KonigDecimal("0.0000000210000000000000000432000000001").roundToOctosAsLongOctos())
            assertEquals(1, KonigDecimal("0.0000000100000000000000000000400000001").roundToOctosAsLongOctos())
        }

        @Test
        fun `Given a KonigDecimal that overflows a Long, when converting it to octos, then an exception is thrown`() {
            assertThrows<ArithmeticException> {
                KonigDecimal("1000000000000000000000000000000000000000000000.0").roundToOctosAsLongOctos()
            }
        }

        @Test
        fun `Given a KonigDecimal that requires decimal truncation, when converting it to octos, then the value just has less precision`() {
            assertEquals(
                KonigDecimal("123.12421412412414124321423412341512535235135124124325235").roundToOctosAsLongOctos(),
                12312421412L
            )
        }

        @Test
        fun `Given a high precision amount with a last precision number greater than or equal to 5, when truncating to octos scale, then the result is truncated and rounded up`() {
            val highPrecisionAmount = KonigDecimal("1.745160278745644599303135888501742")
            val expectedTruncatedAmount = KonigDecimal("1.74516028").roundToScale(Octos)
            val truncatedToOctosScaleAmount = highPrecisionAmount.roundToScale(Octos)

            assertEquals(expectedTruncatedAmount, truncatedToOctosScaleAmount)
            assertEquals(expectedTruncatedAmount.toLong(LongOctos), truncatedToOctosScaleAmount.toLong(LongOctos))
            assertEquals(highPrecisionAmount.roundToOctosAsLongOctos(), truncatedToOctosScaleAmount.toLong(LongOctos))
        }

        @Test
        fun `Given a high precision amount with a last precision number less than 5, when truncating to octos scale, then the result is truncated and rounded down`() {
            val highPrecisionAmount = KonigDecimal("0.12390112444290420939012")
            val expectedTruncatedAmount = KonigDecimal("0.12390112").roundToScale(Octos)
            val truncatedToOctosScaleAmount = highPrecisionAmount.roundToScale(Octos)

            assertEquals(expectedTruncatedAmount, truncatedToOctosScaleAmount)
            assertEquals(expectedTruncatedAmount.toLong(LongOctos), truncatedToOctosScaleAmount.toLong(LongOctos))
            assertEquals(highPrecisionAmount.roundToOctosAsLongOctos(), truncatedToOctosScaleAmount.toLong(LongOctos))
        }

        @Test
        fun `Given a low precision amount, when truncating to octos scale, then the result does not loose any precision`() {
            val lowPrecisionAmount = KonigDecimal("12.2")
            val expectedTruncatedAmount = KonigDecimal("12.20000000").roundToScale(Octos)
            val truncatedToOctosScaleAmount = lowPrecisionAmount.roundToScale(Octos)

            assertEquals(expectedTruncatedAmount, truncatedToOctosScaleAmount)
            assertEquals(expectedTruncatedAmount.toLong(LongOctos), truncatedToOctosScaleAmount.toLong(LongOctos))
            assertEquals(lowPrecisionAmount.roundToOctosAsLongOctos(), truncatedToOctosScaleAmount.toLong(LongOctos))
        }
    }

    @Nested
    inner class RoundToMicrosAsLongMicrosTest {
        @Test
        fun `simple conversions`() {
            // 1,000 Gwei = 1 nano-ETH
            assertEquals(1, KonigDecimal("0.000001").roundToMicrosAsLongMicros())

            // 1 unit = 10^6 micro-units
            assertEquals(10.0.pow(6).toLong(), KonigDecimal(1).roundToMicrosAsLongMicros())
            // 10 units = 10 million micro-units
            assertEquals(10_000_000, KonigDecimal(10).roundToMicrosAsLongMicros())
        }

        @Test
        fun `micro conversion works for fractional amounts`() {
            assertEquals(1, KonigDecimal("0.000001").roundToMicrosAsLongMicros())
        }

        @Test
        fun `numbers less than 1 micros are truncated`() {
            assertEquals(2, KonigDecimal("0.00000210000000000000000432000000001").roundToMicrosAsLongMicros())
            assertEquals(1, KonigDecimal("0.00000100000000000000000000400000001").roundToMicrosAsLongMicros())
        }

        @Test
        fun `Given a KonigDecimal that overflows a Long, when converting it to micros, then an exception is thrown`() {
            assertThrows<ArithmeticException> {
                KonigDecimal("1000000000000000000000000000000000000000000.0").roundToMicrosAsLongMicros()
            }
        }

        @Test
        fun `Given a KonigDecimal that requires decimal truncation, when converting it to micros, then the value just has less precision`() {
            assertEquals(
                KonigDecimal("123.12421412412414124321423412341512535235135124124325235").roundToMicrosAsLongMicros(),
                123124214L
            )
        }

        @Test
        fun `Given a KonigDecimal that requires truncations and has a last precision number greater than or equal to 5, when converting it to micros, then the value has less precision and is rounded up,`() {
            assertEquals(KonigDecimal("23.85019253259231230912049104").roundToMicrosAsLongMicros(), 23850193L)
        }

        @Test
        fun `Given a KonigDecimal that requires truncations and has a last precision number less than 5, when converting it to micros, then the value has less precision and is rounded down,`() {
            assertEquals(KonigDecimal("120.1312124402124014102901124092104909124").roundToMicrosAsLongMicros(), 120131212L)
        }
    }

    @Nested
    inner class RoundToCentisAsLongCentisTest {
        @Test
        fun `simple conversions`() {
            // 1 Cent  = 1
            assertEquals(1, KonigDecimal("0.01").roundToCentisAsLongCentis())
            // 100 units = 1 dollar
            assertEquals(100, KonigDecimal(1).roundToCentisAsLongCentis())

            // 1,000 units = 10 dollars
            assertEquals(1000, KonigDecimal(10).roundToCentisAsLongCentis())
        }

        @Test
        fun `centis conversion works for fractional amounts`() {
            assertEquals(1023, KonigDecimal("10.2345654").roundToCentisAsLongCentis())
        }

        @Test
        fun `Given a KonigDecimal that overflows a Long, when converting it to centis, then an exception is thrown`() {
            assertThrows<ArithmeticException> {
                KonigDecimal("1000000000000000000000000000000000000000000.0").roundToCentisAsLongCentis()
            }
        }

        @Test
        fun `Given a KonigDecimal that requires truncations, rounding works`() {
            assertEquals(1234, KonigDecimal("12.335").roundToCentisAsLongCentis())
            assertEquals(1233, KonigDecimal("12.334").roundToCentisAsLongCentis())
        }

        @Test
        fun `Given a high precision amount with a last precision number greater than or equal to 5, when truncating to centis scale, then the result is truncated and rounded up`() {
            val highPrecisionAmount = KonigDecimal("1.745160278745644599303135888501742")
            val expectedTruncatedAmount = KonigDecimal("1.75").roundToScale(Centis)
            val truncatedToCentisScaleAmount = highPrecisionAmount.roundToScale(Centis)

            assertEquals(expectedTruncatedAmount, truncatedToCentisScaleAmount)
            assertEquals(expectedTruncatedAmount.toLong(LongCentis), truncatedToCentisScaleAmount.toLong(LongCentis))
            assertEquals(highPrecisionAmount.roundToCentisAsLongCentis(), truncatedToCentisScaleAmount.toLong(LongCentis))
        }

        @Test
        fun `Given a high precision amount with a last precision number less than 5, when truncating to centis scale, then the result is truncated and rounded down`() {
            val highPrecisionAmount = KonigDecimal("0.12390112444290420939012")
            val expectedTruncatedAmount = KonigDecimal("0.12").roundToScale(Centis)
            val truncatedToCentisScaleAmount = highPrecisionAmount.roundToScale(Centis)

            assertEquals(expectedTruncatedAmount, truncatedToCentisScaleAmount)
            assertEquals(expectedTruncatedAmount.toLong(LongCentis), truncatedToCentisScaleAmount.toLong(LongCentis))
            assertEquals(highPrecisionAmount.roundToCentisAsLongCentis(), truncatedToCentisScaleAmount.toLong(LongCentis))
        }

        @Test
        fun `Given a low precision amount, when truncating to centis scale, then the result does not loose any precision`() {
            val lowPrecisionAmount = KonigDecimal("12.2")
            val expectedTruncatedAmount = KonigDecimal("12.20").roundToScale(Centis)
            val truncatedToCentisScaleAmount = lowPrecisionAmount.roundToScale(Centis)

            assertEquals(expectedTruncatedAmount, truncatedToCentisScaleAmount)
            assertEquals(expectedTruncatedAmount.toLong(LongCentis), truncatedToCentisScaleAmount.toLong(LongCentis))
            assertEquals(lowPrecisionAmount.roundToCentisAsLongCentis(), truncatedToCentisScaleAmount.toLong(LongCentis))
        }
    }
}
