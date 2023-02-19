
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

prepareBeanFactor方法的逻辑主要如下：

- 设置ClassLoader(用于加载Bean)，另外还会设置表达式解析器和属性编辑器
- 添加BeanPostProcessor（具体的实现类是ApplicationContextAwareProcessor）
- 取消EnvironmentAware、EmbeddedValueResolverAware、ResourceLoaderAware、ApplicationEventPublisherAware、MessageSourceAware、ApplicationContextAware、ApplicationStartupAware接口的自动注入，ApplicationContextAwareProcessor将上述接口实现工作完成了
- 设置特殊的Bean到Spring容器，主要有ResourceLoader、ApplicationEventPublisher、ApplicationContext，这三个接口的在默认的配置下对象都是AnnotationConfigServletWebServerApplicationContext
- 添加BeanPostProcessor（具体的实现类是ApplicationListenerDetector）
- 添加其他bean到Spring容器，有environment、systemProperties、systemEnvironment、applicationStartup

#### postProcessBeanFactory方法

postProcessBeanFactory主要是对BeanFactory初始化的后置操作：

- 首先会调用父类ServletWebServerApplicationContext的postProcessBeanFactory方法
- 在父类的postProcessBeanFactory方法中首先会添加BeanPostProcessor（WebApplicationContextServletContextAwareProcessor）
- 取消ServletContextAware接口的自动注入
- 接着会进行WebApplicationScopes的注册

#### invokeBeanFactoryPostProcessors方法

invokeBeanFactoryPostProcessors方法主要是从Spring容器中找到所有的BeanFactoryPostProcessor并执行，Spring容器会委托给PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors方法去执行。

- BeanFactoryPostProcessor：用来Spring容器中已存在的Bean的定义，使用ConfigurableListableBeanFactory对Bean进行处理
- BeanDefinitionRegistryPostProcessor：继承BeanFactoryPostProcessor，用来修改Spring容器中已存在的Bean定义，只不过是通过BeanDefinitionRegistry对Bean进行处理

invokeBeanFactoryPostProcessors方法的主要逻辑如下：

- 首先从Spring容器中找出BeanDefinitionRegistryPostProcessor类型的Bean
- 然后按照优先级执行，优先找到实现PriorityOrdered接口的BeanDefinitionRegistryPostProcessor，调用其postProcessBeanDefinitionRegistry方法
- 然后找到Ordered接口的BeanDefinitionRegistryPostProcessor执行其postProcessBeanDefinitionRegistry方法，如果Processor在前面已经执行过，则不再执行
- 最后找到没有实现上述两个接口的BeanDefinitionRegistryPostProcessor执行器postProcessBeanDefinitionRegistry方法，前面如果已被执行过则不再重复执行
- 在处理完BeanDefinitionRegistryPostProcessor类型的Bean以后，会以相同的逻辑处理BeanFactoryPostProcessor类型的Bean


在整个Spring中，ConfigurationClassPostProcessor这个BeanDefinitionRegistryPostProcessor优先级最高，它会对项目中的@Configuration、@Component、@ComponentScan、@Import、@ImportResource注解修饰的类进行解析，
解析完成后会将这些Bean注入到BeanFactory中（此时未进行实例化）。

ConfigurationClassPostProcessor的主要逻辑如下：

- 

解析Bean的过程主要依赖于ConfigurationClassParser，该类内部有个属性Map<ConfigurationClass, ConfigurationClass>，用于存储已解析的类，该类保存了类的注解信息、被@Bean注解修饰的方法、@ImportResource注解修饰的信息。

ConfigurationClassParser解析过程如下：

- 处理@PropertySources注解，进行配置信息的解析
- 处理@ComponentScans注解，使用ComponentScanAnnotationParser扫描basePackage下需要解析的类并将其注册到BeanFactory中，对于扫描出来的类会进行递归解析(@SpringBootApplication注解包含了@ComponentScan注解，该注解默认的basePackages是空的，当为空是默认是@Configuration修饰的类所在的包)
- 处理@Import注解，也是通过递归处理
- 处理@ImportResource注解，获取@ImportResource注解的locations属性，得到资源文件的地址信息，然后遍历这些资源文件并把它们添加到配置类（ConfigurationClass）的importedResources属性中。
- 处理@Bean注解：获取被@Bean注解修饰的方法，然后添加到配置类的beanMethods属性中

@Import注解的处理逻辑如下：

- 遍历这些@Import注解内部的属性类集合
- 如果该类是ImportSelector，则会实例化ImportSelector，如果该类实现了DeferredImportSelector，会将其保存在ConfigurationClassParser的DeferredImportSelectorHandler中，否则会调用ImportSelector的的selectImports方法得到需要Import的类，然后对这些类递归做@Import注解的处理
- 如果该类是ImportBeanDefinitionRegistrar，则会将它初始化并并加入importBeanDefinitionRegistrars属性中
- 其它情况下把这个类入队到ConfigurationClassParser的importStack属性中，然后把这个类当成是@Configuration注解修饰的类递归重头开始解析这个类

#### registerBeanPostProcessors方法


该方法主要从Spring容器中找到BeanPostProcessor接口的Bean，并保存到BeanFactory中，该方法会委托给PostProcessorRegistrationDelegate.registerBeanPostProcessors去执行，BeanPostProcessor主要包含以下几种：

- AutowiredAnnotationBeanPostProcessor：用于处理被@Autowired注解修饰的Bean并注入
- CommonAnnotationBeanPostProcessor：用于处理@Resource、@PreDestroy、@PostConstruct等注解

#### finishRefresh方法

- 初始化生命周期处理器，并设置到Spring容器中(LifecycleProcessor)
- 调用生命周期处理器的onRefresh方法，该方法会找出Spring容器中实现了SmartLifecycle接口的类并进行start方法的调用
- 发布ContextRefreshedEvent事件告知对应的ApplicationListener进行响应的操作
- 如果设置了JMX相关的属性org.graalvm.nativeimage.imagecode，则调用LiveBeansView的registerApplicationContext方法