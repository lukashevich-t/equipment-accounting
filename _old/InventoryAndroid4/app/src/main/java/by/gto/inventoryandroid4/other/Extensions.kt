package by.gto.inventoryandroid4.other

import android.view.View
import android.view.ViewManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import org.jetbrains.anko.custom.ankoView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
fun Date.customFormat() = df.format(this)

fun ViewManager.layoutDecoratedBarcodeView(theme: Int = 0) = layoutDecoratedBarcodeView(theme) {}
inline fun ViewManager.layoutDecoratedBarcodeView(theme: Int = 0, init: DecoratedBarcodeView.() -> Unit) = ankoView({ DecoratedBarcodeView(it) }, theme, init)


class Binder<T>(initValue: T) {
    private val bound: MutableMap<Int, (item: T) -> Unit> = HashMap()
    var item: T by Delegates.observable(initValue) { _, old, new -> if (old != new) bound.values.forEach { it(new) } }
    fun bind(id: Int, binding: (item: T) -> Unit) {
        bound.put(id, binding)
        binding(item)
    }

    fun unBind(id: Int) = bound.remove(id)
}

fun <T> View.bind(binder: Binder<T>, binding: (item: T) -> Unit) = binder.bind(this.id, binding)
fun <T> View.unBind(binder: Binder<T>) = binder.unBind(this.id)

fun String?.defaultIfEmptyOrBlank(default: String): String {
    if (this.isNullOrBlank())
        return default
    else
        return this!!
}

//fun firstNonEmpty(vararg strings: String?): String? {
//    strings.forEach { if (!it.isNullOrBlank()) return it }
//    return null
//}