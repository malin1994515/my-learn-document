# 安装



修改配置信息

```shell
vi /etc/sysconfig/network-scripts/ifcfg-ens33 
vi /etc/sysconfig/network
vi /etc/hostname 
service network restart
```



下载软件

```shell
cd /root/Download
wget https://archive.apache.org/dist/zookeeper/zookeeper-3.6.1/apache-zookeeper-3.6.1-bin.tar.gz
tar -zxvf apache-zookeeper-3.6.1-bin.tar.gz
cd apache-zookeeper-3.6.1-bin/
```



# Quick Started

https://zookeeper.apache.org/doc/current/zookeeperStarted.html



## Standalone Opertion(单机运行)

修改配置

```shell
vi config/zoo.cfg
tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181
```

- `tickTime`：心跳时间单位毫秒
- `dataDir`：存储内存中数据库快照以及数据库更新的事务日志的位置
- `clientPort`：端口

启动ZK服务

```shell
bin/zkServer.sh start
```

## Managing Zookeeper Storage

​		对于长期运行的生产系统，Zookeeper存储必须在外部进行管理（dataDir和log）。有关详细信息，请参阅[维护](https://zookeeper.apache.org/doc/current/zookeeperAdmin.html#sc_maintenance)部分。

## Connecting to ZooKeeper

```shell
bin/zkCli.sh -server 127.0.0.1:2181
```



会出现环境信息等

```shell
Connecting to localhost:2181
log4j:WARN No appenders could be found for logger (org.apache.zookeeper.ZooKeeper).
log4j:WARN Please initialize the log4j system properly.
Welcome to ZooKeeper!
JLine support is enabled
[zkshell: 0]
```



在Shell中，键入help以获取可从客户端执行的命令列表

```shell
[zk: 127.0.0.1:2181(CONNECTED) 2] help
ZooKeeper -server host:port -client-configuration properties-file cmd args
	addWatch [-m mode] path # optional mode is one of [PERSISTENT, PERSISTENT_RECURSIVE] - default is PERSISTENT_RECURSIVE
	addauth scheme auth
	close 
	config [-c] [-w] [-s]
	connect host:port
	create [-s] [-e] [-c] [-t ttl] path [data] [acl]
	delete [-v version] path
	deleteall path [-b batch size]
	delquota [-n|-b] path
	get [-s] [-w] path
	getAcl [-s] path
	getAllChildrenNumber path
	getEphemerals path
	history 
	listquota path
	ls [-s] [-w] [-R] path
	printwatches on|off
	quit 
	reconfig [-s] [-v version] [[-file path] | [-members serverID=host:port1:port2;port3[,...]*]] | [-add serverId=host:port1:port2;port3[,...]]* [-remove serverId[,...]*]
	redo cmdno
	removewatches path [-c|-d|-a] [-l]
	set [-s] [-v version] path data
	setAcl [-s] [-v version] [-R] path acl
	setquota -n|-b val path
	stat [-w] path
	sync path
	version 
Command not found: Command not found help

```

开始尝试一些简单的命令来感受这个命令行界面。首先，发出list命令，如ls中所示

```shell
[zk: 127.0.0.1:2181(CONNECTED) 4] ls /
[zookeeper]
```

接下来，通过运行create /zk_test my_data来创建一个新的znode。这将创建一个新的znode并将创建一个新的znode并将字符串"my_data"与节点相关联。

```shell
[zk: 127.0.0.1:2181(CONNECTED) 5] create /zk_test my_data
Created /zk_test
```

发出另一个ls /命令以查看目录

```shell
[zk: 127.0.0.1:2181(CONNECTED) 6] ls /
[zk_test, zookeeper]
```

注意，zk_test目录现在已经创建了。

接下来，通过运行get命令来验证数据是否与znode关联

```shell
[zk: 127.0.0.1:2181(CONNECTED) 6] ls /
[zk_test, zookeeper]
```

我们可以通过发出set命令来更改与zk_test相关的数据

```shell
[zk: 127.0.0.1:2181(CONNECTED) 0] set /zk_test junk
[zk: 127.0.0.1:2181(CONNECTED) 1] get /zk_test
junk
```

注意，我们在设置数据之后做了一个get，它确实发生了变化。

最后，我们通过发出delete删除节点

```shell
[zk: 127.0.0.1:2181(CONNECTED) 2] delete /zk_test
[zk: 127.0.0.1:2181(CONNECTED) 3] 
[zk: 127.0.0.1:2181(CONNECTED) 3] ls /
[zookeeper]
```

就这样吧。要了解更多信息，请参见[Zookeeper CLI](https://zookeeper.apache.org/doc/current/zookeeperCLI.html)

## Programming to ZooKeeper

如何使用编程语言连接ZK。参考官网吧。

## Running Replicated ZooKeeper

在独立模式下运行ZooKeeper可以方便地进行评估，一些开发和测试。但是在生产环境中，应该以 replicated 模式运行ZooKeeper。同一应用程序中的一组复制服务器称为*quorum* 在复制模式下，*quorum*中的所有服务器都有同一配置文件的副本。

> 对于复制模式，至少需要三台服务器，强烈建议使用奇数台服务器。如果你只有两台服务器，那么你将处于这样一种情况：如果其中一台服务器发生故障，则没有足够的计算机组成多数*quorum*。两台服务器本质上比一台服务器不稳定，因为有两个单点故障。

需要`conf/zoo.cfg`复制模式的文件与独立模式中使用的文件类似，但有一些不同。下面是一个列子

```shell
tickTime=2000
dataDir=/var/lib/zookeeper
clientPort=2181
initLimit=5
syncLimit=2
server.1=zoo1:2888:3888
server.2=zoo2:2888:3888
server.3=zoo3:2888:3888
```

新条目`initLimit`是Zookeeper用来限制`quorum`中Zookeeper服务器必须连接到leader的时间长度。条目`syncLimit`限制服务器与主机之间的距离。

对于这两个超时，可以使用tickTime指定之间单位。在本例中，initLimit的超时值为每刻度2000毫秒5个刻度为10秒。

表单`server.X`的条目列出了组成Zookeeper服务的服务器。当服务器启动时，它通过在数据目录中查找myid文件来知道它是哪个服务器。服务器中包含的ASCII编号。

最后，注意每个服务器后的两个端口号：`2888`和`3888`。对等端使用前一个端口连接到其他对等端。这种连接是必要的，以便对等方可以通信，例如，就更新顺序达成一致。更具体地说，ZooKeeper服务器使用此端口将追随者连接到领导者。当一个新的前导出现时，跟随者使用这个端口打开一个到前导的TCP连接。因为默认的领导人选举也使用TCP，我们目前需要另一个端口来进行领导人选举。这是服务器条目中的第二个端口。

> 如果要在一台机器上测试多台服务器，请在服务器的配置文件中将servername指定为`localhost`，并未每个`server.X`指定唯一的`quorum` & leader election端口（即上面示例中的2888:3888，2889:3889，2890:3890）。当然还需要单独的_dataDir_s 和 distinct_clientPort_s （在上面复制的示例中，在单个本地主机上运行，你扔将有三个配置文件）。
>
> 请注意，在一台机器上设置多个服务不会产生任何冗余。如果发生导致机器死机的情况，所有zookeeper服务都将离线。完全冗余要求每个服务器都有自己的机器。它必须是一个完全独立的物理服务器。同一物理主机上的多个虚拟机仍然容易受到该主机完全故障的影响。
>
> 如果Zookeeper计算机中有多个网络接口，还可以指示Zookeeper绑定所有接口，并在网络出现故障时自动切换到正常的接口。有关详细信息[Configuration Parameters](https://zookeeper.apache.org/doc/current/zookeeperAdmin.html#id_multi_address).

### Other Optimizations

还有几个其他配置参数可以大大提高性能：

- 要获得较低的更新延迟，必须有一个专用的事务日志目录。默认情况下，事务日志与数据快照的myid文件放在同一目录中。dataLogDir参数指示用于事务日志的不同目录。

# 其他

参考同目录下`zookeeper.txt`



# FAQ

集群ZK搭建，总体来说看一遍官网文档然后找一个实践的blog。一次性就搞定了。 没有遇到问题一次性搞定。出乎意料的简单。