package by.gto.inventoryandroid4.activities

//import by.gto.inventoryandroid3.R
//import kotlinx.android.synthetic.main.content_x.view.*
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import by.gto.inventoryandroid4.R
import by.gto.inventoryandroid4.other.*
import com.github.kittinunf.fuel.core.FuelManager
import org.jetbrains.anko.*
import java.io.File
import java.security.KeyStore
import java.security.cert.X509Certificate


class SettingsActivity : AppCompatActivity() {

    private var baseUrlField: EditText? = null
    private var keyStorePasswordField: EditText? = null
    private val context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        createView()
        loadValues()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun createView() {
        createTextFields()
        createButtons()
    }

    private fun createTextFields() {
        baseUrlField = findViewById(R.id.etBaseURL) as EditText
        keyStorePasswordField = findViewById(R.id.etKeyStorePassword) as EditText
    }

    private fun createButtons() {
        createKeyImportButton()
        createCertInfoButton()
        createOkButton()
        createCancelButton()
    }

    private fun createKeyImportButton() {
        val keyImportButton: Button = findViewById(R.id.ButtonKeyImport) as Button
        keyImportButton.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                // Check if we have write permission
                val permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                println(permission)
            }
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICKFILE_RESULT_CODE, null)
        }
    }

    private fun createCertInfoButton() {
        val certInfoButton: Button = findViewById(R.id.ButtonCertInfo) as Button
        certInfoButton.setOnClickListener {
            val certFile = File(context.filesDir, CLIENT_KEYSTORE_FILENAME)
            if (!certFile.exists()) {
                context.longToast("Файл сертификата не найден")
            } else {
                val ks = KeyStore.getInstance("PKCS12")
                try {
                    context.openFileInput(CLIENT_KEYSTORE_FILENAME).use { keyStoreStream ->
                        ks.load(keyStoreStream, MyApplication.keyStorePassword.toCharArray())

                        val aliases = ks.aliases().toList()
                        if (aliases.size != 1) {
                            context.longToast("Файл не содержит сертификатов или содержит несколько")
                        }
                        val alias = aliases[0]


                        val cert = ks.getCertificate(alias) as X509Certificate
                        val issuerAlternativeNames = cert.issuerAlternativeNames
                        println("issuerAlternativeNames:")
                        issuerAlternativeNames?.forEach { println(it) }
                        val subjectAlternativeNames = cert.subjectAlternativeNames
                        println("subjectAlternativeNames: ")
                        subjectAlternativeNames?.forEach { println(it) }

                        val certInfo = StringBuilder("Имя: ").append(alias).append("\n")
                                .append("Дата: ").append(cert.notBefore.customFormat()).append("\n")
                                .append("До: ").append(cert.notAfter.customFormat())
                        val builder = AlertDialog.Builder(context).setCancelable(false)
                                .setTitle("Инфо о сертификате").setMessage(certInfo)
                                .setNeutralButton("Принято", { dialog, _ ->
                                    dialog.cancel()
                                })

                        builder.create().show()
                    }
                } catch (ex: Exception) {
                    context.longToast(ex.message ?: "error")
                    ex.printStackTrace()
                }
            }
        }
    }

    private fun createOkButton() {
        val okButton: Button = findViewById(R.id.ButtonOk) as Button
        okButton.setOnClickListener {
            saveValues()
            startActivity<MainActivity>()
        }
    }

    private fun saveValues() {
        MyApplication.baseURL = baseUrlField?.text.toString()
//        MyApplication.userLogin = etUserLogin?.text.toString()
//        MyApplication.userPassword = etUserPassword?.text.toString()
        MyApplication.keyStorePassword = keyStorePasswordField?.text.toString()
        MyApplication.saveSettings()
        FuelManager.instance.basePath = MyApplication.baseURL.trimEnd(' ', '/')
    }

    private fun createCancelButton() {
        val cancelButton: Button = findViewById(R.id.ButtonCancel) as Button
        cancelButton.setOnClickListener {
            startActivity<MainActivity>()
        }
    }

    private fun loadValues() {
        MyApplication.loadSettings()
        baseUrlField?.setText(MyApplication.baseURL)
//        etUserLogin?.text = MyApplication.userLogin
//        etUserPassword?.text = MyApplication.Settings.userPassword
        keyStorePasswordField?.setText(MyApplication.keyStorePassword)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PICKFILE_RESULT_CODE -> if (resultCode == Activity.RESULT_OK) {
                val uri: Uri? = data?.data
                val filePath = uri?.path
                println(filePath)
                if (filePath != null) {
                    importCerificate(context, uri)
                }
            }
        }
    }

    private fun importCerificate(ctx: Context, uri: Uri?, showToast: Boolean = true) {
        try {
            // FileOutputStream(File(contentView.filesDir, CLIENT_KEYSTORE_FILENAME)).use { output ->
            ctx.openFileOutput(CLIENT_KEYSTORE_FILENAME, Context.MODE_PRIVATE).use { output ->
                ctx.contentResolver.openInputStream(uri).use { input ->
                    if (showToast) {
                        ctx.longToast("bytes copied " + input.copyTo(output))
                    }
                }
            }
            MyApplication.instance.initSSL()
        } catch (ex: Exception) {
            if (showToast) {
                ctx.longToast(ex.message ?: "Error!")
            }
        }
    }
}