# Boggle Multiplatform
[![Build](https://github.com/alejandro-rios/Boggle-Multiplatform/actions/workflows/build.yml/badge.svg)](https://github.com/alejandro-rios/Boggle-Multiplatform/actions/workflows/build.yml)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)

![badge-android](http://img.shields.io/badge/platform-android-3DDC84.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-EAEAEA.svg?style=flat)
![badge-desktop](http://img.shields.io/badge/platform-desktop-4D76CD.svg?style=flat)
![badge-browser](https://img.shields.io/badge/platform-js-F0DB4F.svg?style=flat)
![badge-browser](https://img.shields.io/badge/platform-wasm-654FF0.svg?style=flat)

A simple Boggle game inspired on this [site](https://wordshake.com/boggle), the App is implemented with Kotlin and Compose Multiplatform, 
have fun searching words in a 4x4 board of letters, the idea behind this App is to explore and play with [KMP](https://kotlinlang.org/docs/multiplatform.html) and [CMP](https://www.jetbrains.com/lp/compose-multiplatform/).

## 🎲 Game mechanics
You will play with a 4x4 board looking for words with minimum `3 letters`, each word guessed will give you points 
that you'll see in the `Score` value, the game is finished when you find the total words available for the board, 
now, here's some details of the game:

- There's a `Score` marker next to a `Words found/ Total words` counter.
- A Boggle board with a progress line around it (it works but needs to be improved 😅)
- An `Use API solver`: this option let's you choose either to solve the board with the local or the API solver, the 
  main difference is that the local solver will find less words than the API, is checked by default.
- `English` toggle: you can play looking for words on English or Spanish, by default the board is on English, to 
  change for a Spanish board you'll need to first uncheck the `Use API solver` checkbox.
- The `Rotate` button will rotate the board so it might help you to see new words.
- The `New game` button will start a new game.
- The `Give me a hint` button will help you with a word if you can't think on one.
- There's a `Words counter` section from which you'll see the the number of words available per number of letters.
- You can click on each word guessed to see it's definition.

## 📢 Important mentions
- Local solver `getBoardSolutionOne` was implemented and adjusted based on [Ryan Grasell's repo](https://github.com/rgrasell/BoggleSolver/)
- Local solver `getBoardSolutionTwo` was implemented and adjusted based on [Ali Shalabi's repo](https://github.com/alishalabi/boggle), here's the [article](https://medium.com/@ashalabi13/solving-boggle-using-depth-first-searches-and-prefix-trees-9c3faa89ea99) that explains his implementation.
- API solver url is from [Codebox Boggle solver](https://codebox.net/pages/boggle-game-solver-and-web-service)
- Word definitions are obtained from the [Free Dictionary API](https://dictionaryapi.dev/)
- Huge thanks to [Andres Rojas](https://andresrojas.dev/) for helping me with the initial steps of the project.


## 📽️ Showcase
### Android

https://github.com/alejandro-rios/Boggle-Multiplatform/assets/10689052/8bed288b-3a23-44db-915a-bb56a71e4232


### iOS

https://github.com/alejandro-rios/Boggle-Multiplatform/assets/10689052/fb97fa43-0c40-4107-96fb-577f89984a42


### Desktop

https://github.com/alejandro-rios/Boggle-Multiplatform/assets/10689052/e3fb9226-3e85-4d3b-800a-02c3d2b059fb


### Web

https://github.com/alejandro-rios/Boggle-Multiplatform/assets/10689052/77167768-94ba-4dc7-96a5-a1e21d265908


## 📚 Tech stack
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform): Declarative framework for sharing UIs across multiple platforms.
- [Ktor](https://github.com/ktorio/ktor): A framework for building asynchronous server-side and client-side applications with ease.
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines): Simple library used to run Asynchronous or non-blocking calls.
- [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization): Simple library used to serialize data.
- [KStore](https://github.com/xxfast/KStore): A tiny Kotlin multiplatform library that assists in saving and restoring objects.
- [Jetpack ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel): A business logic or screen level state holder.
- [Koin](https://insert-koin.io/): A smart Kotlin injection library.
- [Turbine](https://github.com/cashapp/turbine): A small testing library for kotlinx.coroutines Flow.
- [Mokkery](https://mokkery.dev/): The mocking library for Kotlin Multiplatform, easy to use, boilerplate-free and compiler plugin driven.

## ⚙️ Architecture
The architecture used for the application consists of the following:

- Clean Architecture.
- Repository pattern.
- MVVM pattern.

This project uses a basic UiState (`BoggleUiState`) to handle the application interface state at a given moment.

## 📓 Notes and considerations
This project is not finished, there's a lot of improvements that can be made, and I'm pretty sure that the app has 
some performance issues (on the Compose side) that are going to be fixed.
