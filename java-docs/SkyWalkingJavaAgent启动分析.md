SkyWalking Java Agent的启动步骤主要如下：

- 使用SnifferConfigInitializer初始化配置文件
- 使用PluginBootstrap加载所有的Plugin
- 使用Byte Buddy动态加强
- ServiceManager.INSTANCE.boot()初始化客户端


# 初始化配置文件

初始化配置文件的步骤主要如下：

1. 默认从java agent的jar包中的/config/agent.config文件中读取配置（配置文件的路径可以通过-Dskywalking_config参数来指定）
2. 从系统参数中-D指定的参数读取配置（-D指定的配置必须以skywalking.开头，后面的属性名称和agent.config文件中的属性名一直），参数配置比1的优先级高
3. 从-javaagent的参数中读取格式如下(-javaagent:/path/to/skywalking-agent.jar=agent.application_code=31200,logging.level=debug)，参数配置比2的优先级高
4. 用读取到的Properties初始化Config中静态类的字段（配置参数，对应着agent.config里面的内容）
5. 设置LogResolver

# 加载所有的Plugin

加载Plugin的主要步骤如下：

1. 初始化AgentClassLoader（自定义的类加载器），该类加载器会将plugins目录和activations目录添加到类路径
2. 利用PluginResourcesResolver拿到所有的skywalking-plugin.def文件，该步骤主要找到plugins目录和activations目录下的jar包中的skywalking-plugin.def文件
3. 遍历所有的skywalking-plugin.def，将skywalking-plugin.def中定义的key和value封装成PluginDefine对象（plugin的名称和plugin的类路径）
4. 利用AgentClassLoader和反射初始化所有的AbstractClassEnhancePluginDefine对象
5. 利用SPI机制最后再动态加载一些AbstractClassEnhancePluginDefine

# 什么是Byte Buddy

在Java Agent出来以后，我们可以通过修改class文件的内容来增强字节码，但是手动编码的方式过于繁琐且容易出错，因此出现了大量的字节码增强工具，Byte Buddy就是其中的一个，现有的字节码增强工具主要有以下几种：

![](https://p.ipic.vip/m32w2t.png)

# 使用Byte Buddy动态加强类

在动态加强步骤主要如下：

1. 忽略对部分类的增强，比如：net.bytebuddy.开头的，org.slf4j.以及org.apache.skywalking.开头但不包含（org.apache.skywalking.apm.toolkit.开头的）

```java
AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy).ignore(
    nameStartsWith("net.bytebuddy.")
        .or(nameStartsWith("org.slf4j."))
        .or(nameStartsWith("org.groovy."))
        .or(nameContains("javassist"))
        .or(nameContains(".asm."))
        .or(nameContains(".reflectasm."))
        .or(nameStartsWith("sun.reflect"))
        .or(allSkyWalkingAgentExcludeToolkit())
        .or(ElementMatchers.isSynthetic()));
```

2. 紧接着为找到各个BootStrapPluginDefine，然后找到需要处理的普通方法、构造方法和静态方法和处理他们的Interceptor，然后利用模板类生成特定的class，模板类主要有以下几个：

- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.InstanceMethodInterWithOverrideArgsTemplate
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.ConstructorInterTemplate
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.StaticMethodInterTemplate
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.StaticMethodInterWithOverrideArgsTemplate
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.v2.InstanceMethodInterV2Template
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.v2.InstanceMethodInterV2WithOverrideArgsTemplate
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.v2.StaticMethodInterV2Template
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.template.v2.StaticMethodInterV2WithOverrideArgsTemplate

BootStrapPluginDefine主要有以下几个：

- ThreadPoolExecutorInstrumentation
- CallableInstrumentation
- HttpClientInstrumentation
- HttpsClientInstrumentation
- RunnableInstrumentation

比如ThreadPoolExecutorInstrumentation主要有两个Interceptor类：

- org.apache.skywalking.apm.plugin.ThreadPoolExecuteMethodInterceptor：用来拦截处理ThreadPoolExecutor的execute方法
- org.apache.skywalking.apm.plugin.ThreadPoolSubmitMethodInterceptor：用来拦截处理ThreadPoolExecutor的submit方法

将上述生成的字节码缓存在Map<String, byte[]>中，key为Interceptor名称加_internal（这也是代理类的名称）

3. 将更高优先级的类保存到Map<String, byte[]>中，主要有以下几种：

- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.BootstrapInterRuntimeAssist
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.StaticMethodsAroundInterceptor
- org.apache.skywalking.apm.agent.core.plugin.bootstrap.IBootstrapLog
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.OverrideCallable
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.InstanceMethodsAroundInterceptorV2
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.StaticMethodsAroundInterceptorV2
- org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.v2.MethodInvocationContext

# 初始化客户端


ServiceManager.INSTANCE.boot()初始化所有的链接客户端，比如GRPCChannelManager，通过该类我们会将链路数据上报给SkyWalking OAP Server

