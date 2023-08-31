package by.gto.inventoryandroid4.other

import by.gto.inventoryandroid4.model.JsonResponse
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URLConnection
import java.util.zip.GZIPInputStream
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

val gson = Gson()

open class MyFuelHttpClient(private val proxy: Proxy? = null, private val socketFactory: SSLSocketFactory? = null) : Client {
    override fun executeRequest(request: Request): Response {
        val connection = establishConnection(request) as HttpURLConnection

        try {
            connection.apply {
                connectTimeout = request.timeoutInMillisecond
                readTimeout = request.timeoutReadInMillisecond
                doInput = true
                useCaches = false
                requestMethod = if (request.method == Method.PATCH) Method.POST.value else request.method.value
                instanceFollowRedirects = false

                for ((key, value) in request.headers) {
                    setRequestProperty(key, value)
                }

                if (request.method == Method.PATCH) {
                    setRequestProperty("X-HTTP-Method-Override", "PATCH")
                }

                setDoOutput(connection, request.method)
                setBodyIfDoOutput(connection, request)
            }

            val contentEncoding = connection.contentEncoding ?: ""

            return Response(
                    url = request.url,
                    headers = connection.headerFields.filterKeys { it != null },
                    contentLength = connection.contentLength.toLong(),
                    statusCode = connection.responseCode,
                    responseMessage = connection.responseMessage.orEmpty(),
                    dataStream = try {
                        val stream = connection.errorStream ?: connection.inputStream
                        if (contentEncoding.compareTo("gzip", true) == 0) GZIPInputStream(stream) else stream
                    } catch (exception: IOException) {
                        try {
                            connection.errorStream ?: connection.inputStream?.close()
                        } catch (exception: IOException) {
                        }
                        ByteArrayInputStream(ByteArray(0))
                    }
            )
        } catch (exception: Exception) {
            throw FuelError(exception, ByteArray(0), Response(request.url))
        } finally {
            //As per Android documentation, a connection that is not explicitly disconnected
            //will be pooled and reused!  So, don't close it as we need inputStream later!
            //connection.disconnect()
        }
    }

    private fun establishConnection(request: Request): URLConnection {
        val urlConnection = if (proxy != null) request.url.openConnection(proxy) else request.url.openConnection()
        return if (request.url.protocol == "https") {
            val conn = urlConnection as HttpsURLConnection
            conn.sslSocketFactory = socketFactory
            conn.hostnameVerifier = HostnameVerifier { _, _ -> true }
            conn
        } else {
            urlConnection as HttpURLConnection
        }
    }

    private fun setBodyIfDoOutput(connection: HttpURLConnection, request: Request) {
        val bodyCallback = request.bodyCallback
        if (bodyCallback != null && connection.doOutput) {
            val contentLength = bodyCallback(request, null, 0)

            if (request.type == Request.Type.UPLOAD)
                connection.setFixedLengthStreamingMode(contentLength.toInt())

            BufferedOutputStream(connection.outputStream).use {
                bodyCallback(request, it, contentLength)
            }
        }
    }

    private fun setDoOutput(connection: HttpURLConnection, method: Method) = when (method) {
        Method.GET, Method.DELETE, Method.HEAD -> connection.doOutput = false
        Method.POST, Method.PUT, Method.PATCH -> connection.doOutput = true
    }

}

/**
 * Обёртка вокруг метода Fuel::post
 */
inline fun <A, reified V> postObject(
        url: String, argument: A,
        crossinline errorCallback: (Int?, String) -> Unit,
        crossinline successCallback: (JsonResponse<V>?, String?) -> Unit
) {
    Fuel.post(url)
            .header("Content-Type" to "application/json")
            .body(gson.toJson(argument))
            .responseObject(JsonResponse.Deserializer(V::class.java)) { request, _, httpResult ->
                println(request.toString())
                val (pl, err) = httpResult
                if (err != null) {
                    errorCallback(null, err.message.defaultIfEmptyOrBlank("Unknown FUEL error"))
                } else {
                    val payload = pl!!
                    if (payload.errCode != 0) {
                        errorCallback(pl.errCode, payload.message.defaultIfEmptyOrBlank("Unknown JSON API error"))
                    } else {
                        successCallback(pl, payload.message)
                    }
                }
            }
}


/**
 * Обёртка вокруг метода Fuel::get
 */
inline fun <reified V> getObject(
        url: String,
        crossinline errorCallback: (Int?, String) -> Unit,
        crossinline successCallback: (JsonResponse<V>?, String?) -> Unit
) {
    url.httpGet().responseObject(JsonResponse.Deserializer(V::class.java)) { request, _, httpResult ->
        println(request.toString())
        val (pl, err) = httpResult
        if (err != null) {
            errorCallback(null, err.message.defaultIfEmptyOrBlank("Unknown FUEL error"))
        } else {
            val payload = pl!!
            if (payload.errCode != 0) {
                errorCallback(pl.errCode, payload.message.defaultIfEmptyOrBlank("Unknown JSON API error"))
            } else {
                successCallback(pl, payload.message)
            }
        }
    }
}