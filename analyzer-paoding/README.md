Paoding Analysis摘要
Paoding's Knives 中文分词具有极 高效率 和 高扩展性 。引入隐喻，采用完全的面向对象设计，构思先进。

高效率：在PIII 1G内存个人机器上，1秒 可准确分词 100万 汉字。

采用基于 不限制个数 的词典文件对文章进行有效切分，使能够将对词汇分类定义。

能够对未知的词汇进行合理解析

欢迎
如果对该项目您有任何建议，欢迎您在http://code.google.com/p/paoding/issues/list 中提出各种issues.

用心的贡献，极其能鼓励人

----------------------！

2010-01-20 庖丁 Lucene 3.0 升级说明

(代码已提交svn，下载包稍后稍推迟下)

这次升级的主要目的是支持Lucene 3.0，具体改动如下：

（1）支持Lucene 3.0，对Lucene 3.0以下的版本，请使用 http://paoding.googlecode.com/svn/branches/paoding-for-lucene-2.4/ 中的代码编译。

（2）使用Java 5.0编译，不再支持Java 1.4，以后的新功能将会在Java 5上开发。

（3）PaodingAnalyzer的调用接口没有改动，但在使用上需要适应Lucene 3.0的API，分词示例如下：

//生成analyzer实例 Analyzer analyzer = new PaodingAnalyzer(properties);
//取得Token流 TokenStream stream = analyzer.tokenStream("", reader);
//重置到流的开始位置 stream.reset();
//添加工具类 TermAttribute termAtt = (TermAttribute) stream.addAttribute(TermAttribute.class); OffsetAttribute offAtt = (OffsetAttribute) stream.addAttribute(OffsetAttribute.class);
//循环打印所有分词及其位置 while (stream.incrementToken()) {
System.out.println(termAtt.term() + " " + offAtt.startOffset() + " " + offAtt.endOffset());
}
具体使用方法可以参见net.paoding.analysis.analyzer.estimate以及net.paoding.analysis.examples包下面的示例代码。
