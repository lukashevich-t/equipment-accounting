package by.gto.equipment.account.ws

import by.gto.equipment.account.model.JSONErrorCodesEnum
import by.gto.equipment.account.model.JSONResponse
import by.gto.equipment.account.model.ReferenceAnswerJSON
import by.gto.equipment.account.model.TestDateModel
import by.gto.equipment.account.service.ServiceImpl
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.ejb.Stateless
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.servlet.http.HttpServletRequest
import javax.annotation.security.RolesAllowed
import javax.ws.rs.core.SecurityContext


@ApplicationScoped
@Path("r")
open class ReferencesResource {
    @Inject
    private lateinit var accountingService: ServiceImpl

    @GET
    @Path("eqStates")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getEquipmentStates() = try {
        val eqs: ReferenceAnswerJSON = ReferenceAnswerJSON(LocalDateTime.now(), 0, accountingService.getEquipmentStates().values.toList())
        JSONResponse(JSONErrorCodesEnum.OK, null, eqs)
    } catch (e: Exception) {
        JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, e.message)
    }

    @GET
    @Path("eqTypes")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getEquipmentTypes() = try {
        val eqs: ReferenceAnswerJSON = ReferenceAnswerJSON(LocalDateTime.now(), 0, accountingService.getEquipmentTypes().values.toList())
        JSONResponse(JSONErrorCodesEnum.OK, null, eqs)
    } catch (e: Exception) {
        JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, e.message)
    }

    @GET
    @Path("persons")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getResponsiblePersons() = try {
        val eqs: ReferenceAnswerJSON = ReferenceAnswerJSON(LocalDateTime.now(), 0, accountingService.getResponsiblePersons().values.toList())
        JSONResponse(JSONErrorCodesEnum.OK, null, eqs)
    } catch (e: Exception) {
        JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, e.message)
    }


}
