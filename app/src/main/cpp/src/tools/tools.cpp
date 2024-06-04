#include "tools.h"
#include <linux/input.h>
#include <fcntl.h>
#include <cstdio>
#include <linux/uinput.h>
#include <unistd.h>
#include <iostream>
#include <thread>
#include <filesystem>
#include <vector>

Vector2::Vector2(int x, int y)
{
    this->x = (float) x;
    this->y = (float) y;
}

Vector2::Vector2(float x, float y)
{
    this->x = x;
    this->y = y;
}

Vector2::Vector2()
{
    x = 0;
    y = 0;
}

Vector2::Vector2(Vector2 &va)
{
    this->x = va.x;
    this->y = va.y;
}

Vector2 &Vector2::operator=(const Vector2 &other)
{
    // 防止自赋值
    if (this != &other)
    {
        this->x = other.x;
        this->y = other.y;
    }
    return *this;
}


void touch::InitTouchScreenInfo()
{
    for (const auto &entry: std::filesystem::directory_iterator("/dev/input/"))
    {
        int fd = open(entry.path().c_str(), O_RDWR);
        if(fd < 0)
        {
            std::cout<<"打开"<<entry.path()<<"失败"<<std::endl;
        }
        input_absinfo absinfo{};
        ioctl(fd, EVIOCGABS(ABS_MT_SLOT), &absinfo);
        if (absinfo.maximum == 9)
        {
            this->touchScreenInfo.fd = open(entry.path().c_str(), O_RDWR);
            close(fd);
            break;
        }
    }//遍历/dev/input/下所有eventX，如果ABS_MT_SLOT为9(即最大支持10点触控)就视为物理触摸屏
    input_absinfo absX{}, absY{};
    ioctl(touchScreenInfo.fd, EVIOCGABS(ABS_MT_POSITION_X), &absX);
    ioctl(touchScreenInfo.fd, EVIOCGABS(ABS_MT_POSITION_Y), &absY);
    this->touchScreenInfo.width = absX.maximum;
    this->touchScreenInfo.height = absY.maximum;
}

void touch::InitScreenInfo()
{
    std::string window_size = exec("wm size");
    sscanf(window_size.c_str(), "Physical size: %dx%d", &this->screenInfo.width, &this->screenInfo.height);
}//初始化屏幕分辨率,方向单独放在一个线程了

touch::touch()
{
    InitScreenInfo();
    InitTouchScreenInfo();
    GetScreenorientationThread = std::thread(&touch::GetScrorientation, this);
    sleep(2);
    PTScreenEventToFingerThread = std::thread(&touch::PTScreenEventToFinger, this);
    this->uinputFd = open("/dev/uinput", O_RDWR);
    if (uinputFd < 0)
    {
        perror("打开uinput失败！！");
    }

    ioctl(uinputFd, UI_SET_PROPBIT, INPUT_PROP_DIRECT);//设置为直接输入设备
    ioctl(uinputFd, UI_SET_EVBIT, EV_ABS);
    ioctl(uinputFd, UI_SET_EVBIT, EV_KEY);
    ioctl(uinputFd, UI_SET_EVBIT, EV_SYN);//支持的事件类型

    ioctl(uinputFd, UI_SET_ABSBIT, ABS_MT_TOUCH_MINOR);
    ioctl(uinputFd, UI_SET_ABSBIT, ABS_X);
    ioctl(uinputFd, UI_SET_ABSBIT, ABS_Y);
    ioctl(uinputFd, UI_SET_ABSBIT, ABS_MT_TOUCH_MAJOR);
    ioctl(uinputFd, UI_SET_ABSBIT, ABS_MT_WIDTH_MAJOR);
    ioctl(uinputFd, UI_SET_ABSBIT, ABS_MT_POSITION_X);
    ioctl(uinputFd, UI_SET_ABSBIT, ABS_MT_POSITION_Y);
    ioctl(uinputFd, UI_SET_ABSBIT, ABS_MT_TRACKING_ID);//支持的事件

    ioctl(uinputFd, UI_SET_KEYBIT, BTN_TOUCH);
    ioctl(uinputFd, UI_SET_KEYBIT, BTN_TOOL_FINGER);//支持的事件


    usetup.id.bustype = BUS_SPI;
    usetup.id.vendor = 0x6c90;
    usetup.id.product = 0x8fb0;
    strcpy(usetup.name, "Virtual Touch Screen for muchen");//驱动信息

    usetup.absmin[ABS_X] = 0;
    usetup.absmax[ABS_X] = 1599;
    usetup.absmin[ABS_Y] = 0;
    usetup.absmax[ABS_Y] = 2559;
    usetup.absmin[ABS_MT_POSITION_X] = 0;
    usetup.absmax[ABS_MT_POSITION_X] = touchScreenInfo.width;
    usetup.absfuzz[ABS_MT_POSITION_X] = 0;
    usetup.absflat[ABS_MT_POSITION_X] = 0;
    usetup.absmin[ABS_MT_POSITION_Y] = 0;
    usetup.absmax[ABS_MT_POSITION_Y] = touchScreenInfo.height;
    usetup.absfuzz[ABS_MT_POSITION_Y] = 0;
    usetup.absflat[ABS_MT_POSITION_Y] = 0;
    usetup.absmin[ABS_MT_PRESSURE] = 0;
    usetup.absmax[ABS_MT_PRESSURE] = 1000;//触摸压力的最大最小值
    usetup.absfuzz[ABS_MT_PRESSURE] = 0;
    usetup.absflat[ABS_MT_PRESSURE] = 0;
    usetup.absmax[ABS_MT_TOUCH_MAJOR] = 255; //与屏接触面的最大值
    usetup.absmin[ABS_MT_TRACKING_ID] = 0;
    usetup.absmax[ABS_MT_TRACKING_ID] = 65535; //按键码ID累计叠加最大值

    write(uinputFd, &usetup, sizeof(usetup));//将信息写入即将创建的驱动

    ioctl(uinputFd, UI_DEV_CREATE);//创建驱动

    ioctl(this->touchScreenInfo.fd, EVIOCGRAB, 0x1);//独占输入,只有此进程才能接收到事件 -_-

    std::cout << "触摸屏宽高  " << touchScreenInfo.width << "   " << touchScreenInfo.height << std::endl;
    std::cout << "屏幕分辨率  " << screenInfo.width << "   " << screenInfo.height << std::endl;
    screenToTouchRatio =(float) (screenInfo.width + screenInfo.height) / (float) (touchScreenInfo.width + touchScreenInfo.height);
    if (screenToTouchRatio < 1 && screenToTouchRatio > 0.9)
    {
        screenToTouchRatio = 1;
    }
    input_event down{};
    down.type = EV_KEY;
    down.code = BTN_TOUCH;
    down.value = 1;
    write(uinputFd, &down, sizeof(down));
    sleep(2);
}

touch::~touch()
{
    ioctl(uinputFd, UI_DEV_DESTROY);
    close(uinputFd);
    PTScreenEventToFingerThread.detach();
    GetScreenorientationThread.detach();
}


void touch::PTScreenEventToFinger()
{
    input_event ie{};
    int latestSlot{};
    while (true)
    {
        read(touchScreenInfo.fd, &ie, sizeof(ie));
        {
            if (ie.type == EV_ABS)
            {
                if (ie.code == ABS_MT_SLOT)
                {
                    latestSlot = ie.value;
                    Fingers[0][latestSlot].TRACKING_ID = 114514 + latestSlot;
                    continue;
                }
                if (ie.code == ABS_MT_TRACKING_ID)
                {
                    if (ie.value == -1)
                    {
                        Fingers[0][latestSlot].isDown = false;
                        Fingers[1][latestSlot].isUse = false;
                    } else
                    {
                        Fingers[1][latestSlot].isUse = true;
                        Fingers[0][latestSlot].isDown = true;
                    }
                    continue;
                }
                if (ie.code == ABS_MT_POSITION_X)
                {
                    Fingers[0][latestSlot].x = ie.value;
                    continue;
                }
                if (ie.code == ABS_MT_POSITION_Y)
                {
                    Fingers[0][latestSlot].y = ie.value;
                    continue;
                }
            }
            if (ie.type == EV_SYN)
            {
                if (ie.code == SYN_REPORT)
                {
                    upLoad();
                    continue;
                }
                continue;
            }
        }
    }
}

void touch::upLoad()
{
    std::vector<input_event> events{};
    for (auto &fingers: Fingers)
    {
        for (auto &finger: fingers)
        {
            if (finger.isDown)
            {
                input_event down_events[]
                        {
                                {.type = EV_ABS, .code = ABS_MT_TRACKING_ID, .value = finger.TRACKING_ID},
                                {.type = EV_ABS, .code = ABS_MT_POSITION_X, .value = finger.x},
                                {.type = EV_ABS, .code = ABS_MT_POSITION_Y, .value = finger.y},
                                {.type = EV_SYN, .code = SYN_MT_REPORT, .value = 0},
                        };
                int arrCount = sizeof(down_events) / sizeof(down_events[0]);
                events.insert(events.end(), down_events, down_events + arrCount);
            }
        }
    }
    input_event touchEnd{};
    touchEnd.type = EV_SYN;
    touchEnd.code = SYN_MT_REPORT;
    touchEnd.value = 0;
    events.push_back(touchEnd);
    input_event end{};
    end.type = EV_SYN;
    end.code = SYN_REPORT;
    end.value = 0;
    events.push_back(end);
    for (const auto &event: events)
    {
        write(uinputFd, &event, sizeof(event));
    }
    events.clear();
}

std::string touch::exec(const std::string &command)
{
    char buffer[128];
    std::string result{};

    FILE *pipe = popen(command.c_str(), "r");
    while (!feof(pipe))
    {
        if (fgets(buffer, 128, pipe) != nullptr)
        {
            result += buffer;
        }
    }
    pclose(pipe);
    return result;
}

void touch::GetScrorientation()
{
    while (true)
    {
        this->screenInfo.orientation = atoi(
                exec("dumpsys display | grep 'mCurrentOrientation' | cut -d'=' -f2").c_str());
        std::this_thread::sleep_for(std::chrono::seconds(5));
    }
}

Vector2 touch::rotatePointx(const Vector2 &pos, const Vector2 &wh, bool reverse) const
{
    Vector2 xy{pos.x, pos.y};
    if (this->screenInfo.orientation == 0)//竖
    {
        return xy;
    }
    if (this->screenInfo.orientation == 3)//横
    {
        xy.x = reverse ? pos.y : (float) wh.y - pos.y;
        xy.y = reverse ? (float) wh.y - pos.x : pos.x;
    } else if (this->screenInfo.orientation == 2)//竖
    {
        xy.x = (float) wh.x - pos.x;
        xy.y = (float) wh.y - pos.y;
    } else if (this->screenInfo.orientation == 1)//横
    {
        xy.x = reverse ? (float) wh.x - pos.y : pos.y;
        xy.y = reverse ? pos.x : (float) wh.x - pos.x;
    }
    return xy;
}

int touch::GetindexById(const int &byId)
{
    for (int i{0}; i < 10; i++)
    {
        if (Fingers[1][i].id == byId)
        {
            return i;
        }
    }
    return -1;
}

int touch::GetNoUseIndex()
{
    for (int i{0}; i < 10; i++)
    {
        if (!Fingers[1][i].isUse)
        {
            return i;
        }
    }
    return -1;
}

void touch::touch_down(const int &id, const Vector2 &pos)
{
    int index = GetNoUseIndex();
    if (Fingers[1][index].isDown && Fingers[1][index].isUse)
    {
        return;
    }
    Vector2 newPos = rotatePointx(pos, {screenInfo.width, screenInfo.height}, true);
    newPos.x /= this->screenToTouchRatio;
    newPos.y /= this->screenToTouchRatio;
    Fingers[1][index].isDown = true;
    Fingers[1][index].id = id;
    Fingers[1][index].TRACKING_ID = 415411 + id;
    Fingers[1][index].x = (int) newPos.x;
    Fingers[1][index].y = (int) newPos.y;
    Fingers[1][index].isUse = true;
    this->upLoad();
}

void touch::touch_move(const int &id, const Vector2 &pos)
{
    int index = GetindexById(id);
    if (index == -1)
    {
        return;
    }
    if (!(Fingers[1][index].isUse && Fingers[1][index].isDown))
    {
        return;
    }
    Vector2 newPos = rotatePointx(pos, {screenInfo.width, screenInfo.height}, true);
    newPos.x /= this->screenToTouchRatio;
    newPos.y /= this->screenToTouchRatio;
    Fingers[1][index].x = (int) newPos.x;
    Fingers[1][index].y = (int) newPos.y;
    this->upLoad();
}

void touch::touch_up(const int &id)
{
    int index = GetindexById(id);
    if (!(Fingers[1][index].isDown && Fingers[1][index].isUse))
    {
        return;
    }
    Fingers[1][index].isDown = false;
    Fingers[1][index].isUse = false;
    Fingers[1][index].id = 0;
    this->upLoad();
}
