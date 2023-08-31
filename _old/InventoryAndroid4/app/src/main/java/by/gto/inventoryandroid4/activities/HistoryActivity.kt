package by.gto.inventoryandroid4.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import by.gto.inventoryandroid4.R
import by.gto.inventoryandroid4.model.LogEntries
import by.gto.inventoryandroid4.model.LogEntry
import by.gto.inventoryandroid4.other.getObject
import by.gto.inventoryandroid4.other.reportError
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onItemClick

class HistoryActivity : AppCompatActivity() {
    private lateinit var ui: HistoryActivityUi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = HistoryActivityUi(intent.getSerializableExtra("guid") as String)
        ui.setContentView(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        ui.onActivityResult(requestCode, resultCode, data)
    }
}

class HistoryActivityUi(val guid: String) : AnkoComponent<HistoryActivity> {
    private val TAG = HistoryActivityUi::class.java.simpleName
    private lateinit var contentView: HistoryActivity
    private lateinit var logView: TextView
    private lateinit var listView: ListView
    private var logEntries: List<LogEntry> = emptyList()

    override fun createView(ui: AnkoContext<HistoryActivity>) = with(ui) {
        contentView = ui.owner
        val rootLayout = verticalLayout {
            weightSum = 100f
            this@HistoryActivityUi.listView = listView().lparams(weight = 60f, height = 0, width = matchParent)
            logView = textView().lparams {
                height = 0
            }.lparams(weight = 40f)
            this@HistoryActivityUi.listView.onItemClick { parent, view, position, id ->
                println("onItemClick: parent=$parent, view=$view, position=$position, id = $id")
                val logEntry = logEntries.get(position)
                logView.text = "${logEntry.time} ${logEntry.actionName}\n${logEntry.userName}\n${logEntry.comment}"
            }
        }
        with(rootLayout.layoutParams) {
            width = matchParent
        }
        try {
            getObject<LogEntries>("s/eq/getEqLog?guid=${guid}",
                    { errCode, message -> reportError(errCode, message, this.ctx, logView) }) { value, _ ->
                //                println(value?.content)
//                println(value?.content!!::class.java.canonicalName)
//                println((value.content?.entries)?.get(0)!!::class.java.canonicalName)
                logEntries = value?.content?.entries ?: emptyList()
                listView.adapter = ArrayAdapter<LogEntry>(this.ctx, android.R.layout.simple_list_item_1, logEntries)
                logView.text = ui.owner.resources.getString(R.string.message_history_loaded)
            }
        } catch (ex: Throwable) {
            Log.e(TAG, ex.message ?: "")
        }
        rootLayout
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}