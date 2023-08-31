package by.gto.inventoryandroid4.helpers

class FrequentScansBlocker(val blockPeriod: Long = 1600L) {

    private var lastTimestamp = 0L
    private var lastBarcode = ""

    fun shallIProcessBarcode(barcode: String): Boolean {
        val timestamp = System.currentTimeMillis()
        if (lastBarcode.equals(barcode) && timestamp - lastTimestamp < blockPeriod) {
            return false
        }
        lastTimestamp = timestamp
        lastBarcode = barcode
        return true
    }

}