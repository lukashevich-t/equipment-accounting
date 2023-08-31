package by.gto.equipment.account.model

open class JSONResponse<T> constructor(errorCode: Int, var message: String? = null, var content: T? = null) {
    var errCode = errorCode

    companion object {
        const val CODE_COMMON_SYSTEM_ERROR = -1
        const val CODE_COMMON_USER_ERROR = 1
        const val CODE_OK = 0
    }
}
