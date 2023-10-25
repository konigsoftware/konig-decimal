package com.konigsoftware.decimal

import com.konigsoftware.decimal.KonigDecimalScale.Attos
import com.konigsoftware.decimal.KonigDecimalScale.Centis
import com.konigsoftware.decimal.KonigDecimalScale.Micros
import com.konigsoftware.decimal.KonigDecimalScale.Nanos
import com.konigsoftware.decimal.KonigDecimalScale.Octos
import com.konigsoftware.decimal.LongUnit.LongCentis
import com.konigsoftware.decimal.LongUnit.LongMicros
import com.konigsoftware.decimal.LongUnit.LongNanos
import com.konigsoftware.decimal.LongUnit.LongOctos
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class KonigDecimalTest {
    @Test
    fun `KonigDecimal div non-terminating decimals get truncated`() {
        assertEquals(
            "0.3333333333333333333333333333333333",
            (KonigDecimal(1) / KonigDecimal(3)).toPlainString()
        )

        assertEquals(
            "0.6666666666666666666666666666666667",
            (KonigDecimal(2) / KonigDecimal(3)).toPlainString()

        )
    }

    @Test
    fun `KonigDecimal div terminating decimals do not get truncated`() {
        assertEquals(
            "0.000000000000000000000000000000000000000000000000000000001",
            (
                    KonigDecimal(1) /
                            KonigDecimal("1000000000000000000000000000000000000000000000000000000000")
                    ).toPlainString()
        )
    }

    @Test
    fun `roundToScale tests`() {
        assertEquals("1.012345678909876544", KonigDecimal("1.012345678909876543690").roundToScale(Attos).toPlainString())
        assertEquals("1.012345679", KonigDecimal("1.01234567890").roundToScale(Nanos).toPlainString())
        assertEquals("1.01234568", KonigDecimal("1.01234567890").roundToScale(Octos).toPlainString())
        assertEquals("1.012346", KonigDecimal("1.01234567890").roundToScale(Micros).toPlainString())
        assertEquals("1.01", KonigDecimal("1.01234567890").roundToScale(Centis).toPlainString())
    }

    @Test
    fun `toLong tests`() {
        assertThrows<ArithmeticException>("Unable to convert 1.012345678909876543690 to LongNanos as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).toLong(LongNanos)
        }
        assertThrows<ArithmeticException>("Unable to convert 1.012345678909876543690 to LongOctos as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).toLong(LongOctos)
        }
        assertThrows<ArithmeticException>("Unable to convert 1.012345678909876543690 to LongMicros as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).toLong(LongMicros)
        }
        assertThrows<ArithmeticException>("Unable to convert 1.012345678909876543690 to LongCentis as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).toLong(LongCentis)
        }

        assertEquals(1012345679L, KonigDecimal("1.01234567890").roundToScale(Nanos).toLong(LongNanos))
        assertThrows<ArithmeticException>("Unable to convert 1.01234567890 to LongOctos as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.01234567890").roundToScale(Nanos).toLong(LongOctos)
        }
        assertThrows<ArithmeticException>("Unable to convert 1.01234567890 to LongMicros as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.01234567890").roundToScale(Nanos).toLong(LongMicros)
        }
        assertThrows<ArithmeticException>("Unable to convert 1.01234567890 to LongCentis as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.01234567890").roundToScale(Nanos).toLong(LongCentis)
        }

        assertEquals(101234568L, KonigDecimal("1.01234567890").roundToScale(Octos).toLong(LongOctos))
        assertEquals(1012345680L, KonigDecimal("1.01234567890").roundToScale(Octos).toLong(LongNanos))
        assertThrows<ArithmeticException>("Unable to convert 1.01234567890 to LongMicros as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.01234567890").roundToScale(Octos).toLong(LongMicros)
        }
        assertThrows<ArithmeticException>("Unable to convert 1.01234567890 to LongCentis as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            KonigDecimal("1.01234567890").roundToScale(Octos).toLong(LongCentis)
        }

        assertEquals(1012346L, KonigDecimal("1.01234567890").roundToScale(Micros).toLong(LongMicros))
        assertEquals(1012346000L, KonigDecimal("1.01234567890").roundToScale(Micros).toLong(LongNanos))
        assertThrows<ArithmeticException>("Unable to convert 1.01234567890 to LongCentis as precision will be lost. Please convert to the proper decimal precision before converting to a long") {
            assertEquals(1012346L, KonigDecimal("1.01234567890").roundToScale(Micros).toLong(LongCentis))
        }

        assertEquals(101L, KonigDecimal("1.01234567890").roundToScale(Centis).toLong(LongCentis))
        assertEquals(1010000L, KonigDecimal("1.01234567890").roundToScale(Centis).toLong(LongMicros))
        assertEquals(1010000000L, KonigDecimal("1.01234567890").roundToScale(Centis).toLong(LongNanos))
    }

    @Test
    fun `Given two FixedKonigDecimal's, when adding or subtracting them, then the precision is the same`() {
        val attos1 = KonigDecimal("1.012345678909876543690").roundToScale(Attos)
        val attos2 = KonigDecimal("9.876543210328314953821").roundToScale(Attos)

        assertEquals(KonigDecimal("10.888888889238191498").roundToScale(Attos), attos1 + attos2)
        assertEquals(KonigDecimal("8.86419753141843841").roundToScale(Attos), attos2 - attos1)

        val nanos1 = KonigDecimal("1.01234567890").roundToScale(Nanos)
        val nanos2 = KonigDecimal("9.876543210").roundToScale(Nanos)

        assertEquals(KonigDecimal("10.888888889").roundToScale(Nanos), nanos1 + nanos2)
        assertEquals(KonigDecimal("8.864197531").roundToScale(Nanos), nanos2 - nanos1)

        val octos1 = KonigDecimal("1.01234567890").roundToScale(Octos)
        val octos2 = KonigDecimal("9.876543210").roundToScale(Octos)

        assertEquals(KonigDecimal("10.88888889").roundToScale(Octos), octos1 + octos2)
        assertEquals(KonigDecimal("8.86419753").roundToScale(Octos), octos2 - octos1)

        val micros1 = KonigDecimal("1.01234567890").roundToScale(Micros)
        val micros2 = KonigDecimal("9.876543210").roundToScale(Micros)

        assertEquals(KonigDecimal("10.888889").roundToScale(Micros), micros1 + micros2)
        assertEquals(KonigDecimal("8.864197").roundToScale(Micros), micros2 - micros1)

        val centis1 = KonigDecimal("1.01234567890").roundToScale(Centis)
        val centis2 = KonigDecimal("9.876543210").roundToScale(Centis)

        assertEquals(KonigDecimal("10.89").roundToScale(Centis), centis1 + centis2)
        assertEquals(KonigDecimal("8.87").roundToScale(Centis), centis2 - centis1)
    }

    @Test
    fun `Given two FixedKonigDecimal's, when multiplying or dividing them, then the precision is arbitrary`() {
        val attos1 = KonigDecimal("1.012345678909876543690").roundToScale(Attos)
        val attos2 = KonigDecimal("9.876543210328314953821").roundToScale(Attos)

        assertEquals(KonigDecimal("9.998475841542549607584861127689038976"), attos1 * attos2)
        assertEquals(KonigDecimal("0.1024999999849364564487077849384633"), attos1 / attos2)

        val nanos1 = KonigDecimal("1.01234567890").roundToScale(Nanos)
        val nanos2 = KonigDecimal("9.876543210").roundToScale(Nanos)

        assertEquals(KonigDecimal("9.998475842100289590"), nanos1 * nanos2)
        assertEquals(KonigDecimal("0.1024999999974687500000316406249996"), nanos1 / nanos2)

        val octos1 = KonigDecimal("1.01234567890").roundToScale(Octos)
        val octos2 = KonigDecimal("9.876543210").roundToScale(Octos)

        assertEquals(KonigDecimal("9.9984758519768328"), octos1 * octos2)
        assertEquals(KonigDecimal("0.1025000000987187499987660156250154"), octos1 / octos2)

        val micros1 = KonigDecimal("1.01234567890").roundToScale(Micros)
        val micros2 = KonigDecimal("9.876543210").roundToScale(Micros)

        assertEquals(KonigDecimal("9.998478799878"), micros1 * micros2)
        assertEquals(KonigDecimal("0.1025000346781257369101719093411531"), micros1 / micros2)

        val centis1 = KonigDecimal("1.01234567890").roundToScale(Centis)
        val centis2 = KonigDecimal("9.876543210").roundToScale(Centis)

        assertEquals(KonigDecimal("9.9788"), centis1 * centis2)
        assertEquals(KonigDecimal("0.1022267206477732793522267206477733"), centis1 / centis2)
    }

    @Test
    fun `FixedKonigDecimal roundToNewScale`() {
        assertEquals(
            KonigDecimal("1.012345678909876544").roundToScale(Attos),
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToScale(Attos)
        )
        assertEquals(
            KonigDecimal("1.012345679").roundToScale(Nanos),
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToScale(Nanos)
        )
        assertEquals(
            KonigDecimal("1.01234568").roundToScale(Octos),
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToScale(Octos)
        )
        assertEquals(
            KonigDecimal("1.012346").roundToScale(Micros),
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToScale(Micros)
        )
        assertEquals(
            KonigDecimal("1.01").roundToScale(Centis),
            KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToScale(Centis)
        )

        assertThrows<IllegalStateException>("Cannot round Nanos to new scale: Attos") {
            KonigDecimal("1.01234567890").roundToScale(
                Nanos
            ).roundToScale(Attos)
        }
        assertEquals(
            KonigDecimal("1.012345679").roundToScale(Nanos),
            KonigDecimal("1.01234567890").roundToScale(Nanos).roundToScale(Nanos)
        )
        assertEquals(
            KonigDecimal("1.01234568").roundToScale(Octos),
            KonigDecimal("1.01234567890").roundToScale(Nanos).roundToScale(Octos)
        )
        assertEquals(
            KonigDecimal("1.012346").roundToScale(Micros),
            KonigDecimal("1.01234567890").roundToScale(Nanos).roundToScale(Micros)
        )
        assertEquals(
            KonigDecimal("1.01").roundToScale(Centis),
            KonigDecimal("1.01234567890").roundToScale(Nanos).roundToScale(Centis)
        )

        assertThrows<IllegalStateException>("Cannot round Octos to new scale: Attos") {
            KonigDecimal("1.01234567890").roundToScale(
                Octos
            ).roundToScale(Attos)
        }
        assertThrows<IllegalStateException>("Cannot round Octos to new scale: Nanos") {
            KonigDecimal("1.01234567890").roundToScale(
                Octos
            ).roundToScale(Nanos)
        }
        assertEquals(
            KonigDecimal("1.01234568").roundToScale(Octos),
            KonigDecimal("1.01234567890").roundToScale(Octos).roundToScale(Octos)
        )
        assertEquals(
            KonigDecimal("1.012346").roundToScale(Micros),
            KonigDecimal("1.01234567890").roundToScale(Octos).roundToScale(Micros)
        )
        assertEquals(
            KonigDecimal("1.01").roundToScale(Centis),
            KonigDecimal("1.01234567890").roundToScale(Octos).roundToScale(Centis)
        )

        assertThrows<IllegalStateException>("Cannot round Micros to new scale: Attos") {
            KonigDecimal("1.01234567890").roundToScale(
                Micros
            ).roundToScale(Attos)
        }
        assertThrows<IllegalStateException>("Cannot round Micros to new scale: Nanos") {
            KonigDecimal("1.01234567890").roundToScale(
                Micros
            ).roundToScale(Nanos)
        }
        assertThrows<IllegalStateException>("Cannot round Micros to new scale: Octos") {
            KonigDecimal("1.01234567890").roundToScale(
                Micros
            ).roundToScale(Octos)
        }
        assertEquals(
            KonigDecimal("1.012346").roundToScale(Micros),
            KonigDecimal("1.01234567890").roundToScale(Micros).roundToScale(Micros)
        )
        assertEquals(
            KonigDecimal("1.01").roundToScale(Centis),
            KonigDecimal("1.01234567890").roundToScale(Micros).roundToScale(Centis)
        )

        assertThrows<IllegalStateException>("Cannot round Centis to new scale: Attos") {
            KonigDecimal("1.01234567890").roundToScale(
                Centis
            ).roundToScale(Attos)
        }
        assertThrows<IllegalStateException>("Cannot round Centis to new scale: Nanos") {
            KonigDecimal("1.01234567890").roundToScale(
                Centis
            ).roundToScale(Nanos)
        }
        assertThrows<IllegalStateException>("Cannot round Centis to new scale: Octos") {
            KonigDecimal("1.01234567890").roundToScale(
                Centis
            ).roundToScale(Octos)
        }
        assertThrows<IllegalStateException>("Cannot round Centis to new scale: Micros") {
            KonigDecimal("1.01234567890").roundToScale(
                Centis
            ).roundToScale(Micros)
        }
        assertEquals(
            KonigDecimal("1.01").roundToScale(Centis),
            KonigDecimal("1.01234567890").roundToScale(Centis).roundToScale(Centis)
        )
    }

    @Test
    fun `FixedKonigDecimal equals`() {
        assert(KonigDecimal("1.12").roundToScale(Centis) == KonigDecimal("1.120000").roundToScale(Centis))
        assertFalse(KonigDecimal("1.12").roundToScale(Centis) == KonigDecimal("1.120000").roundToScale(Micros))
        assertFalse(KonigDecimal("1.123").roundToScale(Nanos) == KonigDecimal("12.123").roundToScale(Nanos))
        assertFalse(KonigDecimal("1.123").roundToScale(Centis) == KonigDecimal("12.123").roundToScale(Nanos))
    }

    @Test
    fun `FixedKonigDecimal equalsArbitraryScale`() {
        assert(
            KonigDecimal("1.12").roundToScale(Centis).equalsIgnoreScale(KonigDecimal("1.120000").roundToScale(Centis))
        )
        assert(
            KonigDecimal("1.12").roundToScale(Centis).equalsIgnoreScale(KonigDecimal("1.120000").roundToScale(Micros))
        )
        assertFalse(
            KonigDecimal("11.123").roundToScale(Centis).equalsIgnoreScale(KonigDecimal("12.123").roundToScale(Nanos))
        )
    }

    @Test
    fun `Given a high precision amount with a last precision number greater than or equal to 5, when rounding to attos scale, then the result is truncated and rounded up`() {
        val highPrecisionAmount = KonigDecimal("1.742160278745644597803135888501742")

        assertEquals("1.742160278745644598", highPrecisionAmount.roundToScale(Attos).toPlainString())
    }

    @Test
    fun `Given a high precision amount with a last precision number less than 5, when rounding to attos scale, then the result is truncated and rounded down`() {
        val highPrecisionAmount = KonigDecimal("0.12390112444290420939012")

        assertEquals("0.123901124442904209", highPrecisionAmount.roundToScale(Attos).toPlainString())
    }

    @Test
    fun `Given a low precision amount, when truncating to attos scale, then the result does not loose any precision`() {
        val lowPrecisionAmount = KonigDecimal("12.23415")

        assertEquals("12.234150000000000000", lowPrecisionAmount.roundToScale(Attos).toPlainString())
    }

    // // Nanos Tests
    @Test
    fun nanosConversion() {
        // 1 Gwei = 1 nano-ETH
        assertEquals(1, KonigDecimal("0.000000001").roundToNanosAsLongNanos())

        // 1 unit = 10^9 nano-units
        assertEquals(10.0.pow(9).toLong(), KonigDecimal(1).roundToNanosAsLongNanos())
        // 10 units = 10 billion nano-units
        assertEquals(10_000_000_000, KonigDecimal(10).roundToNanosAsLongNanos())

        // Reverse conversion
        assertEquals("1", KonigDecimal.fromLong(1_000_000_000, LongNanos).toPlainString())
        assertEquals("10", KonigDecimal.fromLong(10_000_000_000, LongNanos).toPlainString())

        // Convert to nanos and back again
        assertEquals("10", KonigDecimal.fromLong(KonigDecimal(10).roundToNanosAsLongNanos(), LongNanos).toPlainString())
    }

    @Test
    fun `nanos conversion works for fractional amounts`() {
        assertEquals("0.000000001", KonigDecimal.fromLong(1, LongNanos).toPlainString())
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
        assertEquals(KonigDecimal("120.1312124402124014102901124092104909124").roundToNanosAsLongNanos(), 120131212440L)
    }

    @Test
    fun `Given a high precision amount with a last precision number greater than or equal to 5, when rounding to nanos scale, then the result is truncated and rounded up`() {
        val highPrecisionAmount = KonigDecimal("1.742160278745644599303135888501742")

        assertEquals("1.742160279", highPrecisionAmount.roundToScale(Nanos).toPlainString())
    }

    @Test
    fun `Given a high precision amount with a last precision number less than 5, when rounding to nanos scale, then the result is truncated and rounded down`() {
        val highPrecisionAmount = KonigDecimal("0.12390112444290420939012")

        assertEquals("0.123901124", highPrecisionAmount.roundToScale(Nanos).toPlainString())
    }

    @Test
    fun `Given a low precision amount, when truncating to nanos scale, then the result does not loose any precision`() {
        val lowPrecisionAmount = KonigDecimal("12.23415")

        assertEquals("12.234150000", lowPrecisionAmount.roundToScale(Nanos).toPlainString())
    }

    // // Octos Tests
    @Test
    fun octosConversion() {
        // 1 Gwei = 1 octo-ETH
        assertEquals(1, KonigDecimal("0.00000001").roundToOctosAsLongOctos())

        // 1 unit = 10^8 octo-units
        assertEquals(10.0.pow(8).toLong(), KonigDecimal(1).roundToOctosAsLongOctos())
        // 10 units = 1 billion octo-units
        assertEquals(1_000_000_000, KonigDecimal(10).roundToOctosAsLongOctos())

        // Reverse conversion
        assertEquals("1", KonigDecimal.fromLong(100_000_000, LongOctos).toPlainString())
        assertEquals("10", KonigDecimal.fromLong(1_000_000_000, LongOctos).toPlainString())

        // Convert to octos and back again
        assertEquals("10", KonigDecimal.fromLong(KonigDecimal(10).roundToOctosAsLongOctos(), LongOctos).toPlainString())
    }

    @Test
    fun `octos conversion works for fractional amounts`() {
        assertEquals("0.00000001", KonigDecimal.fromLong(1, LongOctos).toPlainString())
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

    // // Micros Tests
    @Test
    fun microsConversion() {
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

    // Centi Tests
    @Test
    fun centisConversion() {
        // 1 Cent  = 1
        assertEquals(1, KonigDecimal("0.01").roundToCentisAsLongCentis())
        // 100 units = 1 dollar
        assertEquals(100, KonigDecimal(1).roundToCentisAsLongCentis())

        // 1,000 units = 10 dollars
        assertEquals(1000, KonigDecimal(10).roundToCentisAsLongCentis())

        assertEquals("0.01", KonigDecimal.fromLong(1, LongCentis).toPlainString())
        assertEquals("12.34", KonigDecimal.fromLong(1234, LongCentis).toPlainString())
        assertEquals(1000, KonigDecimal.fromLong(1000, LongCentis).roundToScale(Centis).toLong(LongCentis))
        assertEquals(10000000000, KonigDecimal.fromLong(1000, LongCentis).roundToScale(Centis).toLong(LongNanos))
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
