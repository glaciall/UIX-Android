[TOC]


##准备如何改造呢？
###UI标签细化
1. 布局类
	1. UL
	2. OL
	3. TABLE???
	4. SPAN
	5. CENTER
	6. 
2. 文字类
	1. HEADING(H1, H2, H3, H4, H5, H6)
	2. B
	3. EM
	4. DEL
	5. SUP
	6. SUB
	7. TEXT
3. 表单类
	1. BUTTON
4. 其它类型
	1. 待续
###功能改进
1. 图片分辨率适配
2. 分辨率单位支持浮点数？



###Page结构改造
###去除冗余的模块


##还有哪些要添加进来的呢？
###动画
```java
// 大小变换
// 颜色变换
// 位置变换
// 透明度
target
	.from(Alpha.v(100), Color.v(0xff0000))
    .to(Alpha.v(0), Color.v(0xffffff))
    .start(Speed.FAST);
```
###手势
###样式表，借鉴XSLT和CSS
```css
div[id=abc] span a
{
	color: #ff0000;
    hover-color: #ff00ff;
}
#abc span
{
	width:100px;
    height:100px;
}
```
###模版，使用XML标签来作模板
