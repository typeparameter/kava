package dev.reifiedbeans.kava

import com.google.inject.Injector
import kotlin.reflect.KClass

/**
 * Returns the appropriate instance for the given injection type.
 *
 * This is an extension function on Guice's [Injector] that improves compatibility with Kotlin.
 * Rather than passing a Java [Class] (using Class::class.java syntax),
 * one can instead use the Kotlin [KClass] directly (using Class::class syntax).
 */
fun <T : Any> Injector.getInstance(type: KClass<T>): T = this.getInstance(type.java)
