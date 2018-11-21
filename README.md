# Introduction

A small demo project for recruitment.

## Architecture

Project utilises the official [Jetpack](https://developer.android.com/jetpack)
(former Architecture Components) components to implement recommended [clean app architecture](https://github.com/googlesamples/android-architecture) 
which is robust, maintainable and easy to test. To achieve offline-supported database + network 
model a repository created as single source of truth.

![app architecture](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

## Getting Started

Project consists of 5 packages :

* data (include repository class which represents data layer)
* db (include Room database class, dao and a helper class)
* model (include a model classes that key part of the application)
* ui (includes all ui related classes activities, fragments, other UI-related stuff.)

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



