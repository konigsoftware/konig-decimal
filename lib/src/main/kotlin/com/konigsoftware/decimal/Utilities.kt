package com.konigsoftware.decimal

import java.math.BigDecimal

/**
 * Helper function to convert a Java [BigDecimal] to a [KonigDecimal]
 */
fun BigDecimal.toKonigDecimal() = KonigDecimal(this.toPlainString())

/**
 * Returns the maximum KonigDecimal between [a] and [b]
 */
fun max(a: KonigDecimal, b: KonigDecimal): KonigDecimal = if (a >= b) a else b

/**
 * Returns the minimum KonigDecimal between [a] and [b]
 */
fun min(a: KonigDecimal, b: KonigDecimal): KonigDecimal = if (a <= b) a else b
