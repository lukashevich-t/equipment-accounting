package by.gto.equipment.account.model


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class EquipmentDescr : Equipment, Cloneable {
    var type: String? = null
    var person: String? = null
    var state: String? = null

    /**
     * copy constructor
     */
    constructor(source: EquipmentDescr) : super(source) {
        this.type = source.type
        this.person = source.person
        this.state = source.state
    }

    constructor()
//
//    constructor(guid: String, type: String, person: String, state: String, comment: String, invNumber: String, purchaseDate: String, serial: String) : this() {
//        this.guid = guid
//        this.type = type
//        this.person = person
//        this.state = state
//        this.comment = comment
//        this.invNumber = invNumber
//        this.purchaseDate = purchaseDate
//        this.serial = serial
//    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): EquipmentDescr {
        return super<Equipment>.clone() as EquipmentDescr
    }

    override fun toString(): String {
        return "EquipmentDescr(guid=$guid, type=$type, typeId=$typeId, person=$person, personId=$personId, state=$state, stateId=$stateId, comment=$comment, invNumber=$invNumber, purchaseDate=$purchaseDate, serial=$serial)"
    }

    override fun diff(old: Equipment): String? {
        val diff = super.diff(old)
        if (old is EquipmentDescr) {
            val sb = StringBuilder(diff ?: "")
            if (old.type != this.type) {
                sb.append("type ").append(old.type).append(" -> ").append(this.type).append("\n")
            }
            if (old.state != this.state) {
                sb.append("state ").append(old.state).append(" -> ").append(this.state).append("\n")
            }
            if (old.person != this.person) {
                sb.append("person ").append(old.person).append(" -> ").append(this.person).append("\n")
            }
            return sb.toString()
        } else {
            return diff
        }

    }
}
