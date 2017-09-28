@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package test

import kotlin.internal.RequireLanguageVersion as RLV

@RLV("1.1", "message", DeprecationLevel.WARNING, 42)
class Klass

class Konstructor @RLV("1.1", "message", DeprecationLevel.WARNING, 42) constructor()

@RLV("1.1", "message", DeprecationLevel.WARNING, 42)
typealias Typealias = String

@RLV("1.1", "message", DeprecationLevel.WARNING, 42)
fun function() {}

@RLV("1.1", "message", DeprecationLevel.WARNING, 42)
val property = ""
