package queue;

public class ArrayQueue<T> {

    private T[] items;
    private final int capacity;
    private int head;
    private int tail;

    public ArrayQueue(int capacity) {
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
        this.items = (T[]) new Object[capacity];
    }

    /**
     * 入队
     * @param data
     */
    public void enqueue(T data) {
        if (queueFull()) {
            System.out.println("队列已满，无法入队");
            return;
        }
        if (tail == capacity) {
            // 队列未满，但是无法容纳元素，移动元素
            moveItem();
        }
        items[tail++] = data;
    }

    /**
     * 出队
     * @return
     */
    public T dequeue() {
        if (head == tail) {
            System.out.println("队列已空，无法出队");
            return null;
        }
        return items[head++];
    }

    private void moveItem() {
        int count = tail - head;
        System.out.println("移动元素");
        for (int i = head; i < count; i++) {
            items[i - head] = items[i];
        }
        for (int start = head; start < tail; start++) {
            items[start] = null;
        }
        this.head = 0;
        this.tail = count;
    }

    private boolean queueFull() {
        return tail - head == capacity;
    }

    public void print() {
        System.out.println("当前队列内容：");
        for (int i = head; i < tail; i++) {
            System.out.print(items[i] + ",");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayQueue<Integer> queue = new ArrayQueue<>(4);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);
        queue.print();

        queue.dequeue();
        queue.dequeue();
        queue.print();

        queue.enqueue(1);
        queue.enqueue(2);
        queue.print();

        queue.enqueue(5);
        queue.print();
    }
}
