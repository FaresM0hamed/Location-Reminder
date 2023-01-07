package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.udacity.project4.locationreminders.data.dto.Result

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var localDataSource: RemindersLocalRepository
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

        localDataSource = RemindersLocalRepository(
            database.reminderDao(),
            Dispatchers.Main
        )
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getReminderNotExist_returnReminderNotFound() = runTest {
        // GIVEN - wrong id for reminder
        val reminderId = "230"

        // WHEN  - reminder retrieved by ID.
        val result = localDataSource.getReminder(reminderId)

        // THEN - return Reminder not found!
        result as Result.Error
        MatcherAssert.assertThat(result.message, `is`("Reminder not found!"))
    }

    @Test
    fun getRemindersNoReminders_returnEmptyList() = runTest {
        // GIVEN - No reminders saved in the database.

        // WHEN  - get reminders.
        val result = localDataSource.getReminders()

        // THEN -return empty list
        result as Result.Success
        MatcherAssert.assertThat(result.data, Is.`is`(emptyList()))
    }

    @Test
    fun saveReminder_ReturnReminderByID() = runTest {
        // GIVEN - A new reminder saved in the database.
        database.reminderDao().saveReminder(reminder)

        // WHEN  - Task retrieved by ID.
        val result = localDataSource.getReminder(reminder.id)

        // THEN - Same task is returned.
        result as Result.Success
        MatcherAssert.assertThat(result.data.title, Is.`is`("reminder title"))
        MatcherAssert.assertThat(result.data.description, Is.`is`("reminder Description"))
        MatcherAssert.assertThat(result.data.location, Is.`is`(reminder.location))
        MatcherAssert.assertThat(result.data.longitude, Is.`is`(reminder.longitude))
        MatcherAssert.assertThat(result.data.latitude, Is.`is`(reminder.latitude))
    }


}