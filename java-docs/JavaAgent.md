#### 前言

之所以写关于Agent的东西，是因为最近在搭建公司的链路系统，在整个链路系统中我们会使用到各种各样的Agent，通过探针这种形式，避免了对业务代码的侵入性。

本篇只是开篇点题，实现一个统计方法的耗时时间的Agent，先直观感受一下Agent的作用，大量关于Agent的实现细节我们会在后续陆续更新。

#### 什么是Java Agent?

Java Agent本质上就是一种代理程序，它会利用Java提供的Instrumentation的API来动态更改已经加载到JVM中的字节码，通常代理的工作需要我们来定义两个方法：

- premain(String agentArgs, Instrumentation inst)： 通过-javaagent指定jar，当应用程序启动时会静态进行静态加载，执行该方法
- agentmain(String agentArgs, Instrumentation inst)：通过Java Attach API我们可以在程序运行时动态修改class

#### 如何实现JavaAgent 

1. 引入依赖

```gradle

implementation 'javassist:javassist:3.12.1.GA'

```
2. 编写一段代码，如下该代码会在应用启动时，修改package session.controller下的所有class的method，统计每个方法的执行时长

```java
public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        recordMethodCostTime(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        recordMethodCostTime(inst);
        Class<?>[] classes = inst.getAllLoadedClasses();
        try {
            for (Class<?> c : classes) {
                if (c.isAnnotation() || c.isInterface() || c.isArray() || c.isEnum()) {
                    continue;
                }
                if (c.getName().startsWith("session.controller")) {
                    // 重新装在我们的类
                    inst.retransformClasses(c);
                }
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }

    private static void recordMethodCostTime(Instrumentation inst) {
        inst.addTransformer(new RecordCostTimeTransformer(), true);
    }

    public static class RecordCostTimeTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(
                ClassLoader loader,
                String className,
                Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain,
                byte[] classfileBuffer)
                throws IllegalClassFormatException {
            byte[] byteCode = classfileBuffer;
            if (!className.startsWith("session/controller")) {
                return byteCode;
            }
            ClassPool cp = ClassPool.getDefault();
            try {
                CtClass ctClass = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
                CtMethod[] methods = ctClass.getDeclaredMethods();
                for (CtMethod method : methods) {
                    method.addLocalVariable("startTime", CtClass.longType);
                    method.insertBefore("startTime = System.currentTimeMillis();");
                    method.addLocalVariable("endTime", CtClass.longType);
                    StringBuilder endBlock = new StringBuilder();
                    endBlock.append("endTime = System.currentTimeMillis();");
                    method.addLocalVariable("costTime", CtClass.longType);
                    endBlock.append("costTime = endTime - startTime;");
                    endBlock.append(
                            "System.out.println(\"method: "
                                    + method.getName()
                                    + ", execute cost time:\" + costTime +\" ms\");");
                    method.insertAfter(endBlock.toString());
                }
                byteCode = ctClass.toBytecode();
            } catch (IOException | CannotCompileException e) {
                throw new RuntimeException(e);
            }

            return byteCode;
        }
    }
}
```
3. 定义MAINIFEST.MF文件，内容如下：

```text
Agent-Class: cn.sh.agent.Agent
Can-Redefine-Classes: true
Can-Retransform-Classes: true
Premain-Class: cn.sh.agent.Agent
```

#### 如何静态加载Agent

在Java程序启动时，我们通过-javaagent指定Agent的jar包，如下：

```text
-javaagent:/java-agent-1.0.0-SNAPSHOT.jar
```

![image.png](https://s2.loli.net/2023/02/27/cgXDOsdohqW65rx.png)

#### 如何动态加载Agent

通过动态加载Agent，我们可以对程序运行中的class进行重新加载，利用此功能我们可以对线上增加部分代码进行问题排查和定位。

```java
public class DynamicAgent {

    public static void main(String[] args) {
        try {
            List<VirtualMachineDescriptor> virtualMachineDescriptors = VirtualMachine.list();
            for (VirtualMachineDescriptor descriptor : virtualMachineDescriptors) {
                if (descriptor.displayName().equals("session.DaemonApplication")) {
                    VirtualMachine jvm = VirtualMachine.attach(descriptor);
                    jvm.loadAgent(
                            "/Users/sh/workspace/java-knowledge-system/java-agent/build/libs/java-agent-1.0.0-SNAPSHOT.jar");
                    jvm.detach();
                }
            }
        } catch (AttachNotSupportedException
                | IOException
                | AgentLoadException
                | AgentInitializationException e) {
            throw new RuntimeException(e);
        }
    }
}
```

效果图如下：

![image.png](https://s2.loli.net/2023/02/27/Td4IOSoGcFvUjeq.png)