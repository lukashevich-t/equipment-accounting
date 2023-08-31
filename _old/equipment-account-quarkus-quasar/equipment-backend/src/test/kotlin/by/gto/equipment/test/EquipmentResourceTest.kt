package by.gto.equipment.test

import by.gto.equipment.account.helpers.toBytes
import by.gto.equipment.account.helpers.toGuidBytes
import by.gto.equipment.account.mappers.GlobalMapper
import by.gto.equipment.account.model.CONTEXT_PATH
import by.gto.equipment.account.model.DEFAULT_GUID
import by.gto.equipment.account.model.EQUIPMENT_ALREADY_EXISTS_MESSAGE
import by.gto.equipment.account.model.EQUIPMENT_NOT_FOUND_BY_GUID
import by.gto.equipment.account.model.EquipmentDescr
import by.gto.equipment.account.model.JSONResponse
import by.gto.equipment.account.model.JSONResponseExt
import by.gto.equipment.account.model.LogEntryPresentation
import by.gto.equipment.account.model.REF_EQUIPMENT_STATES_TABLE_NAME
import by.gto.equipment.account.model.REF_EQUIPMENT_TYPES_TABLE_NAME
import by.gto.equipment.account.model.REF_PERSONS_TABLE_NAME
import by.gto.equipment.account.model.WRONG_GUID_FORMAT_MESSAGE
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.common.mapper.TypeRef
import org.jboss.logging.Logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response

private data class EquipmentTestCase(
        val guid: ByteArray, val type: String, val state: String, val person: String,
        val expectedReferencesChanged: Boolean)

@QuarkusTest
@Order(2)
class EquipmentResource {
    private val EXISTING_GUID = "ea0fc72c-070f-4820-9d59-643f70fe0572"
    private val NONEXISTING_GUID = "ea0fc72c-070f-4820-9d59-643f70fe0571"
    private val MALFORMED_GUID = "ea0fc72c-070f-4820-9d59-"
    private val sampleEquipmentDescr = EquipmentDescr().apply {
        this.guid = DEFAULT_GUID
        type = "МФУ"
        person = "Коско Александр Николаевич"
        state = "Новый"
        comment = "comment"
        invNumber = "invNumber"
        serial = "serial"
        purchaseDate = LocalDate.now()
    }

    @BeforeEach
    fun init() {
        RestAssured.basePath = "/$CONTEXT_PATH"
    }

    @Inject
    private lateinit var mapper: GlobalMapper

    @Test
    fun equipmentDescriptionShouldExist() {
        val refAnswer = given()
                .pathParam("guid", EXISTING_GUID)
                .`when`().get("equipment/description/{guid}").then()
                .statusCode(Response.Status.OK.statusCode).contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponse<EquipmentDescr>>() {})
        assertEquals(JSONResponse.CODE_OK, refAnswer.errCode)
        assertNull(refAnswer.message)
        val eqDescr: EquipmentDescr = refAnswer.content
                ?: throw IllegalArgumentException("Equipment description must not be NULL")
        val expected = EquipmentDescr().apply {
            this.guid = EXISTING_GUID.toGuidBytes()
            typeId = 2
            personId = 4
            stateId = 2
            comment = "Comment 0.4079442887152863"
            invNumber = "0.3268372732069208"
            serial = "serial0.24091852119561696"
            purchaseDate = LocalDate.of(2021, 1, 1)
            type = "Ноутбук"
            person = "person0.9312200988162168"
            state = "В эксплуатации"
        }
        assertEquals(expected, eqDescr)
    }

    @Test
    fun equipmentDescriptionShouldNotExist() {
        val refAnswer = given()
                .pathParam("guid", NONEXISTING_GUID)
                .`when`().get("equipment/description/{guid}").then()
                .statusCode(Response.Status.NOT_FOUND.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponse<EquipmentDescr?>>() {})
        assertEquals(JSONResponse.CODE_COMMON_USER_ERROR, refAnswer.errCode)
        assertEquals(EQUIPMENT_NOT_FOUND_BY_GUID, refAnswer.message)
        assertNull(refAnswer.content)
    }

    @Test
    fun equipmentDescriptionWithMalformedGuidShouldFail() {
        val refAnswer = given()
                .pathParam("guid", MALFORMED_GUID)
                .`when`().get("equipment/description/{guid}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponse<EquipmentDescr?>>() {})
        assertEquals(JSONResponse.CODE_COMMON_USER_ERROR, refAnswer.errCode)
        assertEquals(WRONG_GUID_FORMAT_MESSAGE, refAnswer.message)
        assertNull(refAnswer.content)
    }

    @Test
    fun logShouldContainEntries() {
        val answer = given()
                .pathParam("guid", EXISTING_GUID)
                .`when`().get("equipment/log/{guid}")
                .then()
                .statusCode(Response.Status.OK.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponse<List<LogEntryPresentation>>>() {})
        assertNull(answer.message)
        assertEquals(JSONResponse.CODE_OK, answer.errCode)
        assertNotNull(answer.content)
        val expectedLogEntries = listOf(
                LogEntryPresentation().apply {
                    guid = EXISTING_GUID.toGuidBytes()
                    actionName = "Списание"
                    userName = "tim"
                    time = LocalDateTime.of(2022, 1, 1, 14, 15, 16)
                    comment = "writeoff"
                },
                LogEntryPresentation().apply {
                    guid = EXISTING_GUID.toGuidBytes()
                    actionName = "Обслуживание"
                    userName = "tim"
                    time = LocalDateTime.of(2021, 1, 4, 9, 0, 0)
                    comment = "service"
                },
                LogEntryPresentation().apply {
                    guid = EXISTING_GUID.toGuidBytes()
                    actionName = "Изменение"
                    userName = "tim"
                    time = LocalDateTime.of(2021, 1, 2, 8, 0, 0)
                    comment = "modify"
                },
                LogEntryPresentation().apply {
                    guid = EXISTING_GUID.toGuidBytes()
                    actionName = "Создание"
                    userName = "tim"
                    time = LocalDateTime.of(2021, 1, 1, 23, 0, 0)
                    comment = "create"
                }
        )
        assertEquals(expectedLogEntries, answer.content)
    }

    @Test
    fun logShouldNotContainEntriesForNonexistentGuid() {
        val answer = given()
                .pathParam("guid", NONEXISTING_GUID)
                .`when`().get("equipment/log/{guid}")
                .then()
                .statusCode(Response.Status.OK.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponse<List<LogEntryPresentation>>>() {})
        assertNull(answer.message)
        assertEquals(JSONResponse.CODE_OK, answer.errCode)
        assertNotNull(answer.content)
        assertEquals(listOf<LogEntryPresentation>(), answer.content)
    }

    @Test
    fun логЗавершитсяНеудачейПриСломанномGuid() {
        val answer = given()
                .pathParam("guid", MALFORMED_GUID)
                .`when`().get("equipment/log/{guid}")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponse<List<LogEntryPresentation>>>() {})
        assertEquals(WRONG_GUID_FORMAT_MESSAGE, answer.message)
        assertEquals(JSONResponse.CODE_COMMON_USER_ERROR, answer.errCode)
        assertNull(answer.content)
    }

    @Test
    fun сохранениеЗаписиССуществующимGUIDЗавершитсяОшибкой() {
        val newEntry = EquipmentDescr(sampleEquipmentDescr).apply { guid = EXISTING_GUID.toGuidBytes() }
        val answer = given().contentType(APPLICATION_JSON).body(newEntry)
                .`when`().log().all().post("equipment/description")
                .then().log().all()
                .statusCode(Response.Status.BAD_REQUEST.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponseExt<EquipmentDescr?>>() {})
        assertEquals(JSONResponse.CODE_COMMON_SYSTEM_ERROR, answer.errCode)
        assertEquals(false, answer.referencesChanged)
        assertEquals(EQUIPMENT_ALREADY_EXISTS_MESSAGE, answer.message)
    }

    @Test
    fun сохраненныеЗаписиИСправочникиПопадутвБД() {
        // перед тестом убедимся, что в БД нет состояний, типов и лиц, которые должны будут создаться автоматом при записи оборудования:
        assertNull(mapper.getRefIdByName(REF_EQUIPMENT_TYPES_TABLE_NAME, "МФУ1"))
        assertNull(mapper.getRefIdByName(REF_EQUIPMENT_STATES_TABLE_NAME, "Новый1"))
        assertNull(mapper.getRefIdByName(REF_PERSONS_TABLE_NAME, "Коско Александр Николаевич1"))

        for (testCase in prepareInsertEquipmentTescases()) {
            val newEntry = EquipmentDescr(sampleEquipmentDescr).apply {
                guid = testCase.guid
                type = testCase.type
                state = testCase.state
                person = testCase.person
            }
            val answer = given().contentType(APPLICATION_JSON).body(newEntry)
                    .`when`().log().all().post("equipment/description")
                    .then().log().all()
                    .statusCode(Response.Status.CREATED.statusCode)
                    .contentType(APPLICATION_JSON)
                    .extract().`as`(object : TypeRef<JSONResponseExt<EquipmentDescr>>() {})
            assertEquals(JSONResponse.CODE_OK, answer.errCode)
            assertEquals(testCase.expectedReferencesChanged, answer.referencesChanged)
            assertNotNull(answer.content)
            assertTrue(newEntry.compareIgnoringIds(answer.content!!))

            val dbEntry = mapper.loadEquipmentDescr(newEntry.guid)
            assertNotNull(dbEntry)
            assertTrue(newEntry.compareIgnoringIds(dbEntry!!))
        }
        // Убедимся, что новые записи справочников были созданы
        assertNotNull(mapper.getRefIdByName(REF_EQUIPMENT_TYPES_TABLE_NAME, "МФУ1"))
        assertNotNull(mapper.getRefIdByName(REF_EQUIPMENT_STATES_TABLE_NAME, "Новый1"))
        assertNotNull(mapper.getRefIdByName(REF_PERSONS_TABLE_NAME, "Коско Александр Николаевич1"))
    }

    @Test
    fun updateExistingRecordGoesToDb() {
        val newEquipmentType = "МФУ2"
        // убедиться, что МФУ2 еще нет в справочнике типов
        assertNull(mapper.getRefIdByName(REF_EQUIPMENT_TYPES_TABLE_NAME, newEquipmentType))
        val newEntry = EquipmentDescr(sampleEquipmentDescr).apply {
            guid = EXISTING_GUID.toGuidBytes()
            type = newEquipmentType
        }
        val answer = given().contentType(APPLICATION_JSON).body(newEntry)
                .`when`().log().all().put("equipment/description")
                .then().log().all()
                .statusCode(Response.Status.OK.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponseExt<EquipmentDescr>>() {})
        assertEquals(JSONResponse.CODE_OK, answer.errCode)
        assertEquals(true, answer.referencesChanged)
        assertNotNull(answer.content)
        assertTrue(newEntry.compareIgnoringIds(answer.content!!))

        val dbEntry = mapper.loadEquipmentDescr(newEntry.guid)
        assertNotNull(dbEntry)
        dbEntry?.let {
            // при обновлении дата покупки не меняется!
            assertNotEquals(it.purchaseDate, newEntry.purchaseDate)
            it.purchaseDate = newEntry.purchaseDate
            assertTrue(newEntry.compareIgnoringIds(dbEntry))
        }
        // убедиться, что МФУ2 появился в справочнике типов
        assertNotNull(mapper.getRefIdByName(REF_EQUIPMENT_TYPES_TABLE_NAME, newEquipmentType))
    }

    @Test
    fun updateNonExistentRecordGoesToDb() {
        val newPerson = "John Smith"
        // убедиться, что John Smith еще нет в справочнике ответственных лиц
        assertNull(mapper.getRefIdByName(REF_PERSONS_TABLE_NAME, newPerson))
        val newEntry = EquipmentDescr(sampleEquipmentDescr).apply {
            guid = UUID.randomUUID().toBytes()
            person = newPerson
        }
        val answer = given().contentType(APPLICATION_JSON).body(newEntry)
                .`when`().log().all().put("equipment/description")
                .then().log().all()
                .statusCode(Response.Status.CREATED.statusCode)
                .contentType(APPLICATION_JSON)
                .extract().`as`(object : TypeRef<JSONResponseExt<EquipmentDescr>>() {})
        assertEquals(JSONResponse.CODE_OK, answer.errCode)
        assertEquals(true, answer.referencesChanged)
        assertNotNull(answer.content)
        assertTrue(newEntry.compareIgnoringIds(answer.content!!))

        val dbEntry = mapper.loadEquipmentDescr(newEntry.guid)
        assertNotNull(dbEntry)
        assertTrue(newEntry.compareIgnoringIds(dbEntry!!))
        // убедиться, что John Smith появился в справочнике ответственных лиц
        assertNotNull(mapper.getRefIdByName(REF_PERSONS_TABLE_NAME, newPerson))
    }

    @Test
    fun genGuid() {
        val answer = given()
            .`when`().log().all().get("equipment/guid")
            .then().log().all()
            .statusCode(Response.Status.OK.statusCode)
            .contentType(APPLICATION_JSON)
            .extract().`as`(object : TypeRef<JSONResponseExt<String>>() {})
        assertEquals(JSONResponse.CODE_OK, answer.errCode)
        assertNull(answer.message)
        val generatedGuid = answer.content
        assertNotNull(generatedGuid)
        if(generatedGuid != null) {
            assertTrue(
                "^[0-9A-Za-z]{8}(-[0-9A-Za-z]{4}){3}-[0-9A-Za-z]{12}\$".toRegex().matches(generatedGuid),
                "Guid не соответствует маске")
        }
    }

    @Test
    fun addLog() {
//        LocalDateTime.now().toString().substring(0,10)
    }

    private fun prepareInsertEquipmentTescases(): List<EquipmentTestCase> {
        val testCases = listOf(
                EquipmentTestCase(UUID.randomUUID().toBytes(), "МФУ", "Новый", "Коско Александр Николаевич", false),
                EquipmentTestCase(UUID.randomUUID().toBytes(), "МФУ1", "Новый", "Коско Александр Николаевич", true),
                EquipmentTestCase(UUID.randomUUID().toBytes(), "МФУ", "Новый1", "Коско Александр Николаевич", true),
                EquipmentTestCase(UUID.randomUUID().toBytes(), "МФУ", "Новый", "Коско Александр Николаевич1", true),
        )
        return testCases
    }

    private fun serialize(obj: Any): String {
        val om = ObjectMapper()
        val or = om.writer()
        val result = or.writeValueAsString(obj)
        return result
    }

    companion object {
        private val log = Logger.getLogger(EquipmentResource::class.java)
    }
}
