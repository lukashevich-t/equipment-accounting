package by.gto.equipment.account.e

import by.gto.equipment.account.model.JSONResponse
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class MyExceptionMapper : ExceptionMapper<Throwable> {
    override fun toResponse(exception: Throwable): Response {
        var cause: Throwable? = exception
        var httpCode = 400
        var code = -1
        var message = exception.message
        val seenList: MutableList<Throwable> = ArrayList()
//        while (cause != null) {
//            // Избежим циклических ссылок
//            for (t in seenList) {
//                if (t === cause) {
//                    break
//                }
//            }
//            if (cause is BaseException) {
//                message = cause.message
//                httpCode = cause.httpCode
//                code = cause.errCode
//                break
//            }
//            seenList.add(cause)
//            cause = cause.cause
//        }
        val resp = JSONResponse<Any>(code, message)
        return Response
                .status(httpCode).header("Content-Type", "application/json")
                .entity(resp).build()
    }
}
