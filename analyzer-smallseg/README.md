## SmallSeg

### 分词器说明

[smallseg]()开源的的轻量级的中文分词工具包。

`分词效果在线演示`:

https://smallseg.appspot.com/smallseg

友情链接：[结巴分词](https://github.com/fxsjy/jieba)

### 分词器特性

1. 可自定义词典、速度快、可在Google App Engine上运行。

### 项目配置

**停用词**


**扩展词**


### 项目使用

例子：

    cuttest("这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。")
    cuttest("我不喜欢日本和服。")
    cuttest("雷猴回归人间。")
    cuttest("工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作")
    cuttest("我需要廉租房")
    cuttest("永和服装饰品有限公司")
    cuttest("我爱北京天安门")
    cuttest("abc")
    cuttest("隐马尔可夫")
    cuttest("雷猴是个好网站")
    cuttest("“Microsoft”一词由“MICROcomputer（微型计算机）”和“SOFTware（软件）”两部分组成")
    cuttest("草泥马和欺实马是今年的流行词汇")
    cuttest("伊藤洋华堂总府店")
    cuttest("中国科学院计算技术研究所")
    cuttest("罗密欧与朱丽叶")


Load dict...
Dict is OK.
这是 一个 伸手不见五指 黑夜 我叫 孙悟空 我爱 北京 我爱 Python C++
================================
我 不喜欢 日本 和服
================================
雷猴 回归 人间
================================
工信 信处 女干事 每月 经过 下属 科室 都要 亲口 交代 24 口 交换机 等 技术性 器件 安装 工作
================================
我 需要 廉租房
================================
永 和服 装饰品 有限公司
================================
我爱 北京 天安门
================================
abc
================================
隐 马尔可夫
================================
雷猴 猴是 是个 好网站
================================
Microsoft 一词 由 MICROcomputer 微型 计算机 SOFTware 软件 两部分 组成
================================
草泥马 欺实 实马 马是 今年 流行 词汇
================================
伊藤 洋华堂 总府 府店
================================
中国 科学院 计算技术 研究所
================================
罗密欧 与 朱丽叶
================================
性能（对于2000字左右的文章进行分词）：

Load dict...
Dict is OK.
1 times, cost: 0.0309998989105
2 times, cost: 0.0
3 times, cost: 0.0150001049042
4 times, cost: 0.0160000324249
5 times, cost: 0.0309998989105
6 times, cost: 0.0320000648499
7 times, cost: 0.0309998989105
8 times, cost: 0.0310001373291
9 times, cost: 0.0469999313354
10 times, cost: 0.0309998989105
11 times, cost: 0.047000169754
12 times, cost: 0.0469999313354
13 times, cost: 0.0620000362396
14 times, cost: 0.0629999637604
15 times, cost: 0.0620000362396
16 times, cost: 0.0629999637604
17 times, cost: 0.0780000686646
18 times, cost: 0.077999830246
19 times, cost: 0.0940001010895
20 times, cost: 0.0929999351501
21 times, cost: 0.0940001010895
22 times, cost: 0.0939998626709
23 times, cost: 0.0940001010895
24 times, cost: 0.108999967575
25 times, cost: 0.108999967575
26 times, cost: 0.110000133514
27 times, cost: 0.125
28 times, cost: 0.125
29 times, cost: 0.125
30 times, cost: 0.139999866486
31 times, cost: 0.125
32 times, cost: 0.141000032425
33 times, cost: 0.141000032425
34 times, cost: 0.155999898911
35 times, cost: 0.156000137329
36 times, cost: 0.155999898911
37 times, cost: 0.15700006485
38 times, cost: 0.171000003815
39 times, cost: 0.171999931335
40 times, cost: 0.171999931335
41 times, cost: 0.172000169754
42 times, cost: 0.18799996376
43 times, cost: 0.18700003624
44 times, cost: 0.18799996376
45 times, cost: 0.203000068665
46 times, cost: 0.202999830246
47 times, cost: 0.203000068665
48 times, cost: 0.203000068665
49 times, cost: 0.218999862671
50 times, cost: 0.233999967575
51 times, cost: 0.266000032425
52 times, cost: 0.233999967575
53 times, cost: 0.235000133514
54 times, cost: 0.233999967575
55 times, cost: 0.25
56 times, cost: 0.25
57 times, cost: 0.25
58 times, cost: 0.25
59 times, cost: 0.25
60 times, cost: 0.266000032425
61 times, cost: 0.264999866486
62 times, cost: 0.281000137329
63 times, cost: 0.28200006485
64 times, cost: 0.280999898911
65 times, cost: 0.280999898911
66 times, cost: 0.297000169754
67 times, cost: 0.296999931335
68 times, cost: 0.296999931335
69 times, cost: 0.297000169754
70 times, cost: 0.311999797821
71 times, cost: 0.313000202179
72 times, cost: 0.327999830246
73 times, cost: 0.328000068665
74 times, cost: 0.328000068665
75 times, cost: 0.327999830246
76 times, cost: 0.328000068665
77 times, cost: 0.328000068665
78 times, cost: 0.343999862671
79 times, cost: 0.344000101089
80 times, cost: 0.358999967575
81 times, cost: 0.344000101089
82 times, cost: 0.358999967575
83 times, cost: 0.359999895096
84 times, cost: 0.375
85 times, cost: 0.375
86 times, cost: 0.375
87 times, cost: 0.375
88 times, cost: 0.375
89 times, cost: 0.390000104904
90 times, cost: 0.40700006485
91 times, cost: 0.405999898911
92 times, cost: 0.43700003624
93 times, cost: 0.40700006485
94 times, cost: 0.405999898911
95 times, cost: 0.421999931335
96 times, cost: 0.406000137329
97 times, cost: 0.452999830246
98 times, cost: 0.422000169754
99 times, cost: 0.436999797821
100 times, cost: 0.438000202179
------

### 作者信息

[新浪微博: 皮皮数据挖掘](http://www.weibo.com/u/1862087393 "新浪微博")

[微信: wgybzb](https://github.com/wgybzbrobot "微信")

[QQ: 1010437118](https://github.com/wgybzbrobot "QQ")

[邮箱: wgybzb@sina.cn](https://github.com/wgybzbrobot "邮箱")

[GitHub: wgybzbrobot](https://github.com/wgybzbrobot "GitHub首页")
