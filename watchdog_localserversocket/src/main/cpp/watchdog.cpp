//
// Created by Jin,Yalin on 2017/2/16.
//

#include <stdlib.h>
#include <string>
#include <iostream>
#include <android/log.h>
using namespace std;

int main(int argc, char *argv[]) {

    string hello = "Hello from C++";
    cout << "Message from native code: zhangwei " << hello << "\n";
    __android_log_print(ANDROID_LOG_DEBUG,"ssssssssssss","Message from native code: zhangwei: Android Log");

    return EXIT_SUCCESS;
}