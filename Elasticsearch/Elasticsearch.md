# Elasticsearch 安装

- https://www.elastic.co/cn/elasticsearch/
- https://www.elastic.co/cn/downloads/elasticsearch



用迅雷等下载软件先下载好。然后传输到Linux。(wget 下载十几K非常慢)



```shell
# 创建es账号
adduser es

# 授权给 es账号
chown -R es:es elasticsearch-7.10.0

# 切换到 es账号
su es

# 软件移动到非root目录
mv elasticsearch-7.10.0 /home/es/
cd /home/es/elasticsearch-7.10.0

# 启动
./bin/elasticsearch

# 后台启动
./bin/elasticsearch -d
```

## 安装中文分词器



- https://github.com/medcl/elasticsearch-analysis-ik

```shell
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.5.1/elasticsearch-analysis-ik-5.5.1.zip
```



> 下载的是在太慢了。直接GitHub下载插件文件传输到Linux了。
>
> 把文件解压到 es 的 plugins文件夹然后重启 日志有 ik-plugin  加载信息表示成功。(GitHub作者有离线安装的步骤)



# Kinana 安装

- https://www.elastic.co/cn/downloads/kibana



```shell
# 解压

# 修改配置
vim kibana-7.10.0-linux-x86_64/config/kibana.yml
server.host: "0.0.0.0"
elasticsearch.hosts: ["http://192.168.240.230:9200"]

# 启动
./kibana-7.10.0-linux-x86_64/bin/kibana

# 后台启动
./kibana-7.10.0-linux-x86_64/bin/kibana &
```



# ElasticSearch 基本使用

curl: http://www.ruanyifeng.com/blog/2017/08/elasticsearch.html

kibana dev tool: https://blog.csdn.net/sinat_36005594/article/details/90449781

中文分词器(IK)：https://github.com/medcl/elasticsearch-analysis-ik



## REST 风格

```shell
# 创建索引
curl -X PUT 'localhost:9200/weather'

# 删除索引
curl -X DELETE 'localhost:9200/weather'


# 中文分词设置
curl -X PUT 'localhost:9200/accounts' -H 'Content-Type:application/json' -d '{
  "mappings": {
	  "properties": {
		"user": {
		  "type": "text",
		  "analyzer": "ik_max_word",
		  "search_analyzer": "ik_smart"
		},
		"title": {
		  "type": "text",
		  "analyzer": "ik_max_word",
		  "search_analyzer": "ik_smart"
		},
		"desc": {
		  "type": "text",
		  "analyzer": "ik_max_word",
		  "search_analyzer": "ik_smart"
		}
	  }
  }
}'

# 查询mapping 数据结构
curl -X GET 'localhost:9200/accounts?pretty'

# 使用PUT新增数据
curl -X PUT 'localhost:9200/accounts/_create/1' -H 'Content-Type:application/json' -d '{
  "user": "张三",
  "title": "工程师",
  "desc": "数据库管理员"
}'


# 使用POST新增数据
curl -X POST 'localhost:9200/accounts/_create/2' -H 'Content-Type:application/json' -d '{
  "user": "李四",
  "title": "工程师",
  "desc": "系统管理"
}'

# 查询数据
curl -X GET 'localhost:9200/accounts/_search?pretty'
```



## Kibana Dev Tools

感觉和REST 方式没什么区别。通过F12 也是发了一个HTTP请求出去。

```shell
GET _search
{
  "query": {
    "match_all": {}
  }
}

PUT /weather

GET /weather

DELETE /weather


DELETE /accounts/
PUT /accounts
{
  "mappings": {
	  "properties": {
		"user": {
		  "type": "text",
		  "analyzer": "ik_max_word",
		  "search_analyzer": "ik_smart"
		},
		"title": {
		  "type": "text",
		  "analyzer": "ik_max_word",
		  "search_analyzer": "ik_smart"
		},
		"desc": {
		  "type": "text",
		  "analyzer": "ik_max_word",
		  "search_analyzer": "ik_smart"
		}
	  }
  }
}



PUT /accounts/_create/1
{
  "user": "张三",
  "title": "工程师",
  "desc": "数据库管理员"
}


POST /accounts/_create/2
{
  "user": "李四",
  "title": "工程师",
  "desc": "系统管理"
}

GET /accounts/_search
```



# FAQ

## can not run elasticsearch as root

- https://www.cnblogs.com/gcgc/p/10297563.html 创建es账号



## could not find java in bundled jdk at /root/Downloads/elasticsearch-7.10.0/jdk/bin/java

- https://www.zkii.net/system/environment/2844.html 升级JDK
- https://blog.csdn.net/qq_43701760/article/details/102997369 将ES移动到别的目录



## 用IP地址访问被拒绝

```shell
[root@centos es]# curl http://192.168.240.230:9200
curl: (7) Failed connect to 192.168.240.230:9200; Connection refused
```

- https://www.cnblogs.com/gcgc/p/10297563.html



## max file descriptors [4096] for elasticsearch process is too low, increase to at least [65535]

- https://www.cnblogs.com/zhi-leaf/p/8484337.html



##  the default discovery settings are unsuitable for production use; at least one of [discovery.seed_hosts, discovery.seed_providers, cluster.initial_master_nodes] must be configured

- https://blog.csdn.net/happyzxs/article/details/89156068



## error" : "Content-Type header [application/x-www-form-urlencoded] is not supported

- https://blog.csdn.net/weixin_40161254/article/details/86000839 增加header



## ElasticSearch 7.4.2 Root mapping definition has unsupported parameters

- https://blog.csdn.net/yanyf2016/article/details/103972806  创建mappings



# 总结

7.0 + 的ES很多语法都不一样了。这点还是非常坑爹的。