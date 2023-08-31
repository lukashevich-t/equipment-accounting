package by.gto.inventoryandroid4.activities

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import by.gto.inventoryandroid4.R
import org.jetbrains.anko.*


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)
        createView()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun createView(){
        createVersionText()
        createCloseButton()

    }

    private fun createVersionText(){
        val versionText: TextView = findViewById(R.id.versionText) as TextView
        versionText.setText("О программе\nВерсия ${getVersion()}")
    }

    private fun getVersion(): String?{
        val version = try {
            val pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            e.message
        }
        return version
    }

    private fun createCloseButton(){
        val closeButton: Button = findViewById(R.id.ButtonClose) as Button
        closeButton.setOnClickListener {
            startActivity<MainActivity>()
        }
    }
}