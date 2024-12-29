# FATJS

#### 介绍
>FATJS 基于安卓无障碍的自动化框架｜集成找色模块｜引入JS引擎｜Api类似Autojs

#### B站FATJS教程合集
>https://space.bilibili.com/1456611110/

#### FATJS文档
>http://43.143.157.144/

#### 需要注意的点
>1. 养号很重要，先保证账号和手机环境没问题
>2. 不要一直大量重复做单一的动作，操作尽量随机化
>3. 每个点击与滑动的坐标尽量加上随机偏移值
>4. 不要用市面上常见的自动化工具或者软件
>5. 不要打开开发者模式和adb调试
>6. 手机不root，不解bl锁
>7. （仅用于学习交流，切勿用于非法途径！）

#### 预览图

| <img src="./img/image-0.png" alt="image-0" style="zoom: 30%;" /> | <img src="./img/image-1.png" alt="image-1" style="zoom: 30%;" /> | <img src="./img/image-2.png" alt="image-2" style="zoom: 30%;" /> |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="./img/image-3.png" alt="image-3" style="zoom: 30%;" /> | <img src="./img/image-4.png" alt="image-4" style="zoom: 30%;" /> | <img src="./img/image-5.png" alt="image-5" style="zoom: 30%;" /> |

#### Api示例

```js
showLog() //打开悬浮窗
print('返回桌面') //打印日志
home()
print('点击设置')
text('设置').findOne().click()
sleep(2000) //等待2s
back() //返回
hideLog() //隐藏悬浮窗
click(100, 200) // 点击坐标
capture('/sdcard/1.png') // 截屏
...
```
