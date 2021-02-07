# FastDFS 安装



- https://www.cnblogs.com/chiangchou/p/fastdfs.html#_label0
- https://github.com/happyfish100/fastdfs/wiki
  - 原创作者



# 安装

https://github.com/happyfish100/fastdfs/wiki



- 整一台能连外网的 CentOS 机器



# FastDFS-Java 使用

- 这个jar在maven公库里面没有得下源码自己打包
  - https://github.com/happyfish100/fastdfs-client-java
  - https://blog.csdn.net/weixin_44104367/article/details/103048938
- 不使用官方的 java client，使用其他开源的
  - https://github.com/tobato/FastDFS_Client



# FAQ



## 切换fastdfs安装目录后

[root@test nginx-1.15.4]# /etc/init.d/fdfs_trackerd start
-bash: /etc/init.d/fdfs_trackerd: No such file or directory



经过排查