package by.gto.equipment.account.ws

import by.gto.equipment.account.model.*
import by.gto.equipment.account.service.ServiceImpl
import by.gto.library.helpers.GuidHelpers
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext


@ApplicationScoped
@Path("eq")
open class EquipmentResource {
    @Inject
    private lateinit var service: ServiceImpl

    @GET
    @Path("getEqDescr")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getEquipmentDescr(@QueryParam("guid") sGuid: String): JSONResponse {
        val guid: UUID
        try {
            guid = UUID.fromString(sGuid)
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            return JSONResponse(JSONErrorCodesEnum.COMMON_USER_ERROR, "неверный формат guid")
        }

        val eq = service.getEquipmentDescr(guid)
                ?: return JSONResponse(JSONErrorCodesEnum.COMMON_USER_ERROR, "не найдено оборудование с таким guid")
        return JSONResponse(JSONErrorCodesEnum.OK, null, eq)
    }

    @GET
    @Path("getEqLog")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getEquipmentLog(@QueryParam("guid") sGuid: String): JSONResponse {
        try {
            UUID.fromString(sGuid)
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            return JSONResponse(JSONErrorCodesEnum.COMMON_USER_ERROR, "неверный формат guid")
        }

        return JSONResponse(JSONErrorCodesEnum.OK, null, service.getLog(sGuid))
    }

    @POST
    @Path("putEqDescr")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun putEquipmentDescr(
            @Context request: HttpServletRequest,
            @Context secContext: SecurityContext,
            eqdescr: EquipmentDescr?): JSONResponse {
        if (eqdescr == null) {
            return JSONResponse(JSONErrorCodesEnum.COMMON_USER_ERROR, "Отсутствует входной параметр")
        }
        val userId = service.getCachedUserInfo(request.getSession(), secContext.getUserPrincipal().getName()).id
        try {
            val (refsModified, newObj) = service.putEquipmentDescr(eqdescr, userId, true)
            // если были изменены справочники, то строка ответа начинается с пробела:
            return JSONResponse(JSONErrorCodesEnum.OK,
                    (if (refsModified as Boolean) " " else "") + "modified OK (guid ${eqdescr.guid})",
                    newObj)
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            return JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, ex.message)
        }
    }

    @POST
    @Path("updateEqDescr")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    //    @Consumes(MediaType.APPLICATION_FORM_URLENCODED + ";charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun updateEquipmentDescr(
            @Context request: HttpServletRequest,
            @Context secContext: SecurityContext,
            eqdescr: EquipmentDescr?): JSONResponse {
        if (eqdescr == null) {
            return JSONResponse(JSONErrorCodesEnum.COMMON_USER_ERROR, "Отсутствует входной параметр")
        }
        val userId = service.getCachedUserInfo(request.getSession(), secContext.getUserPrincipal().getName()).id

        try {
            val (refsModified, newObj) = service.putEquipmentDescr(eqdescr, userId, false)
            // если были изменены справочники, то строка ответа начинается с пробела:
            return JSONResponseExt(
                    JSONErrorCodesEnum.OK,
                    (if (refsModified as Boolean) " " else "") + "modified OK (guid ${eqdescr.guid})",
                    newObj, refsModified)
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            return JSONResponse(JSONErrorCodesEnum.COMMON_SYSTEM_ERROR, ex.message)
        }
    }

    @GET
    @Path("testdate")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getTestDate() =
            JSONResponse(JSONErrorCodesEnum.OK, null, object {
                val d = Date();
                val ld = LocalDate.now()
                val ldt = LocalDateTime.now()
                val ms = System.currentTimeMillis()
            })

    @GET
    @Path("testlog")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getTestLog() = JSONResponse(JSONErrorCodesEnum.OK, null, service.getLog(existingTestEqipment))

    @GET
    @Path("testsearch")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getTestSearch() =  JSONResponse(JSONErrorCodesEnum.OK, "search", service.searchEquipment(Equipment().apply { invNumber="480699" }))
    @GET
    @Path("testeq")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    open fun getTestEquipment():JSONResponse {

        val eq = service.getEquipmentDescr(UUID.fromString(existingTestEqipment))
                ?: return JSONResponse(JSONErrorCodesEnum.COMMON_USER_ERROR, "не найдено оборудование с таким guid")
        return JSONResponse(JSONErrorCodesEnum.OK, null, eq)
    }

    companion object {
        private val log = LoggerFactory.getLogger(EquipmentResource::class.java)
        private val existingTestEqipment = "f6a20400-393b-4ff0-98b4-67690884310d"
    }
    //    @POST
//    @Path("print_invs2")
//    @Produces("application/pdf")
//    //@Consumes(MediaType.APPLICATION_JSON)
//    open fun printInventoryNumbers2(@FormParam("ids_for_printing") ids_for_printing: String): Response {
//        val sGuids = ArrayList<String>()
//        Collections.addAll(sGuids, *ids_for_printing.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
//
//        val stream = { os ->
//            try {
//                service!!.getInvNumbersFromGUIDs(sGuids, os)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            os.flush()
//        }
//
//        val response = Response.ok(stream)
//        response.header("Content-Disposition", "attachment; filename=inv-numbers.pdf")
//        response.header("Content-type", "application/pdf")
//        response.header("Content-Transfer-Encoding", "binary")
//        return response.build()
//    }
//
//    @GET
//    @Path("gen_inv")
//    @Produces("application/pdf")
//    fun getInventoryNumbers2(@QueryParam("date") sDate: String, @QueryParam("range") sRange: String): Response {
//        val stream = { os ->
//            try {
//                service!!.generateInvNumbers(sDate, sRange, os)
//            } catch (ex: WriterException) {
//                ex.printStackTrace()
//            } catch (ex: JRException) {
//                ex.printStackTrace()
//            } catch (ex: ValueNotSpecifiedException) {
//                ex.printStackTrace()
//            }
//
//            os.flush()
//        }
//
//        val response = Response.ok(stream)
//        response.header("Content-Disposition",
//                "attachment; filename=inv-numbers.pdf")
//        return response.build()
//        //        return Response.ok(stream).build();
//    }
//
//    @GET
//    @Path("getEq")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    fun getEquipment(@QueryParam("guid") sGuid: String): JSONResponse {
//        val guid: UUID
//        try {
//            guid = UUID.fromString(sGuid)
//        } catch (ex: Exception) {
//            return JSONResponse(1, "неверный формат guid", null)
//        }
//
//        val eq = service!!.getEquipment(guid)
//                ?: return JSONResponse(1, "не найдено оборудование с таким guid", null)
//        return JSONResponse(0, null, eq)
//    }

//    @POST
//    @Path("putEq")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    fun putEquipment(eq: Equipment?): JSONResponse {
//        if (eq == null) {
//            return JSONResponse(2, "Отсутствует входной параметр", null)
//        }
//
//        try {
//            val newEq = service!!.putEquipment(eq)
//            return JSONResponse(0, "Added OK with guid " + eq.guid!!, newEq)
//        } catch (e: Exception) {
//            return JSONResponse(1, e.message, null)
//        }
//
//    }
//

//
//    @POST
//    @Path("searchEq")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
//    fun searchEquipment(eq: Equipment?): JSONResponse {
//        if (eq == null) {
//            return JSONResponse(2, "Отсутствует входной параметр", null)
//        }
//
//
//        try {
//            return JSONResponse(0, "search", service!!.searchEquipment(eq))
//        } catch (e: Exception) {
//            return JSONResponse(1, e.message, null)
//        }
//
//    }

//    constructor() {
//        println("EquipmentResource constructor")
//    }
//
//    @PostConstruct
//    open fun postConstruct() {
//        println("EquipmentResource postConstruct $service")
//    }
}