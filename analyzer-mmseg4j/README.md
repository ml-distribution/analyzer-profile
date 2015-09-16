## MMSeg4j

> 目前兼容到Lucene5.1.0和Solr5.1.0版本，在原IK中文分词版本5.0上进行修改。

### 分词器说明

1. mmseg4j 用 Chih-Hao Tsai 的[MMSeg算法](http://technology.chtsai.org/mmseg/)实现的中文分词器，并实现[lucene](http://lucene.apache.org/)
的analyzer和[solr](http://lucene.apache.org/)的TokenizerFactory 以方便在[lucene](http://lucene.apache.org/)和[solr](http://lucene.apache.org/)中使用。

2. MMSeg 算法有两种分词方法：Simple和Complex，都是基于正向最大匹配。Complex 加了四个规则过虑。官方说：词语的正确识别率达到了 98.41%。mmseg4j 已经实现了这两种分词算法。

* 1.5版的分词速度simple算法是 1100kb/s左右、complex算法是 700kb/s左右，（测试机：AMD athlon 64 2800+ 1G内存 xp）。
* 1.6版在complex基础上实现了最多分词(max-word)。“很好听” -> "很好|好听"; “中华人民共和国” -> "中华|华人|共和|国"; “中国人民银行” -> "中国|人民|银行"。
* 1.7-beta 版, 目前 complex 1200kb/s左右, simple 1900kb/s左右, 但内存开销了50M左右. 上几个版都是在10M左右.
* 1.8 后,增加 CutLetterDigitFilter过虑器，切分“字母和数”混在一起的过虑器。比如：mb991ch 切为 "mb 991 ch"。

mmseg4j实现的功能详情请看：[google code](http://mmseg4j.googlecode.com/svn/trunk/CHANGES.txt)。

3. 在 cc.pp.analyzer.mmseg4j.demo包里的类示例了三种分词效果。

4. 在 cc.pp.analyzer.mmseg4j.lucene包里扩展lucene analyzer。MMSegAnalyzer默认使用max-word方式分词(还有：ComplexAnalyzer, SimplexAnalyzer, MaxWordAnalyzer)。

5. 在 cc.pp.analyzer.mmseg4j.solr包里扩展solr tokenizerFactory。 1.9.0 可以不用 dicPath 参数，可以使用 mmseg4j-core-1.9.0.jar 里的 words.dic 在 solr的 schema.xml 
中定义 field type如：

```xml
<fieldtype name="textComplex" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
             <tokenizer class="cc.pp.analyzer.mmseg4j.solr.MMSegTokenizerFactory" mode="complex" dicPath="mmseg4j_dic">
             </tokenizer>
      </analyzer>
</fieldtype>
<fieldtype name="textMaxWord" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
             <tokenizer class="cc.pp.analyzer.mmseg4j.solr.MMSegTokenizerFactory" mode="max-word" dicPath="mmseg4j_dic">
             </tokenizer>
      </analyzer>
</fieldtype>
<fieldtype name="textSimple" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
             <tokenizer class="cc.pp.analyzer.mmseg4j.solr.MMSegTokenizerFactory" mode="simple" dicPath="XXX/solr-4.8-example/sentiment//mmseg4j_dic">
             </tokenizer>
      </analyzer>
</fieldtype>
```

**tokenizer 的参数：**
 * dicPath 参数 － 设置自定义的扩展词库，支持相对路径(相对于 solr_home).
 * mode 参数 － 分词模式。

> dicPath 指定词库位置（每个MMSegTokenizerFactory可以指定不同的目录，当是相对目录时，是相对 solr.home 的目录），mode 指定分词模式（simple|complex|max-word，默认是max-word）。

6. 运行，词典用mmseg.dic.path属性指定、在classpath 目录下或在当前目录下的data目录，默认是 classpath/data 目录。

```java
java -jar analyzer-mmseg4j-5.1.0.jar `这里是字符串`。

java -cp .;analyzer-mmseg4j-5.1.0.jar -Dmmseg.dic.path=./other-dic cc.pp.analyzer.mmseg4j.demo.SimpleDemo `这里是字符串`。

java -cp .;analyzer-mmseg4j-5.1.0.jar cc.pp.analyzer.mmseg4j.demo.MaxWordDemo `这里是字符串`
```

7. 一些字符的处理 英文、俄文、希腊、数字（包括①㈠⒈）的分出一连串的。目前版本没有处理小数字问题， 如ⅠⅡⅢ是单字分，字库(chars.dic)中没找到也单字分。

8. 词库(强制使用 UTF-8)：

`data/chars.dic` 是单字与语料中的频率，一般不用改动，1.5版本中已经加到mmseg4j的jar里了，我们不需要关心它，当然你在词库目录放这个文件可以覆盖它。

`data/units.dic` 是单字的单位，默认读jar包里的，你也可以自定义覆盖它，这功能是试行，如果不喜欢它，可以空的units.dic文件(放到你的词库目录下)覆盖它。

`data/words.dic` 是词库文件，一行一词，当然你也可以使用自己的，1.5版本使用 sogou 词库，1.0的版本是用 rmmseg 带的词库。

`data/wordsxxx.dic` 1.6版支持多个词库文件，data 目录（或你定义的目录）下读到"words"前缀且".dic"为后缀的文件。如：data/words-my.dic。

> 由于 utf-8 文件有带与不带 BOM 之分，建议词库第一行为空行或为无 BOM 格式的 utf-8 文件。

9. MMseg4jHandler(1.8以后支持): 添加 MMseg4jHandler 类，可以在solr中用url的方式来控制加载检测词库。参数：

`dicPath` 是指定词库的目录，特性与MMSegTokenizerFactory中的dicPath一样（相对目录是，是相对 solr.home）。

`check` 是指是否检测词库，其值是true 或 on。

`reload` 是否尝试加载词库，其值是 true 或 on。此值为 true，会忽视 check 参数。

> solrconfig.xml：

```xml
<requesthandler name="/mmseg4j" class="cc.pp.analyzer.mmseg4j.solr.MMseg4jHandler">
    <lst name="defaults">
        <str name="dicPath">
            dic
        </str>
    </lst>
</requesthandler>
```

> 此功能可以让外置程序做相关的控制，如：尝试加载词库，然后外置程序决定是否重做索引。

### 作者信息

[新浪微博: 皮皮数据挖掘](http://www.weibo.com/u/1862087393 "新浪微博")

[微信: wgybzb](https://github.com/wgybzbrobot "微信")

[QQ: 1010437118](https://github.com/wgybzbrobot "QQ")

[邮箱: wgybzb@sina.cn](https://github.com/wgybzbrobot "邮箱")

[GitHub: wgybzbrobot](https://github.com/wgybzbrobot "GitHub首页")

