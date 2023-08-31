package by.gto.equipment.account.model


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.lang.StringBuilder
import java.time.LocalDate
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
open class Equipment: Cloneable {
    var guid: String = ""
    @JsonProperty("type_id")
    var typeId = 0
    @JsonProperty("person_id")
    var personId = 0
    @JsonProperty("state_id")
    var stateId = 0
    var comment: String? = null
    @JsonProperty("inv_number")
    var invNumber: String = ""
    var serial: String? = null
    @JsonProperty("purchase_date")
    var purchaseDate: LocalDate? = null

    constructor(guid: String, typeId: Int, personId: Int, stateId: Int, comment: String?, invNumber: String,
                purchaseDate: LocalDate?, serial: String?) {
        this.guid = guid
        this.typeId = typeId
        this.personId = personId
        this.stateId = stateId
        this.comment = comment
        this.invNumber = invNumber
        this.purchaseDate = purchaseDate
        this.serial = serial
    }

    constructor()
    /**
     * copy constructor
     */
    constructor(source: Equipment) : this(source.guid, source.typeId, source.personId, source.stateId, source.comment,
            source.invNumber, source.purchaseDate, source.serial)

//    constructor(ed: EquipmentDescr) {
//        this.guid = ed.guid
//        this.typeId = ed.typeId
//        this.personId = ed.personId
//        this.stateId = ed.stateId
//        this.comment = ed.comment
//        this.invNumber = ed.invNumber
//        this.serial = ed.serial
//
//        this.purchaseDate = DateHelpers.parseAllDate(ed.purchaseDate)
//    }

    override fun toString(): String {
        return "Equipment(guid=$guid, typeId=$typeId, personId=$personId, stateId=$stateId, comment=$comment, invNumber=$invNumber, serial=$serial, purchaseDate=$purchaseDate)"
    }

    open fun diff(old: Equipment): String? {
        val sb = StringBuilder(128)
        if (old.typeId != this.typeId) {
            sb.append("typeId ").append(old.typeId).append(" -> ").append(this.typeId).append("\n")
        }
        if (old.personId != this.personId) {
            sb.append("personId ").append(old.personId).append(" -> ").append(this.personId).append("\n")
        }
        if (old.stateId != this.stateId) {
            sb.append("stateId ").append(old.stateId).append(" -> ").append(this.stateId).append("\n")
        }
        if (old.comment != this.comment) {
            sb.append("comment ").append(old.comment).append(" -> ").append(this.comment).append("\n")
        }
        if (old.invNumber != this.invNumber) {
            sb.append("invNumber ").append(old.invNumber).append(" -> ").append(this.invNumber).append("\n")
        }
        if (old.serial != this.serial) {
            sb.append("serial ").append(old.serial).append(" -> ").append(this.serial).append("\n")
        }
        if (Objects.equals(old.purchaseDate, this.purchaseDate)) {
            sb.append("purchaseDate ").append(old.purchaseDate).append(" -> ").append(this.purchaseDate).append("\n")
        }
        return if (sb.length == 0) null else sb.toString()
    }
}
