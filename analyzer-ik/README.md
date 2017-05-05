## IK Analyzer

### 分词器说明

[IK Analyzer](https://code.google.com/p/ik-analyzer/)是一个开源的，基于java语言开发的轻量级的中文分词工具包。
从2006年12月推出1.0版开始， [IK Analyzer](https://code.google.com/p/ik-analyzer/)已经推出了4个大版本。
最初，它是以开源项目[Lucene](http://lucene.apache.org/)为应用主体的，结合词典分词和文法分析算法的中文分词组件。
从3.0版本开始，IK发展为面向Java的公用分词组件，独立于[Lucene](http://lucene.apache.org/)项目，同时提供了对[Lucene](http://lucene.apache.org/)的默认优化实现。
在2012版本中，IK实现了简单的分词歧义排除算法，标志着IK分词器从单纯的词典分词向模拟语义分词衍化。

### 分词器特性

1. 采用了特有的**正向迭代最细粒度切分算法**，支持`细粒度`和`智能分词`两种切分模式；

2. 在系统环境：Core2 i7 3.4G双核，4G内存，window 7 64位， Sun JDK 1.6_29 64位 普通pc环境测试，IK2012具有160万字/秒（3000KB/S）的高速处理能力。

3. 2012版本的智能分词模式支持简单的分词排歧义处理和数量词合并输出。

4. 采用了多子处理器分析模式，支持：英文字母、数字、中文词汇等分词处理，兼容韩文、日文字符

5. 优化的词典存储，更小的内存占用。支持用户词典扩展定义。特别的，在2012版本，词典支持中文，英文，数字混合词语。

6. 兼容lucene4.7和solr4.7版本，并在性能上做了很多优化。

### 项目配置

**默认停用词**

`stopword.dic`以及`stopword_*.dic`文件。

**默认扩展词**

`ext.dic`文件。

**外部文件**

ik_dic下面的ext_ik.dic和stopwords_ik.dic文件。

### 项目使用

```java
// 申明Analyzer

```

### 作者信息

[新浪微博: 皮皮数据挖掘](http://www.weibo.com/u/1862087393 "新浪微博")

[微信: wgybzb](https://github.com/wgybzbrobot "微信")

[QQ: 1010437118](https://github.com/wgybzbrobot "QQ")

[邮箱: wgybzb@sina.cn](https://github.com/wgybzbrobot "邮箱")

[GitHub: wgybzbrobot](https://github.com/wgybzbrobot "GitHub首页")
