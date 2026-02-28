package com.habittracker.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.habittracker.core.database.converter.LocalDateTimeConverter
import com.habittracker.core.database.converter.LocalTimeConverter
import com.habittracker.core.database.dao.AlarmDao
import com.habittracker.core.database.dao.DeviceUsageLogDao
import com.habittracker.core.database.dao.HabitDao
import com.habittracker.core.database.dao.HabitLogDao
import com.habittracker.core.database.dao.HabitScheduleDao
import com.habittracker.core.database.entity.AlarmEntity
import com.habittracker.core.database.entity.DeviceUsageLogEntity
import com.habittracker.core.database.entity.HabitEntity
import com.habittracker.core.database.entity.HabitLogEntity
import com.habittracker.core.database.entity.HabitScheduleEntity
import net.zetetic.database.sqlcipher.SupportOpenHelper

@Database(
    entities = [
        HabitEntity::class,
        HabitScheduleEntity::class,
        HabitLogEntity::class,
        DeviceUsageLogEntity::class,
        AlarmEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(LocalDateTimeConverter::class, LocalTimeConverter::class)
abstract class HabitTrackerDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitScheduleDao(): HabitScheduleDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun deviceUsageLogDao(): DeviceUsageLogDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        private const val DATABASE_NAME = "habittracker.db"
        @Volatile
        private var instance: HabitTrackerDatabase? = null

        fun getInstance(context: Context): HabitTrackerDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): HabitTrackerDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                HabitTrackerDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory { configuration ->
                    SupportOpenHelper.Factory(
                        "habittracker_passphrase".toByteArray(),
                        null,
                        null
                    ).openHelper(configuration)
                }
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
