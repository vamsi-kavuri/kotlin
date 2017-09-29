// WITH_RUNTIME
// WITH_COROUTINES
import helpers.*
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.*

class Controller {
    suspend fun suspendHere(): String = suspendCoroutineOrReturn { x ->
        x.resume("K")
        COROUTINE_SUSPENDED
    }

    suspend fun suspendWithArgument(v: String): String = suspendCoroutineOrReturn { x ->
        x.resume(v)
        COROUTINE_SUSPENDED
    }

    suspend fun suspendWithDouble(v: Double): Double = suspendCoroutineOrReturn { x ->
        x.resume(v)
        COROUTINE_SUSPENDED
    }
}

fun builder(c: suspend Controller.() -> Unit) {
    c.startCoroutine(Controller(), EmptyContinuation)
}

class B(val first: String, val second: String, val third: String) {
    override fun toString() = "$first$second$third"
}

fun box(): String {
    var result = "OK"

    builder {
        var count = 0
        while (true) {
            val local = B("O", suspendHere(), if (count >= 1) break else "")

            if (local.toString() != "OK") {
                result = "fail 1: $local"
                return@builder
            }

            count++
        }
    }

    return result
}
