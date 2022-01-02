package stack;

public class ArrayStack<T> {

    private static final int DEFAULT_CAPACITY = 5;

    public T[] items;
    private int count; // 栈中元素个数
    private int capacity; // 栈的最大容量

    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayStack(int capacity) {
        this.capacity = capacity;
        this.items = (T[]) new Object[capacity];
    }

    /**
     * 时间复杂度为O(1)
     * 虽然扩容时为O(n)，但扩容只有在特定时刻发生，所以均摊下来，时间复杂度为O(1)
     * 入栈
     * @param data
     */
    public void push(T data) {
        if (count + 1 > capacity) {
            resize();
        }
        items[count++] = data;
    }

    /**
     * 时间复杂度为O(1)
     * 出栈
     * @return
     */
    public T pop() {
        if (count == 0) {
            return null;
        }
        T data = items[count];
        items[--count] = null;
        return data;
    }

    /**
     * 扩容
     */
    private void resize() {
        // 2倍扩容
        this.capacity = capacity << 1;
        System.out.println("扩容，新容量为：" + this.capacity);
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 0; i < this.count; i++) {
            newItems[i] = items[i];
        }
        this.items = newItems;
    }

    public void print() {
        for (int i = 0; i < count; i++) {
            System.out.print(items[i] + ",");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayStack<Integer> stack = new ArrayStack<>(2);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(5);
        stack.push(6);

        System.out.println("栈内容为：");
        stack.print();

        stack.pop();

        System.out.println("栈内容为：");
        stack.print();

        stack.pop();
        stack.pop();

        System.out.println("栈内容为：");
        stack.print();

        stack.pop();
//        stack.pop();

        System.out.println("栈内容为：");
        stack.print();
    }
}
