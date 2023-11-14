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
import java.lang.IllegalStateException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FixedKonigDecimalTest {
    // Addition
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
    fun `roundToScale tests`() {
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
    fun `FixedKonigDecimal equalsIgnoreScale`() {
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
}