## 功能介绍
* 爬取ElasticSearch官方文档

    爬取使用WebMagic实现,主要逻辑在`EsDocPageProcessor`类中  
    [WebMagic官方文档](http://webmagic.io/docs/zh/posts/ch4-basic-page-processor/selectable.html)  
    [Xpath相关语法](http://www.w3school.com.cn/xpath/index.asp)

* 统计抓取文档的词频
    统计用java实现,主要逻辑在`WordCountUtil`类中  
    词频统计结会根据传入参数`resultFileName`,在项目根目录下写两个文件
    * ${resultFileName}SortedByKey.txt:存储按字母排序的结果
    * ${resultFileName}SortedByValue.txt:存储按词频逆序排序的结果
* 程序入口
    程序入口类`src/test/app/WebMagicTest`    

## 异常处理

1. javax.net.ssl.SSLException: Received fatal alert: protocol_version
   
