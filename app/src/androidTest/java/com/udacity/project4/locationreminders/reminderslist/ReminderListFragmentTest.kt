package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.R
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.koin.androidx.viewmodel.dsl.viewModel
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.mockito.Mockito
import org.mockito.Mockito.mock
import androidx.test.espresso.assertion.ViewAssertions.matches

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var repo: FakeDataSource
    private val reminder =
        ReminderDTO(
            "reminder title", "reminder Description", "egypt",
            5.0, 3.0
        )

    private val testModule = module {
        repo = FakeDataSource(mutableListOf())
        viewModel {
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), repo)
        }
        single {
            FakeDataSource() as ReminderDataSource
        }

    }

    @Before
    fun setUp() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        loadKoinModules(testModule)
    }

    @After
    fun cleanUp() = runTest {
        unloadKoinModules(testModule)
    }


    @Test
    fun reminderList_NoReminders() = runTest {
        // GIVEN - Add reminder
        repo.deleteAllReminders()

        // WHEN - reminders list fragment launched
        val scenario  = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.Theme_Project4)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // THEN - reminder shouldn't displayed reminders
        onView(withText(reminder.title)).check(doesNotExist())
        onView(withText(R.string.no_data)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    //test the displayed data on the UI.
    @Test
    fun reminderList_foundReminder() = runTest {
        //GIVEN - Add reminder
        repo.saveReminder(reminder)

        // WHEN - reminders list fragment launched
        val scenario = launchFragmentInContainer<ReminderListFragment>(
            Bundle(),
            R.style.Theme_Project4
        )
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // THEN - reminder must be displayed with its title
        onView(withText(reminder.title)).check(matches(isDisplayed()))
    }



    //test the navigation
    @Test
    fun clickAddReminder_navigateToSaveReminder() {

        // GIVEN - On the ReminderListFragment
        val scenario = launchFragmentInContainer<ReminderListFragment>(
            Bundle(),
            R.style.Theme_Project4
        )
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the add fab
        onView(withId(R.id.addReminderFAB)).perform(ViewActions.click())

        // THEN - Make sure that we navigate to the save reminder screen
        Mockito.verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }
}