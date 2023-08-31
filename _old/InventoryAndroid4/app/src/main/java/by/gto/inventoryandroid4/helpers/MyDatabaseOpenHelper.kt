package by.gto.inventoryandroid4.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import by.gto.inventoryandroid4.model.References
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
//        db.createTable("Customer", true,
//                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
//                "name" to TEXT,
//                "photo" to BLOB)

        for ((table, _) in References.pairs) {
            db.createTable(table, true,
                    "id" to INTEGER + UNIQUE,
                    "name" to TEXT)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
//        db.dropTable("User", true)
    }
}

// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(applicationContext)