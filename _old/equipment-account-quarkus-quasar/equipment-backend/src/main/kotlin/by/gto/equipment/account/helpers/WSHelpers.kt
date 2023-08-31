package by.gto.equipment.account.helpers

import by.gto.equipment.account.model.JSONResponse
import by.gto.equipment.account.model.UserInfo
import by.gto.equipment.account.service.ServiceImpl
import javax.enterprise.inject.spi.CDI
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.ws.rs.core.SecurityContext
import org.jboss.logging.Logger
import by.gto.equipment.account.model.JSONResponse.Companion.CODE_COMMON_SYSTEM_ERROR
import by.gto.equipment.account.model.JSONResponse.Companion.CODE_OK

var service: ServiceImpl = CDI.current().select(ServiceImpl::class.java).get()
val log: Logger = Logger.getLogger("WSHelpers")

/**
 * Функция-обертка. Вызывает callback, оборачивая его в типовую обработку исключений
 * и передавая ему информацию о текущем пользователе
 * @param sc - {@link javax.ws.rs.core.SecurityContext} - из http-запроса
 * @param rq - {@link javax.servlet.http.HttpServletRequest} - из http-запроса
 * @param func - callback, выполняющий полезную работу
 * @return Обертка для ответа фронтенду. Несет полезную нагрузку, возвращенную callback-функцией func, или сообщение об ошибке
 */
inline fun doSomethingWithUserInfo(sc: SecurityContext, rq: HttpServletRequest, func: (ui: UserInfo) -> Any): JSONResponse<Any?> {
    return try {
        val ui = getCachedUserInfo(rq.session, sc.userPrincipal.name)
        val result = func(ui)
        JSONResponse(CODE_OK, "", result)
    } catch (ex: Exception) {
        log.error(ex.message, ex)
        JSONResponse(CODE_COMMON_SYSTEM_ERROR, ex.message)
    }
}

/**
 * Получает информацию об указанном пользователе из объекта HttpSession
 * Если информации о пользователе в сессии нет, она загружается из БД и помещается в сессию
 *
 * @param session   сессия для поиска
 * @param userLogin имя входа пользователя
 * @return информация о пользователе
 */
@Suppress("NOTHING_TO_INLINE")
inline fun getCachedUserInfo(session: HttpSession, userLogin: String): UserInfo {
    var result = session.getAttribute(userLogin + "_userinfo") as UserInfo?
    if (null == result) {
        //            UserServiceImpl userService = (UserServiceImpl) ApplicationContextProvider.getBean("userService");
        result = service.loadUserInfo(userLogin)
        session.setAttribute(userLogin + "_userinfo", result)
    }
    return result
}
