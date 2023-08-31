package by.gto.equipment.account.ws

import by.gto.equipment.account.model.JSONErrorCodesEnum
import by.gto.equipment.account.model.JSONResponse
import by.gto.equipment.account.model.ReferenceAnswerJSON
import by.gto.equipment.account.service.ServiceImpl
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@ApplicationScoped
@Path("r")
open class ReferencesResource {
    @Inject
    private lateinit var accountingService: ServiceImpl

    @GET
    @Path("eqStates")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getEquipmentStates() = try {
        val eqs =
            ReferenceAnswerJSON(LocalDateTime.now(), 0, accountingService.getEquipmentStates().values.toList())
        JSONResponse(JSONErrorCodesEnum.OK, null, eqs)
    } catch (e: Exception) {
        JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, e.message)
    }

    @GET
    @Path("eqTypes")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getEquipmentTypes() = try {
        val eqs = ReferenceAnswerJSON(LocalDateTime.now(), 0, accountingService.getEquipmentTypes().values.toList())
        JSONResponse(JSONErrorCodesEnum.OK, null, eqs)
    } catch (e: Exception) {
        JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, e.message)
    }

    @GET
    @Path("persons")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getResponsiblePersons() = try {
        val eqs =
            ReferenceAnswerJSON(LocalDateTime.now(), 0, accountingService.getResponsiblePersons().values.toList())
        JSONResponse(JSONErrorCodesEnum.OK, null, eqs)
    } catch (e: Exception) {
        JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, e.message)
    }
}
