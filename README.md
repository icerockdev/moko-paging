![moko-paging](img/logo.png)  
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Download](https://img.shields.io/maven-central/v/dev.icerock.moko/paging) ](https://repo1.maven.org/maven2/dev/icerock/moko/paging) ![kotlin-version](https://img.shields.io/badge/kotlin-1.4.31-orange)

# Mobile Kotlin paging
This is a Kotlin MultiPlatform library that contains pagination logic for kotlin multiplatform

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Versions](#versions)
- [Installation](#installation)
- [Usage](#usage)
- [Samples](#samples)
- [Set Up Locally](#set-up-locally)
- [Contributing](#contributing)
- [License](#license)

## Features
- **Pagination** implements pagination logic for the data from abstract `PagedListDataSource`.
- Managing a data loading process using **Pagination** asynchronous functions: `loadFirstPage`, `loadNextPage`,
`refresh` or their duplicates with `suspend` modifier.
- Observing states of **Pagination** using `LiveData` from **moko-mvvm**.

## Requirements
- Gradle version 6.0+
- Android API 16+
- iOS version 9.0+

## Versions
### Bintray
- kotlin 1.3.61
  - 0.1.0
- kotlin 1.3.70
  - 0.2.0
  - 0.2.1
  - 0.2.2
  - 0.3.0
  - 0.3.1
- kotlin 1.4.0
  - 0.4.0
- kotlin 1.4.21
  - 0.4.1
  - 0.4.2
  - 0.4.3
### mavenCentral
- kotlin 1.4.31
  - 0.4.4
  - 0.4.5

## Installation
root build.gradle  
```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
```

project build.gradle
```groovy
dependencies {
    commonMainApi("dev.icerock.moko:paging:0.4.6")
}
```

## Usage

You can use **Pagination** in `commonMain` sourceset.

**Pagination** creation:

```kotlin
val pagination: Pagination<Int> = Pagination(
        parentScope = coroutineScope,
        dataSource = LambdaPagedListDataSource { currentList ->
            extrenalRepository.loadPage(currentList) 
        },
        comparator = Comparator { a: Int, b: Int ->
            a - b
        },
        nextPageListener = { result: Result<List<Int>> ->
            if (result.isSuccess) {
                println("Next page successful loaded")
            } else {
                println("Next page loading failed")
            }
        },
        refreshListener = { result: Result<List<Int>> ->
            if (result.isSuccess) {
                println("Refresh successful")
            } else {
                println("Refresh failed")
            }
        },
        initValue = listOf(1, 2, 3)
    )
```

Managing data loading:

```kotlin
// Loading first page
pagination.loadFirstPage()

// Loading next page
pagination.loadNextPage()

// Refreshing pagnation
pagination.refresh()

// Setting new list
pagination.setData(itemsList)
```

Observing **Pagination** states:

```kotlin
// Observing the state of the pagination
pagination.state.addObserver { state: ResourceState<List<ItemClass>, Throwable> -> 
    // ...
}

// Observing the next page loading process
pagination.nextPageLoading.addObserver { isLoading: Boolean -> 
    // ...
}

// Observing the refresh process
pagination.refreshLoading.addObserver { isRefreshing: Boolean -> 
    // ...    
}
```

## Samples
Please see more examples in the [sample directory](sample).

## Set Up Locally 
- The [paging directory](paging) contains the `paging` library;
- The [sample directory](sample) contains sample apps for Android and iOS; plus the mpp-library connected to the apps.

## Contributing
All development (both new features and bug fixes) is performed in the `develop` branch. This way `master` always contains the sources of the most recently released version. Please send PRs with bug fixes to the `develop` branch. Documentation fixes in the markdown files are an exception to this rule. They are updated directly in `master`.

The `develop` branch is pushed to `master` on release.

For more details on contributing please see the [contributing guide](CONTRIBUTING.md).

## License
        
    Copyright 2020 IceRock MAG Inc.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
