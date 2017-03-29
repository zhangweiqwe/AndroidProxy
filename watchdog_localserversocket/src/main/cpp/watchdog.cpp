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

#define PROC_DIRECTORY "/proc/"
#define CASE_SENSITIVE    1
#define CASE_INSENSITIVE  0
#define EXACT_MATCH       1
#define INEXACT_MATCH     0
#define MAX_LINE_LEN 5
#define  TAG    "daemon"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
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
        LOGI("popen null,so exit");
        exit(0);
    }
}
void check_and_restart_service(char* service) {
    LOGI("当前所在的进程pid=");
    char cmdline[200];
    sprintf(cmdline, "am startservice --user 0 -n %s", service);
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


    char srvname[] = "cn.wsgwz.gravity/cn.wsgwz.gravity.service.ProxyService";
    struct rlimit r;

    int pid = fork();
    LOGI("fork pid: %d", pid);
    if (pid < 0) {
        LOGI("first fork() error pid %d,so exit", pid);
        exit(0);
    } else if (pid != 0) {
        LOGI("first fork(): I'am father pid=%d", getpid());
        //exit(0);
    } else { //  第一个子进程
        LOGI("first fork(): I'am child pid=%d", getpid());
        setsid();
        LOGI("first fork(): setsid=%d", setsid());
        umask(0); //为文件赋予更多的权限，因为继承来的文件可能某些权限被屏蔽

        int pid = fork();
        if (pid == 0) { // 第二个子进程
            // 这里实际上为了防止重复开启线程，应该要有相应处理

            LOGI("I'am child-child pid=%d", getpid());
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

                int stdfd = open ("/dev/null", O_RDWR);
                dup2(stdfd, STDOUT_FILENO);
                dup2(stdfd, STDERR_FILENO);

                while(1){
                    check_and_restart_service(srvname);
                    sleep(4);
                }

        } else {
            exit(0);
        }
    }
    return 0;
}





