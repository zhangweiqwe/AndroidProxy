#include <jni.h>
#include <iostream>

#include <android/log.h>

#define  LOG_TAG "sssssssssssssssNative"

char config1[] = "<config version=\"2.0\" dns=\"129.29.29.29\"  apn_apn=\"cmwap\" apn_proxy=\"10.0.0.172\" apn_port=\"80\">\n"
        "\n"
        "    <http host=\"10.0.0.172\" port=\"80\">\n"
        "        <delate>host , x-online-host</delate>\n"
        "        <first-line>\n"
        "            [method][tab] [url][tab] [version]\\r\\n\n"
        "            Host: [tab][host]\\r\\n\n"
        "            Accept:[tab] */*,[tab] application/vnd.wap.mms-message,[tab] application/vnd.wap.sic\\r\\n\n"
        "            Content-Type:[tab] application/vnd.wap.mms-message\\r\\n\n"
        "        </first-line>\n"
        "    </http>\n"
        "\n"
        "\n"
        "    <https host=\"10.0.0.172\" port=\"80\" switch=\"on\">\n"
        "        <delate>host , x-online-host</delate>\n"
        "        <first-line>\n"
        "            [method][tab] / [tab] [version]\\r\\n\n"
        "            Host: [tab][host]:443\\r\\n\n"
        "            Accept:[tab] */*,[tab] application/vnd.wap.mms-message,[tab] application/vnd.wap.sic\\r\\n\n"
        "            Content-Type:[tab] application/vnd.wap.mms-message\\r\\n\n"
        "        </first-line>\n"
        "    </https>\n"
        "\n"
        "</config>\n"
        "\n"
        "\n"
        "";
char config1s[] ="<config version=\"2.0\"  dns=\"114.114.114.114\"   apn_apn=\"cmwap\" apn_proxy=\"10.0.0.172\" apn_port=\"80\">\n"
        "\n"
        "    <http host=\"10.0.0.172\" port=\"80\">\n"
        "        <delate>host , x-oline-host</delate>\n"
        "        <first-line>\n"
        "            [method][tab] [url][tab] [version]\\r\\n\n"
        "            Host: [tab][host]\\r\\n\n"
        "            Accept:[tab] */*,[tab] application/vnd.wap.mms-message,[tab] application/vnd.wap.sic\\r\\n\n"
        "            Content-Type:[tab] application/vnd.wap.mms-message\\r\\n\n"
        "        </first-line>\n"
        "    </http>\n"
        "\n"
        "\n"
        "    <https host=\"10.0.0.172\" port=\"80\" switch=\"on\">\n"
        "        <delate>host , x-oline-host</delate>\n"
        "        <first-line>\n"
        "            [method][tab] [url] [tab] [version]\\r\\n\n"
        "            Host: [tab][host]:443\\r\\n\n"
        "            Accept:[tab] */*,[tab] application/vnd.wap.mms-message,[tab] application/vnd.wap.sic\\r\\n\n"
        "            Content-Type:[tab] application/vnd.wap.mms-message\\r\\n\n"
        "        </first-line>\n"
        "    </https>\n"
        "\n"
        "</config>\n"
        "\n"
        "\n"
        "";
char config2[] = "\n"
        "<config version=\"2.0\" dns=\"129.29.29.29\"  apn_apn=\"cmwap\" apn_proxy=\"10.0.0.172\" apn_port=\"80\">\n"
        "\n"
        "    <http host=\"10.0.0.172\" port=\"80\">\n"
        "        <delate>host , x-online-host</delate>\n"
        "        <first-line>\n"
        "            [method][tab] [url][tab] [version]\\r\\n\n"
        "           \\n[method] [tab]http://wap.10086.cn[uri] [tab]  [version]\\r\\n\n"
        "           Host:[host]\\r\\n\n"
        "        </first-line>\n"
        "    </http>\n"
        "\n"
        "\n"
        " <https host=\"10.0.0.172\" port=\"80\"  switch=\"on\">\n"
        "        <delate>host , x-online-host</delate>\n"
        "        <first-line>\n"
        "            [method][tab] [url][tab] [version]\\r\\n\n"
        "            Host: [tab][host]:443\\r\\n\n"
        "            Accept:[tab] */*, [tab]application/vnd.wap.mms-message, [tab]application/vnd.wap.sic\\r\\n\n"
        "            Content-Type:[tab] application/vnd.wap.mms-message\\r\\n\n"
        "        </first-line>\n"
        "    </https>\n"
        "\n"
        "</config>";


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



    if(strcmp(typeStr,"config1")==0){
        return env->NewStringUTF(config1);
    }else if(strcmp(typeStr,"config1s")==0){
        return env->NewStringUTF(config1s);
    }
    else if(strcmp(typeStr,"config2")==0){
        return env->NewStringUTF(config2);
    }

    return env->NewStringUTF(NULL);
}







