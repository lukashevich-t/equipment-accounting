package by.gto.equipment.account.model

import by.gto.equipment.account.helpers.toGuidString
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.jetbrains.annotations.NotNull

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

    /**
     * copy constructor
     */
    constructor(source: Equipment) : super(source)

    constructor()

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): EquipmentDescr {
        return super<Equipment>.clone() as EquipmentDescr
    }

    override fun toString(): String {
        return "EquipmentDescr(guid=${guid.toGuidString()}, type=$type, typeId=$typeId, person=$person, personId=$personId, state=$state, stateId=$stateId, comment=$comment, invNumber=$invNumber, purchaseDate=$purchaseDate, serial=$serial)"
    }

    override fun diff(old: Equipment): String {
        val diff = super.diff(old)
        if (old is EquipmentDescr) {
            val sb = StringBuilder(diff)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as EquipmentDescr

        if (type != other.type) return false
        if (person != other.person) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (person?.hashCode() ?: 0)
        result = 31 * result + (state?.hashCode() ?: 0)
        return result
    }

    /**
     *  Для сравнения двух записей без учета идентификаторов (typeId, stateId, personId)
     */
    fun compareIgnoringIds(other: EquipmentDescr): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false

        return this.guid.contentEquals(other.guid) &&
                this.comment == other.comment &&
                this.invNumber == other.invNumber &&
                this.serial == other.serial &&
                this.purchaseDate == other.purchaseDate &&
                type == other.type &&
                person == other.person &&
                state == other.state
    }
}
