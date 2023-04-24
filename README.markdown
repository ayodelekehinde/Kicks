![](/assets/graphic.png)

# Kicks

Kicks is a simple music streaming app built with
**Kotlin Multiplatform Mobile** and **Compose Multiplatform**

| Platforms | ![](https://img.shields.io/badge/Android-black.svg?style=for-the-badge&logo=android) ![](https://img.shields.io/badge/iOS-black.svg?style=for-the-badge&logo=apple)  |
|-----------|---|


### How does it work?

It streams free audio medias; from [Pixabay](https://pixabay.com/music/). The app uses expect/actual to delegate 
the [AudioPlayer](https://github.com/ayodelekehinde/Kicks/blob/f144c2dc614f5680e0685f99a31cd977559809c1/shared/src/commonMain/kotlin/io/github/kicks/audioplayer/AudioPlayer.kt)
to both platforms using AVPlayer on iOS and Media3 on Android.


### 📱 Sneak peak
### Screenshots

![](/assets/android.png)
![](/assets/iOS.png)


#### ▶️ Android
https://user-images.githubusercontent.com/61739400/234006497-cbabc218-3bbc-44f4-b81c-a792af78a70f.gif



#### ▶️ iOS

https://user-images.githubusercontent.com/61739400/234008361-54ef31d7-e492-445c-92f4-22e723d6d2f8.mp4

---

## Built with

- [Kotlin](kotlinlang.org): Programming language
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html): For building multi-platform applications in the single codebase.
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/): For a shared UI between Android and iOS.
- [AVPlayer](https://developer.apple.com/documentation/avfoundation/avplayer): Foundation Media player library for iOS
- [Media3](https://developer.android.com/guide/topics/media/media3): Exoplayer based Media player library for Android
- [UI Design](https://webdesign.tutsplus.com/tutorials/music-player-app-ui-design-adobe-xd--cms-34793): design for the UI.

## TODOs

Currently, this is a one screen app, showing playlist and controls. More work to be done.

### ✅ Completed
- [x] Playlist and controls UI
- [x] Basic controls play/pause/next/prev
- [x] Auto next
- [x] Seek
- [x] Current duration
- [x] Auto scroll if currently played item is hidden

### 🚧 To be done
- [ ] Full player screen
- [ ] Background play
- [ ] Player notification
- [ ] Maybe a library out off all this.

## Setting up project 👨🏻‍💻

- Refer to the ***"Setting up environment"*** section of [this repository](https://github.com/JetBrains/compose-multiplatform-ios-android-template/main/README.md)
  for knowing the setup guidelines
- After validating requirements as per the above guide, clone this repository.
- Open this project in Android Studio Electric Eel or newer version.
- Build project 🔨 and see if everything is working fine.
- Run App
    - Select "androidApp" as run configuration and you'll be able to run the Android app.
    - Select "iosApp" as run configuration and you'll be able to run the iOS app _(XCode can also be used to run the app)_.

## Project structure

This Compose Multiplatform project includes three modules:

### [`shared`](/shared)
This is a Kotlin module that contains the logic common for both Android and iOS applications, the code you share between platforms.
This shared module is also where you write your Compose Multiplatform code. In `shared/src/commonMain/kotlin/App.kt`, you can find the shared root `@Composable` function for your app.
It uses Gradle as the build system. You can add dependencies and change settings in `shared/build.gradle.kts`. The shared module builds into an Android library and an iOS framework.

### [`androidApp`](/androidApp)
This is a Kotlin module that builds into an Android application. It uses Gradle as the build system. The `androidApp` module depends on and uses the shared module as a regular Android library.

### [`iosApp`](/iosApp)
This is an Xcode project that builds into an iOS application. It depends on and uses the shared module as a CocoaPods dependency.

---

## Contribute

If you want to contribute to this library, you're always welcome!
See [Contributing Guidelines](CONTRIBUTING.md).

## Discuss 💬

Have any questions, doubts or want to present your opinions, views? You're always welcome.
You can [start discussions](https://github.com/PatilShreyas/Foodium-KMM/discussions).

## Acknowledgements

- [JetBrains/compose-multiplatform-ios-android-template](https://github.com/JetBrains/compose-multiplatform-ios-android-template#readme):
  For Starter template
- [Fooduim-Kmm](https://github.com/PatilShreyas/Foodium-KMM/README.md): For an awesome readme template.
  

## License

```
Copyright 2023 Ayodele Kehinde

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
