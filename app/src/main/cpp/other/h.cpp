//
// Created by Jeremy Wang on 2016/12/16.
//
#include <demo.h>
#include <android/log.h>
#define LOG_TAG "ssssssssss"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C"
int main()
{
    LOGD("是否运行=%s", "正常");
    return 0;
}
