//
// Created by Jin,Yalin on 2017/2/16.
//

#include <stdlib.h>
#include <string>
#include <iostream>
#include <android/log.h>
#include <unistd.h>


#include <string.h>
#include <jni.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

#include <sys/resource.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/wait.h>
#include <pthread.h>
#include <fstream>
#define PROC_DIRECTORY "/proc/"
#define CASE_SENSITIVE    1
#define CASE_INSENSITIVE  0
#define EXACT_MATCH       1
#define INEXACT_MATCH     0
#define MAX_LINE_LEN 5
#define  TAG    "daemon"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
using namespace std;

/**
 * 执行命令
 */
void ExecuteCommandWithPopen(char* command, char* out_result,
                             int resultBufferSize) {
    FILE * fp;
    out_result[resultBufferSize - 1] = '\0';
    fp = popen(command, "r");
    if (fp) {
        fgets(out_result, resultBufferSize - 1, fp);
        out_result[resultBufferSize - 1] = '\0';
        pclose(fp);
    } else {
        LOGD("popen null,so exit");
        exit(0);
    }
}
void check_and_restart_service(char* service ,char* srvaction) {
    __android_log_print(ANDROID_LOG_ERROR,TAG,"--------->ok");
    char cmdline[200];
    //sprintf(cmdline, "am startservice --user 0 -n %s", service);
    sprintf(cmdline, "am start  -n %s -a %s --user 0", service,srvaction);
    char tmp[200];
    sprintf(tmp, "cmd=%s", cmdline);
    ExecuteCommandWithPopen(cmdline, tmp, 200);

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
}

int main(int argc, char *argv[]) {

    sprintf(argv[0], "nativeGuard");

    FILE *file;

    char *srvname = NULL;
    char *srvaction = NULL;
    char *lock_file = NULL;
    char *interval = NULL;
    if (argc >= 5) {
        srvname = argv[1];
        srvaction = argv[2];
        lock_file = argv[3];
        interval = argv[4];
    } else {
        return 1;
    }

    ofstream fout( lock_file );
    if ( fout ) { // 如果创建成功
        fout << "写入内容" << endl; // 使用与cout同样的方式进行写入
        fout.close();  // 执行完操作后关闭文件句柄
    }

    if ((file = fopen(lock_file, "w")) == NULL) {
        LOGD("watchdog_native: open file failed");
        return 1;
    }
    fprintf(file, "watchdog native");
    fclose(file);

    int intervalSecond = atoi(interval);
    //char srvname[] = "cn.wsgwz.gravity/cn.wsgwz.gravity.service.ProxyService";
    //char srvname[] = "cn.wsgwz.gravity/cn.wsgwz.gravity.nativeGuard.OnePixelActivity";
    struct rlimit r;

    int pid = fork();

    LOGD("fork pid: %d", pid);
    if (pid < 0) {
        LOGD("first fork() error pid %d,so exit", pid);
        exit(0);
    } else if (pid != 0) {
        LOGD("first fork(): I'am fatherss pid=%d", getpid());
        //exit(0);
    } else { //  第一个子进程
        LOGD("first fork(): I'am child pid=%d", getpid());
        setsid();
        LOGD("first fork(): setsid=%d", setsid());
        umask(0); //为文件赋予更多的权限，因为继承来的文件可能某些权限被屏蔽

        int pid = fork();
        if (pid == 0) { // 第二个子进程
            // 这里实际上为了防止重复开启线程，应该要有相应处理

            LOGD("I'am child-child pid=%d", getpid());
            chdir("/"); //<span style="font-family: Arial, Helvetica, sans-serif;">修改进程工作目录为根目录，chdir(“/”)</span>
            //关闭不需要的从父进程继承过来的文件描述符。
            if (r.rlim_max == RLIM_INFINITY) {
                r.rlim_max = 1024;
            }
            int i;
            for (i = 0; i < r.rlim_max; i++) {
                close(i);
            }

            umask(0);
                while(1){
                    sleep(intervalSecond);
                    file = fopen(lock_file, "r");
                    if (file)
                        fclose(file);
                    else
                        break;

                    check_and_restart_service(srvname, srvaction);
                }




        } else {
            exit(0);
        }
    }
    return 0;
}





