package queue;

import java.util.Stack;

/**
 *
 */
public class StackQueue {

    private Stack<Integer> inStack;
    private Stack<Integer> outStack;

    public StackQueue() {
        inStack = new Stack<>();
        outStack = new Stack<>();
    }

    public void appendTail(int value) {
        inStack.push(value);
    }

    public int deleteHead() {
        if (outStack.isEmpty()) {
            transfer();
        }
        if (outStack.isEmpty()) {
            return -1;
        }
        return outStack.pop();
    }

    private void transfer() {
        while (!inStack.isEmpty()) {
            outStack.push(inStack.pop());
        }
    }
}
