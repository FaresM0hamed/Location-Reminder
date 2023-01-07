package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    private val reminder =
        ReminderDTO(
            "reminder title", "reminder Description", "egypt",
            5.0, 3.0
        )

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun deleteAllReminder() = runTest {
        // GIVEN - Insert reminder .
        database.reminderDao().saveReminder(reminder)

        // GIVEN - Delete all reminder .
        database.reminderDao().deleteAllReminders()

        // WHEN - get all reminders from database .
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data has the correct number of reminders
        assertThat(loaded.size, `is`(0))
    }

    @Test
    fun insertReminder_GetAll() = runTest {
        // GIVEN - Insert a reminder.
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by all reminders from the database.
        val loaded = database.reminderDao().getReminders()

        // THEN - The loaded data has the correct number of reminders
        assertThat(loaded.size, `is`(2))
    }


    @Test
    fun insetReminder_GetById() = runTest {
        // GIVEN - Insert a reminder.
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the task by id
        val loaded = database.reminderDao().getReminderById(reminder.id)

        // THEN - loaded data
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
        assertThat(loaded.id, `is`(reminder.id))
    }




}