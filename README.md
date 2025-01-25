<h1>My Image Scraping</h1>

<p>
  <a href="https://kotlinlang.org"><img alt="Kotlin Version" src="https://img.shields.io/badge/Kotlin-2.0.21-blueviolet.svg?style=flat"/></a>
  <a href="https://android-arsenal.com/api?level=24"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://developer.android.com/studio/releases/gradle-plugin"><img alt="AGP" src="https://img.shields.io/badge/AGP-8.5.2-blue?style=flat"/></a>
</p>

<img src="/screen/main.png" width="30%" height="30%" title="main" alt="main"/>

This sample demonstrates how to scraping image url at site on Android.

https://pixabay.com/ko/photos/?q=test

In some cases, you may want to customize this specific url. When doing so, change the url.
For example, see Constants.BASE_URL and the ApiService getPhotos method in this sample.

# Android

- Supports Android Studio Koala
- minsdk 24
- targetSdk 35
- AGP 8.5.2
- Gradle 8.7


# Language

- [Kotlin](https://kotlinlang.org)


# JetPack [AAC(Android Architecture Components)](https://blog.naver.com/dev2jb/223230422126)

- Data Binding - Requires kapt plugin
- LifeCycles
- LiveData
- ViewModel


# UI

- SplashScreen
- MainActivity
- Light and Dark Mode


# Architectural Patterns

- [MVVM](/screen/mvvm.png)


# Dependency Injection

- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Use Kapt plugin or Ksp plugin


# Asynchronous

- Coroutine
- Flow
- Sealed class


# 3rd Party Libraries

- [Jsoup](https://github.com/jhy/jsoup)
- [Retrofit](https://github.com/square/retrofit)
- [Glide](https://github.com/bumptech/glide) - https://bumptech.github.io/glide/doc/configuration.html


# Build Dependency

- [version catalog](https://developer.android.com/build/migrate-to-catalogs)


# Troubleshooting
- Migrate from kapt to KSP : https://developer.android.com/build/migrate-to-ksp
- Migrate your splash screen implementation to Android 12 and later : https://developer.android.com/develop/ui/views/launch/splash-screen/migrate


****
License
-------

Copyright JB.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.