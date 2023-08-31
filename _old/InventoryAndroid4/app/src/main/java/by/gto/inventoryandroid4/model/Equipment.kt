package by.gto.inventoryandroid4.model

//@JsonIgnoreProperties(ignoreUnknown = true)
data class Equipment(var guid: String? = null, var type: String? = null,
                     var person: String? = null, var state: String? = null,
                     var comment: String? = null, var inv_number: String? = null,
                     var purchase_date: String? = null, var serial: String? = null) : java.io.Serializable {
    var type_id: Short = 0
    var person_id: Int = 0
    var state_id: Short = 0

    companion object {
        private val serialVersionUID = -7065561067157570239L
    }
}