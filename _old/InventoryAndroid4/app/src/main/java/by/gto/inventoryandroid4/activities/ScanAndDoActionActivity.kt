package by.gto.inventoryandroid4.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.LocaleList
import android.view.KeyEvent
import android.widget.TextView
import by.gto.inventoryandroid4.R
import by.gto.inventoryandroid4.helpers.SoundHelper
import by.gto.inventoryandroid4.model.Equipment
import by.gto.inventoryandroid4.model.References
import by.gto.inventoryandroid4.other.*
import by.gto.inventoryandroid4.other.ScanProcessActions.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import org.jetbrains.anko.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate


class ScanAndDoActionActivity : Activity() {
    private lateinit var ui: CaptureForActionActivityUi
    lateinit var scanMode: ScanProcessActions private set
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        scanMode = intent.getSerializableExtra("mode") as ScanProcessActions
        ui = CaptureForActionActivityUi()
        ui.setContentView(this)

//        if (ui.scanMode == ScanProcessActions.REGISTER) {
//            ui.equipment = intent.getSerializableExtra("equipment") as Equipment
//        }
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

class CaptureForActionActivityUi : AnkoComponent<ScanAndDoActionActivity> {
    var barcodeView: DecoratedBarcodeView? = null
    //    private var beepManager: BeepManager? = null
    private var lastText = ""
    //    private  var logView: TextView?  =null
    private lateinit var logView: TextView
    //    private var barcodePreview: ImageView? = null
    lateinit var aContext: AnkoContext<ScanAndDoActionActivity>
    lateinit var scanMode: ScanProcessActions

    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == lastText) {
                // Prevent duplicate scans
                return
            }
//            val sb = "text " + result.text + "\n" +
//                    "format " + result.barcodeFormat + "\n"
//            aContext.longToast(sb)
            lastText = result.text

//            beepManager?.playBeepSoundAndVibrate()
            if (scanMode == SERIAL) {
                val data = Intent()
                data.putExtra("stringResult", result.text.replace('\n', ' '))
                aContext.owner.setResult(Activity.RESULT_OK, data)
                aContext.owner.finish()
            }

            val m = qrInventoryNumberPattern.matcher(result.text)
            if (!(result.barcodeFormat == BarcodeFormat.QR_CODE && m.matches())) {
                aContext.longToast("Wrong scan data format!")
                SoundHelper.playSound(aContext.owner, R.raw.bad_code)
                return
            }

            val dateString = try {
                val p = SimpleDateFormat("yyyyMMdd").parse(m.group(3))
                SimpleDateFormat("yyyy-MM-dd").format(p)
            } catch (_: Exception) {
                null
            }
            when (scanMode) {
                REGISTER -> {
                    val eq: Equipment = aContext.owner.intent.getSerializableExtra("equipment") as Equipment
                    eq.guid = m.group(1)
                    eq.inv_number = m.group(2)
                    eq.purchase_date =dateString
                    registerEquipment(eq)
                }
                MODIFY -> {
                    val eq = Equipment(guid = m.group(1), inv_number = m.group(2), purchase_date = dateString)
                    modifyEquipment(eq)
                }
                SERIAL -> TODO()
            }

//            try {
//                "s/eq/getEqDescr?guid=${m.group(1)}".httpGet().responseObject(JsonResponse.Deserializer(InventoryInfo::class.java)) { req, res, r ->
//                    //result is of type Result<User, Excep`tion>
//                    val (payLoad, err) = r
//                    if (err != null) {
//                        logView?.text = err.message
//                    } else {
//
//                        val msg = """
//                            ${payLoad?.content?.person}
//                            ${payLoad?.content?.inv_number}
//                            ${payLoad?.content?.type}
//                            ${payLoad?.content?.state}""".trimIndent()
//                        println(msg)
//                        logView?.text = msg
//                    }
//                }
//            } catch (ex: Throwable) {
//                Log.e(TAG, ex.message ?: "")
//            }
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }


    private fun registerEquipment(eq: Equipment) {
        postObject<Equipment, Equipment>("s/eq/putEqDescr", eq, { a, b -> reportError(a, b, aContext.owner, logView) }) { _, message ->
            SoundHelper.playSound(aContext.owner, R.raw.success)
            if (message.defaultIfEmptyOrBlank("_")[0] == ' ') {
                References.loaded.item = false
            }
            aContext.owner.finish()
        }
//        Fuel.post("s/eq/putEqDescr").body(Gson().toJson(eq)).response { request, response, httpResult ->
//            println(httpResult)
//            val (payload, err) = httpResult
//            if (err != null) {
//                SoundHelper.playSound(aContext.owner, R.raw.error)
//                aContext.longToast(err.message ?: "error")
//                return@response
//            }
//            SoundHelper.playSound(aContext.owner, R.raw.success)
//            aContext.owner.finish()
//        }
    }

    @SuppressLint("SetTextI18n")
    private fun modifyEquipment(eq: Equipment) {
        getObject<Equipment>("s/eq/getEqDescr?guid=${eq.guid}", { errCode, message -> reportError(errCode, message, aContext.owner, logView) }) { value, _ ->
            if (value?.content == null) {
                SoundHelper.playSound(aContext.owner, R.raw.error)
                logView.text = "от сервера вернулся null"
            } else {
                SoundHelper.playSound(aContext.owner, R.raw.success)
                aContext.startActivity<ParamsActivity>(
                        "mode" to MODIFY,
                        "equipment" to value.content)
            }
        }
//        "s/eq/getEqDescr?guid=${eq.guid}".httpGet().responseObject(JsonResponse.Deserializer(Equipment::class.java)) { req, res, result ->
//            println("xxx")
//            val (payload, err) = result
//            if (err != null) {
//                Log.e(this.javaClass.name, Log.getStackTraceString(err))
//                logView.text = err.message
//                SoundHelper.playSound(aContext.owner, R.raw.error)
//            } else {
//                SoundHelper.playSound(aContext.owner, R.raw.success)
////                aContext.owner.runOnUiThread {
//                aContext.startActivity<ParamsActivity>("mode" to ScanProcessActions.MODIFY,
//                        "equipment" to payload?.content)
////                }
//            }
//        }
    }

    override fun createView(ui: AnkoContext<ScanAndDoActionActivity>) = with(ui) {
        this@CaptureForActionActivityUi.aContext = ui
        val ll = verticalLayout {
            weightSum = 100f

            barcodeView = layoutDecoratedBarcodeView {}.lparams(width = matchParent, height = dip(0)) {
                weight = 70f
                height = 0
            }
//            barcodeView?.id = ViewIDs.barcodeScanner.ordinal

            logView = textView().lparams {
                weight = 30f
                height = 0
            }

            scanMode = ui.owner.scanMode
            if (scanMode == SERIAL) {
                barcodeView?.barcodeView?.decoderFactory = DefaultDecoderFactory(listOf(
                        BarcodeFormat.CODABAR, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93,
                        BarcodeFormat.CODE_128, BarcodeFormat.EAN_8, BarcodeFormat.EAN_13,
                        BarcodeFormat.ITF, BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED,
                        BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.UPC_EAN_EXTENSION
                ))
            } else {
                barcodeView?.barcodeView?.decoderFactory = DefaultDecoderFactory(listOf(
                        BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
                )
            }
            barcodeView?.decodeContinuous(callback)

//            beepManager = BeepManager(ui.owner)
        }
        with(ll.layoutParams) {
            width = matchParent
            height = matchParent
        }
        ll
    }

    fun onResume() {
        barcodeView?.resume()
        lastText = ""
    }

    fun onPause() {
        barcodeView?.pause()
    }

}