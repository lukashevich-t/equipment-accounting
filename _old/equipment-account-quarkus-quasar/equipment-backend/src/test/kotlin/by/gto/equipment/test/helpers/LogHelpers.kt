package by.gto.equipment.test.helpers

import io.restassured.filter.FilterContext
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.UrlDecoder
import io.restassured.internal.print.RequestPrinter
import io.restassured.internal.print.ResponsePrinter
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import org.jboss.logging.Logger
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

fun writeObjectToFile(obj: Any?, path: String, append: Boolean = true) {
    if (obj == null) return
    val f = File(path)
    val method = if (append) f::appendText else f::writeText
    method(obj.toString(), StandardCharsets.UTF_8)
}

/**
 * Класс для организации записи HTTP запросов и ответов в логи. Использует JBoss Logging для выхлопа.
 *
 * Пример:
 * ```kotlin
 * private val log = org.jboss.logging.Logger.getLogger(TestClass1::class.java)
 * private val requestLogger = RestAssuredLoggingFilterJbossLogging(log, org.jboss.logging.Logger..Level.ERROR, io.restassured.filter.log.LogDetail.ALL)
 * ...
 * io.restassured.RestAssured.given().filter(requestLogger).`when`()...
 * ```
 */
class RestAssuredLoggingFilterJbossLogging(val logger: Logger, var level: Logger.Level, var logDetail: LogDetail) :
    io.restassured.filter.Filter {
    var showUrlEncodedUri = false
    var shouldPrettyPrint = false
    var blacklistedHeaders = setOf<String>()
    override fun filter(
        requestSpec: FilterableRequestSpecification,
        responseSpec: FilterableResponseSpecification,
        ctx: FilterContext
    ): Response {
        val response = ctx.next(requestSpec, responseSpec)
        if (logger.isEnabled(level)) {
            var uri = requestSpec.uri
            if (!showUrlEncodedUri) {
                uri = UrlDecoder.urlDecode(
                    uri,
                    Charset.forName(requestSpec.config.encoderConfig.defaultQueryParameterCharset()),
                    true
                )
            }
            ByteArrayOutputStream().use { baos ->
                PrintStream(baos, true, StandardCharsets.UTF_8.toString()).use { stream ->
                    stream.print("\nRequest:\n\n")
                    RequestPrinter.print(
                        requestSpec,
                        requestSpec.method,
                        uri,
                        logDetail,
                        blacklistedHeaders,
                        stream,
                        shouldPrettyPrint
                    )
                    stream.print("\n\nResponse:\n\n")
                    ResponsePrinter.print(response, response, stream, logDetail, shouldPrettyPrint, blacklistedHeaders)
                }

                logger.log(level, baos.toString(StandardCharsets.UTF_8))
            }
        }
        return response
    }
}
