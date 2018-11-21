# Introduction

A small demo project for recruitment.

# Getting Started

## Architecture

Project utilises the official [Jetpack](https://developer.android.com/jetpack)
(former Architecture Components) components to implement recommended [clean app architecture](https://github.com/googlesamples/android-architecture) 
which is robust, maintainable and easy to test. A ViewModel is created to encapsulate UI-related 
data in a lifecycle aware way, to make app architecture more flexible and easy test data handling is
not integrated into activity nor viewmodel, a repository created as a single data source and it can 
be further implement multiple data sources local and remote. As a result activity and fragment only
contained action related code and UI and data layer separated clearly. 

![app architecture](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

## Project Structure

Project consists of 5 main packages :

* data (include repository class which represents data layer)
* db (include Room database class, dao and a helper class)
* model (include a model classes that key part of the application)
* ui (includes all ui related classes activities, fragments, other UI-related stuff.)


## Business scenario
Requirement is to make geofence alert feature that notifies the user whenever he/she enters and exits
a specified region and using Google Location API is not allowed.

## Design choices
At first my design scheme is to maintain a list of areas with minimum radius of 200 meters
and check them with 5 minutes latency using WorkManager. Implementing, testing and running correctly 
is seemed hard to accomplish in two days. After reading the LocationManager document I 
realize it has offered addProximityAlert method to create geofence alert, So I achieve this scenario 
using LocationManger#addProximityAlert api. Latency of the api is 5s to 10m and system uses it's
location service to optimize battery life and I checked the running services of the os in Xiaomi 10
it uses in-house FusionLocationProvider(which utilizes all kind of location providers) to fix location.


### Build

To build and run project correctly you should fulfill some requirements:

Android Studio Version: At least 3.1

Gradle Version: 4.6

Android Gradle Plugin Version: 3.1.4

Build Tools: At least 28.0.0

Compile SDK Version: 28

Target SDK Version: 28

Minimum SDK Version: 22


### Libraries

* [Jetpack Components](https://developer.android.com/jetpack/) 
    * [AppCompat Support Libraries](https://developer.android.com/topic/libraries/support-library/)
    * [Lifecycles](https://developer.android.com/topic/libraries/architecture/lifecycle)
    * [Room](https://developer.android.com/topic/libraries/architecture/room)
    * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
    * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
    * [Test](https://developer.android.com/topic/libraries/testing-support-library/index.html)
* [Rxjava](https://github.com/reactivex/rxjava)
* [MapBox SDK](https://www.mapbox.com/)

## Contact

Author: Alimjan Qadir

Email: alimjanqadir@qq.com



