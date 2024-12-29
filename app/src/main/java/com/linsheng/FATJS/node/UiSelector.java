package com.linsheng.FATJS.node;

import static com.linsheng.FATJS.config.GlobalVariableHolder.mHeight;
import static com.linsheng.FATJS.config.GlobalVariableHolder.mWidth;
import static com.linsheng.FATJS.config.GlobalVariableHolder.waitOneSecond;
import static com.linsheng.FATJS.node.AccUtils.printLogMsg;
import static com.linsheng.FATJS.node.AccUtils.timeSleep;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;
import com.linsheng.FATJS.utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

/**
 * UiSelector 即选择器，用于通过各种条件选取屏幕上的控件，再对这些控件进行点击、长按等动作。这里需要先简单介绍一下控件和界面的相关知识。
 * 一般软件的界面是由一个个控件构成的，例如图片部分是一个图片控件(ImageView)，文字部分是一个文字控件(TextView)；
 * 同时，通过各种布局来决定各个控件的位置，例如，线性布局(LinearLayout)里面的控件都是按水平或垂直一次叠放的，
 * 列表布局(AbsListView)则是以列表的形式显示控件。
 * 控件有各种属性，包括文本(text), 描述(desc), 类名(className), id 等等。我们通常用一个控件的属性来找到这个控件，
 * 例如，想要点击 QQ 聊天窗口的"发送"按钮，我们就可以通过他的文本属性为"发送"来找到这个控件并点击他，具体代码为:
 * var sendButton = text("发送").findOne();
 * sendButton.click();
 * 在这个例子中, text("发送")表示一个条件(文本属性为"发送")，findOne()表示基于这个条件找到一个符合条件的控件，
 * 从而我们可以得到发送按钮 sendButton，再执行sendButton.click()即可点击"发送"按钮。
 * 用文本属性来定位按钮控件、文本控件通常十分有效。但是，如果一个控件是图片控件，比如 Auto.js 主界面右上角的搜索图标，
 * 他没有文本属性，这时需要其他属性来定位他。
 * 我们如何查看他有什么属性呢？首先打开悬浮窗和无障碍服务，点击蓝色的图标(布局分析), 可以看到以下界面：
 * 之后我们点击搜索图标，可以看到他有以下属性：
 * 我们注意到这个图标的 desc(描述)属性为"搜索"，那么我们就可以通过 desc 属性来定位这个控件，得到点击搜索图标的代码为:
 * desc("搜索").findOne().click();
 * 可能心细的你可能注意到了，这个控件还有很多其他的属性，例如 checked, className, clickable 等等，为什么不用这些属性来定位搜索图标呢？
 * 答案是，其他控件也有这些值相同的属性、尝试一下你就可以发现很多其他控件的 checked 属性和搜索控件一样都是false，如果我们用checked(false)作为条件，
 * 将会找到很多控件，而无法确定哪一个是搜索图标。因此，要找到我们想要的那个控件，选择器的条件通常需要是可唯一确定控件的。
 * 我们通常用一个独一无二的属性来定位一个控件，例如这个例子中就没有其他控件的 desc(描述)属性为"搜索"。
 * 另外，对于这个搜索图标而言，id 属性也是唯一的，我们也可以用id("action_search").findOne().click()来点击这个控件。如果一个控件有 id 属性，那么这个属性很可能是唯一的，除了以下几种情况：
 * QQ 的控件的 id 属性很多都是"name"，也就是在 QQ 界面难以通过 id 来定位一个控件
 * 列表中的控件，比如 QQ 联系人列表，微信联系人列表等
 * 尽管 id 属性很方便，但也不总是最方便的，例如对于微信和网易云音乐，每次更新他的控件 id 都会变化，导致了相同代码对于不同版本的微信、网易云音乐并不兼容。
 * 除了这些属性外，主要还有以下几种属性：
 * className 类名。类名表示一个控件的类型，例如文本控件为"android.widget.TextView", 图片控件为"android.widget.ImageView"等。
 * packageName 包名。包名表示控件所在的应用包名，例如 QQ 界面的控件的包名为"com.tencent.mobileqq"。
 * bounds 控件在屏幕上的范围。
 * drawingOrder 控件在父控件的绘制顺序。
 * indexInParent 控件在父控件的位置。
 * clickable 控件是否可点击。
 * longClickable 控件是否可长按。
 * checkable 控件是否可勾选。
 * checked 控件是否可已勾选。
 * scrollable 控件是否可滑动。
 * selected 控件是否已选择。
 * editable 控件是否可编辑。
 * visibleToUser 控件是否可见。
 * enabled 控件是否已启用。
 * depth 控件的布局深度。
 * 有时候只靠一个属性并不能唯一确定一个控件，这时需要通过属性的组合来完成定位，例如className("ImageView").depth(10).findOne().click()，通过链式调用来组合条件。
 * 通常用这些技巧便可以解决大部分问题，即使解决不了问题，也可以通过布局分析的"生成代码"功能来尝试生成一些选择器代码。接下来的问题便是对选取的控件进行操作，包括：
 * click() 点击。点击一个控件，前提是这个控件的 clickable 属性为 true
 * longClick() 长按。长按一个控件，前提是这个控件的 longClickable 属性为 true
 * setText() 设置文本，用于编辑框控件设置文本。
 * scrollForward(), scrollBackward() 滑动。滑动一个控件(列表等), 前提是这个控件的 scrollable 属性为 true
 * exits() 判断控件是否存在
 * waitFor() 等待控件出现
 * 这些操作包含了绝大部分控件操作。根据这些我们可以很容易写出一个"刷屏"脚本(代码仅为示例，请不要在别人的群里测试，否则容易被踢):
 * while(true){
 *     className("EditText").findOne().setText("刷屏...");
 *     text("发送").findOne().clicK();
 * }
 * 上面这段代码也可以写成：
 * while(true){
 *     className("EditText").setText("刷屏...");
 *     text("发送").clicK();
 * }
 * 如果不加findOne()而直接进行操作，则选择器会找出所有符合条件的控件并操作。
 * 另外一个比较常用的操作的滑动。滑动操作的第一步是找到需要滑动的控件，例如要滑动 QQ 消息列表则在悬浮窗布局层次分析中找到AbsListView，这个控件就是消息列表控件，如下图：
 * 长按可查看控件信息，注意到其 scrollable 属性为 true，并找出其 id 为"recent_chat_list"，从而下滑 QQ 消息列表的代码为：
 * id("recent_chat_list").className("AbsListView").findOne().scrollForward();
 * scrollForward()为向前滑，包括下滑和右滑。
 * 选择器的入门教程暂且要这里，更多信息可以查看下面的文档和选择器进阶。
 */
public class UiSelector implements IUiSelector{

    public UiSelector() {
    }

    // *******************************属性**********************************
     public Map<String, Object> containsAttributes = new HashMap<>(); // 包含的属性名

    // *******************************方法**********************************

    /**
     * str {string} 控件文本
     * 返回 {UiSelector} 返回选择器自身以便链式调用
     * 为当前选择器附加控件"text 等于字符串 str"的筛选条件。
     * @param str
     * @return
     */
    public UiSelector text(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("text", str);
        return this;
    }

    /**
     * str {string} 要包含的字符串
     * 为当前选择器附加控件"text 需要包含字符串 str"的筛选条件。
     * @param str
     * @return
     */
    public UiSelector textContains(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("textContains", str);
        return this;
    }

    /**
     * prefix {string} 前缀
     * 为当前选择器附加控件"text 需要以 prefix 开头"的筛选条件。
     * @param prefix
     * @return
     */
    public UiSelector textStartsWith(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            return null;
        }

        containsAttributes.put("textStartsWith", prefix);
        return this;
    }

    /**
     * suffix {string} 后缀
     * 为当前选择器附加控件"text 需要以 suffix 结束"的筛选条件。
     * @param suffix
     * @return
     */
    public UiSelector textEndsWith(String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            return null;
        }

        containsAttributes.put("textEndsWith", suffix);
        return this;
    }

    /**
     * reg {string} | {Regex} 要满足的正则表达式。
     * 为当前选择器附加控件"text 需要满足正则表达式 reg"的条件。
     * @param reg
     * @return
     */
    public UiSelector textMatches(String reg) {
        if (StringUtils.isEmpty(reg)) {
            return null;
        }

        containsAttributes.put("textMatches", reg);
        return this;
    }

    /**
     * str {string} 控件文本
     * 返回 {UiSelector} 返回选择器自身以便链式调用
     * 为当前选择器附加控件"desc 等于字符串 str"的筛选条件。
     * @param str
     * @return
     */
    public UiSelector desc(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("desc", str);
        return this;
    }

    /**
     * str {string} 要包含的字符串
     * 为当前选择器附加控件"desc 需要包含字符串 str"的筛选条件。
     * @param str
     * @return
     */
    public UiSelector descContains(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("descContains", str);
        return this;
    }

    /**
     * prefix {string} 前缀
     * 为当前选择器附加控件"desc 需要以 prefix 开头"的筛选条件。
     * @param prefix
     * @return
     */
    public UiSelector descStartsWith(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            return null;
        }

        containsAttributes.put("descStartsWith", prefix);
        return this;
    }

    /**
     * suffix {string} 后缀
     * 为当前选择器附加控件"desc 需要以 suffix 结束"的筛选条件。
     * @param suffix
     * @return
     */
    public UiSelector descEndsWith(String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            return null;
        }

        containsAttributes.put("descEndsWith", suffix);
        return this;
    }

    /**
     * reg {string} | {Regex} 要满足的正则表达式。
     * 为当前选择器附加控件"desc 需要满足正则表达式 reg"的条件。
     * @param reg
     * @return
     */
    public UiSelector descMatches(String reg) {
        if (StringUtils.isEmpty(reg)) {
            return null;
        }

        containsAttributes.put("descMatches", reg);
        return this;
    }

    /**
     * resId {string} 控件的 id，以"包名:id/"开头，例如"com.tencent.mm:id/send_btn"。
     * 也可以不指定包名，这时会以当前正在运行的应用的包名来补全 id。例如 id("send_btn"),在 QQ 界面想当于 id("com.tencent.mobileqq:id/send_btn")。
     * 为当前选择器附加"id 等于 resId"的筛选条件。
     * @param resId
     * @return
     */
    public UiSelector id(String resId) {
        if (StringUtils.isEmpty(resId)) {
            return null;
        }

        containsAttributes.put("id", resId);
        return this;
    }

    /**
     * str {string} id 要包含的字符串
     * 为当前选择器附加控件"id 包含字符串 str"的筛选条件。比较少用。
     * @param str
     * @return
     */
    public UiSelector idContains(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("idContains", str);
        return this;
    }

    /**
     * prefix {string} id 前缀
     * 为当前选择器附加"id 需要以 prefix 开头"的筛选条件。比较少用。
     * @param prefix
     * @return
     */
    public UiSelector idStartsWith(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            return null;
        }

        containsAttributes.put("idStartsWith", prefix);
        return this;
    }

    /**
     * suffix {string} id 后缀
     * 为当前选择器附加"id 需要以 suffix 结束"的筛选条件。比较少用。
     * @param suffix
     * @return
     */
    public UiSelector idEndsWith(String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            return null;
        }

        containsAttributes.put("idEndsWith", suffix);
        return this;
    }

    /**
     * reg {Regex} | {string} id 要满足的正则表达式
     * 附加 id 需要满足正则表达式。
     * @param reg
     * @return
     */
    public UiSelector idMatches(String reg) {
        if (StringUtils.isEmpty(reg)) {
            return null;
        }

        containsAttributes.put("idMatches", reg);
        return this;
    }


    /**
     * str {string} 控件文本
     * 返回 {UiSelector} 返回选择器自身以便链式调用
     * 为当前选择器附加控件"className 等于字符串 str"的筛选条件。
     * 控件的 className(类名)表示一个控件的类别，例如文本控件的类名为 android.widget.TextView。
     * 因为这个比较常用，所以改成了和classNameContains一样的效果，例如文本控件可以直接用className("TextView")的选择器。
     * @param str
     * @return
     */
    public UiSelector className(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("className", str);
        return this;
    }


    /**
     * str {string} 要包含的字符串
     * 为当前选择器附加控件"className 需要包含字符串 str"的筛选条件。
     * @param str
     * @return
     */
    public UiSelector classNameContains(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("classNameContains", str);
        return this;
    }

    /**
     * prefix {string} 前缀
     * 为当前选择器附加控件"className 需要以 prefix 开头"的筛选条件。
     * @param prefix
     * @return
     */
    public UiSelector classNameStartsWith(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            return null;
        }

        containsAttributes.put("classNameStartsWith", prefix);
        return this;
    }

    /**
     * suffix {string} 后缀
     * 为当前选择器附加控件"className 需要以 suffix 结束"的筛选条件。
     * @param suffix
     * @return
     */
    public UiSelector classNameEndsWith(String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            return null;
        }

        containsAttributes.put("classNameEndsWith", suffix);
        return this;
    }

    /**
     * reg {string} | {Regex} 要满足的正则表达式。
     * 为当前选择器附加控件"className 需要满足正则表达式 reg"的条件。
     * @param reg
     * @return
     */
    public UiSelector classNameMatches(String reg) {
        if (StringUtils.isEmpty(reg)) {
            return null;
        }

        containsAttributes.put("classNameMatches", reg);
        return this;
    }

    /**
     * str {string} 控件文本
     * 返回 {UiSelector} 返回选择器自身以便链式调用
     * 为当前选择器附加控件"packageName 等于字符串 str"的筛选条件。
     * @param str
     * @return
     */
    public UiSelector packageName(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("packageName", str);
        return this;
    }

    /**
     * str {string} 要包含的字符串
     * 为当前选择器附加控件"packageName 需要包含字符串 str"的筛选条件。
     * @param str
     * @return
     */
    public UiSelector packageNameContains(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        containsAttributes.put("packageNameContains", str);
        return this;
    }

    /**
     * prefix {string} 前缀
     * 为当前选择器附加控件"packageName 需要以 prefix 开头"的筛选条件。
     * @param prefix
     * @return
     */
    public UiSelector packageNameStartsWith(String prefix) {
        if (StringUtils.isEmpty(prefix)) {
            return null;
        }

        containsAttributes.put("packageNameStartsWith", prefix);
        return this;
    }

    /**
     * suffix {string} 后缀
     * 为当前选择器附加控件"packageName 需要以 suffix 结束"的筛选条件。
     * @param suffix
     * @return
     */
    public UiSelector packageNameEndsWith(String suffix) {
        if (StringUtils.isEmpty(suffix)) {
            return null;
        }

        containsAttributes.put("packageNameEndsWith", suffix);
        return this;
    }

    /**
     * reg {string} | {Regex} 要满足的正则表达式。
     * 为当前选择器附加控件"packageName 需要满足正则表达式 reg"的条件。
     * @param reg
     * @return
     */
    public UiSelector packageNameMatches(String reg) {
        if (StringUtils.isEmpty(reg)) {
            return null;
        }

        containsAttributes.put("packageNameMatches", reg);
        return this;
    }

    /**
     * left {number} 控件左边缘与屏幕左边的距离
     * top {number} 控件上边缘与屏幕上边的距离
     * right {number} 控件右边缘与屏幕左边的距离
     * bottom {number} 控件下边缘与屏幕上边的距离
     * 一个控件的 bounds 属性为这个控件在屏幕上显示的范围。我们可以用这个范围来定位这个控件。
     * 尽管用这个方法定位控件对于静态页面十分准确，却无法兼容不同分辨率的设备；同时对于列表页面等动态页面无法达到效果，因此使用不推荐该选择器。
     * 注意参数的这四个数字不能随意填写，必须精确的填写控件的四个边界才能找到该控件。例如，要点击 QQ 主界面的右上角加号，我们用布局分析查看该控件的属性，如下图：
     * 可以看到 bounds 属性为(951, 67, 1080, 196)，此时使用代码bounds(951, 67, 1080, 196).clickable().click()即可点击该控件。
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public UiSelector bounds(int left, int top, int right, int bottom) {
        int[] screen = {left, top, right, bottom};
        containsAttributes.put("bounds", screen);
        return this;
    }

    /**
     * left {number} 范围左边缘与屏幕左边的距离
     * top {number} 范围上边缘与屏幕上边的距离
     * right {number} 范围右边缘与屏幕左边的距离
     * bottom {number} 范围下边缘与屏幕上边的距离
     * 为当前选择器附加控件"bounds 需要在 left, top, right, bottom 构成的范围里面"的条件。
     * 这个条件用于限制选择器在某一个区域选择控件。例如要在屏幕上半部分寻找文本控件 TextView，代码为:
     * var w = className("TextView").boundsInside(0, 0, device.width, device.height / 2).findOne();
     * log(w.text());
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public UiSelector boundsInside(int left, int top, int right, int bottom) {
        int[] screen = {left, top, right, bottom};
        containsAttributes.put("boundsInside", screen);
        return this;
    }

    public UiSelector boundsInScreen() {
        int[] screen = {0, 0, mWidth, mHeight};
        containsAttributes.put("boundsInScreen", screen);
        return this;
    }

    /**
     * left {number} 范围左边缘与屏幕左边的距离
     * top {number} 范围上边缘与屏幕上边的距离
     * right {number} 范围右边缘与屏幕左边的距离
     * bottom {number} 范围下边缘与屏幕上边的距离
     * 为当前选择器附加控件"bounds 需要包含 left, top, right, bottom 构成的范围"的条件。
     * 这个条件用于限制控件的范围必须包含所给定的范围。例如给定一个点(500, 300), 寻找在这个点上的可点击控件的代码为:
     * var w = boundsContains(500, 300, device.width - 500, device.height - 300).clickable().findOne();
     * w.click();
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public UiSelector boundsContains(int left, int top, int right, int bottom) {
        //未实现
        //containsAttributes.put("packageNameMatches", reg);
        return this;
    }


    /**
     * order {number} 控件在父视图中的绘制顺序
     * 为当前选择器附加控件"drawingOrder 等于 order"的条件。
     * drawingOrder 为一个控件在父控件中的绘制顺序，通常可以用于区分同一层次的控件。
     * 但该属性在 Android 7.0 以上才能使用。
     * @param order 从 1 开始的
     * @return
     */
    public UiSelector drawingOrder(int order) {
        containsAttributes.put("drawingOrder", order);
        return this;
    }

    /**
     * b {Boolean} 表示控件是否可点击
     * 为当前选择器附加控件是否可点击的条件。但并非所有 clickable 为 false 的控件都真的不能点击，这取决于控件的实现。
     * 对于自定义控件(例如显示类名为 android.view.View 的控件)很多的 clickable 属性都为 false 都却能点击。
     * 需要注意的是，可以省略参数b而表示选择那些可以点击的控件，例如
     * className("ImageView").clickable()表示可以点击的图片控件的条件，
     * className("ImageView").clickable(false)表示不可点击的图片控件的条件。
     * @param b
     * @return
     */
    public UiSelector clickable(boolean b) {
        containsAttributes.put("clickable", b);
        return this;
    }


    /**
     * b {Boolean} 表示控件是否可长按
     * 为当前选择器附加控件是否可长按的条件。
     * @param b
     * @return
     */
    public UiSelector longClickable(boolean b) {
        containsAttributes.put("longClickable", b);
        return this;
    }

    /**
     * b {Boolean} 表示控件是否已经被选中
     * @param b
     * @return
     */
    public UiSelector checked(boolean b) {
        containsAttributes.put("checked", b);
        return this;
    }

    /**
     * b {Boolean} 表示控件是否可勾选
     * 为当前选择器附加控件是否可勾选的条件。勾选通常是对于勾选框而言的，例如图片多选时左上角通常有一个勾选框。
     * @param b
     * @return
     */
    public UiSelector checkable(boolean b) {
        containsAttributes.put("checkable", b);
        return this;
    }

    /**
     * b {Boolean} 表示控件是否被选
     * 为当前选择器附加控件是否已选中的条件。被选中指的是，例如 QQ 聊天界面点击下方的"表情按钮"时，
     * 会出现自己收藏的表情，这时"表情按钮"便处于选中状态，其 selected 属性为 true。
     * @param b
     * @return
     */
    public UiSelector selected(boolean b) {
        containsAttributes.put("selected", b);
        return this;
    }

    /**
     * b {Boolean} 表示控件是否已启用
     * 为当前选择器附加控件是否已启用的条件。大多数控件都是启用的状态(enabled 为 true)，处于“禁用”状态通常是灰色并且不可点击。
     * @param b
     * @return
     */
    public UiSelector enabled(boolean b) {
        containsAttributes.put("enabled", b);
        return this;
    }

    /**
     * b {Boolean} 表示控件是否可滑动
     * 为当前选择器附加控件是否可滑动的条件。滑动包括上下滑动和左右滑动。
     * 可以用这个条件来寻找可滑动控件来滑动界面。例如滑动 Auto.js 的脚本列表的代码为:
     * className("android.support.v7.widget.RecyclerView").scrollable().findOne().scrollForward();
     * //或者classNameEndsWith("RecyclerView").scrollable().findOne().scrollForward();
     * @param b
     * @return
     */
    public UiSelector scrollable(boolean b) {
        containsAttributes.put("scrollable", b);
        return this;
    }

    /**
     * b {Boolean} 表示控件是否可编辑
     * 为当前选择器附加控件是否可编辑的条件。一般来说可编辑的控件为输入框(EditText)，但不是所有的输入框(EditText)都可编辑。
     * @param b
     * @return
     */
    public UiSelector editable(boolean b) {
        containsAttributes.put("editable", b);
        return this;
    }

    /**
     * b {Boolean} 表示文本或输入框控件是否是多行显示的
     * 为当前选择器附加控件是否文本或输入框控件是否是多行显示的条件。
     * @param b
     * @return
     */
    public UiSelector multiLine(boolean b) {
        containsAttributes.put("multiLine", b);
        return this;
    }

    /**
     * 返回 UiObject
     * 根据当前的选择器所确定的筛选条件，对屏幕上的控件进行搜索，直到屏幕上出现满足条件的一个控件为止，并返回该控件。
     * 如果找不到控件，当屏幕内容发生变化时会重新寻找，直至找到。
     * 需要注意的是，如果屏幕上一直没有出现所描述的控件，则该函数会阻塞，直至所描述的控件出现为止。因此此函数不会返回null。
     * 另外，如果屏幕上有多个满足条件的控件，findOne()采用深度优先搜索(DFS)，会返回该搜索算法找到的第一个控件。注意控件找到的顺序有时会起到作用。
     * @return
     */
    public UiObject untilFindOne() {
        //printLogMsg("Attributes " + this.containsAttributes);
        Map<String, Object> attributes = this.containsAttributes;
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return new UiObject();
        }
        AccessibilityNodeInfo accNodeInfo;
        while (true) {
            accNodeInfo = AccUtils.getRootInActiveMy();
            if (accNodeInfo == null) {
                continue;
            }
            UiObject uiObject = depthFirstSearch(accNodeInfo, attributes);
            if (uiObject != null) {
                //printLogMsg("untilFindOne: found");
                accNodeInfo.recycle(); // 释放
                this.containsAttributes = new HashMap<>();
                return uiObject;
            }
            //printLogMsg("untilFindOne: wait 1s");
            timeSleep(waitOneSecond);
        }
    }

    /**
     * timeout {number} 搜索的超时时间，单位毫秒
     * 返回 UiObject
     * 根据当前的选择器所确定的筛选条件，对屏幕上的控件进行搜索，直到屏幕上出现满足条件的一个控件为止，并返回该控件；
     * 如果在 timeout 毫秒的时间内没有找到符合条件的控件，则终止搜索并返回null。
     * 该函数类似于不加参数的findOne()，只不过加上了时间限制。
     * 示例：
     * //启动Auto.js
     * launchApp("Auto.js");
     * //在6秒内找出日志图标的控件
     * var w = id("action_log").untilFindOne(6000);
     * //如果找到控件则点击
     * if(w != null){
     *     w.click();
     * }else{
     *     //否则提示没有找到
     *     toast("没有找到日志图标");
     * }
     * @param timeout
     * @return
     */
    public UiObject untilFindOne(int timeout) {
        printLogMsg("Attributes " + this.containsAttributes, 0);
        Map<String, Object> attributes = this.containsAttributes;
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return new UiObject();
        }
        long startTime = System.currentTimeMillis();
        AccessibilityNodeInfo accNodeInfo;
        for (int i = 0; i < 999; i++) {
            accNodeInfo = AccUtils.getRootInActiveMy();
            UiObject uiObject = depthFirstSearch(accNodeInfo, attributes);
            if (uiObject != null) {
                accNodeInfo.recycle(); // 释放
                printLogMsg("untilFindOne timeout: found", 0);
                this.containsAttributes = new HashMap<>();
                printLogMsg("loop_num: " + i,0);
                return uiObject;
            }
            AccUtils.timeSleep(10);
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= timeout) {
                accNodeInfo.recycle(); // 释放
                printLogMsg("untilFindOne timeout: not found", 0);
                this.containsAttributes = new HashMap<>();
                printLogMsg("loop_num: " + i,0);
                return new UiObject();
            }
        }
        printLogMsg("untilFindOne timeout: return null", 0);
        this.containsAttributes = new HashMap<>();
        return new UiObject();
    }

    /**
     * 返回 UiObject
     * 根据当前的选择器所确定的筛选条件，对屏幕上的控件进行搜索，如果找到符合条件的控件则返回该控件；否则返回null。
     * @return
     */
    public UiObject findOne() {
        //printLogMsg("Attributes " + this.containsAttributes);
        Map<String, Object> attributes = this.containsAttributes;
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return new UiObject();
        }
        AccessibilityNodeInfo accNodeInfo = AccUtils.getRootInActiveMy();
        UiObject uiObject = depthFirstSearch(accNodeInfo, attributes);
        if (uiObject == null) {
            if (accNodeInfo != null)
                accNodeInfo.recycle(); // 释放
            this.containsAttributes = new HashMap<>();
            return new UiObject();
        }
        accNodeInfo.recycle(); // 释放
        this.containsAttributes = new HashMap<>();
        return uiObject;
    }

    /**
     * 根据当前的选择器所确定的筛选条件，对屏幕上的控件进行搜索，是从传入的accNodeInfo节点树往下找。
     * 如果没有找到符合条件的控件，则返回null。
     * @param accNodeInfo
     * @return
     */
    public UiObject findOne(AccessibilityNodeInfo accNodeInfo) {
        //printLogMsg("Attributes " + this.containsAttributes);
        Map<String, Object> attributes = this.containsAttributes;
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return new UiObject();
        }
        UiObject uiObject = depthFirstSearch(accNodeInfo, attributes);
        if (uiObject == null) {
            if (accNodeInfo != null)
                accNodeInfo.recycle(); // 释放
            this.containsAttributes = new HashMap<>();
            return new UiObject();
        }
        accNodeInfo.recycle(); // 释放
        this.containsAttributes = new HashMap<>();
        return uiObject;
    }

    /**
     * i {number} 索引
     * 根据当前的选择器所确定的筛选条件，对屏幕上的控件进行搜索，并返回第 i + 1 个符合条件的控件；
     * 如果没有找到符合条件的控件，或者符合条件的控件个数 < i, 则返回null。
     * 注意这里的控件次序，是搜索算法深度优先搜索(DSF)决定的。
     * @param i
     * @return
     */
    public UiObject findOne(int i) {
        //printLogMsg("Attributes " + this.containsAttributes);
        Map<String, Object> attributes = this.containsAttributes;
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return new UiObject();
        }
        AccessibilityNodeInfo accNodeInfo = AccUtils.getRootInActiveMy();
        UiCollection uiCollection = new UiCollection();
        depthFirstSearchAll(accNodeInfo, attributes, uiCollection);
        if (uiCollection.nodeList.size() - 1 >= i) {
            UiObject uiObject = new UiObject();
            uiObject.node = uiCollection.nodeList.get(i);
            accNodeInfo.recycle(); // 释放 TODO 还应该释放列表中其他的
            this.containsAttributes = new HashMap<>();
            return uiObject;
        }
        if (accNodeInfo != null)
            accNodeInfo.recycle(); // 释放
        this.containsAttributes = new HashMap<>();
        return null;
    }

    /**
     * 返回 UiCollection
     * 根据当前的选择器所确定的筛选条件，对屏幕上的控件进行搜索，找到所有满足条件的控件集合并返回。
     * 这个搜索只进行一次，并不保证一定会找到，因而会出现返回的控件集合为空的情况。
     * 不同于findOne()或者findOnce()只找到一个控件并返回一个控件，find()函数会找出所有满足条件的控件并返回一个控件集合。之后可以对控件集合进行操作。
     * 可以通过 empty()函数判断找到的是否为空。例如：
     * var c = className("AbsListView").find();
     * if(c.empty()){
     *     toast("没找到╭(╯^╰)╮");
     * }else{
     *     toast("找到啦");
     * }
     * @return
     */
    public UiCollection find() {
        //printLogMsg("Attributes " + this.containsAttributes);
        Map<String, Object> attributes = this.containsAttributes;
        UiCollection uiCollection = new UiCollection();
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return uiCollection;
        }
        AccessibilityNodeInfo accNodeInfo = AccUtils.getRootInActiveMy();
        depthFirstSearchAll(accNodeInfo, attributes, uiCollection);
        if (accNodeInfo != null)
            accNodeInfo.recycle(); // 释放
        this.containsAttributes = new HashMap<>();
        return uiCollection;
    }
    private void depthFirstSearchAll(AccessibilityNodeInfo root, Map<String, Object> attributesMap, UiCollection uiCollection) {
        UiObject uiObject = null;
        int numFlag = 0;
        for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (checkAttribute(root, key, value)) {
                numFlag++;
            } else {
                numFlag = 0;
                break;
            }
        }
        if (numFlag == attributesMap.size()) {
            uiCollection.nodeList.add(root);
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            depthFirstSearchAll(root.getChild(i), attributesMap, uiCollection);
        }
    }

    /**
     * 返回 UiCollection
     * 根据当前的选择器所确定的筛选条件，对屏幕上的控件进行搜索，直到找到至少一个满足条件的控件为止，并返回所有满足条件的控件集合。
     * 该函数与find()函数的区别在于，该函数永远不会返回空集合；但是，如果屏幕上一直没有出现满足条件的控件，则该函数会保持阻塞。
     * @return
     */
    private UiCollection untilFind() {

        return null;
    }

    /**
     * 返回 {Boolean}
     * 判断屏幕上是否存在控件符合选择器所确定的条件。例如要判断某个文本出现就执行某个动作，可以用：
     * if(text("某个文本").exists()){
     *     //要支持的动作
     * }
     * @return
     */
    public boolean exists() {
        printLogMsg("Attributes " + this.containsAttributes, 0);
        Map<String, Object> attributes = this.containsAttributes;
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return false;
        }
        AccessibilityNodeInfo accNodeInfo = AccUtils.getRootInActiveMy();
        UiObject uiObject = depthFirstSearch(accNodeInfo, attributes);
        if (uiObject != null) {
            printLogMsg("exists: true", 0);
            accNodeInfo.recycle(); // 释放
            if (uiObject.exists())
                uiObject.node.recycle();
            this.containsAttributes = new HashMap<>();
            return true;
        }
        printLogMsg("exists: false", 0);
        if (accNodeInfo != null)
            accNodeInfo.recycle(); // 释放
        this.containsAttributes = new HashMap<>();
        return false;
    }

    /**
     * 等待屏幕上出现符合条件的控件；在满足该条件的控件出现之前，该函数会一直保持阻塞。
     * 例如要等待包含"哈哈哈"的文本控件出现的代码为：
     * textContains("哈哈哈").waitFor();
     */
    public void waitFor() {
        //printLogMsg("Attributes " + this.containsAttributes);
        Map<String, Object> attributes = this.containsAttributes;
        if (attributes == null || attributes.isEmpty()) {
            this.containsAttributes = new HashMap<>();
            return;
        }
        UiObject uiObject;
        AccessibilityNodeInfo accNodeInfo;
        while (true) {
            accNodeInfo = AccUtils.getRootInActiveMy();
            uiObject = depthFirstSearch(accNodeInfo, attributes);
            if (uiObject != null) {
                //printLogMsg("waitFor: found");
                accNodeInfo.recycle(); // 释放
                if (uiObject.exists())
                    uiObject.node.recycle();
                this.containsAttributes = new HashMap<>();
                return;
            }
            //printLogMsg("waitFor: wait 1s");
            timeSleep(waitOneSecond);
        }
    }

    /**
     *f {Function} 过滤函数，参数为 UiObject，返回值为 boolean
     * 为当前选择器附加自定义的过滤条件。
     * 例如，要找出屏幕上所有文本长度为 10 的文本控件的代码为：
     * var uc = className("TextView").filter(function(w){
     *     return w.text().length == 10;
     * });
     */
    /*public void filter(Greeting greeting) {
        greet("John", UiSelector::sayHello);
    }
    private static void sayHello(String name) {
        System.out.println("Hello, " + name + "!");
    }
    interface Greeting {
        void greet(String name);
    }
    private void greet(String name, Greeting greeting) {
        greeting.greet(name);
    }*/

    private UiObject findOneBFS() {
        AccessibilityNodeInfo root = AccUtils.getRootInActiveMy();
        return breadthFirstSearch(root, this.containsAttributes);
    }
    // 广度优先算法
    private static UiObject breadthFirstSearch(AccessibilityNodeInfo root, Map<String, Object> attributesMap) {
        Queue<AccessibilityNodeInfo> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();

            // 检查节点是否满足指定的属性条件
            boolean isMatched = true;
            for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!checkAttribute(node, key, value)) {
                    isMatched = false;
                    break;
                }
            }

            if (isMatched) {
                UiObject uiObject = new UiObject();
                uiObject.node = node;
                return uiObject;
            }

            // 将子节点添加到队列中
            for (int i = 0; i < Objects.requireNonNull(node).getChildCount(); i++) {
                queue.offer(node.getChild(i));
            }
        }

        return null;
    }
    // 深度优先算法
    private UiObject depthFirstSearch(AccessibilityNodeInfo root, Map<String, Object> attributesMap) {
        UiObject uiObject = null;
        int numFlag = 0;
        for (Map.Entry<String, Object> entry : attributesMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (checkAttribute(root, key, value)) {
                numFlag++;
            } else {
                numFlag = 0;
                break;
            }
        }
        if (numFlag == attributesMap.size()) {
            uiObject = new UiObject();
            uiObject.node = root;
            return uiObject;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            uiObject = depthFirstSearch(root.getChild(i), attributesMap);
            if (uiObject != null) {
                return uiObject;
            }
        }
        return null;
    }

    private static boolean checkAttribute(AccessibilityNodeInfo node, String key, Object value) {
        switch (key) {
            case "text":
            case "textContains":
                return String.valueOf(node.getText()).contains(String.valueOf(value));
            case "textStartsWith":
                return String.valueOf(node.getText()).startsWith(String.valueOf(value));
            case "textEndsWith":
                return String.valueOf(node.getText()).endsWith(String.valueOf(value));
            case "textMatches":
                String text = String.valueOf(node.getText());
                text = text.equals("null") ? "" : text;
                return text.matches(String.valueOf(value));
            case "desc":
            case "descContains":
                return String.valueOf(node.getContentDescription()).contains(String.valueOf(value));
            case "descStartsWith":
                return String.valueOf(node.getContentDescription()).startsWith(String.valueOf(value));
            case "descEndsWith":
                return String.valueOf(node.getContentDescription()).endsWith(String.valueOf(value));
            case "descMatches":
                String desc = String.valueOf(node.getContentDescription());
                desc = desc.equals("null") ? "" : desc;
                return desc.matches(String.valueOf(value));
            case "id":
            case "idContains":
                return String.valueOf(node.getViewIdResourceName()).contains(String.valueOf(value));
            case "idStartsWith":
                return String.valueOf(node.getViewIdResourceName()).startsWith(String.valueOf(value));
            case "idEndsWith":
                return String.valueOf(node.getViewIdResourceName()).endsWith(String.valueOf(value));
            case "idMatches":
                return String.valueOf(node.getViewIdResourceName()).matches(String.valueOf(value));
            case "className":
            case "classNameContains":
                return String.valueOf(node.getClassName()).contains(String.valueOf(value));
            case "classNameStartsWith":
                return String.valueOf(node.getClassName()).startsWith(String.valueOf(value));
            case "classNameEndsWith":
                return String.valueOf(node.getClassName()).endsWith(String.valueOf(value));
            case "classNameMatches":
                return String.valueOf(node.getClassName()).matches(String.valueOf(value));
            case "packageName":
            case "packageNameContains":
                return String.valueOf(node.getPackageName()).contains(String.valueOf(value));
            case "packageNameStartsWith":
                return String.valueOf(node.getPackageName()).startsWith(String.valueOf(value));
            case "packageNameEndsWith":
                return String.valueOf(node.getPackageName()).endsWith(String.valueOf(value));
            case "bounds":
                int[] __value = (int[]) value;
                Rect _rect = new Rect();
                node.getBoundsInScreen(_rect);
                return (__value[0] == _rect.left && __value[1] == _rect.top && __value[2] == _rect.right && __value[3] == _rect.bottom);
            case "boundsInScreen":
            case "boundsInside":
                int[] _value = (int[]) value;
                Rect rect = new Rect();
                node.getBoundsInScreen(rect);
                return (rect.top <= rect.bottom && rect.left <= rect.right) &&
                (_value[0] <= rect.left && _value[1] <= rect.top && _value[2] >= rect.right && _value[3] >= rect.bottom);
            case "drawingOrder":
                return (int) value == node.getDrawingOrder();
            case "clickable":
                return (boolean) value == node.isClickable();
            case "longClickable":
                return (boolean) value == node.isLongClickable();
            case "checked":
                return (boolean) value == node.isChecked();
            case "checkable":
                return (boolean) value == node.isCheckable();
            case "selected":
                return (boolean) value == node.isSelected();
            case "enabled":
                return (boolean) value == node.isEnabled();
            case "scrollable":
                return (boolean) value == node.isScrollable();
            case "editable":
                return (boolean) value == node.isEditable();
            case "multiLine":
                return (boolean) value == node.isMultiLine();
            default:
                return false;
        }
    }
}
