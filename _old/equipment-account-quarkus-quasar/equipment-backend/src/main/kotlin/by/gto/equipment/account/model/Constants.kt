package by.gto.equipment.account.model

//const val DS_JNDI_NAME = "java:/env/jdbc/EquipmentDs"

/**
 * GUID из 16 нулей
 */
val DEFAULT_GUID = ByteArray(16) { 0 }

const val QR_SIZE = 1000

const val WRONG_GUID_FORMAT_MESSAGE = "неверный формат guid"

const val EQUIPMENT_ALREADY_EXISTS_MESSAGE = "Такой guid уже зарегистрирован"

const val EQUIPMENT_NOT_FOUND_BY_GUID = "не найдено оборудование с таким guid"

const val REF_PERSONS_TABLE_NAME = "responsible_person"
const val REF_EQUIPMENT_TYPES_TABLE_NAME = "equipment_type"
const val REF_EQUIPMENT_STATES_TABLE_NAME = "equipment_state"
const val CONTEXT_PATH = "api"
