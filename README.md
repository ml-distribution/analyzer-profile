
# 分词服务项目

> 基于Java实现，当前分词器版本为5.5.0。

----

## 项目简介

### 项目起因

本项目主要为中文数据处理服务的。

### 项目框架

`analyzer-ik`: 模拟语义分词器

`analyzer-mmseg4j`: 基于MMSeg算法的分词器

`analyzer-smartcn`: Lucene官方自带的分词器

`analyzer-web`: Restful风格的分词接口


> **备注:** 该分词框架持续更新中。

### API文档
http://wiki.dev.zx.soft/doku.php

> **备注:** API文档统一放在公司的wiki上。

----

## 项目架构

1. 对象池技术： commons-pool
2. Restful风格的接口：restlet
3. 海量数据测评：mapreduce

### 常见约束词
Item      | Value
--------- | -----
analyzer  | 解析器
wordsegment    | 分词器
testing  | 测试模块
web      |  接口服务

### 打包命令

mvn deploy [-Dmaven.test.skip=true]

### 示例代码

```
    ** 分词示例 **      
    ...
    参考源码中Demo.
    ...
	
    ** 嵌入Lucene示例 ** 
    ...
    参考源码中Demo.
    ...
    
    ** 嵌入Solr示例 ** 
    ...
    参考源码中Demo.
    ...

```

## 开发人员

[新浪微博: PlayBigData](http://www.weibo.com/u/1862087393 "新浪微博")

[微信: wgybzb](https://github.com/wgybzbrobot "微信")

[QQ: 1010437118](https://github.com/wgybzbrobot "QQ")

[邮箱: wgybzb@sina.cn](https://github.com/wgybzbrobot "邮箱")

[GitHub: wgybzbrobot](https://github.com/wgybzbrobot "GitHub首页")


