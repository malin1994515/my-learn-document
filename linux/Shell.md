# Shell 教程

https://www.runoob.com/linux/linux-shell.html

## Shell脚本

shell脚本(shell script)，是一种为shell编写的脚本程序。

业界所说的shell通常都是指shell脚本，但shell和shell script是两个不同的概念。

由于习惯的原因，简洁起见，本文出现的“shell编程“都是指shell脚本编程，不是指开发shell本身。



## shell环境

shell编程跟JavaScript、php编程一样，只要有一个能编写代码的文本编辑器和一个能解释执行的脚本解释器就可以了。

Linux的Shell种类众多，常见的有



- Bourne Shell（/usr/bin/sh或/bin/sh）
- Bourne Again Shell（/bin/bash）
- C Shell（/usr/bin/csh）
- K Shell（/usr/bin/ksh）
- Shell for Root（/sbin/sh）
- ……



本教程灌注的是Bash，也就是Bourne Again Shell，由于易用和免费，Bash在日常工作中被广泛 使用。同时，Bash也是大多数Linux系统默认的Shell。

在一般情况下，人们并不区分Bourne Shell和Bourne Again Shell，所以，想**#!/bin/sh**，它同样也可以改为**#!/bin/bash**。

**#!** 告诉系统其后路径所指定的程序既是解释此脚本文件的Shell程序。

## 第一个shell脚本

创建shell脚本。

```shell
vim test.sh

#!/bin/bash
echo "Hello World!"
```



## 运行shell脚本有两种方法



**1、作为可执行程序**

```shell
chmod +x ./test.sh 	# 使脚本具有执行权限
./test.sh 			# 执行脚本
```



**2、作为解释器参数**

这种允许方式是，执行运行解释器，其参数就是shell脚本的文件名

```shell
/bin/sh test.sh
/bin/php test.php
```



# Shell变量

定义变量时，变量名不加$符号($, php语言中变量需要)，如：

```shell
your_name="runoob.com"
```



除了显式地直接赋值，还可以用语句给变量赋值，如：

```shell
for file in `ls /etc`
或
for file in $(ls /etc)
```

以上语句将/etc下目录的文件名循环打印出来。



**使用变量**

使用一个定义过的变量，只要在变量名前面加$符号即可，如：

```shell
your_name="malin"
echo $your_name
echo ${your_name}
```

变量名外面的花括号是可选的，加不加都行，加花括号是为了帮助解释器识别变量的边界，比如下面这种情况：

```shell
for skill in Ada Coffe Action Java;
do
	echo "I am good at ${skill}Script"
done
```

如果不给skill变量加花括号，写成`echo "I am good at $skillScript"`，解释器就会把`$skillScript`当成一个变量(其值为空)，代码执行结果就不是原来的样子了。

> 推荐给所有变量加上花括号，这是个好的编程习惯。

已定义的变量，可以被重新定义，如：

```shell
your_name="tom"
echo $your_name
your_name="alibaba"
echo $your_name
```

> 注意第二次赋值不能写`$your_name="alibaba"`，只有使用变量的时候才加上**$**符号。



**只读变量**

使用`readonly`命令可以将变量定义为只读变量，只读变量的值不能被改变。

下面的例子尝试更改只读变量，结果报错：

```shell
#!/bin/bash
myUrl="https://www.google.com"
readonly myUrl
myUrl="https://www.runoob.com"
```

运行脚本，结果如下

```shell
/bin/sh: NAME: This variable is read only.
```



**删除变量**

使用`unset`命令可以删除变量。语法：

```shell
unset variable_name
```

变量被删除后不能再次使用。`unset`命令不能删除只读变量。

实例

```shell
#!/bin/sh
myUrl="https://www.runoob.com"
unset myUrl
echo $myUrl
```

以上实例执行将没有任何输出。



**变量类型**

运行shell时，会同时存在三种变量：

- **局部变量**：局部变量在脚本或命令中定义，仅在当前shell实例中有效，其他shell启动的程序不能访问局部变量。
- **环境变量**：所有的程序，包括shell启动的程序，都能访问环境变量，有些程序需要环境变量来保证其正常运行。必要的时候shell脚本也可以定义环境变量。
- **shell变量**：shell变量是由shell程序设置的特殊变量。shell变量中有一部分是环境变，有一部分是局部变量，这些变量保证了shell的正常运行。



## Shell字符串

字符串是shell编程中最常用的数据类型(除了数字和字符串，也没啥其它类型好用了)，字符串可以用单引号，也可以用双引号，也可以不用引号。



**单引号**

```shell
str='this is a string'
```

单引号字符串的限制

- 单引号里的任何字符都会原样输出，**单引号字符串的变量是无效的。**
- 单引号字符串中不能出现单独一个单引号（对单引号使用转义符后也不行），但可成对出现，作为字符串拼接使用。



**双引号**

```shell
your_name='runoob'
str="Hello, I know your are \"$your_name\"! \n"
echo -e $str
```

输出结果为

```shell
Hello, I know your are "runoob"!
```

双引号的有点：

- 双引号里可以有变量
- 双引号里可以出现转义符



**拼接字符串**

```shell
your_name="runoob"
# 使用双引号拼接
greeting="hello, "$your_name" !"
greeting_1="hello, ${your_name} !"
echo $greeting $greeting_1
# 使用单引号拼接
greeting_2='hello, '$your_name' !'
greeting_3='hello, ${your_name} !'
echo $greeting_2 $greeting_3
```

输出结果为：

```shell
hello, runoob ! hello, runoob !
hello, runoob ! hello, ${your_name} !
```



**获取字符串长度**

```shell
string="abcd"
echo ${#string} # 输出 4
```



**提取子字符串**

以下实例从字符串第**2**个字符开始截取**4**个字符：

```shell
string="runoob is a great site"
echo ${string:1:4) # 输出 unoo
```

> 注意：第一个字符的索引值为0。



**查找子字符串**

查找字符**i**或**o**的位置(哪个字母先出现就计算哪个)

```shell
string="runoob is a great site"
echo `expr index "$string" io`  # 输出 4
```

> **注意**：以上脚本中`是反引号，而不是单引号'



## Shell数组

bash支持一维数组(不支持多维数组)，并且没有限定数组的大小。

类似于C语言，数组元素的下表由0开始。获取数组中的元素要利用下标，下标可以是整数或算术表达式，其值应大于或等于0.



**定义数组**

在Shell中，用括号来表示数组，数组元素用“空格”符号分隔开。定义数组的一般形式为：

```shell
数组名=(值1 值2 ... 值n)
```

例如:

```shell
array_name=(value0 value1 value2 value3)
```

或者

```shell
array_name=(
value0
value1
value2
value3
)
```

还可以单独定义数组的各个变量：

```shell
array_name[0]=value0
array_name[1]=value1
array_name[n]=valuen
```

可以不使用连续的下标，而且下标的范围没有限制。



**读取数组**

读取数组元素值的一般格式是：

```shell
${数组名[下标]}
```

例如：

```shell
valuen=${array_name[n]}
```

使用`@`符号可以获取数组中的所有元素，例如：

```shell
echo ${array_name[@]}
```



**获取数组的长度**

获取数组长度的方法与获取字符串长度的方法相同，例如：

```shell
# 取得数组元素的个数
length=${#array_name[@]}
# 或者
length=${#array_name[*]}
# 取得数组单个元素的长度
lengthn=${#array_name[n]}
```



## Shell注释

以`#`开头的行就是注释，会被解释器忽略。

通过每一行加一个`#`号设置多行注释，像这样：

```shell
#--------------------------------------------
# 这是一个注释
# author：菜鸟教程
# site：www.runoob.com
# slogan：学的不仅是技术，更是梦想！
#--------------------------------------------
##### 用户配置区 开始 #####
#
#
# 这里可以添加脚本描述信息
# 
#
##### 用户配置区 结束  #####
```

如果在开发过程中，遇到大段的代码需要临时注释起来，过一会儿又取消注释，怎么办呢？

每一行加个`#`符号太费力了，可以把这一段要注释的代码用一对花括号括起来，定义成一个函数，没有地方调用这个函数，这块代码就不会执行，达到了和注释一样的效果。



**多行注释**

多行注释还可以使用一下格式

```shell
:<<EOF
注释内容...
注释内容...
注释内容...
EOF
```

EOF也可以使用其他符号

```shell
:<<'
注释内容...
注释内容...
注释内容...
'

:<<!
注释内容...
注释内容...
注释内容...
!
```



# Shell传递参数

我们可以在执行Shell脚本时，向脚本传递参数，脚本内获取参数的格式为：**$n**。**n**代表一个数字，**1**为执行脚本的第一个参数，**2**为执行脚本的第二个参数，以此类推...



**实例**

以下实例我们向脚本传递三个参数，并分别输出，其中**$0**为执行的文件名(包含文件路径)：

```shell
#!/bin/bash

echo "Shell 传递参数实例!"
echo "执行的文件名:$0"
echo "第一个参数为:$1"
echo "第二个参数为:$2"
echo "第三个参数为:$3"
```

为脚本设置可执行权限，并执行脚本，输出结果如下所示：

```shell
chmod +x ./arg.sh
./arg.sh 1 2 3

Shell 传递参数实例!
执行的文件名:./test.sh
第一个参数为:1
第二个参数为:2
第三个参数为:3
```

另外，还有几个特殊字符用来处理参数

| 参数处理 | 说明                                                         |
| -------- | ------------------------------------------------------------ |
| $#       | 传递到脚本的参数个数                                         |
| $*       | 以一个单字符串显示所有向脚本传递的参数。<br/>如"$*"用**"**括起来的情况，以"$1 $2 ... $n"的形式输出所有参数。 |
| $$       | 脚本运行的当前进程ID号                                       |
| $!       | 后台运行的最后一个进程的ID号                                 |
| $@       | 与$*相同，但是使用时加引号，并在引号中返回每个参数。<br/>如$@用"括起来的情况，以"$1" "$2" ... "$n"的形式输出所有参数。 |
| $-       | 显示Shell使用的当前选项，与set命令功能相同。                 |
| $?       | 显示最后命令的推出状态。0表示没有错误，其他任何值表明有错误。 |



```shell
#!/bin/bash

echo "Shell 传递参数实例!"
echo "第一个参数为:$1"

echo "参数个数为:$#"
echo "传递的参数作为一个字符串显示：$*"
```

执行脚本，输出结果如下所示

```shell
chmod +x arg2.sh
./arg2.sh 1 2 3

Shell 传递参数实例!
第一个参数为：1
参数个数为：3
传递的参数作为一个字符串显示：1 2 3
```

> $* 与 $@ 区别
>
> - 相同点：都是引用所有参数
> - 不同点：只有在双引号中体现出来。假设在脚本运行时写了三个参数 1、2、3，则"*"等价于"1 2 3"(传递了一个参数)，而"@"等价于"1" "2" "3"（传递了三个参数）。



```shell
#!/bin/bash

echo "--\$*演示--"
for i in "$*"
do
	echo $i
done

echo "--\$@演示--"
for i in "$@"
do
	echo $i
done	
```

执行脚本，输出结果如下所示

```shell
chmod +x arg3.sh
./arg3.sh 1 2 3

--$*演示--
1 2 3
--$@演示--
1
2
3
```



# Shell数组

数组中可以存放多个值。Bash Shell只支持一维数组（不支持多维数组），初始化时不需要定义数组大小（与PHP类似）。与大部分编程语言类似，数组元素的下标由0开始。

Shell数组用括号来表示，元素用“空格”符号分割开，语法格式如下

```shell
array_name=(value1 value2 ... valuen)
```



**实例**

```shell
#!/bin/bash

my_array=(A B "C" D)
```

我也可以使用下标来定义数组

```shell
array_name[0]=value0
array_name[1]=value1
array_name[2]=value2
```



**读取数组**

读取数组元素值的一般格式是

```shell
${array_name[index]}
```



**实例**

```shell
#!/bin/bash

my_array=(A B "C" D)

echo "第一个元素为：${my_array[0]}"
echo "第二个元素为：${my_array[1]}"
echo "第三个元素为：${my_array[2]}"
echo "第四个元素为：${my_array[3]}"
```

执行脚本，输出结果如下所示

```shell
chmod +x array.sh
./array.sh

第一个元素为：A
第一个元素为：B
第一个元素为：C
第一个元素为：D
```



**获取数组中的所有元素**

使用@或*可以获取数组中的所有元素，例如：

```shell
#!/bin/bash

my_array[0]=A
my_array[1]=B
my_array[2]=C
my_array[3]=D

echo "数组元素个数为：${my_array[*]}"
echo "数组元素个数为：${my_array[@]}"
```

执行脚本，输出结果如下所示

```shell
chmod +x array.sh
./array.sh

数组元素个数为：4
数组元素个数为：4
```

# Shell基本运算符

Shell和其他编程语言一样，支持多种运算符，包括

- 算术运算符
- 关系运算符
- 布尔运算符
- 字符串运算符
- 文件测试运算符

原生bash不支持简单的数学运算，但是可以通过其他命令来实现，例如awk和expr。expr最常用。

expr是一款表达式计算工具，使用它能完成表达式的求值操作。

例如，两个数相加(注意使用的是反引号`而不是单引号')



**实例**

```shell
#!/bin/bash

val = `expr 2 + 2`
echo "两数之和为：$val"
```

执行脚本，输出结果如下所示

```shell
两数之和为：4
```

两点注意

- 表达式和运算符之间要有空格，例如2+2是不对的，**必须写成2 + 2**，这与我们熟悉的大多数编程语言不一样。
- 完整的表达式要被`包含，注意这个字符不是常用的单引号，是反引号。



## 算术运算符

下表列出了常用的算术运算符，假定变量a为10，变量b为20.

| 运算符 | 说明                                         | 距离                   |
| ------ | -------------------------------------------- | ---------------------- |
| +      | 加法                                         | expr $a + $b 结果为30  |
| -      | 减法                                         | expr $a - $b 结果为-10 |
| *      | 乘法                                         | expr $a * $b 结果为200 |
| /      | 除法                                         | expr $a / $b 结果为2   |
| %      | 取余                                         | expr $a % $b 结果为0   |
| =      | 赋值                                         | a = $b把变量b的值赋给a |
| ==     | 相等。用于比较两个数字，相同则返回true。     | [$a == $b] 返回false   |
| !=     | 不相等。用于比较两个数字，不相同则返回true。 | $a != $b 返回true      |

**注意：**条件表达式要放在方括号之间，并且要有空格，例如：[$a==$b]是错误的，必须写成[$a == $b]。



**实例**

```shell
#!/bin/bash

a=10
b=20

val=`expr $a + $b`
echo "a + b : $val"

val=`expr $a - $b`
echo "a - b : $val"

val=`expr $a \* $b`
echo "a * b : $val"

val=`expr $b / $a`
echo "b / a : $val"

val=`expr $b % $a`
echo "b % a : $val"

if [ $a == $b ]
then
   echo "a 等于 b"
fi
if [ $a != $b ]
then
   echo "a 不等于 b"
fi
```

执行脚本，输出结果如下所示

```shell
a + b : 30
a - b : -10
a * b : 200
b / a : 2
b % a : 0
a 不等于 b
```

**注意**

- 乘号*前边必须加反斜杠\才能实现乘法运算
- if...then...fi 是条件语句，后续将会讲解
- 在MAC中shell的expr语法是：**$((表达式))**，此处表达式中的 * 不需要转义符号 \。



## 关系运算符

关系运算符只支持数字，不支持字符串，除非字符串的值是数字。

下标列出了常用的关系运算符，假定变量a为10，变量b为20

| 运算符 | 说明                                               | 举例                 |
| ------ | -------------------------------------------------- | -------------------- |
| -eq    | 检测两个数是否相等，相等返回true                   | [$a -eq $b]返回false |
| -ne    | 检测两个数是否不相等，不相等返回true               | [$a -ne $b]返回true  |
| -gt    | 检测左边的数是否大于右边的，如果是，则返回true     | [$a -gt $b]返回false |
| -lt    | 检测左边的数是否小于右边的，如果是，则返回true     | [$a -lt $b]返回true  |
| -ge    | 检测左边的数是否大于等于右边的，如果是，则返回true | [$a -ge $b]返回false |
| -le    | 检测左边的数是否小于等于右边的，如果是，则返回true | [$a -le $b]返回true  |



**实例**

```shell
#!/bin/bash

a=10
b=20

if [ $a -eq $b ]
then
   echo "$a -eq $b : a 等于 b"
else
   echo "$a -eq $b: a 不等于 b"
fi
if [ $a -ne $b ]
then
   echo "$a -ne $b: a 不等于 b"
else
   echo "$a -ne $b : a 等于 b"
fi
if [ $a -gt $b ]
then
   echo "$a -gt $b: a 大于 b"
else
   echo "$a -gt $b: a 不大于 b"
fi
if [ $a -lt $b ]
then
   echo "$a -lt $b: a 小于 b"
else
   echo "$a -lt $b: a 不小于 b"
fi
if [ $a -ge $b ]
then
   echo "$a -ge $b: a 大于或等于 b"
else
   echo "$a -ge $b: a 小于 b"
fi
if [ $a -le $b ]
then
   echo "$a -le $b: a 小于或等于 b"
else
   echo "$a -le $b: a 大于 b"
fi
```

执行脚本，输出结果如下所示

```shell
10 -eq 20: a 不等于 b
10 -ne 20: a 不等于 b
10 -gt 20: a 不大于 b
10 -lt 20: a 小于 b
10 -ge 20: a 小于 b
10 -le 20: a 小于或等于 b
```



## 布尔运算符

下表列出了常用的布尔运算符，假定变量a为10，变量b为20：

| 运算符 | 说明                                            | 举例                               |
| ------ | ----------------------------------------------- | ---------------------------------- |
| !      | 非运算符，表达式为true则返回false，否则返回true | [!false]返回true                   |
| -o     | 或运算符，有一个表达式为true则返回true          | [$a -lt 20 -o $b -gt 100]返回true  |
| -a     | 与运算符，两个表达式都为true则返回true          | [$a -lt 20 -a $b -gt 100]返回false |



**实例**

```shell
#!/bin/bash

a=10
b=20

if [ $a != $b ]
then
   echo "$a != $b : a 不等于 b"
else
   echo "$a == $b: a 等于 b"
fi
if [ $a -lt 100 -a $b -gt 15 ]
then
   echo "$a 小于 100 且 $b 大于 15 : 返回 true"
else
   echo "$a 小于 100 且 $b 大于 15 : 返回 false"
fi
if [ $a -lt 100 -o $b -gt 100 ]
then
   echo "$a 小于 100 或 $b 大于 100 : 返回 true"
else
   echo "$a 小于 100 或 $b 大于 100 : 返回 false"
fi
if [ $a -lt 5 -o $b -gt 100 ]
then
   echo "$a 小于 5 或 $b 大于 100 : 返回 true"
else
   echo "$a 小于 5 或 $b 大于 100 : 返回 false"
fi
```

执行脚本，输出结果如下所示

```shel
10 != 20 : a 不等于 b
10 小于 100 且 20 大于 15 : 返回 true
10 小于 100 或 20 大于 100 : 返回 true
10 小于 5 或 20 大于 100 : 返回 false
```



## 逻辑运算符

以下介绍Shell的逻辑运算符，假定变量a为10，变量b为20。

| 运算符 | 说莫       | 举例                                   |
| ------ | ---------- | -------------------------------------- |
| &&     | 逻辑的 AND | [[$a -lt 100 && $b -gt 100]]返回false  |
| \|\|   | 逻辑的 OR  | [[$a -lt 100 \|\| $b -gt 100]]返回true |

**实例**

```shell
#!/bin/bash

a=10
b=20

if [[ $a -lt 100 && $b -gt 100 ]]
then
   echo "返回 true"
else
   echo "返回 false"
fi

if [[ $a -lt 100 || $b -gt 100 ]]
then
   echo "返回 true"
else
   echo "返回 false"
fi
```

执行脚本，输出结果如下所示

```shel
返回 false
返回 true
```



## 字符串运算符

下表列出了常用的字符串运算符，假定变量a为"abc"，变量b为"efg"

| 运算符 | 说明                                       | 举例               |
| ------ | ------------------------------------------ | ------------------ |
| =      | 检测两个字符串是否相等，相等返回true。     | [$a = $b]返回false |
| !=     | 检测两个字符串是否不相等，不相等返回true。 | [$a != $b]返回true |
| -z     | 检测字符串长度是否为0，为0返回true。       | [-z $a]返回false   |
| -n     | 检测字符串长度是否不为0，不为0返回true。   | [-n "$a"]返回true  |
| $      | 检测字符串是否为空，不为空返回true。       | [$a]返回true       |

**实例**

```shell
#!/bin/bash

a="abc"
b="efg"

if [ $a = $b ]
then
   echo "$a = $b : a 等于 b"
else
   echo "$a = $b: a 不等于 b"
fi
if [ $a != $b ]
then
   echo "$a != $b : a 不等于 b"
else
   echo "$a != $b: a 等于 b"
fi
if [ -z $a ]
then
   echo "-z $a : 字符串长度为 0"
else
   echo "-z $a : 字符串长度不为 0"
fi
if [ -n "$a" ]
then
   echo "-n $a : 字符串长度不为 0"
else
   echo "-n $a : 字符串长度为 0"
fi
if [ $a ]
then
   echo "$a : 字符串不为空"
else
   echo "$a : 字符串为空"
fi
```

执行脚本，输出结果如下所示

```shell
abc = efg: a 不等于 b
abc != efg : a 不等于 b
-z abc : 字符串长度不为 0
-n abc : 字符串长度不为 0
abc : 字符串不为空
```

## 文件测试运算符

文件测试运算符用于检测Unix文件的各种属性。

| 操作符  | 说明                                                         | 举例                |
| ------- | ------------------------------------------------------------ | ------------------- |
| -b file | 检测文件是否是块设备文件，如果是，则返回true。               | [-b $file]返回false |
| -c file | 检测文件是否是字符设备文件，如果是，则返回true。             | [-c $file]返回false |
| -d file | 检测文件是否是目录，如果是，则返回true。                     | [-d $file]返回false |
| -f file | 检测文件是否是普通文件(既不是目录，也不是设备文件)，<br/>如果是，则返回true。 | [-f $file]返回true  |
| -g file | 检测文件是否设置了SGID位，如果是，则返回true。               | [-g $file]返回false |
| -k file | 检测文件是否设置了粘着位(Sticky Bit)，如果是，则返回true。   | [-k $file]返回false |
| -p file | 检测文件是否是管道，如果是，怎返回true。                     | [-p $file]返回false |
| -u file | 检测文件是否设置了SUID位，如果是，则返回true。               | [-u $file]返回false |
| -r file | 检测文件是否可读，如果是，则返回true。                       | [-r $file]返回true  |
| -w file | 检测文件是否可写，如果是，则返回true。                       | [-w $file]返回true  |
| -x file | 检测文件是否可执行，如果是，则返回true。                     | [-x $file]返回true  |
| -s file | 检测文件是否为空(文件大小是否大于0)，不为空返回true。        | [-s $file]返回true  |
| -e file | 检测文件(包括目录)是否存在，如果是，则返回true。             | [-e $file]返回true  |
| -S file | 判断文件是否socket。                                         |                     |
| -L file | 检测文件是否存在并且是一个符号链接。                         |                     |



**实例**

变量 file 表示文件 **/var/www/runoob/test.sh**，它的大小为 100 字节，具有 **rwx** 权限。下面的代码，将检测该文件的各种属性

```shell
#!/bin/bash

file="/var/www/runoob/test.sh"
if [ -r $file ]
then
   echo "文件可读"
else
   echo "文件不可读"
fi
if [ -w $file ]
then
   echo "文件可写"
else
   echo "文件不可写"
fi
if [ -x $file ]
then
   echo "文件可执行"
else
   echo "文件不可执行"
fi
if [ -f $file ]
then
   echo "文件为普通文件"
else
   echo "文件为特殊文件"
fi
if [ -d $file ]
then
   echo "文件是个目录"
else
   echo "文件不是个目录"
fi
if [ -s $file ]
then
   echo "文件不为空"
else
   echo "文件为空"
fi
if [ -e $file ]
then
   echo "文件存在"
else
   echo "文件不存在"
fi
```

执行脚本，输出结果如下所示

```shell
文件可读
文件可写
文件可执行
文件为普通文件
文件不是个目录
文件不为空
文件存在
```



# Shell echo命令

Shell的echo指令与PHP的echo指令类似，都是用于字符串的输出。命令格式：

```shell
echo string
```

您可以使用echo实现更复杂的输出格式控制。



1、显示普通字符串

```shell
echo "It is a test"
```

这里的双引号完全可以省略，以下命令与上面实例效果一直

```shell
echo It is a test
```

