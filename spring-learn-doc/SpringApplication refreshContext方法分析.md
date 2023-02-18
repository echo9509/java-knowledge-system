
```java
private void refreshContext(ConfigurableApplicationContext context) {
    if (this.registerShutdownHook) {
        shutdownHook.registerApplicationContext(context);
    }
    refresh(context);
}
```

#### 注册ShutdownWebHook

```java
void registerApplicationContext(ConfigurableApplicationContext context) {
    addRuntimeShutdownHookIfNecessary();
    synchronized (SpringApplicationShutdownHook.class) {
        assertNotInProgress();
        context.addApplicationListener(this.contextCloseListener);
        this.contexts.add(context);
    }
}
```
- 首先会判断是否需要添加ShutdownHook，如果需要添加则都会调用Runtime.getRuntime().addShutdownHook方法来添加（SpringApplicationShutdownHook）
- 接着会通过contextAddApplicationListener添加ApplicationContextClosedListener，用来在收到ContextClosedEvent事件时从已保存的上下文中移除当前关闭的上下文
- 最后保存Spring上下文到SpringApplicationShutdownHook中

#### ShutdownWebhook触发时会做哪些操作

```java
@Override
public void run() {
    Set<ConfigurableApplicationContext> contexts;
    Set<ConfigurableApplicationContext> closedContexts;
    Set<Runnable> actions;
    synchronized (SpringApplicationShutdownHook.class) {
        this.inProgress = true;
        contexts = new LinkedHashSet<>(this.contexts);
        closedContexts = new LinkedHashSet<>(this.closedContexts);
        actions = new LinkedHashSet<>(this.handlers.getActions());
    }
    contexts.forEach(this::closeAndWait);
    closedContexts.forEach(this::closeAndWait);
    actions.forEach(Runnable::run);
}
```

- 遍历当前保存的contexts，然后调用SpringApplicationShutdownHook的closeAndWait方法将其关闭
- 遍历当前保存的closeAndWait，然后调用SpringApplicationShutdownHook的closeAndWait方法将其关闭

#### Context.closeAndWait方法

- 首先判断Context是否还是active，如果不是active那么直接返回
- 如果是active则调用Context的close方法将其关闭，并且等待Context关闭完成或者超时（默认超时时间10分钟）

#### Context.close方法

- 该方法首先会判断Context是否是Active并且没有被关闭过，如果满足active并且closed标识为false则执行接下来的关闭操作
- 发布ContextClosedEvent事件
- 通过LifecycleProcessor的onClose方法关闭所有实现了LifeCycle接口的Bean
- 通过destroyBeans方法销毁其他的Bean
- 通过closeBeanFactory方法销毁BeanFactory
- 最后将Active设置为false

#### Context.refresh方法

在注册完ShutdownWebhook以后，会使用Context的refresh方法进行上下文刷新。

```java
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        StartupStep contextRefresh = this.applicationStartup.start("spring.context.refresh");
        prepareRefresh();
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
        prepareBeanFactory(beanFactory);
        try {
            postProcessBeanFactory(beanFactory);
            StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");
            invokeBeanFactoryPostProcessors(beanFactory);
            registerBeanPostProcessors(beanFactory);
            beanPostProcess.end();
            initMessageSource();
            initApplicationEventMulticaster();
            onRefresh();
            registerListeners();
            finishBeanFactoryInitialization(beanFactory);
            finishRefresh();
        }
        catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                        "cancelling refresh attempt: " + ex);
            }
            destroyBeans();
            cancelRefresh(ex);
            throw ex;
        }
        finally {
            resetCommonCaches();
            contextRefresh.end();
        }
    }
}
```

![image.png](https://s2.loli.net/2023/02/18/54ZClwFTdjhY2Hc.png)

- 首先调用prepareRefresh准备刷新前的前置工作
- 从Spring容器中获取BeanFactory，然后调用prepareBeanFactory进行相关设置为后续刷新做准备
- 调用postProcessBeanFactory进行BeanFactory的后置操作
- 调用invokeBeanFactoryPostProcessors方法，在Spring容器中找出实现了BeanFactoryPostProcessor接口的processor并执行，Spring容器会委托给PostProcessorRegistrationDelegate的invokeBeanFactoryPostProcessors方法执行
- 调用registerBeanPostProcessors，在Spring容器中找出BeanPostProcessor的bean设置到BeanFactory，后续Bean在被实例化的时候会调用这个BeanPostProcessor
- 调用initMessageSource初始化一些国际化属性
- 调用initApplicationEventMulticaster方法初始化事件广播器，用于发布事件
- 调用onRefresh方法，此方法会根据Spring容器的类型执行不同的操作
- 调用registerListeners方法将Spring容器中的事件监听器和BeanFactory中的事件监听器添加到事件广播器中
- 调用finishBeanFactoryInitialization方法实例化BeanFactory中已注册但未被实例化的所有实例
- 调用finishRefresh方法完成refresh的最后操作

#### prepareRefresh方法

prepareRefresh方法主要做以下事情：

- 设置Spring容器的启动事件，将close状态改为false，active状态改为true
- 初始化属性源消息
- 验证环境信息里面必须存在的属性

#### prepareBeanFactory方法


