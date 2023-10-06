//-------------------- pre set -------------------//
let task = new Task()
const width = task._width;
const height = task._height;
const screenSize = () => task._screenSize();
const sleep = (time) => task._sleep(time);
const open = (name) => task._open(name);
const print = (msg) => task._print(msg);
const clickNode = (node) => task._clickNode(node);
const clickNodePoint = (node) => task._clickNodePoint(node);
const click = (x, y) => task._click(x, y);
const doubleClick = (x, y) => task._doubleClick(x, y);
const swipe = (x1, y1, x2, y2, duration) => task._swipe(x1, y1, x2, y2, duration);
const scrollUp = () => task._scrollUp();
const getBounds = (node) => task._getBounds(node);
const back = () => task._back();
const home = () => task._home();
const getRoot = () => task._getRoot();
const move = (str) => task._move(str);
const backToDesk = () => task._backToDesk();
const viewVideo = () => task._viewVideo();
const text = (str) => task._text(str);
const textContains = (str) => task._textContains(str);
const textStartsWith = (prefix) => task._textStartsWith(prefix);
const textEndsWith = (suffix) => task._textEndsWith(suffix);
const textMatches = (reg) => task._textMatches(reg);
const desc = (str) => task._desc(str);
const descContains = (str) => task._descContains(str);
const descStartsWith = (prefix) => task._descStartsWith(prefix);
const descEndsWith = (suffix) => task._descEndsWith(suffix);
const descMatches = (reg) => task._descMatches(reg);
const id = (resId) => task._id(resId);
const idContains = (str) => task._idContains(str);
const idStartsWith = (prefix) => task._idStartsWith(prefix);
const idEndsWith = (suffix) => task._idEndsWith(suffix);
const idMatches = (reg) => task._idMatches(reg);
const className = (str) => task._className(str);
const classNameContains = (str) => task._classNameContains(str);
const classNameStartsWith = (prefix) => task._classNameStartsWith(prefix);
const classNameEndsWith = (suffix) => task._classNameEndsWith(suffix);
const classNameMatches = (reg) => task._classNameMatches(reg);
const packageName = (str) => task._packageName(str);
const packageNameContains = (str) => task._packageNameContains(str);
const packageNameStartsWith = (prefix) => task._packageNameStartsWith(prefix);
const packageNameEndsWith = (suffix) => task._packageNameEndsWith(suffix);
const packageNameMatches = (reg) => task._packageNameMatches(reg);
const bounds = (left, top, right, bottom) => task._bounds(left, top, right, bottom);
const boundsInScreen = () => task._boundsInScreen();
const boundsInside = (left, top, right, bottom) => task._boundsInside(left, top, right, bottom);
const boundsContains = (left, top, right, bottom) => task._boundsContains(left, top, right, bottom);
const drawingOrder = (order) => task._drawingOrder(order);
const clickable = (b) => task._clickable(b);
const longClickable = (b) => task._longClickable(b);
const checkable = (b) => task._checkable(b);
const selected = (b) => task._selected(b);
const enabled = (b) => task._enabled(b);
const scrollable = (b) => task._scrollable(b);
const editable = (b) => task._editable(b);
const multiLine = (b) => task._multiLine(b);
const untilFindOne = (...params) => { // 配置重载方法
    if(params.length === 0) {
        task._untilFindOne();
    }
    task._untilFindOne(params);
}
const findOne = (...params) => { // 配置重载方法
    if(params.length === 0) {
        task._findOne();
    }
    task._findOne(params);
}
const find = () => task._find();
const exists = () => task._exists();
const waitFor = () => task._waitFor();
//-------------------- pre set -------------------//