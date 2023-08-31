package by.gto.equipment.account.helpers

import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JRField

class HibernateQueryResultDataSource(list: List<*>, private val fields: Array<String>) : JRDataSource {
    private val iterator: Iterator<*>
    private var currentValue: Any? = null

    init {
        this.iterator = list.iterator()
    }

    override fun getFieldValue(field: JRField): Any? {
        var value: Any? = null
        val index = getFieldIndex(field.name)
        if (index > -1) {
            val values = currentValue as Array<Any>?
            value = values!![index]
        }
        return value
    }

    override fun next(): Boolean {
        currentValue = if (iterator.hasNext()) iterator.next() else null
        return currentValue != null
    }

    private fun getFieldIndex(field: String): Int {
        var index = -1
        for (i in fields.indices) {
            if (fields[i] == field) {
                index = i
                break
            }
        }
        return index
    }

}
