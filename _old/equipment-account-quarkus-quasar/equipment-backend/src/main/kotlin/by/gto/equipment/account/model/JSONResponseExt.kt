package by.gto.equipment.account.model

class JSONResponseExt<T>(errorCode: Int, message: String? = null, content: T? = null, refChanged: Boolean = false) : JSONResponse<T>(errorCode, message, content) {
    val referencesChanged = refChanged
}
