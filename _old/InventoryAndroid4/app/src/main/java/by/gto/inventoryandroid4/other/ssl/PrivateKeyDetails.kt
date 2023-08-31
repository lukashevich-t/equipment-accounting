package by.gto.inventoryandroid4.other.ssl



import java.security.cert.X509Certificate
import java.util.Arrays

/**
 * Private key details.
 *
 */
class PrivateKeyDetails(type: String?, val certChain: Array<X509Certificate>) {

    val type: String

    init {
        this.type = Args.notNull(type, "Private key type")
    }

    override fun toString(): String {
        return type + ':'.toString() + Arrays.toString(certChain)
    }

}