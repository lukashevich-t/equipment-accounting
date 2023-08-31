package by.gto.inventoryandroid4.other

import android.content.Context
import android.widget.TextView
import by.gto.inventoryandroid4.R
import by.gto.inventoryandroid4.helpers.SoundHelper
import org.jetbrains.anko.longToast

fun reportError(errCode: Int?, message: String, ctx: Context, log: TextView? = null) {
//    ctx.runOnUiThread {
        println("error $errCode $message")
        SoundHelper.playSound(ctx, R.raw.error)
        val msg = "$message ($errCode)"
        ctx.longToast(msg)
//        if (log != null) log.text = msg
        log?.text = msg
//    }
}