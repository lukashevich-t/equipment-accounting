package by.gto.inventoryandroid4.other.ssl

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.Socket
import java.net.URL
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.Principal
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.HashMap
import java.util.LinkedHashSet

import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509ExtendedKeyManager
import javax.net.ssl.X509TrustManager

/**
 * Builder for [javax.net.ssl.SSLContext] instances.
 *
 *
 * Please note: the default Oracle JSSE implementation of [SSLContext.init]
 * accepts multiple key and trust managers, however only only first matching type is ever used.
 * See for example:
 * [
 * SSLContext.html#init
](http://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLContext.html#init%28javax.net.ssl.KeyManager[],%20javax.net.ssl.TrustManager[],%20java.security.SecureRandom%29) *
 *
 */
class SSLContextBuilder {

    private var protocol: String? = null
    private val keymanagers: MutableSet<KeyManager>
    private val trustmanagers: MutableSet<TrustManager>
    private var secureRandom: SecureRandom? = null

    init {
        this.keymanagers = LinkedHashSet()
        this.trustmanagers = LinkedHashSet()
    }

    fun useProtocol(protocol: String): SSLContextBuilder {
        this.protocol = protocol
        return this
    }

    fun setSecureRandom(secureRandom: SecureRandom): SSLContextBuilder {
        this.secureRandom = secureRandom
        return this
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class)
    fun loadTrustMaterial(
            truststore: KeyStore?,
            trustStrategy: TrustStrategy?): SSLContextBuilder {
        val tmfactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
        tmfactory.init(truststore)
        val tms = tmfactory.trustManagers
        if (tms != null) {
            if (trustStrategy != null) {
                for (i in tms.indices) {
                    val tm = tms[i]
                    if (tm is X509TrustManager) {
                        tms[i] = TrustManagerDelegate(
                                tm, trustStrategy)
                    }
                }
            }
            for (tm in tms) {
                this.trustmanagers.add(tm)
            }
        }
        return this
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class)
    fun addTrustManager(m: TrustManager): SSLContextBuilder {
        trustmanagers.add(m)
        return this
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class)
    fun loadTrustMaterial(
            trustStrategy: TrustStrategy): SSLContextBuilder {
        return loadTrustMaterial(null, trustStrategy)
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class, CertificateException::class, IOException::class)
    @JvmOverloads
    fun loadTrustMaterial(
            file: File,
            storePassword: CharArray? = null,
            trustStrategy: TrustStrategy? = null): SSLContextBuilder {
        Args.notNull(file, "Truststore file")
        val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val instream = FileInputStream(file)
        try {
            trustStore.load(instream, storePassword)
        } finally {
            instream.close()
        }
        return loadTrustMaterial(trustStore, trustStrategy)
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class, CertificateException::class, IOException::class)
    @JvmOverloads
    fun loadTrustMaterial(
            url: URL,
            storePassword: CharArray,
            trustStrategy: TrustStrategy? = null): SSLContextBuilder {
        Args.notNull(url, "Truststore URL")
        val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val instream = url.openStream()
        try {
            trustStore.load(instream, storePassword)
        } finally {
            instream.close()
        }
        return loadTrustMaterial(trustStore, trustStrategy)
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class, UnrecoverableKeyException::class)
    @JvmOverloads
    fun loadKeyMaterial(
            keystore: KeyStore,
            keyPassword: CharArray,
            aliasStrategy: PrivateKeyStrategy? = null): SSLContextBuilder {
        val kmfactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm())
        kmfactory.init(keystore, keyPassword)
        val kms = kmfactory.keyManagers
        if (kms != null) {
            if (aliasStrategy != null) {
                for (i in kms.indices) {
                    val km = kms[i]
                    if (km is X509ExtendedKeyManager) {
                        kms[i] = KeyManagerDelegate(km, aliasStrategy)
                    }
                }
            }
            for (km in kms) {
                keymanagers.add(km)
            }
        }
        return this
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class, UnrecoverableKeyException::class, CertificateException::class, IOException::class)
    @JvmOverloads
    fun loadKeyMaterial(
            file: File,
            storePassword: CharArray,
            keyPassword: CharArray,
            aliasStrategy: PrivateKeyStrategy? = null): SSLContextBuilder {
        Args.notNull(file, "Keystore file")
        val identityStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val instream = FileInputStream(file)
        try {
            identityStore.load(instream, storePassword)
        } finally {
            instream.close()
        }
        return loadKeyMaterial(identityStore, keyPassword, aliasStrategy)
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class, UnrecoverableKeyException::class, CertificateException::class, IOException::class)
    @JvmOverloads
    fun loadKeyMaterial(
            url: URL,
            storePassword: CharArray,
            keyPassword: CharArray,
            aliasStrategy: PrivateKeyStrategy? = null): SSLContextBuilder {
        Args.notNull(url, "Keystore URL")
        val identityStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val instream = url.openStream()
        try {
            identityStore.load(instream, storePassword)
        } finally {
            instream.close()
        }
        return loadKeyMaterial(identityStore, keyPassword, aliasStrategy)
    }

    @Throws(KeyManagementException::class)
    protected fun initSSLContext(
            sslcontext: SSLContext,
            keyManagers: Collection<KeyManager>,
            trustManagers: Collection<TrustManager>,
            secureRandom: SecureRandom?) {
        sslcontext.init(
                if (!keyManagers.isEmpty()) keyManagers.toTypedArray() else null,
                if (!trustManagers.isEmpty()) trustManagers.toTypedArray() else null,
                secureRandom)
    }

    @Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
    fun build(): SSLContext {
        val sslcontext = SSLContext.getInstance(this.protocol ?: TLS)
        initSSLContext(sslcontext, keymanagers, trustmanagers, secureRandom)
        return sslcontext
    }

    internal class TrustManagerDelegate(private val trustManager: X509TrustManager, private val trustStrategy: TrustStrategy) : X509TrustManager {

        @Throws(CertificateException::class)
        override fun checkClientTrusted(
                chain: Array<X509Certificate>, authType: String) {
            this.trustManager.checkClientTrusted(chain, authType)
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
                chain: Array<X509Certificate>, authType: String) {
            if (!this.trustStrategy.isTrusted(chain, authType)) {
                this.trustManager.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return this.trustManager.acceptedIssuers
        }

    }

    internal class KeyManagerDelegate(private val keyManager: X509ExtendedKeyManager, private val aliasStrategy: PrivateKeyStrategy) : X509ExtendedKeyManager() {

        override fun getClientAliases(
                keyType: String, issuers: Array<Principal>): Array<String> {
            return this.keyManager.getClientAliases(keyType, issuers)
        }

        fun getClientAliasMap(
                keyTypes: Array<String>?, issuers: Array<Principal>?): Map<String, PrivateKeyDetails> {
            val validAliases = HashMap<String, PrivateKeyDetails>()
            for (keyType in keyTypes!!) {
                val aliases = this.keyManager.getClientAliases(keyType, issuers)
                if (aliases != null) {
                    for (alias in aliases) {
                        validAliases[alias] = PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias))
                    }
                }
            }
            return validAliases
        }

        fun getServerAliasMap(
                keyType: String?, issuers: Array<Principal>?): Map<String, PrivateKeyDetails> {
            val validAliases = HashMap<String, PrivateKeyDetails>()
            val aliases = this.keyManager.getServerAliases(keyType, issuers)
            if (aliases != null) {
                for (alias in aliases) {
                    validAliases[alias] = PrivateKeyDetails(keyType, this.keyManager.getCertificateChain(alias))
                }
            }
            return validAliases
        }

        override fun chooseClientAlias(
                keyTypes: Array<String>, issuers: Array<Principal>, socket: Socket): String {
            val validAliases = getClientAliasMap(keyTypes, issuers)
            return this.aliasStrategy.chooseAlias(validAliases, socket)
        }

        override fun getServerAliases(
                keyType: String, issuers: Array<Principal>): Array<String> {
            return this.keyManager.getServerAliases(keyType, issuers)
        }

        override fun chooseServerAlias(
                keyType: String, issuers: Array<Principal>, socket: Socket): String {
            val validAliases = getServerAliasMap(keyType, issuers)
            return this.aliasStrategy.chooseAlias(validAliases, socket)
        }

        override fun getCertificateChain(alias: String): Array<X509Certificate> {
            return this.keyManager.getCertificateChain(alias)
        }

        override fun getPrivateKey(alias: String): PrivateKey {
            return this.keyManager.getPrivateKey(alias)
        }

        override fun chooseEngineClientAlias(
                keyTypes: Array<String>?, issuers: Array<Principal>?, sslEngine: SSLEngine?): String {
            val validAliases = getClientAliasMap(keyTypes, issuers)
            return this.aliasStrategy.chooseAlias(validAliases)
        }

        override fun chooseEngineServerAlias(
                keyType: String?, issuers: Array<Principal>?, sslEngine: SSLEngine?): String {
            val validAliases = getServerAliasMap(keyType, issuers)
            return this.aliasStrategy.chooseAlias(validAliases)
        }

    }

    companion object {

        internal val TLS = "TLS"

        fun create(): SSLContextBuilder {
            return SSLContextBuilder()
        }
    }

}
