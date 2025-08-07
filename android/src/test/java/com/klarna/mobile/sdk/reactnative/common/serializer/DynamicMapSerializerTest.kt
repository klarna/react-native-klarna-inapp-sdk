package com.klarna.mobile.sdk.reactnative.common.serializer

import com.klarna.mobile.sdk.reactnative.common.util.ParserUtil
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertContentEquals

class DynamicMapSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `test serialization of primitive values`() {
        val map = mapOf(
            "nullValue" to null,
            "booleanValue" to true,
            "intValue" to 42,
            "longValue" to 123456789L,
            "floatValue" to 3.14f,
            "doubleValue" to 2.71828,
            "stringValue" to "hello",
            "charValue" to 'A'
        )

        val jsonString = json.encodeToString(DynamicMapSerializer, map)
        val expectedJson =
            """{"nullValue":null,"booleanValue":true,"intValue":42,"longValue":123456789,"floatValue":3.14,"doubleValue":2.71828,"stringValue":"hello","charValue":"A"}"""

        assertEquals(expectedJson, jsonString)
    }

    @Test
    fun `test deserialization of primitive values`() {
        val jsonString =
            """{"nullValue":null,"booleanValue":true,"intValue":42,"longValue":123456789,"floatValue":3.14,"doubleValue":2.71828,"stringValue":"hello"}"""

        val map = json.decodeFromString(DynamicMapSerializer, jsonString)

        assertEquals(null, map["nullValue"])
        assertEquals(true, map["booleanValue"])
        assertEquals(42L, map["intValue"])
        assertEquals(123456789L, map["longValue"])
        assertEquals(3.14, map["floatValue"])
        assertEquals(2.71828, map["doubleValue"])
        assertEquals("hello", map["stringValue"])
    }

    @Test
    fun `test serialization of nested maps`() {
        val nestedMap = mapOf(
            "inner" to mapOf(
                "key1" to "value1",
                "key2" to 42
            )
        )

        val jsonString = json.encodeToString(DynamicMapSerializer, nestedMap)
        val expectedJson = """{"inner":{"key1":"value1","key2":42}}"""

        assertEquals(expectedJson, jsonString)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test deserialization of nested maps`() {
        val jsonString = """{"outer":{"inner":{"key1":"value1","key2":42}}}"""

        val map = json.decodeFromString(DynamicMapSerializer, jsonString)

        val outer = map["outer"] as Map<String, Any?>
        val inner = outer["inner"] as Map<String, Any?>

        assertEquals("value1", inner["key1"])
        assertEquals(42L, inner["key2"])
    }

    @Test
    fun `test serialization of collections`() {
        val map = mapOf(
            "list" to listOf(1, 2, 3),
            "mixedList" to listOf(1, "two", 3.0)
        )

        val jsonString = json.encodeToString(DynamicMapSerializer, map)
        val expectedJson = """{"list":[1,2,3],"mixedList":[1,"two",3.0]}"""

        assertEquals(expectedJson, jsonString)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test deserialization of collections`() {
        val jsonString = """{"list":[1,2,3],"mixedList":[1,"two",3.0]}"""

        val map = json.decodeFromString(DynamicMapSerializer, jsonString)

        val list = map["list"] as List<Long>
        val mixedList = map["mixedList"] as Collection<Any?>

        assertContentEquals(listOf(1L, 2L, 3L), list)
        assertEquals(1L, mixedList.elementAt(0))
        assertEquals("two", mixedList.elementAt(1))
        assertEquals(3.0, mixedList.elementAt(2))
    }

    @Test
    fun `test serialization of arrays`() {
        val map = mapOf(
            "array" to arrayOf(1, 2, 3)
        )

        val jsonString = json.encodeToString(DynamicMapSerializer, map)
        val expectedJson = """{"array":[1,2,3]}"""

        assertEquals(expectedJson, jsonString)
    }

    @Test
    fun `test serialization of complex nested structures`() {
        val complex = mapOf(
            "name" to "Complex",
            "properties" to mapOf(
                "nested" to listOf(
                    mapOf("id" to 1, "value" to "first"),
                    mapOf("id" to 2, "value" to "second")
                )
            ),
            "tags" to arrayOf("important", "test")
        )

        val jsonString = json.encodeToString(DynamicMapSerializer, complex)
        val expectedJson =
            """{"name":"Complex","properties":{"nested":[{"id":1,"value":"first"},{"id":2,"value":"second"}]},"tags":["important","test"]}"""

        assertEquals(expectedJson, jsonString)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test serialization and deserialization of data classes`() {
        // Define a data class for testing
        data class TestDataClass2(
            val street: String,
            val city: String,
            val zipCode: String
        )

        data class TestDataClass1(
            val name: String,
            val age: Int,
            val address: TestDataClass2
        )

        // Create test data
        val address = TestDataClass2("123 Main St", "Anytown", "12345")
        val person = TestDataClass1("John Doe", 30, address)

        // Create a map with the data class
        val map = mapOf(
            "person" to person,
            "simpleValue" to 42
        )

        // Serialize
        val jsonString = json.encodeToString(DynamicMapSerializer, map)

        // Expected JSON structure
        val expectedJson =
            """{"person":{"name":"John Doe","age":30,"address":{"street":"123 Main St","city":"Anytown","zipCode":"12345"}},"simpleValue":42}"""

        // Verify serialization
        assertEquals(ParserUtil.parse(expectedJson), ParserUtil.parse(jsonString))

        // Deserialize
        val deserializedMap = json.decodeFromString(DynamicMapSerializer, jsonString)

        // Verify deserialization
        assertEquals(42L, deserializedMap["simpleValue"])

        // Check the nested structure
        val deserializedPerson = deserializedMap["person"] as Map<String, Any?>
        assertEquals("John Doe", deserializedPerson["name"])
        assertEquals(30L, deserializedPerson["age"])

        val deserializedAddress = deserializedPerson["address"] as Map<String, Any?>
        assertEquals("123 Main St", deserializedAddress["street"])
        assertEquals("Anytown", deserializedAddress["city"])
        assertEquals("12345", deserializedAddress["zipCode"])
    }

    @Test
    fun `test serialization and deserialization of serializable objects`() {
        // Define a serializable class
        @Serializable
        data class SerializableUser(
            val id: Int,
            val username: String
        )

        // Create test data with serializable object
        val user = SerializableUser(id = 1001, username = "testuser")
        val map = mapOf(
            "user" to user,
            "timestamp" to 1634567890L
        )

        // Serialize
        val jsonString = json.encodeToString(DynamicMapSerializer, map)

        // Expected JSON structure
        val expectedJson = """{"user":{"id":1001,"username":"testuser"},"timestamp":1634567890}"""

        // Verify serialization
        assertEquals(ParserUtil.parse(expectedJson), ParserUtil.parse(jsonString))

        // Deserialize
        val deserializedMap = json.decodeFromString(DynamicMapSerializer, jsonString)

        // Verify deserialization
        assertEquals(1634567890L, deserializedMap["timestamp"])

        // Check the serializable object
        val deserializedUser = deserializedMap["user"] as Map<String, Any?>
        assertEquals(1001L, deserializedUser["id"])
        assertEquals("testuser", deserializedUser["username"])
    }
}
