package by.gto.equipment.account.mappers

import by.gto.equipment.account.model.Equipment
import by.gto.equipment.account.model.EquipmentDescr
import by.gto.equipment.account.model.EquipmentSearchTemplate
import by.gto.equipment.account.model.REF_EQUIPMENT_STATES_TABLE_NAME
import by.gto.equipment.account.model.REF_EQUIPMENT_TYPES_TABLE_NAME
import by.gto.equipment.account.model.REF_PERSONS_TABLE_NAME
import java.lang.StringBuilder
import java.util.UUID

class SQLProviders {
    fun searchEquipment(template: EquipmentSearchTemplate): String {
        val header = """
SELECT e.id guid, e.type_id typeId, e.person_id personId, e.state_id stateId, e.comment, e.inv_number invNumber, 
    e.purchase_date purchaseDate, e.serial, es.name state, et.name `type`, rp.name person FROM equipment e
LEFT JOIN ${REF_PERSONS_TABLE_NAME} rp ON e.person_id = rp.id
LEFT JOIN ${REF_EQUIPMENT_STATES_TABLE_NAME} es ON e.state_id = es.id
LEFT JOIN ${REF_EQUIPMENT_TYPES_TABLE_NAME} et ON e.type_id = et.id 
where """
        val cond = StringBuilder()
        if (template.personId > 0) {
            cond.append(" (e.person_id = #{personId}) and")
        }

        if (template.typeId > 0) {
            cond.append(" (e.type_id = #{typeId}) and")
        }

        if (template.stateId > 0) {
            cond.append(" (e.state_id = #{stateId}) and")
        }

        if (!template.comment.isNullOrEmpty()) {
            cond.append(" (e.comment like #{comment}) and")
            template.comment = "%${template.comment}%"
        }

        val purchaseDate = template.purchaseDate
        if (purchaseDate != null) {
            cond.append(" (e.purchase_date = #{purchaseDate}) and")
        }

        if (!template.serial.isNullOrEmpty()) {
            cond.append(" (e.serial like #{serial}) and")
            template.serial = "%${template.serial}%"
        }

        if (template.invNumber.isNotEmpty()) {
            cond.append(" (e.inv_number like #{invNumber}) and")
            template.invNumber = "%${template.invNumber}%"
        }
        if (cond.isEmpty()) {
            return header + "(1 = 0)"
        }
        // отрезать последний оператор AND:
        cond.setLength(cond.length - 4)
        return header + cond.toString()
    }
}
