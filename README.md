## Student Deliverables

**1- APK file of final project** 
     Project4/APK/

**2- Git Repository with code**
    https://github.com/FaresM0hamed/Project4
   
**Testing**

**1- androidTest:**

   - Unit test for:
     - ReminderLocalRepository that including:
       - save reminder and retrive reminder by id 
       - get reimnder with no reminders 
       - get reminder not exist 

     - ReminderDao that including:
    	 - Insert and get all from Database  
       - Insert and get by id from Database
     	- delete all from Database
 
   - UI Testing for:
     - ReminderListFragmentTest that including: 
       - add reminder then check if displayed on the ui 
       - delete all reminder then assert that no data is displayed with icon 
       - click on add reminder button to check if navigate to reminder fragment 
     
     - RemindersActivity that including: 
       - add reminder with out enter title to test the snack bar errors for title  
       - add reminder with out enter location to test the snack bar errors for location 
       - add reminder with all requirements "title, description and location"
     
     
**2- test:**

 - Test SaveReminderViewModel that including:
     -  validate entered data when invalid title is entered and show snack bar errors
     -  validate entered data when invalid location is entered show snack bar errors
     -  validate entered data when all entered and show toast! 
     -  save reminder and navigate back after save 
     -  save reminder and pause dispater show loading 

 - Test RemindersListViewModel that including: 
     - load reminders and assert that is shows data 
     - load reminder and pause dispater to shows loading 
     - load reminder and pause dispater and set error to true to shows error
