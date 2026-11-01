package com.arjunsinh.shortvideo.roomDatabase

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.concurrent.Volatile


@Database(entities = [VideoModel::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): Dao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(applicationContext: Application): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = databaseBuilder(applicationContext, AppDatabase::class.java, "video_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build()
                    }
                }
            }
            return INSTANCE
        }
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE mst_video ADD COLUMN previewImage TEXT NOT NULL DEFAULT ''")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE mst_video ADD COLUMN selectionForDelete INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}