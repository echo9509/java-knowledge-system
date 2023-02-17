
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
- 销毁其他的Bean
