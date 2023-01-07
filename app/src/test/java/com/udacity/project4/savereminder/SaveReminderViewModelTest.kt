package com.udacity.project4.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.udacity.project4.R
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var reminderDataSource: ReminderDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        reminderDataSource = FakeDataSource()
        viewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }


    /**
     * Save reminder and show saved snack bar
     */
    @Test
    fun saveReminder_showSnackBarSaved() {
        //create a reminder
        val reminder = ReminderDataItem("reminder title", "reminder Description", "egypt", 5.0, 3.0)


        //save reminder
        viewModel.saveReminder(reminder)
        val value = viewModel.showToast.getOrAwaitValue()
        Assert.assertEquals(value, "Reminder Saved !")
    }

    /**
     * Save reminder and navigate back
     */
    @Test
    fun saveReminder_navigateBack() {
        //create a reminder
        val reminder = ReminderDataItem("reminder title", "reminder Description", "egypt", 5.0, 3.0)

        //save reminder
        viewModel.saveReminder(reminder)

        //navigate back
        val value = viewModel.navigationCommand.getOrAwaitValue()

        //assert that is navigation working
        Assert.assertEquals(value, NavigationCommand.Back)
    }

    /**
     * Save reminder and show loading
     */
    @Test
    fun saveReminder_showLoading() {
        //create a reminder
        val reminder = ReminderDataItem("reminder title", "reminder Description", "egypt", 5.0, 3.0)

        //pause dispatcher to verify initial values.
        mainCoroutineRule.pauseDispatcher()

        //save reminder
        viewModel.saveReminder(reminder)

        //asset that show loading equals true
        val loadingValue = viewModel.showLoading.getOrAwaitValue()
        Assert.assertEquals(loadingValue, true)

        //execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        //asset that show loading equals false
        Assert.assertEquals(loadingValue, true)
    }

    /**
     * Test invalid Title and show snack bar title error
     */
    @Test
    fun validateEnteredData_invalidTitle() {
        //enter wrong title
        val reminder = ReminderDataItem(null, "reminder Description", "egypt", 5.0, 3.0)

        //validate entered data
        viewModel.validateEnteredData(reminder)
        val value = viewModel.showSnackBarInt.getOrAwaitValue()
        Assert.assertEquals(value, R.string.err_enter_title)
    }

    /**
     * Test invalid location and show snack bar title error
     */
    @Test
    fun validateEnteredData_invalidLocation() {
        //enter wrong location
        val reminder = ReminderDataItem("reminder title", "reminder Description", null, 5.0, 3.0)

        //validate entered data
        viewModel.validateEnteredData(reminder)
        val value = viewModel.showSnackBarInt.getOrAwaitValue()

        //assert that show enter location error
        Assert.assertEquals(value, R.string.err_select_location)
    }





}