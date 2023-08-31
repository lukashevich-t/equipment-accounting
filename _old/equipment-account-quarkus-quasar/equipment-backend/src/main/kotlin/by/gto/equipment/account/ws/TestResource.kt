package by.gto.equipment.account.ws

import by.gto.equipment.account.helpers.toGuidBytes
import by.gto.equipment.account.mappers.GlobalMapper
import by.gto.equipment.account.model.Action
import by.gto.equipment.account.model.BaseReference
import by.gto.equipment.account.model.EQUIPMENT_NOT_FOUND_BY_GUID
import by.gto.equipment.account.model.Equipment
import by.gto.equipment.account.model.EquipmentSearchTemplate
import by.gto.equipment.account.model.JSONResponse
import by.gto.equipment.account.model.REF_EQUIPMENT_STATES_TABLE_NAME
import by.gto.equipment.account.model.REF_EQUIPMENT_TYPES_TABLE_NAME
import by.gto.equipment.account.model.REF_PERSONS_TABLE_NAME
import by.gto.equipment.account.service.ServiceImpl
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.UUID
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

private val TEST_GUID = "ea0fc72c-070f-4820-9d59-643f70fe0572".toGuidBytes()

@ApplicationScoped
@Path("t")
class TestResource {
    @Inject
    lateinit var mapper: GlobalMapper

    @Inject
    private lateinit var service: ServiceImpl

    @POST
    @Path("logEquipmentChange")
    @Produces(APPLICATION_JSON)
    fun logEquipmentChange(): Any {
        return mapper.logEquipmentChange(
                TEST_GUID, Action.EQUIPMENT_CREATE.id,
                1, "Create", LocalDateTime.now()
        )
    }

    @POST
    @Path("createReferences")
    @Produces(APPLICATION_JSON)
    fun createReferences(): Any {
        val state = BaseReference(name = "state " + Math.random().toString())
        mapper.createReference(REF_EQUIPMENT_STATES_TABLE_NAME, state)
        val type = BaseReference(name = "type " + Math.random().toString())
        mapper.createReference(REF_EQUIPMENT_TYPES_TABLE_NAME, type)
        val person = BaseReference(name = "person " + Math.random().toString())
        mapper.createReference(REF_PERSONS_TABLE_NAME, person)
        return state.toString() + "\n" + type + "\n" + person
    }

    @POST
    @Path("saveEquipment")
    @Produces(APPLICATION_JSON)
    fun saveEquipment(): Any {
        val create = Math.random() >= 0.5
        val result = StringBuilder()
        val guid = if (create) {
            result.append("new\n")
            UUID.randomUUID().toString().toGuidBytes()
        } else {
            result.append("modified\n")
            TEST_GUID
        }
        val eq = Equipment(guid, 1, 1, 1, "Comment " + Math.random(),
                Math.random().toString(), null, "serial" + Math.random())
        result.append(mapper.saveEquipment(eq)).append("\n").append(eq)
        return result.toString()
    }

    @GET
    @Path("loadEquipmentsByGuidList")
    @Produces(APPLICATION_JSON)
    fun loadEquipmentsByGuidList(): Any {
        return mapper.loadEquipmentsByGuidList(listOf(
                TEST_GUID,
                "1555973d-edb9-444c-a732-8a4944906c1a".toGuidBytes(),
                "53d91886-df03-4b2f-8f13-e4c5b3a5c865".toGuidBytes()
        ))

    }

    @GET
    @Path("loadEquipmentDescr")
    @Produces(APPLICATION_JSON)
    fun loadEquipmentDescr() = mapper.loadEquipmentDescr(TEST_GUID)

    @GET
    @Path("getRefIdByName/{ref}/{name}")
    @Produces(APPLICATION_JSON)
    fun getRefIdByName(@PathParam("ref") ref: String, @PathParam("name") name: String) = mapper.getRefIdByName(ref, name)

    @GET
    @Path("loadUserInfoByLogin/{login}")
    @Produces(APPLICATION_JSON)
    fun getRefIdByName(@PathParam("login") login: String) = mapper.loadUserInfoByLogin(login)

    @GET
    @Path("loadUserInfoByDn/{dn}")
    @Produces(APPLICATION_JSON)
    fun loadUserInfoByDn(@PathParam("dn") dn: String): Any? {
        val a = mapper.loadUserInfoByDn(dn)
        return a
    }

    @GET
    @Path("getLog/{guid}")
    @Produces(APPLICATION_JSON)
    fun getLog(@PathParam("guid") guid: String): Any? {
        val a = mapper.getLog(guid.toGuidBytes())
        return a
    }
    @GET
    @Path("testdate")
    @Produces(APPLICATION_JSON)
    fun getTestDate() =
            JSONResponse<Map<String, Any>>(JSONResponse.CODE_OK, null, mapOf(
                    "d" to Date(),
                    "ld" to LocalDate.now(),
                    "ldt" to LocalDateTime.now(),
                    "ms" to System.currentTimeMillis()
            ))

    @GET
    @Path("testlog")
    @Produces(APPLICATION_JSON)
    fun getTestLog() = JSONResponse(JSONResponse.CODE_OK, null, service.getLog(existingTestEqipment))

    @GET
    @Path("testsearch")
    @Produces(APPLICATION_JSON)
    fun getTestSearch() = JSONResponse(
            JSONResponse.CODE_OK,
            "search",
            service.searchEquipment(EquipmentSearchTemplate().apply { invNumber = "480699" })
    )

    @GET
    @Path("testeq")
    @Produces(APPLICATION_JSON)
    fun getTestEquipment(): JSONResponse<Any> {

        val eq = service.loadEquipmentDescription(existingTestEqipment)
                ?: return JSONResponse(JSONResponse.CODE_COMMON_USER_ERROR, EQUIPMENT_NOT_FOUND_BY_GUID)
        return JSONResponse(JSONResponse.CODE_OK, null, eq)
    }

    companion object {
        private val existingTestEqipment = "f6a20400-393b-4ff0-98b4-67690884310d".toGuidBytes()
    }
}
