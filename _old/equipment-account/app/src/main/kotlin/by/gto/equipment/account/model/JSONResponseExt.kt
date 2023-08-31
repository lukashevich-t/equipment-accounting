package by.gto.equipment.account.model

class JSONResponseExt(ec: JSONErrorCodesEnum, message: String? = null, content: Any? = null, refChanged: Boolean = false) : JSONResponse(ec, message, content) {
    val referencesChanged = refChanged
}