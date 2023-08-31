package by.gto.equipment.account.model


class BaseReference() {
    var id: Int = 0
    var name: String = ""

    constructor(id: Int, name: String) : this() {
        this.id = id
        this.name = name
    }

    override fun toString() = name
}
