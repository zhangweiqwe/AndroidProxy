# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.

set(CMAKE_VERBOSE_MAKEFILE on)

set(EXECUTABLE_OUTPUT_PATH  "${CMAKE_CURRENT_SOURCE_DIR}/src/main/assets/${ANDROID_ABI}")

add_executable( # Specifies the name of the library.
             watchdog_local_server_socket

             # Provides a relative path to your source file(s).
             src/main/cpp/watchdog.cpp )

find_library(
             log-lib

             log)

target_link_libraries( # Specifies the target library.
                       watchdog_local_server_socket

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )