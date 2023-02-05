SpringApplication在构造完以后，我们会调用run方法启动应用程序，run方法的主要逻辑有：

![image.png](http://tva1.sinaimg.cn/large/007uBA8Ggy1hasppl20gej306l0h53zz.jpg)

整个Run方法的逻辑主要如上图：

- 设置headless模式
- 启动SpringApplicationRunListeners
- 创建Environment
- 创建应用上下文（ApplicationContext）
- 准备应用上下文环境
- 刷新应用上下文
- 发布应用启动的事件
- 调用启动类中的任务


```java
public ConfigurableApplicationContext run(String... args) {
    long startTime = System.nanoTime();
    DefaultBootstrapContext bootstrapContext = createBootstrapContext();
    ConfigurableApplicationContext context = null;
    //设置java.awt.headless系统属性为true，Headless模式是系统的一种配置模式。
    //在该模式下，系统缺少了显示设备、键盘或鼠标。但是服务器生成的数据需要提供给显示设备等使用。
    //因此使用headless模式，一般是在程序开始激活headless模式，告诉程序现在你要工作在Headless模式下，依靠系统的计算能力模拟出这些特性来
    configureHeadlessProperty();
    SpringApplicationRunListeners listeners = getRunListeners(args);
    listeners.starting(bootstrapContext, this.mainApplicationClass);
    try {
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        // 设置Environment（此处就会读取application.yaml的配置文件）
        ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
        configureIgnoreBeanInfo(environment);
        Banner printedBanner = printBanner(environment);
        //创建应用上下文环境，也就是Spring的IOC容器
        context = createApplicationContext();
        context.setApplicationStartup(this.applicationStartup);
        //准备应用上下文环境，会去加载配置类基于注解的bean、xml配置文件中定义的bean
        prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
        //刷新上下文，对于servlet应用程序这个方法会去创建和启动web服务器
        refreshContext(context);
        afterRefresh(context, applicationArguments);
        Duration timeTakenToStartup = Duration.ofNanos(System.nanoTime() - startTime);
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), timeTakenToStartup);
        }
        //应用运行时监听器发布应用启动事件
        listeners.started(context, timeTakenToStartup);
        //调用启动类中的任务
        callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, listeners);
        throw new IllegalStateException(ex);
    }
    try {
        Duration timeTakenToReady = Duration.ofNanos(System.nanoTime() - startTime);
        listeners.ready(context, timeTakenToReady);
    }
    catch (Throwable ex) {
        handleRunFailure(context, ex, null);
        throw new IllegalStateException(ex);
    }
    return context;
}
```