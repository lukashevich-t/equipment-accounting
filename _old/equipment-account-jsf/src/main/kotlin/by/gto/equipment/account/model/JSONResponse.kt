package by.gto.equipment.account.model

open class JSONResponse constructor(ec: JSONErrorCodesEnum, var message: String? = null, var content: Any? = null) {
    var errCode = ec.errorCode
}
