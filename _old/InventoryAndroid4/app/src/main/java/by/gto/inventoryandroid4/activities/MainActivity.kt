package by.gto.inventoryandroid4.activities

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import by.gto.inventoryandroid4.R
import by.gto.inventoryandroid4.model.*
import by.gto.inventoryandroid4.other.*
import by.gto.inventoryandroid4.other.MyApplication.Settings.loadSettings
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import org.jetbrains.anko.*
import java.io.File
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.X509Certificate


class MainActivity : AppCompatActivity() {

    private var log: TextView? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        val intent: Intent
        when (id) {
            R.id.action_settings -> {
                intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_reload_refs -> {
                updateReferences()
                return true
            }
            R.id.test_fuel -> {
                testFuel()
                return true
            }
            R.id.test_certificates -> {
                testCertificates(resources, this)
                return true
            }
            R.id.show_about -> {
                intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        loadSettings()
        FuelManager.instance.basePath = MyApplication.baseURL.trimEnd(' ', '/')
        References.readReferences(this)
        updateReferences()
        verifyStoragePermissions(this)
        createView()
    }

    private fun updateReferences() {
        References.updateReferences(this) { msg: String ->
            runOnUiThread {
                log?.setText(msg)
                println(msg)
            }

        }
    }

    private fun createView(){
        createLogView()
        createButtons()
    }

    private fun createLogView(){
        log = findViewById(R.id.LogText) as TextView
    }

    private fun createButtons(){
        createScanButton()
        createRegisterButton()
        createChangeButton()
        createSettingsButton()
    }

    private fun createScanButton(){
        val scanButton: Button = findViewById(R.id.ButtonScan) as Button
        scanButton.setOnClickListener {
            startActivity<ContinuousCaptureActivity>()
        }
    }

    private fun createRegisterButton(){
        val registerButton: Button = findViewById(R.id.ButtonRegister) as Button
        registerButton.setOnClickListener {
            startActivity<ParamsActivity>("mode" to ScanProcessActions.REGISTER)
        }
        registerButton.isEnabled = isButtonEnable()
    }

    private fun createChangeButton(){
        val changeButton: Button = findViewById(R.id.ButtonChange) as Button
        changeButton.setOnClickListener {
            startActivity<ScanAndDoActionActivity>("mode" to ScanProcessActions.MODIFY)
        }
        changeButton.isEnabled = isButtonEnable()
    }

    private fun isButtonEnable(): Boolean{
        return References.loaded.item
    }

    private fun createSettingsButton(){
        val settingsButton: Button = findViewById(R.id.ButtonSettings) as Button
        settingsButton.setOnClickListener {
            startActivity<SettingsActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        log?.setText("")
    }

    @TargetApi(23)
    private fun verifyStoragePermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)
            return
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.MANAGE_DOCUMENTS),
                    REQUEST_EXTERNAL_STORAGE
            )

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toast("FIle Permission Granted")
//                    val extsd = Environment.getExternalStorageDirectory()
//                    importCerificate(this, Uri.parse("file://$extsd/Download/ltv.p12"))
                } else {
                    longToast("File Permission Denied")
//                    finish()
                    val builder = AlertDialog.Builder(this).setCancelable(false)
                            .setTitle("Внимание!").setMessage("Работа по протоколу HTTPS невозможна, т.к. у приложения нет доступа к файловой системе, на которой хранится сертификат клиента")
                            .setNeutralButton("Принято", { dialog, _ ->
                                dialog.cancel()
                                this.finish()
                            })

                    builder.create().show()
                }
                return

            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    private fun testCertificates(resources: Resources, ctx: ContextWrapper) {
        // Environment.getDataDirectory() - FAIL
        // getExternalStorageDirectory() - OK
        ctx.filesDir.list().forEach { filename ->
            println(filename)
            if (!File(ctx.filesDir, filename).isDirectory) {
                try {
                    ctx.openFileInput(filename).use { stream ->
                        println(stream)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }


//                    val filesDir = aContext.owner.filesDir
//                    val certFile = File(filesDir, "ltv.p12")
//                    resources.openRawResource(R.raw.ltv).use { keyStoreStream ->
//                        FileOutputStream(certFile).use { fos ->
//
//                        }
//                    }
        try {
            val ks = KeyStore.getInstance("PKCS12")
            val keyStoreStream: InputStream = resources.openRawResource(R.raw.test_cert)
            ks.load(keyStoreStream, "123456".toCharArray())
//                        ks.load(javaClass.classLoader.getResourceAsStream("ltv.p12"), "123456".toCharArray())
            val aliases = ks.aliases().toList()
            println("aliases:")
            aliases.forEach { alias ->
                println(alias)
                val certificate = ks.getCertificate(alias) as X509Certificate
                val issuerAlternativeNames = certificate.issuerAlternativeNames
                println("issuerAlternativeNames:")
                issuerAlternativeNames?.forEach { println(it) }
                val subjectAlternativeNames = certificate.subjectAlternativeNames
                println("subjectAlternativeNames: ")
                subjectAlternativeNames?.forEach { println(it) }

                println(certificate)
                println(certificate.javaClass.canonicalName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun testFuel() {
        try {
            "s/r/eqStates".httpGet().responseObject(JsonResponse.Deserializer(RefContent::class.java)) { _, _, result ->
                //result is of type Result<User, Excep`tion>
                val (user, err) = result
                if (err != null) {
//                                longToast(err.localizedMessage)
                    Log.e(this.javaClass.name, Log.getStackTraceString(err))
                } else {
                    val msg = """
                                err: $err
                                errCode: ${user?.errCode}
                                message: ${user?.message}
                                content: ${user?.content}""".trimIndent()

                    println(msg)
//                            longToast(msg)
                }
            }
        } catch (ex: Throwable) {
            Log.e(this.javaClass.name, ex.message)
//                        toast(ex.message ?: "")
        }
    }
}
