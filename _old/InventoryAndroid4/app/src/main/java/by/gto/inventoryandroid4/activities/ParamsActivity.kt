package by.gto.inventoryandroid4.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.*
import by.gto.inventoryandroid4.R
import by.gto.inventoryandroid4.fragments.DatePickerFragment
import by.gto.inventoryandroid4.helpers.SoundHelper
import by.gto.inventoryandroid4.model.Equipment
import by.gto.inventoryandroid4.model.RefEntry
import by.gto.inventoryandroid4.model.References
import by.gto.inventoryandroid4.other.*
import by.gto.inventoryandroid4.other.ScanProcessActions.*
import com.google.zxing.client.android.BeepManager
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*


class ParamsActivity : AppCompatActivity() {

    enum class IDs {
        _unused, bSelectEqType, bSelectEqState, bSelectPerson
    }

    val context: Context = this
    lateinit var scanMode: ScanProcessActions
    var eq = Equipment()
    private var beepManager: BeepManager? = null

    lateinit var etEqType: AutoCompleteTextView
    lateinit var etEqState: AutoCompleteTextView
    lateinit var etPerson: AutoCompleteTextView
    lateinit var etComment: EditText
    lateinit var etSerial: EditText
    private lateinit var tvLog: TextView

    private lateinit var dataAdapterEqTypes: ArrayAdapter<RefEntry>
    private lateinit var dataAdapterEqStates: ArrayAdapter<RefEntry>
    private lateinit var dataAdapterPersons: ArrayAdapter<RefEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ui = ParamsActivityUi()
//        ui.setContentView(this)
        setContentView(R.layout.params_activity)
        beepManager = BeepManager(this)
        createView()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        aContext.scanMode = ScanProcessActions.valueOf(intent.getStringExtra("mode"))
        scanMode = intent.getSerializableExtra("mode") as ScanProcessActions

        when (scanMode) {
            MODIFY -> {
                eq = intent.getSerializableExtra("equipment") as Equipment
                etEqType.setText(eq.type)
                etEqState.setText(eq.state)
                etPerson.setText(eq.person)
                etComment.setText(eq.comment)
                etSerial.setText(eq.serial)
//                etPurchaseDate.setText(eq.purchase_date)
            }
            REGISTER -> {
                eq = Equipment()
                fillDefaults()
            }
            else -> {
            }
        }
    }

    private fun createView(){
        createAdapters()
        createFields()
        createButtons()
    }

    private fun createAdapters(){
        dataAdapterEqTypes = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, References.equipmentTypes)
        dataAdapterEqStates = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, References.equipmentStates)
        dataAdapterPersons = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, References.persons)
    }

    private fun createFields(){
        etEqType = findViewById(R.id.EquipmentTypeText) as AutoCompleteTextView
        etEqType.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, References.equipmentTypes))

        etEqState = findViewById(R.id.StatusTypeText) as AutoCompleteTextView
        etEqState.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, References.equipmentStates))

        etPerson = findViewById(R.id.ResponsibleTypeText) as AutoCompleteTextView
        etPerson.setAdapter(ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, References.persons))

        etComment = findViewById(R.id.CommentText) as EditText
        etSerial = findViewById(R.id.SerialNumberText) as EditText
        tvLog = findViewById(R.id.LogText) as TextView
    }

    private fun createButtons(){
        createOkButton()
        createbSelectEqTypeButton()
        createbSelectEqStateButton()
        createbSelectPersonButton()
        createBarcodeButton()
    }

    private fun createOkButton(){
        val okButton: Button = findViewById(R.id.ButtonOk) as Button
        okButton.setOnClickListener {
            val newState = etEqState.text.toString()
            val newType = etEqType.text.toString()
            val newPerson = etPerson.text.toString()
//        var sNewDate: String = etPurchaseDate.text.toString()

            if (newState.length < 2 || newType.length < 2 || newPerson.length < 5) {
                context.longToast("Не выбраны параметры")
            } else {
//        if (scanMode === MODIFY) {
//            val d = parseInputDate(sNewDate)
//            if (d == null) {
//                aContext.toast("Неверный формат даты")
//                return
//            }
//            sNewDate = formatXMLDate(d)
//            println(sNewDate)
//        }
                tvLog.text = ""
                eq.type = newType
                eq.state = newState
                eq.person = newPerson
                eq.comment = etComment.text.toString()
                eq.serial = etSerial.text.toString()
//        eq.purchase_date = sNewDate

                when (scanMode) {
                    MODIFY -> {
                        postObject<Equipment, Equipment>("s/eq/updateEqDescr", eq, { a, b -> reportError(a, b, context, tvLog) }) { value, message ->
                            println("success $message $value")
//                    aContext.owner.runOnUiThread {
                            SoundHelper.playSound(context, R.raw.success)
//                    }
                            if (message.defaultIfEmptyOrBlank("_")[0] == ' ') {
                                References.loaded.item = false;
                            }
                            startActivity<MainActivity>()
                        }
                    }
                    REGISTER -> {
                        val equipment = Equipment(null, newType, newPerson, newState,
                                etComment.text.toString(), null, null, etSerial.text.toString())
                        startActivity<ScanAndDoActionActivity>(
                                "mode" to REGISTER,
                                "equipment" to equipment)
                    }
                    SERIAL -> TODO()
                }
            }
        }
    }

    private fun createbSelectEqTypeButton(){
        val bSelectEqTypeButton: ImageButton = findViewById(R.id.bSelectEqType) as ImageButton
        bSelectEqTypeButton.setOnClickListener {
            onClickSelector(IDs.bSelectEqType.ordinal)
        }
    }

    private fun createbSelectEqStateButton(){
        val bSelectEqStateButton: ImageButton = findViewById(R.id.bSelectEqState) as ImageButton
        bSelectEqStateButton.setOnClickListener {
            onClickSelector(IDs.bSelectEqState.ordinal)
        }
    }

    private fun createbSelectPersonButton(){
        val bSelectPersonButton: ImageButton = findViewById(R.id.bSelectPerson) as ImageButton
        bSelectPersonButton.setOnClickListener {
            onClickSelector(IDs.bSelectPerson.ordinal)
        }
    }

    private fun onClickSelector(id: Int) {

        val title: String
        val adapter: ArrayAdapter<*>
        val array: List<RefEntry>
        val control: TextView

        when (id) {
            IDs.bSelectEqType.ordinal -> {
                title = "Тип оборудования"
                adapter = dataAdapterEqTypes
                control = etEqType
                array = References.equipmentTypes
            }
            IDs.bSelectEqState.ordinal -> {
                title = "Состояние оборудования"
                adapter = dataAdapterEqStates
                control = etEqState
                array = References.equipmentStates
            }
            IDs.bSelectPerson.ordinal -> {
                title = "Ответственный"
                adapter = dataAdapterPersons
                control = etPerson
                array = References.persons
            }
            else -> return
        }
        val builder: AlertDialog.Builder
        builder = AlertDialog.Builder(context)
        builder.setTitle(title)
                .setAdapter(adapter) { dialog, which ->
                    try {
                        control.setFocusable(false)
                        control.setFocusableInTouchMode(false)
                        control.setText(array[which].name)
                        control.setFocusable(true)
                        control.setFocusableInTouchMode(true)
                    } catch (e: Exception) {
                    }

                    dialog.dismiss()
                }.create().show()
    }

    private fun createBarcodeButton(){
        val barcodeButton: ImageButton = findViewById(R.id.SerialNumberButton) as ImageButton
        barcodeButton.setOnClickListener {
            startActivityForResult<ScanAndDoActionActivity>(SCAN_SERIAL_RESULT_CODE, "mode" to SERIAL)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SCAN_SERIAL_RESULT_CODE -> if (resultCode == Activity.RESULT_OK && intent != null) {
                try {
                    val stringResult = intent.getStringExtra("stringResult")
                    etSerial.setText(stringResult)
                } catch (_: Exception) {
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tvLog.text = ""
    }

    fun fillDefaults() {
//        etPurchaseDate.setText(formatReadableDate(Date()))
        etEqType.setText("Системный блок")
        etEqState.setText("Новый")
    }
}