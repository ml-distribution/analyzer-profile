## MMSeg4j

### 分词器说明

1. mmseg4j 用 Chih-Hao Tsai 的[MMSeg算法](http://technology.chtsai.org/mmseg/)实现的中文分词器，并实现[lucene](http://lucene.apache.org/)
的analyzer和[solr](http://lucene.apache.org/)的TokenizerFactory 以方便在[lucene](http://lucene.apache.org/)和[solr](http://lucene.apache.org/)中使用。

2. MMSeg 算法有两种分词方法：Simple和Complex，都是基于正向最大匹配。Complex 加了四个规则过虑。官方说：词语的正确识别率达到了 98.41%。mmseg4j 已经实现了这两种分词算法。

* 1.5版的分词速度simple算法是 1100kb/s左右、complex算法是 700kb/s左右，（测试机：AMD athlon 64 2800+ 1G内存 xp）。
* 1.6版在complex基础上实现了最多分词(max-word)。“很好听” -> "很好|好听"; “中华人民共和国” -> "中华|华人|共和|国"; “中国人民银行” -> "中国|人民|银行"。
* 1.7-beta 版, 目前 complex 1200kb/s左右, simple 1900kb/s左右, 但内存开销了50M左右. 上几个版都是在10M左右.
* 1.8 后,增加 CutLetterDigitFilter过虑器，切分“字母和数”混在一起的过虑器。比如：mb991ch 切为 "mb 991 ch"。

mmseg4j实现的功能详情请看：[google code](http://mmseg4j.googlecode.com/svn/trunk/CHANGES.txt)。

3. 在 com.chenlb.mmseg4j.example包里的类示例了三种分词效果。

4. 在 com.chenlb.mmseg4j.analysis包里扩展lucene analyzer。MMSegAnalyzer默认使用max-word方式分词(还有：ComplexAnalyzer, SimplexAnalyzer, MaxWordAnalyzer)。

5. 在 com.chenlb.mmseg4j.solr包里扩展solr tokenizerFactory。 1.9.0 可以不用 dicPath 参数，可以使用 mmseg4j-core-1.9.0.jar 里的 words.dic 在 solr的 schema.xml 
中定义 field type如：

```java
<fieldtype name="textComplex" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
             <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="complex" dicPath="dic">
             </tokenizer>
      </analyzer>
</fieldtype>
<fieldtype name="textMaxWord" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
             <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="max-word" dicPath="dic">
             </tokenizer>
      </analyzer>
</fieldtype>
<fieldtype name="textSimple" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
             <tokenizer class="com.chenlb.mmseg4j.solr.MMSegTokenizerFactory" mode="simple" dicPath="n:/OpenSource/apache-solr-1.3.0/example/solr/my_dic">
             </tokenizer>
      </analyzer>
</fieldtype>
```

dicPath 指定词库位置（每个MMSegTokenizerFactory可以指定不同的目录，当是相对目录时，是相对 solr.home 的目录），mode 指定分词模式（simple|complex|max-word，默认是max-word）。

6. 运行，词典用mmseg.dic.path属性指定、在classpath 目录下或在当前目录下的data目录，默认是 classpath/data 目录。如果使用 mmseg4j-with-dic.jar 包可以不指定词库目录（如果指定也可以，它们也可以被加载）。

```java
java -jar mmseg4j-core-1.9.0.jar `这里是字符串`。

java -cp .;mmseg4j-core-1.9.0.jar -Dmmseg.dic.path=./other-dic com.chenlb.mmseg4j.example.Simple `这里是字符串`。

java -cp .;mmseg4j-core-1.9.0.jar com.chenlb.mmseg4j.example.MaxWord `这里是字符串`
```

7. 一些字符的处理 英文、俄文、希腊、数字（包括①㈠⒈）的分出一连串的。目前版本没有处理小数字问题， 如ⅠⅡⅢ是单字分，字库(chars.dic)中没找到也单字分。

8. 词库(强制使用 UTF-8)：

data/chars.dic 是单字与语料中的频率，一般不用改动，1.5版本中已经加到mmseg4j的jar里了，我们不需要关心它，当然你在词库目录放这个文件可以覆盖它。
data/units.dic 是单字的单位，默认读jar包里的，你也可以自定义覆盖它，这功能是试行，如果不喜欢它，可以空的units.dic文件(放到你的词库目录下)覆盖它。
data/words.dic 是词库文件，一行一词，当然你也可以使用自己的，1.5版本使用 sogou 词库，1.0的版本是用 rmmseg 带的词库。
data/wordsxxx.dic 1.6版支持多个词库文件，data 目录（或你定义的目录）下读到"words"前缀且".dic"为后缀的文件。如：data/words-my.dic。
由于 utf-8 文件有带与不带 BOM 之分，建议词库第一行为空行或为无 BOM 格式的 utf-8 文件。

9. MMseg4jHandler(1.8以后支持): 添加 MMseg4jHandler 类，可以在solr中用url的方式来控制加载检测词库。参数：

dicPath 是指定词库的目录，特性与MMSegTokenizerFactory中的dicPath一样（相对目录是，是相对 solr.home）。

check 是指是否检测词库，其值是true 或 on。

reload 是否尝试加载词库，其值是 true 或 on。此值为 true，会忽视 check 参数。

solrconfig.xml：

<requesthandler name="/mmseg4j" class="com.chenlb.mmseg4j.solr.MMseg4jHandler">
    <lst name="defaults">
        <str name="dicPath">
            dic
        </str>
    </lst>
</requesthandler>

此功能可以让外置程序做相关的控制，如：尝试加载词库，然后外置程序决定是否重做索引。

在 solr 1.3/1.4 与 lucene 2.3/2.4/2.9 测试过，官方博客 http://blog.chenlb.com/category/mmseg4j。

mmseg4j 1.8.3 只支持 lucene 2.9/3.0 接口 和 solr 1.4。其它没改动。

mmseg4j 1.8.5 支持 lucene 3.1, solr 3.1。

mmseg4j 1.9.0 支持 lucene 4.0, solr 4.0。

mmseg4j 1.9.1 支持 solr/lucene 4.3.1

1.7.2 与 1.6.2 开始核心的程序与 lucene 和 solr 扩展分开打包，方便兼容低版本的 lucene，低版本（<= lucene 2.2）的 lucene 扩展请仿照 MMSegTokenizer.java。

有任何疑问、建议，欢迎到论坛 http://groups.google.com/group/mmseg4j/topics?hl=zh_CN讨论。

可以在 http://code.google.com/p/mmseg4j/issues/list 提出 Bug 或 希望 mmseg4j 有的功能。

历史版本：

1.7.3 http://mmseg4j.googlecode.com/svn/branches/mmseg4j-1.7/
1.8.5 http://mmseg4j.googlecode.com/svn/branches/mmseg4j-1.8/


### 作者信息

[新浪微博: 皮皮数据挖掘](http://www.weibo.com/u/1862087393 "新浪微博")

[微信: wgybzb](https://github.com/wgybzbrobot "微信")

[QQ: 1010437118](https://github.com/wgybzbrobot "QQ")

[邮箱: wgybzb@sina.cn](https://github.com/wgybzbrobot "邮箱")

[GitHub: wgybzbrobot](https://github.com/wgybzbrobot "GitHub首页")

