package by.gto.inventoryandroid4.other

import java.util.*
import java.util.regex.Pattern


val BASE_URL = "baseUrl"
val USER_LOGIN = "userLogin"
val USER_PASSWORD = "userPassword"
val KEYSTORE_PASSWORD = "keyStorePassword"
val PICKFILE_RESULT_CODE = 1
val SCAN_SERIAL_RESULT_CODE = 1
val CLIENT_KEYSTORE_FILENAME = "client.p12"
val REQUEST_EXTERNAL_STORAGE = 1

val ERRCODE_PARSE = -4

val DEFAULT_LOCALE = Locale.getDefault()

//val DEFAULT_BASE_URL = "https://10.0.2.2:8445/equipment-account/" // for android emulator
//val DEFAULT_BASE_URL = "https://192.168.200.232:8445/equipment-account/"
val DEFAULT_BASE_URL = "https://vpn.gto.by:8445/equipment-account/"
//val DEFAULT_BASE_URL = "https://192.168.200.232:8445/EquipmentAccounting/"

val qrInventoryNumberPattern = Pattern.compile("^(\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12})\n([^\n]*)\n(\\d{8})?$")
//val qrInventoryNumberPattern = "^(\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12})\n([^\n]*)\n(\\d{8})?$".toRegex()

enum class ScanProcessActions : java.io.Serializable {
    REGISTER, MODIFY, SERIAL
}