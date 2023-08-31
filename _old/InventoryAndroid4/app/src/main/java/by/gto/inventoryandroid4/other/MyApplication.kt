package by.gto.inventoryandroid4.other

import android.app.Application
import android.content.Context
import by.gto.inventoryandroid4.other.ssl.SSLContextBuilder
import com.github.kittinunf.fuel.core.FuelManager
//import com.squareup.leakcanary.LeakCanary
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class MyApplication : Application() {
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
//        LeakCanary.install(this)
        loadSettings()
        initSSL()
    }

     fun initSSL() {
//        val client: Client = FuelManager.instance.client
//        println(client);client.javaClass.canonicalName

        val trustAllCerts = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }

            override fun checkClientTrusted(
                    certs: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun checkServerTrusted(
                    certs: Array<java.security.cert.X509Certificate>, authType: String) {
            }
        }
        val keystore = loadKeystore()
        val sslContextBuilder = SSLContextBuilder().addTrustManager(trustAllCerts).useProtocol("SSL").setSecureRandom(SecureRandom())
        if (keystore != null) {
            sslContextBuilder.loadKeyMaterial(keystore, keyStorePassword.toCharArray())
        }


        val socketFactory: SSLSocketFactory = sslContextBuilder.build().socketFactory
        HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory)
        FuelManager.instance.client = MyFuelHttpClient(socketFactory = socketFactory)
    }

     fun loadKeystore(fileName: String = CLIENT_KEYSTORE_FILENAME): KeyStore? {
        try {
//            verifyStoragePermissions(instance)
//                println(instance.filesDir)
            val ks = KeyStore.getInstance("PKCS12")
//            val certFile = File(instance.filesDir, fileName)

            instance.openFileInput(fileName).use { keyStoreStream ->
                ks.load(keyStoreStream, keyStorePassword.toCharArray())
            }
            return ks
        } catch (ex: Exception) {
            return null
        }
    }

    companion object Settings {
        lateinit var instance: MyApplication
        get
        private set
        var userLogin: String = ""
        var userPassword: String = ""
        var keyStorePassword: String = ""
        var baseURL: String = ""


        fun saveSettings() {
            val sPrefs = instance.getSharedPreferences("db", Context.MODE_PRIVATE);
            val ed = sPrefs.edit()

            ed.putString(BASE_URL, baseURL)
            ed.putString(USER_LOGIN, userLogin)
            ed.putString(USER_PASSWORD, userPassword)
            ed.putString(KEYSTORE_PASSWORD, keyStorePassword)
            ed.apply()

        }

        fun loadSettings() {
            val sPrefs = instance.getSharedPreferences("db", Context.MODE_PRIVATE);
            Settings.baseURL = sPrefs.getString(BASE_URL, DEFAULT_BASE_URL)
            Settings.userLogin = sPrefs.getString(USER_LOGIN, "");
            Settings.userPassword = sPrefs.getString(USER_PASSWORD, "");
            Settings.keyStorePassword = sPrefs.getString(KEYSTORE_PASSWORD, "123456");
        }
    }
}

//private class Permissions(private val context: Context) : Activity() {
//    private val MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1
//    private var result: Boolean? = null
//    fun checkWriteExternalStoragePermission() {
//        val MY_PERMISSIONS_REQUEST_PHONE_CALL = 1
//        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(context as Activity,
//                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                    MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE)
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE -> {
//                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
//                    result = true
//
//                } else {
//                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
//                    result = false
//                }
//                return
//
//            }
//        }// other 'case' lines to check for other
//        // permissions this app might request
//    }
//
//    fun waitForResult() : Boolean {
//        while(result == null) {
//
//        }
//    }
//}

