package com.konigsoftware.decimal

/**
 * Represents the unit of a value when in a Long format. Used to convert to/from a Long and KonigDecimal where the KonigDecimal
 * has no unit. For example, if you have a long: `123456` in the LongMicros unit, the equivalent `KonigDecimal` without a unit
 * would be KonigDecimal(0.123456).
 *
 * To add your own custom unit, create an object that extends the `LongUnit` interface. This example is for a unit where
 * 1 CustomUnit = 0.0000001:
 * ```kotlin
 * object CustomUnit : LongUnit {
 *   override val oneInLongUnit = KonigDecimal(10_000_000)
 * }
 * ```
 */
interface LongUnit {
    /**
     * The value in _this_ LongUnit that equals the unit-less value of 1. For example, if I have a LongUnit like nanos (10^-9)
     * that means 1 LongNanos = 0.000000001. So 1,000,000,000 LongNanos = 1. So the value for [oneInLongUnit] for nanos should be
     * 1,000,000,000.
     */
    val oneInLongUnit: KonigDecimal

    /**
     * LongNanos is 10^-9. ie: 1 LongNano = 0.000000001
     */
    object LongNanos : LongUnit {
        override val oneInLongUnit = KonigDecimal(1_000_000_000)
    }

    /**
     * LongOctos is 10^-8. ie: 1 LongOcto = 0.00000001
     */
    object LongOctos : LongUnit {
        override val oneInLongUnit = KonigDecimal(100_000_000)
    }

    /**
     * LongMicros is 10^-6. ie: 1 LongMicro = 0.000001
     */
    object LongMicros : LongUnit {
        override val oneInLongUnit = KonigDecimal(1_000_000)
    }

    /**
     * LongQuatros is 10^-4. ie: 1 LongQuatro = 0.0001
     */
    object LongQuatros : LongUnit {
        override val oneInLongUnit: KonigDecimal = KonigDecimal(1_000)
    }

    /**
     * LongCentis is 10^-2. ie: 1 LongCenti = 0.01
     */
    object LongCentis : LongUnit {
        override val oneInLongUnit = KonigDecimal(100)
    }
}

/**
 * Converts a KonigDecimal amount without a unit to the LongUnit specified as a KonigDecimal
 *
 * Example:
 *
 * ```kotlin
 * LongNanos.convert(KonigDecimal("1.12345")) // KonigDecimal("1123450000")
 * LongMicros.convert(KonigDecimal("1.12345")) // KonigDecimal("1123450")
 * LongCentis.convert(KonigDecimal("1.12345")) // KonigDecimal("112.345")
 * ```
 */
internal fun LongUnit.convert(amountNoUnit: KonigDecimal): KonigDecimal = amountNoUnit * oneInLongUnit

/**
 * Converts a long in the unit specified by the LongUnit child object to the amount without unit (10^0)
 */
internal fun LongUnit.convertFromLong(amountLong: Long): KonigDecimal = KonigDecimal(amountLong.toString()) / oneInLongUnit
