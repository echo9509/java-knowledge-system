package cn.sh.agent;


import com.sun.tools.attach.*;

import java.io.IOException;
import java.util.List;

public class DynamicAgent {

    public static void main(String[] args) {
        try {
            VirtualMachine jvm = VirtualMachine.attach("16304");
            jvm.loadAgentPath("C:/Users/18852/workspace/java-knowledge-system/java-agent/build/libs/java-agent-1.0.0-SNAPSHOT.jar");
            jvm.detach();
        } catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException e) {
            throw new RuntimeException(e);
        }

//        List<VirtualMachineDescriptor> list = VirtualMachine.list();
//        for (VirtualMachineDescriptor vmd : list)
//        {
//            System.out.println("pid:" + vmd.id() + ":" + vmd.displayName());
//        }
    }
}
