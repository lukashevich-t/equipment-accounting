package by.gto.inventoryandroid4.other.ssl

import java.net.Socket

/**
 * A strategy allowing for a choice of an alias during SSL authentication.
 *
 */
interface PrivateKeyStrategy {

    /**
     * Determines what key material to use for SSL authentication.
     *
     * @param aliases available private key material
     * @param socket socket used for the connection. Please note this parameter can be `null`
     * if key material is applicable to any socket.
     */
    fun chooseAlias(aliases: Map<String, PrivateKeyDetails>, socket: Socket): String
    fun chooseAlias(aliases: Map<String, PrivateKeyDetails>): String

}