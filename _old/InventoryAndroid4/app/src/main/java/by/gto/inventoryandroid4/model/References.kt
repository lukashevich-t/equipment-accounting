package by.gto.inventoryandroid4.model

import android.content.Context
import android.util.Log
import by.gto.inventoryandroid4.helpers.database
import by.gto.inventoryandroid4.other.Binder
import com.github.kittinunf.fuel.httpGet
import org.jetbrains.anko.db.*
import org.jetbrains.anko.doAsync
import java.util.*

// class LoadRefAsyncTask : AsyncTask<String, Void, List<RefEntry>> {
//     override fun doInBackground(vararg params: String?): List<RefEntry> {
//         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//     }
//
//
// }


object References {
    var persons: List<RefEntry> = listOf()
    var equipmentStates: List<RefEntry> = listOf()
    var equipmentTypes: List<RefEntry> = listOf()
    var loaded: Binder<Boolean> private set
    var pairs = createPairs()

    private fun loadRef(url: String, message: StringBuilder): List<RefEntry> {
        var result: List<RefEntry> = Collections.emptyList()
        try {
            val (_, _, httpResult) = url.httpGet().responseObject(JsonResponse.Deserializer(RefContent::class.java))
//            url.httpGet().responseObject(JsonResponse.Deserializer(RefContent::class.java)) { req, res, httpResult ->
            //result is of type Result<User, Excep`tion>
            val (payload, err) = httpResult

            if (err != null) {
                Log.e(this.javaClass.name, Log.getStackTraceString(err))
                message.append(err.message).append('\n')
            } else {
                val msg = """
                                err: $err
                                errCode: ${payload?.errCode}
                                message: ${payload?.message}
                                content: ${payload?.content}""".trimIndent()

                println(msg)
                result = payload?.content?.data ?: result
            }
//            }
        } catch (ex: Throwable) {
            loaded.item = false
            Log.e(this.javaClass.name, ex.message)
            message.append(ex.message)
        }
        return result
    }

    fun updateReferences(ctx: Context, callback: (String) -> Unit) {
        val message = StringBuilder()
        loaded.item = false
        doAsync {
            try {
                equipmentStates = loadRef("s/r/eqStates", message).sortedBy { it.name }
                equipmentTypes = loadRef("s/r/eqTypes", message).sortedBy { it.name }
                persons = loadRef("s/r/persons", message).sortedBy { it.name }
                pairs = createPairs()
                loaded.item = message.isEmpty()
                // Обновить справочники во внутренней БД:
                if (loaded.item) {
                    ctx.database.use {
                        for ((table, ref) in pairs) {
                            delete(table, "")
                            ref.forEach { entry ->
                                insert(table, "id" to entry.id, "name" to entry.name)
                            }
                        }
                    }
                }
                if (loaded.item) {
                    callback("Справочники успешно обновлены")
                } else {
                    callback(message.toString())
                }
            } catch (ex: Exception) {
                message.append("\n").append(ex.message ?: "Exception")
                callback(message.toString())
            } finally {
                pairs = createPairs()
            }
        }

    }

    fun readReferences(ctx: Context): String =
            try {
                ctx.database.use {
                    select("equipment_state", "id", "name").exec {
                        this@References.equipmentStates = parseList(classParser<RefEntry>())
                    }
                    select("equipment_type", "id", "name").exec {
                        this@References.equipmentTypes = parseList(classParser<RefEntry>())
                    }
                    select("responsible_person", "id", "name").exec {
                        this@References.persons = parseList(classParser<RefEntry>())
                    }
                    ""
                }
            } catch (ex: Exception) {
                ex.message ?: "Exception"
            } finally {
                pairs = createPairs()
            }


    private fun createPairs() = listOf("equipment_state" to References.equipmentStates, "equipment_type" to References.equipmentTypes, "responsible_person" to References.persons)

    init {
        loaded = Binder(true)
    }
}