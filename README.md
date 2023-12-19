KonigDecimal
============

[![Gradle Build Status](https://github.com/konigsoftware/konig-decimal/actions/workflows/build.yaml/badge.svg?query=branch=main)](https://github.com/konigsoftware/konig-decimal/actions/workflows/build.yaml?query=branch%3Amain)

[![konig-kontext](https://img.shields.io/maven-central/v/com.konigsoftware/konig-decimal.svg?label=konig-decimal)](https://central.sonatype.com/search?q=com.konigsoftware%3Akonig-decimal&smo=true)

Allows safe and developer friendly arithmetic in idiomatic Kotlin, built on top of [BigDecimal](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html). 
Prevents accidental precision loss during arithmetic operations by enforcing type safety on limited precision arithmetic.

Installation
------------

### Gradle:

<details open>
<summary>Kotlin</summary>
<br>
Add the following to your `build.gradle.kts`:

```kotlin
implementation("com.konigsoftware:konig-decimal:1.0.0")
```
</details>

<details>
<summary>Groovy</summary>
<br>
Add the following to your `build.gradle`:

```groovy
implementation 'com.konigsoftware:konig-decimal:1.0.0'
```
</details>

Usage
-----

The [`KonigDecimal`](https://github.com/konigsoftware/konig-decimal/blob/main/lib/src/main/kotlin/com/konigsoftware/decimal/KonigDecimal.kt) class is the main class that represents arbitrary precision decimal values. It supports
various constructors for creating instances from different types such as String, Int, and Double:

```kotlin
val value1 = KonigDecimal("10.123")
val value2 = KonigDecimal(5)
val value3 = KonigDecimal(1.24325)

val result = value1 + value2 + value3
println(result) // Output: 16.36625
```

There is also a convenience method for constructing a `KonigDecimal` from a `Long` given a certain [`LongUnit`](https://github.com/konigsoftware/konig-decimal/blob/main/lib/src/main/kotlin/com/konigsoftware/decimal/LongUnit.kt):

```kotlin
val value1 = KonigDecimal.fromLong(10145L, LongCentis) // equivalent to 101.45
val value2 = KonigDecimal("1.23")

val result = value1 + value2
println(result) // Output: 102.68
```

This method can be helpful when interacting with applications that represent floating point numbers as integers with 
some scale attached. A common example is an application that represents US Dollar amounts in cents rather than the actual
dollar amount (ie: 10145 = $101.45). 

This library comes with several pre-existing `LongUnit`'s, but you can easily add your own units when needed. See [here](https://github.com/konigsoftware/konig-decimal?tab=readme-ov-file#custom-longunit) for 
an example custom `LongUnit`.

### Rounding

An arbitrary precision `KonigDecimal` can be rounded to a fixed precision `FixedKonigDecimal` with a given scale. The scale
is represented by the [`KonigDecimalScale`](https://github.com/konigsoftware/konig-decimal/blob/main/lib/src/main/kotlin/com/konigsoftware/decimal/KonigDecimalScale.kt) class
which comes with several predefined scales like Nanos, Micros, and Centis. Again you can easily add your own custom scale 
by following the [example here](https://github.com/konigsoftware/konig-decimal?tab=readme-ov-file#custom-konigdecimalscale).

```kotlin
val arbitraryPrecision = KonigDecimal("1.012345678909876543690")

val roundedCentis = arbitraryPrecision.roundToScale(Centis) // Result: FixedKonigDecimal<Centis>("1.01")
val roundedNanos = arbitraryPrecision.roundToScale(Nanos)   // Result: FixedKonigDecimal<Nanos>("1.012345679")
val roundedMicros = arbitraryPrecision.roundToScale(Micros) // Result: FixedKonigDecimal<Micros>("1.012346")
```

Arithmetic operations between two `FixedKonigDecimal`'s with different scales will not be allowed by the type system. This 
prevents accidental operations between two fixed precision numbers with different scales that could result in a loss of precision.
The developer must explicitly round two numbers to the same fixed precision before they can perform arithmetic on the numbers.

```kotlin
val value1 = KonigDecimal("1.012492414").roundToScale(Centis)
val value2 = KonigDecimal("39.29490358234").roundToScale(Micros)

val result = value1 * value2 // <-- !Results in compiler error!
```

A `FixedKonigDecimal` can be converted into a `Long` using the `toLong` method. Again, this can be helpful when interacting with applications that represent floating point numbers as integers with
some scale attached.

```kotlin
val arbitraryPrecisionAmount = KonigDecimal("12.981240")
val amountCents = arbitraryPrecisionAmount.roundToScale(Centis).toLong(LongCentis)

println(amountCents) // Output: 1298

// You can optionally use a convenience method to produce the same result

val amountCents2 = arbitraryPrecisionAmount.roundToCentisAsLongCentis()

println(amountCents) // Output: 1298
```

### Custom LongUnit

To add a custom [`LongUnit`](https://github.com/konigsoftware/konig-decimal/blob/main/lib/src/main/kotlin/com/konigsoftware/decimal/LongUnit.kt) simply
implement the `LongUnit` interface. See example below:

```kotlin
// 1 CustomUnit = 0.0000001

object CustomUnit : LongUnit {
    override val oneInLongUnit = KonigDecimal(10_000_000)
}
```

### Custom KonigDecimalScale

To add a custom [`KonigDecimalScale`](https://github.com/konigsoftware/konig-decimal/blob/main/lib/src/main/kotlin/com/konigsoftware/decimal/KonigDecimalScale.kt)
simply implement the `KonigDecimalScale` interface. See example below:

```kotlin
// MyCustomScale has a scale of 7
// Scale is defined as the total number of digits to the right of the decimal point

object MyCustomScale : KonigDecimalScale { 
    override val scale = 7 
    override val roundingMode = RoundingMode.HALF_EVEN
}
```
