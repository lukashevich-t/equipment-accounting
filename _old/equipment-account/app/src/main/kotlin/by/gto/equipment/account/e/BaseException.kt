package by.gto.equipment.account.e

open class BaseException : Exception {
    constructor() : super()
    constructor(msg: String) : super(msg)
    constructor(msg: String, cause: Throwable) : super(msg, cause)
    constructor(cause: Throwable) : super(cause)
}