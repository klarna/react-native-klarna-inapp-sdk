package com.testapp.extensions

inline fun <T> tryOptional(expression: () -> T): T? {
    return try {
        expression()
    } catch (ignore: Throwable) {
        null
    }
}