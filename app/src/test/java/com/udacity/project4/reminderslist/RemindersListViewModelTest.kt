package com.udacity.project4.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import com.google.common.truth.Truth.assertThat


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var reminderDataSource: FakeDataSource
    private val reminder =
        ReminderDTO(
            "reminder title", "reminder Description", "egypt",5.0,3.0)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        reminderDataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    /**
     * Load all reminder and assert that it shows error
     */
    @Test
    fun loadReminders_showError() {
        //pause dispatcher to verify initial values.
        mainCoroutineRule.pauseDispatcher()

        //show error.
        reminderDataSource.setReturnError(true)

        //load reminders
        viewModel.loadReminders()

        //execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        //assert that error showing with title Error while getting reminders
        assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Can't get reminders")
    }
    /**
     * Load all reminders and assert that it shows data
     */
    @Test
    fun loadReminders_showData() = runTest {
        //save reminder for testing
        reminderDataSource.saveReminder(reminder)

        //load all reminders
        viewModel.loadReminders()
        val value = viewModel.showNoData.getOrAwaitValue()

        //assert that showNoData is false .
        Assert.assertEquals(value, false)
    }
    /**
     * Load all reminders and assert that it shows loading
     */
    @Test
    fun loadReminders_showLoading() {
        //pause dispatcher to verify initial values.
        mainCoroutineRule.pauseDispatcher()

        //load the task in the view model.
        viewModel.loadReminders()

        //assert that the progress indicator is shown.
        MatcherAssert.assertThat(viewModel.showLoading.getOrAwaitValue(), Is.`is`(true))

        //execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        //then assert that the progress indicator is hidden.
        MatcherAssert.assertThat(viewModel.showLoading.getOrAwaitValue(), Is.`is`(false))
    }


}