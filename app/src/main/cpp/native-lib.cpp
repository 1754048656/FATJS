#include <jni.h>
#include <string>
#include <unistd.h>
#include <fcntl.h>
#include <exception>
#include <android/log.h>
#include "native_log.h"
#include "tools.h"

void executeCommandAndLogOutput(const char* command) {
    FILE* fp;
    char buffer[128];

    // 打开命令进行读取
    fp = popen(command, "r");
    if (fp == NULL) {
        LOGE("Failed to run command: %s", command);
        return;
    }

    // 读取命令的输出并打印
    while (fgets(buffer, sizeof(buffer), fp) != NULL) {
        LOGE("%s", buffer);
    }

    // 关闭文件指针
    pclose(fp);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_linsheng_FATJS_activitys_aione_1editor_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject thiz) {
    // TODO: implement stringFromJNI()
    std::string hello = "Hello from C++";
    LOGE("Hello from C++ native");

    executeCommandAndLogOutput("su -c 'ls /dev/input/'");

    touch* touchTest = nullptr;
    try {
        touchTest = new touch{};
        touchTest->touch_down(110, {100, 200});
        sleep(2);
        touchTest->touch_move(110, {1300, 850});
        sleep(2);
        touchTest->touch_up(110);
        sleep(999999999);

    } catch (const std::exception& e) {
        // 捕获并处理标准异常
        // 使用 std::string 拼接异常信息
        std::string errorMsg = std::string("Standard exception caught: ") + e.what();
        LOGE("%s", errorMsg.c_str());
    } catch (...) {
        // 捕获并处理其他所有异常
        LOGE("Unknown exception caught");
    }

    delete touchTest;

    return env->NewStringUTF(hello.c_str());
}