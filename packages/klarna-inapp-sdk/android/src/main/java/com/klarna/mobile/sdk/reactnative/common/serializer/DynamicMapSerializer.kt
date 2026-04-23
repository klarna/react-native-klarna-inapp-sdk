package com.klarna.mobile.sdk.reactnative.common.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializerOrNull
import kotlin.reflect.full.memberProperties

object DynamicMapSerializer : KSerializer<Map<String, Any?>> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("EnhancedDynamicMap")

    override fun serialize(
        encoder: Encoder,
        value: Map<String, Any?>,
    ) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("Only JSON is supported")
        val jsonObject =
            buildJsonObject {
                value.forEach { (key, any) ->
                    put(key, serializeValue(any, jsonEncoder))
                }
            }
        jsonEncoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): Map<String, Any?> {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Only JSON is supported")
        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject
        return jsonObject.mapValues { (_, element) -> deserializeValue(element) }
    }

    private fun serializeValue(value: Any?, encoder: JsonEncoder): JsonElement =
        when (value) {
            null -> JsonNull
            is Boolean -> JsonPrimitive(value)
            is Byte -> JsonPrimitive(value)
            is Short -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            is Float -> JsonPrimitive(value)
            is Double -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Char -> JsonPrimitive(value.toString())
            is Enum<*> -> JsonPrimitive(value.toString())
            is Map<*, *> -> {
                buildJsonObject {
                    value.forEach { (k, v) ->
                        put(k.toString(), serializeValue(v, encoder))
                    }
                }
            }

            is Collection<*> -> {
                buildJsonArray {
                    value.forEach { add(serializeValue(it, encoder)) }
                }
            }

            is Array<*> -> {
                buildJsonArray {
                    value.forEach { add(serializeValue(it, encoder)) }
                }
            }

            else -> {
                serializerFor(encoder.serializersModule, value)?.let { serializer ->
                    // Use the serializer to convert the object to JsonElement
                    val jsonElement = encoder.json.encodeToJsonElement(serializer, value)
                    jsonElement
                } ?: run {
                    // Check if it's a data class
                    if (value::class.isData) {
                        // Convert data class to Map by using reflection
                        val properties = value::class.memberProperties
                            .associate { it.name to it.call(value) }

                        buildJsonObject {
                            properties.forEach { (k, v) ->
                                put(k, serializeValue(v, encoder))
                            }
                        }
                    } else {
                        JsonPrimitive(value.toString()) // Fallback to string representation
                    }
                }
            }
        }

    private fun deserializeValue(element: JsonElement): Any? =
        when (element) {
            is JsonNull -> null
            is JsonPrimitive -> {
                when {
                    element.isString -> element.content
                    element.content.equals("true", ignoreCase = true) -> true
                    element.content.equals("false", ignoreCase = true) -> false
                    // Try parsing as numbers with proper type detection
                    element.content.contains('.') -> {
                        element.doubleOrNull ?: element.floatOrNull ?: element.content
                    }

                    else -> {
                        element.longOrNull ?: element.intOrNull ?: element.content
                    }
                }
            }

            is JsonObject -> element.mapValues { (_, value) -> deserializeValue(value) }
            is JsonArray -> element.map { deserializeValue(it) }
        }

    private fun serializerFor(
        serializersModule: SerializersModule,
        obj: Any?,
    ): KSerializer<Any>? {
        return obj?.let {
            serializersModule.serializerOrNull(obj::class.java)
        }
    }
}
