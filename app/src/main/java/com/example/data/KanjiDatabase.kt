package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [KanjiEntity::class], version = 1, exportSchema = false)
abstract class KanjiDatabase : RoomDatabase() {

    abstract fun kanjiDao(): KanjiDao

    companion object {
        @Volatile
        private var INSTANCE: KanjiDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): KanjiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KanjiDatabase::class.java,
                    "kanji_database"
                )
                    .addCallback(KanjiDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class KanjiDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.kanjiDao()
                    if (dao.getCount() == 0) {
                        dao.insertAll(KanjiInitialData.getInitialKanjis())
                    }
                }
            }
        }
    }
}
