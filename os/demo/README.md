# 摘要

1. 搭建开发测试环境
2. 程序分析
3. 地址、vstart
4. CPU工作原理
5. 精彩预告

# 1. 搭建开发测试环境（MacOS）
## 1.1 安装一些必要工具
```shell
# 安装汇编编译器
brew install nasm
# 安装虚拟机
brew install qemu
```

## 1.2 测试代码
该代码的功能是在屏幕上打印"hello os"，这里不再过多解释这个代码，这段代码主要是为了后文介绍几个基础概念。

```S
;主引导程序
SECTION MBR vstart=0x7c00
    mov ax,cs
    mov ds,ax
    mov es,ax
    mov ss,ax
    mov fs,ax
    mov sp,0x7c00

    mov ax,0x600
    mov bx,0x700
    mov cx,0
    mov dx,0x184f
    int 0x10

    mov ah,3
    mov bh,0
    int 0x10

    mov ax,message
    mov bp,ax

    mov cx,8
    mov ax,0x1301
    mov bx,0x2
    int 0x10

    jmp $
    message db "hello os"
    times 510-($-$$) db 0
    db 0x55,0xaa
```

## 1.3 运行程序
```shell
# 编译汇编程序
nasm -o print.bin print.S
# 创建虚拟机镜像
qemu-img create -f raw vm1.raw 1G
# 将编译后的汇编程序写入第一个扇区
dd if=print.bin of=vm1.raw bs=512 count=1 conv=notrunc 
# 启动虚拟机
qemu-system-x86_64 vm1.raw 
```
启动以后效果如下：

![WX20210620-144156@2x.png](http://ww1.sinaimg.cn/large/001VZ9Acgy1groq7okkt0j613g0n8q9c02.jpg)

# 2. 程序分析

[如何加载操作系统](https://mp.weixin.qq.com/s?__biz=MzU4ODM1NjY5NQ==&mid=2247484944&idx=1&sn=c53320f40439c1a458814d785b3573d6&chksm=fddf4b28caa8c23eca907b161951b55579efc1566e965f5f921c72221b15a73212d21460f276&token=1186511230&lang=zh_CN#rd)
已经降到了如何加载我们的操作系统，在加载操作系统是我们提到了主引导程序，上述代码其实就是相当于MBR程序（但这里并不是去引导加载操作系统，只是在屏幕上打印一句话）。

BIOS在完成自检等初始化操作以后，会跳转到固定0x7c00处执行主引导程序，因此我们的代码使用vstart=0x7c00来表明我的代码会被初始化在内存的0x7c00处。

因此在我们启动虚拟机后，BIOS完成操作以后会跳转到我们的程序，CPU然后根据我们程序的指令执行，最终在屏幕打印了hello os。

# 3. 地址、vstart

地址是源码文件中各符号偏移文件开头的距离，一般一个文件的默认开始地址为0。

我们的应用程序使用vstart以后，应用程序便被赋予了一个虚拟的起始地址，不管是虚拟地址还是真实地址，只要将这个地址交给了地址总线，地址总线便会去
查找该地址处的内容。因此如果在开发中使用了vstart，必须要保证程序会出现在物理内存的该地址处。

# 4. CPU工作原理

CPU的工作模式分为实模式和保护模式，实模式的最大寻址空间为1M。关于实模式和保护模式在后续我们会讲解。在介绍这两种模式之前，我们还是先将CPU的工作原理。

**CPU的唯一任务就是执行指令**，在执行指令的过程中，需要CPU的是三个部件共同协作完成：

- 控制单元
- 运算单元
- 存储单元

**控制单元**是CPU的核心，只有通过控制单元CPU才能知道自己下一部需要做啥，控制单元大致由三部分组成：

- 指令寄存器IR
- 指令译码器ID
- 操作控制器OC

当我们的应用程序（也就是指令）被加载至内存以后，**指令指针寄存器IP**指向内存中下一条待执行的指令，该指令被送上地址总线以后，控制单元会获取指令
然后将其加载到**指令寄存器IR中**，此时的指令只是一串数字和字母，CPU如果要知道其真正的执行含义必须借助**指令译码器ID**对这串数字和字母按照
指令的格式进行解码分析。IA32（x86）指令格式如下： 

![WX20210620-135236@2x.png](http://ww1.sinaimg.cn/large/001VZ9Acgy1grooso4osqj61l401wwfz02.jpg)

**前缀**在指令格式中是可选模块，**操作码**对着我们应用程序中的mov，jmp等，**寻址方式**又分为基址寻址、变址寻址等（后续细讲），**操作数类型**记录的是
使用的寄存器。**立即数和偏移量**分别对应我们在寻址过程中用到的是立即数还是偏移量。

**存储单元**是指CPU中的L1、L2缓存和寄存器，用于存储指令中用到的数据。

在指令和数据都完善以后，控制单元中的操作控制器会给相关部件发送信号开始执行。

![WX20210620-142333@2x.png](http://ww1.sinaimg.cn/large/001VZ9Acgy1gropoifxx9j610u0quaj302.jpg)

# 5. 精彩预告
下一节中我们会讲述常见的寄存器以及CPU如何在实模式下运行。


