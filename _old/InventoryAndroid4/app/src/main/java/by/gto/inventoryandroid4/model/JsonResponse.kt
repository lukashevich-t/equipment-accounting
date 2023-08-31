package by.gto.inventoryandroid4.model

import android.util.Log
import by.gto.inventoryandroid4.other.ERRCODE_PARSE
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

private class DateDeserializer : JsonDeserializer<Date> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date {
        if (json.isJsonPrimitive) {
            val primitive = json.asJsonPrimitive
            if (primitive.isNumber) {
                return Date(primitive.asLong)
            }
            val stringPresentation = primitive.asString
            for (f in formats) {
                try {
                    val sdf = SimpleDateFormat(f)
                    val result = sdf.parse(stringPresentation)
                    return result
                } catch (_: Exception) {
                }
            }
        }
        throw com.google.gson.JsonSyntaxException("${json} is not date")
    }

    companion object {
        val formats = listOf("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd")
    }
}

//private class LogEntryDeserializer : JsonDeserializer<LogEntry?> {
//    @Throws(JsonParseException::class)
//    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LogEntry? {
//        json.isJsonPrimitive
//        return null
//        throw com.google.gson.JsonSyntaxException("${json} is not date")
//    }
//}

fun getGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer())
//    gsonBuilder.registerTypeAdapter(LogEntry::class.java, LogEntryDeserializer())
    return gsonBuilder.create()
}

data class JsonResponse<T>(var errCode: Int = 0, var message: String? = null, var content: T? = null) {
    class Deserializer<T>(private val dataClass: Class<T>) : ResponseDeserializable<JsonResponse<T>> {
        private val gson = getGson()
        override fun deserialize(content: String): JsonResponse<T>? {
            if (content.isBlank()) {
                return null
            }
            return try {
                val fromJson: JsonResponse<T>? = gson.fromJson(content, getType(JsonResponse::class.java, dataClass))
                fromJson
            } catch (ex: Exception) {
                JsonResponse(message = (ex.message
                        ?: "Неизвестная ошибка при разборе JSON") + content, errCode = ERRCODE_PARSE)
            }
        }

        private fun getType(rawClass: Class<*>, parameterClass: Class<*>): Type {
            return object : ParameterizedType {
                override fun getActualTypeArguments(): Array<Type> {
                    return arrayOf<Type>(parameterClass)
                }

                override fun getRawType(): Type {
                    return rawClass
                }

                override fun getOwnerType(): Type? {
                    return null
                }

            }
        }
    }
}

data class RefContent(var date: Date = Date(0), var ver: Short = 0, var data: List<RefEntry>? = null)

data class RefEntry(var id: Int = 0, var name: String = "") {
    override fun toString() = name
}

data class InventoryInfo(
        var comment: String = "",
        var guid: String = "",
        var inv_number: String = "",
        var person: String = "",
        var person_id: Int = 0,
        var purchase_date: String = "",
        var serial: String = "",
        var state: String = "",
        var state_id: Int = 0,
        var type: String = "",
        var type_id: Int = 0
)

data class TestDateEntry(
        var d: Date? = null,
        var ld: Date? = null,
        var ldt: Date? = null,
        var ms: Date? = null)