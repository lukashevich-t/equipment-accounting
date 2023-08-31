package by.gto.equipment.test

import by.gto.equipment.account.model.BaseReference
import by.gto.equipment.account.model.CONTEXT_PATH
import by.gto.equipment.account.model.JSONResponse
import by.gto.equipment.account.model.ReferenceAnswerJSON
import by.gto.equipment.test.helpers.writeObjectToFile
import by.gto.equipment.test.lifecycle.MariaDBTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.common.mapper.TypeRef
import io.restassured.response.ValidatableResponse
import org.eclipse.microprofile.config.ConfigProvider
import org.jboss.logging.Logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.util.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@QuarkusTest
@QuarkusTestResource(MariaDBTestResource::class)
@Order(1)
class ReferenceResource {

    @BeforeEach
    fun init() {
        RestAssured.basePath = "/${CONTEXT_PATH}"
    }

    @Test
    fun logSomeInfo() {
        val sb = StringBuilder(Date().toString()).append("\n\n")
        val config = ConfigProvider.getConfig()
        for (propertyName in config.propertyNames) {
            sb.append(propertyName).append(" => ").append(config.getConfigValue(propertyName).rawValue).append("\n")
        }
        writeObjectToFile(sb.append("\n\n"), "build/quarkus-test-properties.log", false)
    }

    @Test
    @Throws(Throwable::class)
    fun testReferenceEndpoints() {
        val expectedStateEntries = listOf(
            BaseReference(1, "Новый"),
            BaseReference(2, "В эксплуатации"),
            BaseReference(3, "В резерве"),
            BaseReference(4, "В ремонте"),
            BaseReference(5, "Списан"),
            BaseReference(6, "state0.6415836732604231"),
            BaseReference(7, "state 0.30081789597396824"),
            BaseReference(8, "ttt"),
            BaseReference(9, "yyy")
        )
        testReferenceEndpoint("/reference/state", 0, expectedStateEntries)

        val expectedTypeEntries = listOf(
            BaseReference(7, "Notebook"),
            BaseReference(9, "type 0.13426956922414446"),
            BaseReference(8, "type0.5021845515835421"),
            BaseReference(3, "МФУ"),
            BaseReference(2, "Ноутбук"),
            BaseReference(1, "Системный блок")
        )
        testReferenceEndpoint("/reference/type", 0, expectedTypeEntries)

        val expectedPersons = listOf(
            BaseReference(1, "Коско Александр Николаевич"),
            BaseReference(2, "Бурая Наталья Владимировна"),
            BaseReference(3, "Лукашевич Тимофей Викентьевич"),
            BaseReference(4, "person0.9312200988162168"),
            BaseReference(5, "person0.8335635554693495")
        )
        testReferenceEndpoint("/reference/person", 0, expectedPersons)
    }

    private fun testReferenceEndpoint(path: String, expectedVersion: Int, expectedEntries: List<BaseReference>) {
        val response = given().`when`().get(path)
        val vResponse: ValidatableResponse = response.then()
        vResponse.statusCode(Response.Status.OK.statusCode)
            .contentType(MediaType.APPLICATION_JSON)
        val refAnswer = vResponse.extract().`as`(object : TypeRef<JSONResponse<ReferenceAnswerJSON>>() {})
        assertEquals(refAnswer.errCode, JSONResponse.CODE_OK)
        assertNull(refAnswer.message)
        val refs: ReferenceAnswerJSON = refAnswer.content
            ?: throw IllegalArgumentException("reference content must not be NULL")

        assertEquals(expectedVersion, refs.ver)
        assertNotNull(refs.date)
        assertEquals(expectedEntries.sortedBy { it.id }, refs.data.sortedBy { it.id })
    }

    companion object {
        private val log = Logger.getLogger(ReferenceResource::class.java)
    }
}
