# 搭建VMware和CentOS7

https://gper.club/articles/7e7e7f7ff3g5bgc1g69

# CentOS更新yum源使用阿里云

https://gper.club/articles/7e7e7f7ff4g58gc8

# windows中使用vagrant+virtualbox安装centos常见问题解决方案

https://gper.club/articles/7e7e7f7ff7g58gc1g6e



# vmware 和 docker冲突

https://blog.csdn.net/hgdkmh/article/details/88665755



# network.service: control process exited, code=exited status=1

设置固定IP后，重启机器发现 ens-33 网卡没了。

https://www.cnblogs.com/duzhaoqi/p/7283144.html



# 命令学习

## 内存

https://www.linuxprobe.com/check-linux-memory.html

1. `cat /proc/meminfo` 这个动态更新的虚拟文件事实上是诸如`free`, `top`和`ps`这些与内存相关的工具的信息来源。从可用/闲置物理内存数量到等待写入缓存的数量或者已写回磁盘的数量，只要是你想要的关于内存使用的信息，`cat /proc/meminfo`应有尽有。特定进程的内存信息也可以通过 `cat /proc/statm`和 `cat /proc/status`来获取

2. `atop`命令是用于终端环境的机遇ncurses的交互式的系统和进程监测工具。它展示了动态更新的系统资源摘要（CPU，内存，网络，输入/输出，内核），并用醒目的颜色把系统高负债的部分以警告信息标注出来。它同样提供了类似于top的线程（或用户）资源使用视图，因此系统管理员可以找到哪个进程或者用户导致的系统负载。内存统计报告包括了总计/闲置内存，缓存的/缓冲的内存和已提交的虚拟内存。
   1. http://www.yunweibuluo.com/?p=247
3. `free`命令是一个用来获得内存使用概况的快速简单点的方法，这些信息从`/proc/meminfo`获取。它提供了一个快照，用于展示总计/闲置的物理内存和系统交换区，以及已使用/闲置的内核缓冲区。
   1. `free -h`

## 硬盘

https://www.cnblogs.com/kingsonfu/p/11884807.html

### df命令

用来查看硬盘的挂载点，以及对应的硬盘容量信息。包括硬盘的总大小，已经使用的大小，剩余大小。以及使用的空间占有的百分比等。

参数选项

- `-a` 列出所有的文件系统，包括系统特有的`/proc`等文件系统
- `-k` 以KB的容量显示各文件系统
- `-m` 以MB的容量显示各文件系统
- `-h` 以人们容易阅读的GB、MB、KB等格式自行显示
- `-H` 以M=1000K替代M=1024K的进位方式
- `-T` 显示文件系统类型
- `-i` 不用硬盘容量，而以inode的数量来显示
- `-l` 只显示本机的文件系统



常用

```shell
df -h
df -hl
```



### du命令

使用du命令查看指定目录的使用情况

```shell
du [option] 文件/目录
```

选项

- `-h` 输出文件系统分区使用的情况，例如：10KB，10MB，10GB等
- `-s` 显示文件或整个目录的大小，默认单位是KB



常用

```shell
# 当前目录下硬盘使用大小
du -sh
# 当前目录下子目录的大小
du -sh *
# xxx目录的大小
du -sh xxx
# xxx目录下子目录大小
du -sh xxx/*
```

## 网络使用率

https://www.cnblogs.com/dugk/p/8987028.html



vmstat