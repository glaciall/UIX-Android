package com.youaix.framework.ui;

public final class Attribute
{
    public static final int WIDTH_MATCH_PARENT = -1;
    public static final int HEIGHT_MATCH_PARENT = -1;

    public static final int WRAP_CONTENT = -2;              // 使用子元素大小
    public static final int FILL_PARENT = -1;               // 填充父元素大小
    public static final int FILL_REST = -3;                 // 填充父剩余大小
    
    public static final int POSITION_UNSPECIFIED = -100000;	// 位置未设置
    public static final int POSITION_CENTER = -100001;		// 位置水平居中
    public static final int POSITION_MIDDLE = -100002;		// 位置垂直居中
    
	public static final int BG_LEFT = -10000001;				// 背景图从水平左边开始
	public static final int BG_CENTER = -10000002;			// 背景图从水平中间开始
	public static final int BG_RIGHT = -10000003;			// 背景图从水平右边开始
	public static final int BG_TOP = -10000004;				// 背景图从垂直方向的上边开始
	public static final int BG_MIDDLE = -10000005;			// 背景图从垂直方向的中间开始
	public static final int BG_BOTTOM = -10000006;			// 背景图从垂直方向的下边开始
    
    public static final int DIR_VERTICAL = 1;				// 垂直方向
    public static final int DIR_HORIZONTAL = 0;				// 水平方向

    public static final int ALIGN_TOP = 0x01;               // 垂直居上
    public static final int ALIGN_MIDDLE = 0x02;            // 垂直居中
    public static final int ALIGN_BOTTOM = 0x03;            // 垂直居下
    public static final int ALIGN_LEFT = 0x04;              // 水平居左
    public static final int ALIGN_CENTER = 0x05;            // 水平居中
    public static final int ALIGN_RIGHT = 0x06;             // 水平居右

    public static final int COLOR_WHITE = 0xffffffff;       // 白色
    public static final int COLOR_BLACK = 0xff000000;       // 黑色
    public static final int COLOR_RED = 0xffff0000;         // 红色
    public static final int COLOR_GREEN = 0xff00ff00;       // 绿色
    public static final int COLOR_BLUE = 0xff0000ff;        // 蓝色
    public static final int COLOR_ORANGE = 0xffff3300;      // 桔黄色
    public static final int COLOR_GRAY = 0xffcccccc;        // 灰色
    public static final int COLOR_DARK_GRAY = 0xff333333;   // 深灰色

    public static int color(int rr, int gg, int bb)
    {
        return (((rr & 0xff) << 16) | ((gg & 0xff) << 8) | (bb & 0xff)) | 0xff000000;
    }

    public static int color(int color)
    {
        return (color & 0x00ffffff) | 0xff000000;
    }

    public static int alpha(int color, int percent)
    {
        percent = ((int)(255f / 100f * (float)percent)) & 0xff;
        return (color & 0x00ffffff) | (percent << 24);
    }
}
