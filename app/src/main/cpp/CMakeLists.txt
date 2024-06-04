cmake_minimum_required(VERSION 3.22.1)

project("fatjs")

set(NDK_PATH /Users/linsheng/Library/Android/sdk/ndk/25.0.8775105) #记得修改一下ndk路径
set (CMAKE_CXX_STANDARD 17)
set(CMAKE_SYSTEM_NAME ANDROID)
set(CMAKE_BUILD_TYPE Debug)
set(CMAKE_SYSTEM_VERSION 21)
set(ANDROID_PLATFORM 21)
set(ANDROID_SDK_TOOLS_VERSION 20)
set(ANDROID_ABI arm64-v8a)
set(ANDROID_NDK ${NDK_PATH})
set(CMAKE_TOOLCHAIN_FILE ${NDK_PATH}/build/cmake/android.toolchain.cmake)
set(ANDROID_SDK_ROOT ${NDK_PATH})

include_directories(
        ./include
        ./include/tools
)

file(GLOB_RECURSE src "./src/*.cpp")

add_library(${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp
        ${src})  # 包含所有 src 目录下的 cpp 文件

target_link_libraries(${CMAKE_PROJECT_NAME}
        # List libraries link to the target library
        android
        log)