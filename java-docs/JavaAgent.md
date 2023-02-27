
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

#### 如何启动我们的Agent

在Java程序启动时，我们通过-javaagent指定Agent的jar包，如下：

```text
-javaagent:/java-agent-1.0.0-SNAPSHOT.jar
```

#### 如何动态加载Agent

```

```

