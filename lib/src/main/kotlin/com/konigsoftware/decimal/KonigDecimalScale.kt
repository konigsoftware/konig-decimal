package com.konigsoftware.decimal

import java.math.BigDecimal
import java.math.RoundingMode
import java.math.RoundingMode.HALF_EVEN

/**
 * Represents the scale of a KonigDecimal. Scale is defined as the total number of digits to the right of the decimal point.
 * For example, the number `1.123456` has a scale of `6`, or "Micros" scale.
 *
 * To add your own custom scale, create an object that extends the `KonigDecimalScale` interface. This example has a scale
 * of 7:
 * ```kotlin
 * object MyCustomScale : KonigDecimalScale {
 *   override val scale = 7
 *   override val roundingMode = RoundingMode.HALF_EVEN
 * }
 * ```
 */
interface KonigDecimalScale {
    /**
     * The integer scale that this class represents. Scale is defined as the total number of digits to the right of the decimal point.
     * For example, the number `1.123456` would have a scale of `6`.
     */
    val scale: Int

    /**
     * The [RoundingMode] used when rounding a decimal to the scale defined by [scale]
     */
    val roundingMode: RoundingMode

    /**
     * Attos scale is 10^-18
     */
    object Attos : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 18
    }

    /**
     * Nanos scale is 10^-9
     */
    object Nanos : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 9
    }

    /**
     * Octos scale is 10^-8
     */
    object Octos : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 8
    }

    /**
     * Micros scale is 10^-6
     */
    object Micros : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 6
    }

    /**
     * Quatros scale is 10^-4
     */
    object Quatros : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 4
    }

    /**
     * Centis scale is 10^-2
     */
    object Centis : KonigDecimalScale {
        override val roundingMode = HALF_EVEN
        override val scale = 2
    }
}

internal fun KonigDecimalScale.round(amount: BigDecimal): KonigDecimal = KonigDecimal(amount.setScale(scale, roundingMode).toPlainString())
