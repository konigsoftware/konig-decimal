package com.konigsoftware.decimal

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
import java.lang.Exception
import java.math.BigDecimal
import java.math.MathContext

class KonigDecimal private constructor(private val amount: BigDecimal) {
    /**
     * Construct an unlimited precision KonigDecimal from a [String] representation of a KonigDecimal
     *
     * @throws NumberFormatException if [amount] is not a valid representation of a KonigDecimal.
     */
    constructor(amount: String) : this(BigDecimal(amount, MathContext.UNLIMITED))
    /**
     * Construct an unlimited precision KonigDecimal from an [Int] representation of a KonigDecimal with a scale of 0.
     */
    constructor(amount: Int) : this(BigDecimal(amount, MathContext.UNLIMITED))

    /**
     * Construct an unlimited precision KonigDecimal from a [Double] representation of a KonigDecimal
     */
    constructor(amount: Double) : this(BigDecimal(amount.toString(), MathContext.UNLIMITED))

    companion object {
        /**
         * Convert the [amountLong] in [longUnit] to an unlimited precision KonigDecimal representation without a unit.
         * This function is useful if you have a value from an external data source that is specified in a certain unit,
         * but you would like to keep the value without a unit in your application logic, or perform safe arithmetic on this value.
         *
         * Example:
         *
         * ```kotlin
         * // External data source represents USD value in LongCentis (1000 == $10.00)
         * KonigDecimal.fromLong(1000L, LongCentis) // KonigDecimal("10.00")
         *
         * KonigDecimal.fromLong(19380120000, LongNanos) // KonigDecimal("19.38012")
         * ```
         */
        fun fromLong(
            amountLong: Long,
            longUnit: LongUnit
        ): KonigDecimal = longUnit.convertFromLong(amountLong)

        // Constant values, used for convenience
        val ZERO = KonigDecimal(0)
        val ONE = KonigDecimal(1)
        val TEN = KonigDecimal(10)
    }

    /**
     * Rounds the arbitrary precision KonigDecimal to the specified scale.
     * Rounding follows the [java.math.RoundingMode] defined in the provided [KonigDecimalScale].
     *
     * Example:
     *
     * ```kotlin
     * val arbitraryPrecision = KonigDecimal("1.012345678909876543690")
     *
     * arbitraryPrecision.roundToScale(Attos)  // 1.012345678909876544
     * arbitraryPrecision.roundToScale(Nanos) // 1.012345679
     * arbitraryPrecision.roundToScale(Octos) // 1.01234568
     * arbitraryPrecision.roundToScale(Micros) // 1.012346
     * arbitraryPrecision.roundToScale(Centis) // 1.01
     * ```
     */
    fun <Scale : KonigDecimalScale> roundToScale(scale: Scale) =
        FixedKonigDecimal(scale.round(this.amount), scale)

    // Arithmetic functions

    operator fun plus(other: KonigDecimal): KonigDecimal = this.amount.plus(other.amount).toKonigDecimal()

    operator fun minus(other: KonigDecimal): KonigDecimal = this.amount.minus(other.amount).toKonigDecimal()

    operator fun times(other: KonigDecimal): KonigDecimal {
        return this.amount.times(other.amount).toKonigDecimal()
    }

    /**
     * Returns a KonigDecimal whose value is (this / [other]).
     *
     * If the quotient has a non-terminating decimal expansion (unlimited precision representation of the quotient is impossible)
     * the quotient will be represented with a precision setting matching the precision of the IEEE 754-2019 decimal128 format,
     * 34 digits, and a rounding mode of [java.math.RoundingMode.HALF_EVEN].
     */
    operator fun div(other: KonigDecimal): KonigDecimal {
        return try {
            this.amount.divide(other.amount, MathContext.UNLIMITED).toKonigDecimal()
        } catch (e: ArithmeticException) {
            // TODO: Do we log a warning here since unlimited precision is impossible? Do we throw an error? Do we add parameters to customize the outcome here?
            this.amount.divide(other.amount, MathContext.DECIMAL128).toKonigDecimal()
        }
    }

    // Comparison functions

    operator fun compareTo(other: KonigDecimal): Int {
        return this.amount.compareTo(other.amount)
    }

    override operator fun equals(other: Any?) =
        if (other is KonigDecimal) this.amount == other.amount else false

    override fun hashCode(): Int {
        return amount.hashCode()
    }

    // Utility functions

    override fun toString() = amount.toString()
    fun toPlainString(): String = this.amount.toPlainString()

    fun toDouble(): Double = this.amount.toDouble()


    /**
     * Converts arbitrary precision KonigDecimal to a Long by rounding the arbitrary precision to Nanos scale
     * and converting to a Long in LongNanos units.
     */
    fun roundToNanosAsLongNanos(): Long = this.roundToScale(Nanos).toLong(LongNanos)

    /**
     * Converts arbitrary precision KonigDecimal to a Long by rounding the arbitrary precision to Octos scale
     * and converting to a Long in LongOctos units.
     */
    fun roundToOctosAsLongOctos(): Long = this.roundToScale(Octos).toLong(LongOctos)

    /**
     * Converts arbitrary precision KonigDecimal to a Long by rounding the arbitrary precision to Micros scale
     * and converting to a Long in LongMicros units.
     */
    fun roundToMicrosAsLongMicros(): Long = this.roundToScale(Micros).toLong(LongMicros)

    /**
     * Converts arbitrary precision KonigDecimal to a Long by rounding the arbitrary precision to Quatros scale
     * and converting to a Long in LongQuatros units.
     */
    fun roundToQuatrosAsLongQuatros(): Long = this.roundToScale(Quatros).toLong(LongQuatros)

    /**
     * Converts arbitrary precision KonigDecimal to a Long by rounding the arbitrary precision to Centis scale
     * and converting to a Long in LongCentis units.
     */
    fun roundToCentisAsLongCentis(): Long = this.roundToScale(Centis).toLong(LongCentis)

    inner class FixedKonigDecimal<Scale : KonigDecimalScale>(
        private val amount: KonigDecimal,
        private val scale: Scale
    ) {
        operator fun plus(other: FixedKonigDecimal<Scale>): FixedKonigDecimal<Scale> =
            this.amount.plus(other.amount)
                .roundToScale(this.scale) // roundToScale should be a no-op as adding two fixed precision numbers produces the same fixed precision

        operator fun plus(other: KonigDecimal): KonigDecimal = this.amount.plus(other)

        operator fun minus(other: FixedKonigDecimal<Scale>): FixedKonigDecimal<Scale> =
            this.amount.minus(other.amount)
                .roundToScale(this.scale) // roundToScale should be a no-op as subtracting two fixed precision numbers produces the same fixed precision

        operator fun minus(other: KonigDecimal): KonigDecimal = this.amount.minus(other)

        operator fun div(other: FixedKonigDecimal<Scale>): KonigDecimal =
            this.amount.div(other.amount) // Dividing two fixed precision numbers does not always produce a quotient with the same fixed precision, so an arbitrary precision is returned here

        operator fun div(other: KonigDecimal): KonigDecimal = this.amount.div(other)

        operator fun times(other: FixedKonigDecimal<Scale>): KonigDecimal =
            this.amount.times(other.amount) // Multiplying two fixed precision numbers does not always produce a product with the same fixed precision, so an arbitrary precision is returned here

        operator fun times(other: KonigDecimal): KonigDecimal = this.amount.times(other)

        operator fun compareTo(other: FixedKonigDecimal<Scale>): Int {
            return this.amount.compareTo(other.amount)
        }

        override fun hashCode(): Int {
            return amount.hashCode()
        }

        fun toPlainString(): String = this.amount.toPlainString()

        /**
         * Converts FixedKonigDecimal to a Long.
         *
         * @param longUnit The unit the returned long value is in.
         * @return A long representation of the FixedKonigDecimal in the unit specified by longUnit
         *
         *
         * Example:
         * ```kotlin
         * KonigDecimal("1.01234567890").roundToScale(Nanos).toLong(LongNanos) // 1012345679L
         * KonigDecimal("1.01234567890").roundToScale(Octos).toLong(LongOctos) // 101234568L
         * KonigDecimal("1.01234567890").roundToScale(Micros).toLong(LongMicros) // 1012346L
         * KonigDecimal("1.01234567890").roundToScale(Centis).toLong(LongCentis) // 101L
         *
         * KonigDecimal("1.01234567890").roundToScale(Centis).toLong(Nanos) // 1010000000L
         * KonigDecimal("1.01234567890").roundToScale(Centis).toUint256(LongAttos) // 1010000000000000000
         *
         * KonigDecimal("1.01234567890").roundToScale(Nanos).toLong(LongMicros) // <- THROWS ERROR there is precision loss converting nanos scale to LongMicros. Would need to do KonigDecimal("1.01234567890").roundToScale(Micros).toLong(LongMicros) to obtain the equivalent of 1.01234567890 in LongMicros
         * KonigDecimal("1.01234567890").roundToScale(Micros).toLong(LongCentis) // <- THROWS ERROR there is precision loss converting micros scale to LongCentis. Would need to do KonigDecimal("1.01234567890").roundToScale(Centis).toLong(LongCentis) to obtain the equivalent of 1.01234567890 in LongCentis
         * ```
         */
        fun toLong(longUnit: LongUnit): Long {
            val roundedAmount =
                this.scale.round(amount.amount) // First round the amount to the scale specified on `this`
            val multipliedAmount =
                longUnit.convert(roundedAmount) // Then convert the rounded amount to the LongUnit specified

            // Convert the amount (in the specified longUnit) to a long ensuring no overflow, or precision loss
            return try {
                multipliedAmount.amount.longValueExact()
            } catch (e: Exception) {
                if (e.localizedMessage == "Overflow") {
                    throw ArithmeticException("Unable to convert ${this.amount.toPlainString()} to $longUnit due to overflow")
                }

                throw ArithmeticException("Unable to convert ${this.amount.toPlainString()} to $longUnit as precision will be lost. Please convert to the proper scale before converting to a long")
            }
        }

        /**
         * Rounds the already fixed precision FixedKonigDecimal to a further fixed precision FixedKonigDecimal.
         * Rounding follows the [half-even scale](https://docs.oracle.com/javase/7/docs/api/java/math/RoundingMode.html#HALF_EVEN)
         *
         * Example:
         *
         * ```kotlin
         * KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToNewScale(Attos) // FixedKonigDecimal<Attos>("1.012345678909876544") (no-op)
         * KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToNewScale(Nanos) // FixedKonigDecimal<Nanos>("1.012345679")
         * KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToNewScale(Octos) // FixedKonigDecimal<Octos>("1.01234568")
         * KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToNewScale(Micros) // FixedKonigDecimal<Micros>("1.012346")
         * KonigDecimal("1.012345678909876543690").roundToScale(Attos).roundToNewScale(Centis) // FixedKonigDecimal<Centis>("1.01")
         *
         * KonigDecimal("1.01234567890").roundToScale(Nanos).roundToNewScale(Attos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Nanos).roundToNewScale(Nanos) // FixedKonigDecimal<Nanos>("1.012345679") (no-op)
         * KonigDecimal("1.01234567890").roundToScale(Nanos).roundToNewScale(Octos) // FixedKonigDecimal<Octos>("1.01234568")
         * KonigDecimal("1.01234567890").roundToScale(Nanos).roundToNewScale(Micros) // FixedKonigDecimal<Micros>("1.012346")
         * KonigDecimal("1.01234567890").roundToScale(Nanos).roundToNewScale(Centis) // FixedKonigDecimal<Centis>("1.01")
         *
         * KonigDecimal("1.01234567890").roundToScale(Octos).roundToNewScale(Attos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Octos).roundToNewScale(Nanos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Octos).roundToNewScale(Octos) // FixedKonigDecimal<Octos>("1.01234568") (no-op)
         * KonigDecimal("1.01234567890").roundToScale(Octos).roundToNewScale(Micros) // FixedKonigDecimal<Micros>("1.012346")
         * KonigDecimal("1.01234567890").roundToScale(Octos).roundToNewScale(Centis) // FixedKonigDecimal<Centis>("1.01")
         *
         * KonigDecimal("1.01234567890").roundToScale(Micros).roundToNewScale(Attos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Micros).roundToNewScale(Nanos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Micros).roundToNewScale(Octos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Micros).roundToNewScale(Micros) // FixedKonigDecimal<Micros>("1.012346") (no-op)
         * KonigDecimal("1.01234567890").roundToScale(Micros).roundToNewScale(Centis) // FixedKonigDecimal<Centis>("1.01")
         *
         * KonigDecimal("1.01234567890").roundToScale(Centis).roundToNewScale(Attos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Centis).roundToNewScale(Nanos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Centis).roundToNewScale(Octos) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Centis).roundToNewScale(Micros) // <- THROWS ERROR. Cannot add precision to already fixed precision
         * KonigDecimal("1.01234567890").roundToScale(Centis).roundToNewScale(Centis) // FixedKonigDecimal<Centis>("1.01") (no-op)
         * ```
         */
        fun <NewScale : KonigDecimalScale> roundToScale(newScale: NewScale): FixedKonigDecimal<NewScale> =
            if (this.scale.scale >= newScale.scale) {
                FixedKonigDecimal(newScale.round(this.amount.amount), newScale)
            } else throw IllegalStateException("Cannot round ${this.scale::class.simpleName} to new scale: ${newScale::class.simpleName}. Existing scale is lower precision (${this.scale.scale}) than new scale (${newScale.scale})")

        /**
         * Compares two FixedKonigDecimal's ensuring that the scale on each FixedKonigDecimal is also equal.
         *
         * Example:
         *
         * ```kotlin
         * KonigDecimal("1.12").roundToScale(Centis) == KonigDecimal("1.120000").roundToScale(Centis) // true
         * KonigDecimal("1.12").roundToScale(Centis) == KonigDecimal("1.120000").roundToScale(Micros) // false
         * ```
         */
        override fun equals(other: Any?): Boolean =
            other is FixedKonigDecimal<*> && this::class.typeParameters[0] == other::class.typeParameters[0] && this.amount == other.amount

        /**
         * Compares two FixedKonigDecimal's without checking the scale.
         *
         * Example:
         *
         * ```kotlin
         * KonigDecimal("1.12").roundToScale(Centis).equalsIgnoreScale(KonigDecimal("1.120000").roundToScale(Centis)) // true
         * KonigDecimal("1.12").roundToScale(Centis).equalsIgnoreScale(KonigDecimal("1.120000").roundToScale(Micros)) // true
         * ```
         */
        fun equalsIgnoreScale(other: Any?): Boolean =
            other is FixedKonigDecimal<*> && this.amount.amount.compareTo(other.amount.amount) == 0
    }
}
