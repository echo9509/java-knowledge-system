package cn.sh.agent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

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
