#include <jni.h>
#include <iostream>
JNIEXPORT jobject JNICALL
Java_cn_wsgwz_gravity_util_NativeUtils_readLine(JNIEnv *env, jclass type, jobject in) {

    // TODO


}

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_wsgwz_gravity_util_NativeUtils_demo(JNIEnv *env, jclass type) {

    // TODO


    return env->NewStringUTF("----");
}


