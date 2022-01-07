# Applications Tracker
Lately I have been applying for many roles, and it has become a hard task to keep track of all the applications. Spreadsheets are a great way to store this data, but I understand how hard it becomes to check it regularly.
So this is an attempt to make our lives easier, a dedicated app for this purpose only. All the roles that you apply for, **add** them up inside the app, mark if you are **waiting for a referral**, or have you **applied** or not, and get **regular notifications** to remind you about these status, so that you don't keep waiting for the referral the next time you apply.

## Jetpack Libraries
Jetpack is a suite of libraries to help developers follow best practices, reduce boilerplate code, and write code that works consistently across Android versions and devices so that developers can focus on the code they care about. 

In this project following libraries are(and will be) used -
| Library | Used For|
|---------|:--------|
|[Navigation](https://developer.android.com/jetpack/androidx/releases/navigation)|- Navigation between screens <br> - Passing arguments between screens <br> - Handling fragment transactions |
|[Room](https://developer.android.com/jetpack/androidx/releases/room)| - Storing all the applications added by user <br> - Allowing updates and deletion <br>|
|[Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)|- View Models <br> - LiveData <br> - Flow|
|[WorkManager](https://developer.android.com/jetpack/androidx/releases/work)|- Scheduling notifications based on job status|

## Screenshots
I have implemented both Day(Light) and Night(Dark) Themes for the apps, but th theme cannot be changed from inside the app **as of now**. Adding screenshots of the screens created so far.
> I don't think there are anymore screens that need to be created, I have built all the screens and we will work on fixing issues and adding features.

|    | Light UI | Dark UI |
|----|----------|---------|
|Home Screen|<img src="screenshots/home-light.png" alt="Home Light UI" width="250px">|<img src="screenshots/home-dark.png" alt="Home Dark UI" width="250px">|
|Add Application|<img src="screenshots/add-application-light.png" alt="Add Application Light UI" width="250px">|<img src="screenshots/add-application-dark.png" alt="Add Application Dark UI" width="250px">|
|Application Details|<img src="screenshots/application-details-light.png" alt="Application Details Light UI" width="250px">|<img src="screenshots/application-details-dark.png" alt="Application Details Dark UI" width="250px">|

## Contribution
Before you start contributing, keep following things in mind - 
* In case you are doing anything related to UI(Adding Features, Enhancements, Bug Fixes), make sure you to attach revelant images.
* Don't work on an issue, that has been assigned to someone else.
* Work on an issue, only if it has been assigned to you.
* Be respectful to fellow contributors and maintainers.
