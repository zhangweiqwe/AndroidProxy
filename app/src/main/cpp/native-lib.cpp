#include <jni.h>
#include <android/log.h>
#include <string>
#include <stdlib.h>
#include <unistd.h>

#define LOG_TAG "daemons"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)



int isRun(){
    FILE *fstream=NULL;
    char buff[1024];
    memset(buff,0,sizeof(buff));
    int b = 0;
    if(NULL==(fstream=popen("ps | grep -i \"cn.wsgwz.gravity:remoteProxy\"","r"))) //ls -l
    {
        b = 1;
        //fprintf(stderr,"execute command failed: %s",strerror(errno));
       // LOGD("execute command failed: ");
    }
    if(NULL!=fgets(buff, sizeof(buff), fstream))
    {
        b = 1;
        // printf("%s",buff);
        LOGD("%s",buff);
    }

    pclose(fstream);
    return b;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_cn_wsgwz_gravity_MainActivity_getString(JNIEnv *env, jobject instance) {

   /* int i = isRun();
    LOGD("isRun%d",i);
    if(i){
        LOGD("isRun");
    }*/
    char buf[1024];
    FILE *file = popen("ps | grep -i \"cn.wsgwz.gravity:remoteProxy\"","r");
    //FILE *file = popen("cd ..\nls","r");
    if(!file==NULL){
        while (fgets(buf, sizeof(buf), file) != NULL) {
            if (buf[strlen(buf) - 1] == '\n') {
                buf[strlen(buf) - 1] = '\0'; //去除换行符
            }
            LOGD("%s",buf);
            pclose(popen("am startservice --user 0 -n cn.wsgwz.gravity/cn.wsgwz.gravity.service.ProxyService","r"));
        }

    } else{
        LOGD("file==NULL ");
    }
    pclose(file);


    LOGD("445 ");



    /*  execlp("am", "am", "startservice", "--user", "0", "-n",
         "cn.wsgwz.gravity/cn.wsgwz.gravity.service.ProxyService",
         (char *) NULL);*/
    /*  execlp("am", "am", "start", "--user", "0", "-a",
             "android.intent.action.VIEW", "-d",
             "https://sunnyxibei.github.io/", (char *) NULL);
  */
    /* execlp("am", "am", "startservice", "--user", "0", "-n",
            "cn.wsgwz.gravity/cn.wsgwz.gravity.service.ProxyService",
            (char *) NULL);*/

    return env->NewStringUTF("fdfd");
}
/*int32_t myexec(const char *cmd, vector<string> &resvec) {
    resvec.clear();
    FILE *pp = popen(cmd, "r"); //建立管道
    if (!pp) {
        return -1;
    }
    char tmp[1024]; //设置一个合适的长度，以存储每一行输出
    while (fgets(tmp, sizeof(tmp), pp) != NULL) {
        if (tmp[strlen(tmp) - 1] == '\n') {
            tmp[strlen(tmp) - 1] = '\0'; //去除换行符
        }
        resvec.push_back(tmp);
    }
    pclose(pp); //关闭管道
    return resvec.size();
}*/
