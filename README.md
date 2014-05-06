
# 分词服务项目

> 基于Java实现。

## 项目内容

- [项目简介](#项目简介)
- [项目架构](#项目架构)
- [开发人员](#开发人员)

----

## 项目简介

### 项目起因

本项目主要为中文数据处理服务的。

### 项目框架

`analyzer-parent`: jar和插件依赖工程

`analyzer-ictclas4j`: 基于FreeICTCLAS的分词器

`analyzer-ik`: 模拟语义分词器

`analyzer-imdict`: 基于HMM算法的分词器

`analyzer-mmseg4j`: 基于MMSeg算法的分词器

`analyzer-paoding`: 庖丁分词器

`analyzer-smallseg`: 基于DFA的轻量级分词器

`analyzer-fudan`: 复旦大学的分词器

`analyzer-smartcn`: Lucene官方自带的分词器

`analyzer-testing`: 各分词器性能测试

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

### 示例代码

```
    ** 分词示例 **      
    
	
    ** 嵌入Lucene示例 ** 
    
    
    ** 嵌入Solr示例 ** 

```

### 作者信息

[新浪微博: 皮皮数据挖掘](http://www.weibo.com/u/1862087393 "新浪微博")

[微信: wgybzb](https://github.com/wgybzbrobot "微信")

[QQ: 1010437118](https://github.com/wgybzbrobot "QQ")

[邮箱: wgybzb@sina.cn](https://github.com/wgybzbrobot "邮箱")

[GitHub: wgybzbrobot](https://github.com/wgybzbrobot "GitHub首页")


