package com.linsheng.FATJS.findColor;

import static com.linsheng.FATJS.config.GlobalVariableHolder.tag;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class GBData {
    private static final String TAG = tag;
    public static ImageReader reader;
    private static Bitmap bitmap;
    private static Image image;


    public static Bitmap getImageBitmap() { //截图
        if (reader == null){
            Log.w(TAG, "getColor: reader is null");
            return null;
        }
        // 从虚拟显示器读取一张图片
        try {
            image = reader.acquireLatestImage();
        }catch (IllegalStateException e){
            return null;
        }

        if (image == null) {
            if (bitmap == null) {
                Log.w(TAG, "getColor: image is null");
                return null;
            }
            return null;//bitmap
        }
        //得到图片
        int width = image.getWidth();
        int height = image.getHeight();

        //获取一帧 所有像素
        final Image.Plane[] planes = image.getPlanes();

        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        }
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();

        return bitmap;
    }

    public static int[] getRGB(Bitmap bit, int x, int y){//返回RGB数组
        int color = bit.getPixel(x, y);
        return new int[]{Color.red(color),Color.green(color),Color.blue(color)};

    }
    //解析连续的子字符串
    public static ArrayList<Integer[]> getColorListSub(String subColor){
        ArrayList<Integer[]> colorArraySub = new ArrayList<Integer[]>();
        String[] sList=subColor.split(",");
        for (String value : sList) {
            String[] s = value.split("\\|");
            String R16 = String.format("%s%s", String.valueOf(s[2].charAt(2)), String.valueOf(s[2].charAt(3)));
            String G16 = String.format("%s%s", String.valueOf(s[2].charAt(4)), String.valueOf(s[2].charAt(5)));
            String B16 = String.format("%s%s", String.valueOf(s[2].charAt(6)), String.valueOf(s[2].charAt(7)));
            int R10 = Integer.parseInt(R16, 16);
            int G10 = Integer.parseInt(G16, 16);
            int B10 = Integer.parseInt(B16, 16);
            colorArraySub.add(new Integer[]{Integer.valueOf(s[0]), Integer.valueOf(s[1]), R10, G10, B10});
        }
        return colorArraySub;
    }

    public static String to0xStr(String s){
        return s.replaceFirst("ff","0x");
    }

    public static double SinglePointColorContrast_LAB (int[] color1, int[] color2){//单点比较颜色 返回欧氏距离
        int R1=color1[0];int G1=color1[1]; int B1=color1[2];
        int R2=color2[0];int G2=color2[1]; int B2=color2[2];
        double rmean = (R1 + R2 ) / 2;
        int R = R1 - R2;
        int G = G1 - G2;
        int B = B1 - B2;
        return Math.sqrt((2 + rmean/256) * (Math.pow(R, 2)) + 4 * (Math.pow(G, 2)) + (2 + (255 - rmean) / 256) * (Math.pow(B, 2)));
    }

    public static int[] color16To10_Str(String s16){ //16转10进制RGB颜色
        //0x05cc65
        String R16 = String.format("%s%s", String.valueOf(s16.charAt(2)), String.valueOf(s16.charAt(3)));
        String G16 = String.format("%s%s", String.valueOf(s16.charAt(4)), String.valueOf(s16.charAt(5)));
        String B16 = String.format("%s%s", String.valueOf(s16.charAt(6)), String.valueOf(s16.charAt(7)));
        int R10 = Integer.parseInt(R16, 16);
        int G10 = Integer.parseInt(G16, 16);
        int B10 = Integer.parseInt(B16, 16);
        return new int[]{R10,G10,B10};
    }

    public static int[] color16To10_int(int s16) {//返回RGB数组
        return new int[]{Color.red(s16), Color.green(s16), Color.blue(s16)};
    }

    // 多点模糊找色（优化）
    public static int[] MultiPointFindColor(int[] mainColors, ArrayList<Integer[]> subColors, double distance, int x1, int y1, int x2, int y2){
        for (int i = x1; i < x2; i++) {
            for (int j =  y1; j < y2; j++) {
                double _distance = SinglePointColorContrast_LAB(getRGB(bitmap, i, j), mainColors);//当前颜色和主颜色对比
                if (_distance<=distance){    // <= 欧氏距离 = 找到颜色
                    boolean  flag = false;   // 标志是否找到颜色。
                    for (Integer[] subxy : subColors) {   //检查所有相对坐标的子颜色
                        int subx = subxy[0] + i;
                        int suby = subxy[1] + j;
                        if (subx>-1 && subx < bitmap.getWidth() && suby > -1 && suby < bitmap.getHeight()){ //不能超出屏幕范围
                            double _disend = SinglePointColorContrast_LAB(getRGB(bitmap, subx, suby), new int[]{subxy[2],subxy[3],subxy[4]});//当前子颜色和子颜色对比
                            if (_disend > distance){  //没有找到颜色 则 不再继续对比其它的子颜色了
                                flag = false;
                                break;
                            }
                            flag = true; //找到颜色
                        }else{ //超出找色范围
                            flag = false;
                            break;
                        }
                    }
                    if (flag){
                        return new int[]{i,j,(int)_distance}; //返回找到的颜色坐标
                    }
                }
            }
        }
        return null; //没有找到颜色 返回 null
    }

    // 多点模糊找色
    public static int[] MultiPointFindColor(int mainColor, String subColors, double distance, int x1, int y1, int x2, int y2){
        assert bitmap != null;
        ArrayList<Integer[]> subcs = GBData.getColorListSub(subColors);//16进制文本颜色转成10进制颜色数组（子颜色列表）
        int[] mainColors = color16To10_int(mainColor);//文本转10进制颜色（主颜色）
        for (int i = x1; i < x2; i++) {
            for (int j =  y1; j < y2; j++) {
                double _distance = SinglePointColorContrast_LAB(getRGB(bitmap, i, j), mainColors);//当前颜色和主颜色对比
                if (_distance <= distance){    // <= 欧氏距离 = 找到颜色
                    boolean flag = false;   // 标志是否找到颜色。
                    for (Integer[] subxy : subcs) {   //检查所有相对坐标的子颜色
                        int subx = subxy[0] + i;
                        int suby = subxy[1] + j;
                        if (subx > -1 && subx < bitmap.getWidth() && suby > -1 && suby < bitmap.getHeight()){ //不能超出屏幕范围
                            double _disend = SinglePointColorContrast_LAB(getRGB(bitmap, subx, suby), new int[]{subxy[2], subxy[3], subxy[4]});//当前子颜色和子颜色对比
                            if (_disend > distance){  //没有找到颜色 则 不再继续对比其它的子颜色了
                                flag = false;
                                break;
                            }
                            flag = true; //找到颜色
                        }else{ //超出找色范围
                            flag = false;
                            break;
                        }
                    }
                    if (flag){
                        return new int[]{i,j,(int)_distance}; //返回找到的颜色坐标
                    }
                }
            }
        }
        return null; //没有找到颜色 返回 null
    }
}
