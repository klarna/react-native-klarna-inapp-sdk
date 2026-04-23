package com.klarna.mobile.sdk.reactnative.common.util

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object ParserUtil {
    val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
        }
    }

    fun <T> toJson(
        serializer: SerializationStrategy<T>,
        src: T,
    ): String {
        val res = json.encodeToString(serializer, src)
        return res
    }

    fun parse(json: String): JsonElement = this.json.parseToJsonElement(json)
}
