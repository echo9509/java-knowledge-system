package cn.sh.agent;

import com.sun.tools.attach.*;
import java.io.IOException;
import java.util.List;

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
