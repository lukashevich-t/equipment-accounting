package by.gto.inventoryandroid4.activities

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import by.gto.inventoryandroid4.R
import by.gto.inventoryandroid4.helpers.FrequentScansBlocker
import by.gto.inventoryandroid4.helpers.SoundHelper
import by.gto.inventoryandroid4.model.InventoryInfo
import by.gto.inventoryandroid4.model.JsonResponse
import by.gto.inventoryandroid4.model.LogEntry
import by.gto.inventoryandroid4.other.getObject
import by.gto.inventoryandroid4.other.layoutDecoratedBarcodeView
import by.gto.inventoryandroid4.other.qrInventoryNumberPattern
import by.gto.inventoryandroid4.other.reportError
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class ContinuousCaptureActivity : Activity() {
    private lateinit var ui: ContinuousCaptureActivityUi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ui = ContinuousCaptureActivityUi()
        ui.setContentView(this)
    }

    override fun onResume() {
        super.onResume()
        ui.onResume()
    }

    override fun onPause() {
        super.onPause()
        ui.onPause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return ui.barcodeView?.onKeyDown(keyCode, event) ?: false || super.onKeyDown(keyCode, event)
    }
}


class ContinuousCaptureActivityUi : AnkoComponent<ContinuousCaptureActivity>, DecoratedBarcodeView.TorchListener {
    private val TAG = ContinuousCaptureActivityUi::class.java.simpleName
    var barcodeView: DecoratedBarcodeView? = null
    private var beepManager: BeepManager? = null
    private val scansBlocker = FrequentScansBlocker()
    private lateinit var logView: TextView
    private lateinit var switchFlashlightButton: Button
    lateinit var aContext: AnkoContext<ContinuousCaptureActivity>
    private var flashlightOn = false
    private var scanned: JsonResponse<InventoryInfo>? = null

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || !scansBlocker.shallIProcessBarcode(result.text)) {
                // Prevent duplicate scans
                return
            }

//            val sb = "text " + result.text + "\n" +
//                    "format " + result.barcodeFormat + "\n"
//            aContext.longToast(sb)
//            lastText = result.text

            beepManager?.playBeepSoundAndVibrate()
            val m = qrInventoryNumberPattern.matcher(result.text)
            if (!(result.barcodeFormat == BarcodeFormat.QR_CODE && m.matches())) {
                aContext.longToast("Wrong scan data format!")
                SoundHelper.playSound(aContext.owner, R.raw.bad_code)
                return
            }

            try {
                getObject<InventoryInfo>("s/eq/getEqDescr?guid=${m.group(1)}",
                        { errCode, message -> reportError(errCode, message, aContext.owner, logView) }) { value, _ ->
                    this@ContinuousCaptureActivityUi.scanned = value
                    val msg = """
                            ${value?.content?.inv_number} ${value?.content?.serial}
                            ${value?.content?.purchase_date}
                            ${value?.content?.person}
                            ${value?.content?.type}  ${value?.content?.state}
                            ${value?.content?.comment}
                            tap for history""".trimIndent()
                    println(msg)
                    logView.text = msg
                }
            } catch (ex: Throwable) {
                Log.e(TAG, ex.message ?: "")
            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun onTorchOn() {
        switchFlashlightButton.text = "Отключить подсветку"
    }

    override fun onTorchOff() {
        switchFlashlightButton.text = "Включить подсветку"
    }

    private fun hasFlash(): Boolean {
        return aContext.owner.applicationContext.packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun createView(ui: AnkoContext<ContinuousCaptureActivity>) = with(ui) {
        this@ContinuousCaptureActivityUi.aContext = ui
        val ll = verticalLayout {
            weightSum = 100f

            barcodeView = layoutDecoratedBarcodeView {}.lparams(width = matchParent, height = dip(0)) {
                weight = 70f
                height = 0
            }
            switchFlashlightButton = button("Включить подсветку") {
                if (hasFlash()) {
                    isEnabled = true
                    barcodeView?.setTorchListener(this@ContinuousCaptureActivityUi)
                } else {
                    isEnabled = false
                }

                onClick {
                    flashlightOn = !flashlightOn
                    if (flashlightOn) {
                        barcodeView?.setTorchOn()
                    } else {
                        barcodeView?.setTorchOff()
                    }
                }
            }
            logView = textView().lparams {
                weight = 30f
                height = 0
            }
            logView.setOnClickListener {
                val guid = this@ContinuousCaptureActivityUi.scanned?.content?.guid
                if (guid != null) {
                    startActivity<HistoryActivity>("guid" to guid)
                }
            }

            barcodeView?.barcodeView?.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39))
            barcodeView?.decodeContinuous(callback)

            beepManager = BeepManager(ui.owner)
        }
        with(ll.layoutParams) {
            width = matchParent
            height = matchParent
        }
        ll
    }

    fun onResume() {
        barcodeView?.resume()
    }

    fun onPause() {
        barcodeView?.pause()
    }
}