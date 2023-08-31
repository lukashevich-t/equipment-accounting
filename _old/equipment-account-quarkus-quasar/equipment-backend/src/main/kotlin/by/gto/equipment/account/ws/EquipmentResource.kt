package by.gto.equipment.account.ws

import by.gto.equipment.account.helpers.toGuidBytes
import by.gto.equipment.account.model.Action
import by.gto.equipment.account.model.EQUIPMENT_NOT_FOUND_BY_GUID
import by.gto.equipment.account.model.EquipmentDescr
import by.gto.equipment.account.model.EquipmentSearchTemplate
import by.gto.equipment.account.model.JSONResponse
import by.gto.equipment.account.model.JSONResponse.Companion.CODE_COMMON_SYSTEM_ERROR
import by.gto.equipment.account.model.JSONResponse.Companion.CODE_COMMON_USER_ERROR
import by.gto.equipment.account.model.JSONResponse.Companion.CODE_OK
import by.gto.equipment.account.model.JSONResponseExt
import by.gto.equipment.account.model.LogEntry
import by.gto.equipment.account.model.WRONG_GUID_FORMAT_MESSAGE
import by.gto.equipment.account.service.ServiceImpl
import org.jboss.logging.Logger
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.FormParam
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.MediaType.WILDCARD
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@ApplicationScoped
@Path("equipment")
class EquipmentResource {
    @Inject
    private lateinit var service: ServiceImpl

//    private val log = Logger.getLogger(EquipmentResource::class.java)

    @Inject
    lateinit var log: Logger

    // covered by test
    @GET
    @Path("description/{guid}")
    @Produces(APPLICATION_JSON)
    fun loadEquipmentDescr(@PathParam("guid") sGuid: String): Response {
        return try {
            val eq = service.loadEquipmentDescription(sGuid.toGuidBytes())
            if (eq == null) {
                Response.status(Response.Status.NOT_FOUND)
                    .entity(JSONResponse<Any>(CODE_COMMON_USER_ERROR, EQUIPMENT_NOT_FOUND_BY_GUID))
            } else {
                Response.ok().entity(JSONResponse(CODE_OK, null, eq))
            }
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST)
                .entity(JSONResponse<Any>(CODE_COMMON_USER_ERROR, WRONG_GUID_FORMAT_MESSAGE))
        }.build()
    }

    // covered by test
    @GET
    @Path("log/{guid}")
    @Produces(APPLICATION_JSON)
    fun getEquipmentLog(@PathParam("guid") sGuid: String): Response {
        return try {
            val result = JSONResponse(CODE_OK, null, service.getLog(sGuid.toGuidBytes()))
            Response.status(Response.Status.OK).entity(result)
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST)
                .entity(JSONResponse<Any>(CODE_COMMON_USER_ERROR, WRONG_GUID_FORMAT_MESSAGE))
        }.build()
    }

    // covered by test
    @POST
    @Path("description")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    fun putEquipmentDescr(
        @Context rq: HttpServletRequest,
        @Context sc: SecurityContext,
        eqdescr: EquipmentDescr
    ): Response {
        val userId = service.getCachedUserInfo(rq, sc).id
        return try {
            val (newObj, refsModified, created) = service.putEquipmentDescription(eqdescr, userId, true)
            val result = JSONResponseExt(CODE_OK, "", newObj, refsModified)
            (if (created) Response.status(Response.Status.CREATED) else Response.ok())
                .entity(result)
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST).entity(
                JSONResponseExt<Any>(CODE_COMMON_SYSTEM_ERROR, ex.message)
            )
        }.build()
    }

    // covered by test
    @PUT
    @Path("description")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    fun updateEquipmentDescr(
        @Context rq: HttpServletRequest,
        @Context sc: SecurityContext,
        eqdescr: EquipmentDescr
    ): Response {
        val userId = service.getCachedUserInfo(rq, sc).id
        return try {
            val (newObj, refsModified, created) = service.putEquipmentDescription(eqdescr, userId, false)
            val result = JSONResponseExt(CODE_OK, "", newObj, refsModified)
            (if (created) Response.status(Response.Status.CREATED) else Response.ok())
                .entity(result)
        } catch (ex: Exception) {
            log.error(ex.message, ex)
            Response.status(Response.Status.BAD_REQUEST).entity(
                JSONResponseExt<Any>(CODE_COMMON_SYSTEM_ERROR, ex.message)
            )
        }.build()
    }

    // covered by test
    /**
     * генерация UUID
     */
    @GET
    @Path("guid")
    fun genGuid() = JSONResponse(CODE_OK, content = UUID.randomUUID().toString())

    @POST
    @Path("print-numbers")
    @Produces("application/pdf")
    fun printInventoryNumbers(@FormParam("ids_for_printing") ids_for_printing: String): Response {
        val sGuids: List<String> = ids_for_printing.split(',').dropLastWhile { it.isEmpty() }

        val baos = ByteArrayOutputStream()
        return try {
            val numbers = service.loadInvNumbersFromGUIDs(sGuids)
            service.writeNumbersToPdfStream(numbers, baos, "a4_6x6.jasper")
            baos.flush()
            baos.close()
            Response.ok(baos.toByteArray())
                .header("Content-type", "application/pdf")
//                    .header("Content-Disposition", "attachment; filename=inv-numbers.pdf")
                .header("Content-Transfer-Encoding", "binary")
                .build()
        } catch (ex: Exception) {
            ex.printStackTrace()
//            StretchTypeEnum
            Response.serverError().entity(ex.message).status(Response.Status.INTERNAL_SERVER_ERROR).build()
        } finally {
            baos.close()
        }
    }

    @GET
    @Path("generate-numbers")
    @Produces(WILDCARD)
    fun generateInventoryNumbers(
        @QueryParam("date") sDate: String?,
        @QueryParam("range") sRange: String,
        @QueryParam("templateFilename") templateFilename: String?
    ): Response {
        val baos = ByteArrayOutputStream()

        return try {
            val date = if (sDate.isNullOrBlank()) null else LocalDate.parse(sDate)
            val numbers = service.generateInvNumbers(date, sRange)
            service.writeNumbersToPdfStream(numbers, baos, templateFilename ?: "a4_6x6.jasper")
            baos.flush()
            baos.close()
            Response.ok(baos.toByteArray())
                .header("Content-type", "application/pdf")
                .build()
        } catch (ex: Exception) {
            ex.printStackTrace()
            Response.serverError().entity(ex.message).status(Response.Status.INTERNAL_SERVER_ERROR).build()
        } finally {
            baos.close()
        }
    }

    @POST
    @Path("search")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_JSON)
    fun searchEquipment(eq: EquipmentSearchTemplate): JSONResponse<Any> =
        try {
            JSONResponse(CODE_OK, "search", service.searchEquipment(eq))
        } catch (e: Exception) {
            JSONResponse(CODE_COMMON_USER_ERROR, e.message, null)
        }

    @POST
    @Path("log-entry")
    fun addLog(@Context rq: HttpServletRequest, @Context sc: SecurityContext, logEntry: LogEntry) = try {
        // TODO: in production userId is mandatory
        val userId = service.getCachedUserInfo(rq, sc).id
        val result = service.logEquipmentChange(
            logEntry.guid,
            Action.fromId(logEntry.actionId),
            userId,
            logEntry.comment ?: "",
            LocalDateTime.now()
        )
        JSONResponse(CODE_OK, result.toString(), result)
    } catch (e: Exception) {
        JSONResponse(CODE_COMMON_USER_ERROR, e.message, null)
    }
}
