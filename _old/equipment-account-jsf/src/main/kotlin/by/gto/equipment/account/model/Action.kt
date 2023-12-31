package by.gto.equipment.account.model

enum class Action(val id: Int, val readableName: String, val selectable: Boolean = false) {
    /**
     * Действие-заглушка. Означает отсутствие действия
     */
    NONE(0, "Нет действия"),
    /**
     * Создание записи об оборудовании
     */
    EQUIPMENT_CREATE(1, "Создание"),
    /**
     * Изменение записи об оборудовании
     */
    EQUIPMENT_MODIFY(2, "Изменение"),

    /**
     * Обслуживание оборудования
     */
    EQUIPMENT_SERVICE(3, "Обслуживание", true),

    /**
     * Списание оборудования
     */
    EQUIPMENT_WRITEOFF(4, "Списание", true);

    override fun toString() = readableName
}