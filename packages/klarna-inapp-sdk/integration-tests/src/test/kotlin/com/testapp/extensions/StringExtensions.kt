package com.testapp.extensions

fun String.removeWhitespace(): String {
    return replace("\\s".toRegex(), "")
}