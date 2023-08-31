package by.gto.equipment.account.model

import by.gto.equipment.account.helpers.JsonGuidDeserializer
import by.gto.equipment.account.helpers.JsonGuidSerializer
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDate
import java.util.Objects

@JsonIgnoreProperties(ignoreUnknown = true)
open class EquipmentSearchTemplate : Cloneable {
    @JsonSerialize(using = JsonGuidSerializer::class)
    @JsonDeserialize(using = JsonGuidDeserializer::class)
    var guid: ByteArray? = null
    var typeId = 0
    var personId = 0
    var stateId = 0
    var comment: String? = null
    var invNumber: String = ""
    var serial: String? = null
    var purchaseDate: LocalDate? = null

    override fun toString(): String {
        return "Equipment(guid=$guid, typeId=$typeId, personId=$personId, stateId=$stateId, comment=$comment, invNumber=$invNumber, serial=$serial, purchaseDate=$purchaseDate)"
    }
}
