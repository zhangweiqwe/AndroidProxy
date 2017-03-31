#include <jni.h>

JNIEXPORT jstring JNICALL
Java_cn_wsgwz_mylibrary_NativeUtil_e(JNIEnv *env, jclass type) {

    // TODO


    return (*env)->NewStringUTF(env, returnValue);
}