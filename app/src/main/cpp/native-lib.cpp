#include <jni.h>
#include <iostream>

#include <android/log.h>
#include <unistd.h>
#define LOG_TAG "sssssssssssssNDK"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT void JNICALL
Java_cn_wsgwz_gravity_util_NativeUtils_fork(JNIEnv *env, jclass type) {

 //TODO
    int pid = fork();
    FILE *f;

    if(pid>0){
        //创建进程成功
        LOGD("pid = %d", pid);
    } else if(pid==0){
        while (1){
            sleep(1);
            LOGD("pid = %d", pid);
            LOGD("ForkTest状态？ = %s", "正常");
            //获取 父进程ID
            int ppid = getppid();
            LOGD("ppid = %d", ppid);
            //判断父进程ID 如果Fork的父进程变成ID = 1 说明 要么卸载  要么被杀掉了
            if (ppid == 1) {
                f = fopen("/data/data/cn.wsgwz.gravity", "r");
                if (f == NULL) {
                    //被卸载了  弹出一个网页
                    //linux回收的这个进程的时候 会把里面的代码执行完毕 并强行杀死当前进程
                    execlp("am", "am", "start", "--user", "0", "-a",
                           "android.intent.action.VIEW", "-d",
                           "https://sunnyxibei.github.io/", (char *) NULL);
                } else {
                    //被杀掉了 重新开启
                    LOGD("重启代码执行了吗？ = %s", "execlp代码执行前");
                 /*   execlp("am", "am", "start", "--user", "0", "-n",
                           "cn.wsgwz.gravity/cn.wsgwz.gravity.Restart",
                           (char *) NULL);*/
                    execlp("am", "am", "start", "--user", "0", "-n",
                           "cn.wsgwz.gravity/cn.wsgwz.gravity.Restart",
                           (char *) NULL);

                    LOGD("重启代码执行了吗？ = %s", "execlp代码执行后");
                }
            }
        }
    } else{
        //小于0 创建失败
        LOGD("pid = %d", pid);
    }


}

extern "C"
JNIEXPORT void JNICALL
Java_cn_wsgwz_gravity_util_NativeUtils_serverToClient(JNIEnv *env, jclass type,
                                                      jobject remoteSocket, jobject clientSocket) {
    char logStr[] = "ndk----serverToClient";
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s", logStr);
    // TODO

}

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_wsgwz_gravity_util_NativeUtils_getConfig(JNIEnv *env, jclass type, jstring typeStr_) {
   const char *typeStr = env->GetStringUTFChars(typeStr_, 0);

    // TODO

    env->ReleaseStringUTFChars(typeStr_, typeStr);
   // __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s", typeStr);



   /* if(strcmp(typeStr,"config1")==0){
        return env->NewStringUTF(config1);
    }else if(strcmp(typeStr,"config1s")==0){
        return env->NewStringUTF(config1s);
    }
    else if(strcmp(typeStr,"config2")==0){
        return env->NewStringUTF(config2);
    } else if(strcmp(typeStr,"configL1")==0){
        return env->NewStringUTF(configL1);
    }*/

    return env->NewStringUTF(NULL);
}







