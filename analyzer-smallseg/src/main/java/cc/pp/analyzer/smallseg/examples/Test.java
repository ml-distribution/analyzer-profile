package cc.pp.analyzer.smallseg.examples;

import cc.pp.analyzer.smallseg.core.SmallSeg;

public class Test {

	public static void main(String[] args) throws Exception {

		System.out.println(SmallSeg.cut("日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。"));
		System.out.println(SmallSeg.cut("伊藤洋华堂总府店"));
		System.out.println(SmallSeg
				.cut("工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作"));
		System.out.println(SmallSeg.cut("我购买了道具和服装。草泥马"));
		System.out.println(SmallSeg.cut("我爱北京天安门"));
		System.out.println(SmallSeg.cut("中国科学院"));
		System.out.println(SmallSeg.cut("雷猴是个好网站"));
		System.out.println(SmallSeg.cut(("总经理完成了这件事情")));
		System.out.println(SmallSeg.cut(("电脑修好了")));
		System.out.println(SmallSeg.cut(("做好了这件事情就一了百了了")));
		System.out.println(SmallSeg.cut(("人们审美的观点是不同的")));
		System.out.println(SmallSeg.cut(("我们买了一个美的空调")));
		System.out.println(SmallSeg.cut(("中国的首都是北京")));
		System.out.println(SmallSeg.cut(("买水果然后来世博园")));
		System.out.println(SmallSeg.cut(("还需要很长的路要走")));
		System.out.println(SmallSeg.cut(("进行有偿家教谋取私利的行为。续梅表示，在极少数教师身上存在“该讲的内容上课不讲，而是放到课后有偿家教的时候讲”的情况")));
		System.out.println(SmallSeg.cut(("60周年首都阅兵")));
		System.out.println(SmallSeg.cut(("hello你好人们审美的观点是不同的")));
		System.out.println(SmallSeg.cut(("很好但主要是基于网页形式")));
		System.out.println(SmallSeg.cut(("但是后来我才知道你是对的")));
		System.out.println(SmallSeg.cut(("为什么我不能拥有想要的生活")));
		System.out.println(SmallSeg.cut(("使用了它就可以解决一些问题")));
		System.out.println(SmallSeg.cut(("，使用了它就可以解决一些问题")));
	}
}
