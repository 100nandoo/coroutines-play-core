package com.example.coroutineplaycore

import com.google.android.play.core.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.await(): T {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            result
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                cont.resume(result, { exception })
            } else {
                cont.resumeWithException(e)
            }
        }

        addOnSuccessListener {
            val e = exception
            if (e == null) {
                cont.resume(result, { exception })
            } else {
                cont.resumeWithException(e)
            }
        }

        addOnFailureListener {
            cont.resumeWithException(it)
        }
    }
}
