#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_wsgwz_gravity_MainActivity_getString(JNIEnv *env, jobject instance) {

    // TODO


    return env->NewStringUTF("fdfd");
}

