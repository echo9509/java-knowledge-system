package queue;

public class CircularQueue<T> {

    private T[] items;
    private final int capacity;
    // 当前元素数量
    private int size;
    private int head;
    private int tail;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        this.items = (T[]) new Object[capacity];
    }

    /**
    * 入队
    *
    * @param data
    */
    public void enqueue(T data) {
        if (size == capacity) {
            System.out.println("队列满了，无法入队");
            return;
        }
        items[tail] = data;
        tail = (tail + 1) % capacity;
        size++;
    }

    /** 出队 */
    public T dequeue() {
        if (head == tail && size == 0) {
            System.out.println("队列已空，无法出队");
            return null;
        }
        T data = items[head];
        head = (head + 1) % capacity;
        size--;
        return data;
    }

    public void print() {
        System.out.print("当前队列内容：");
        int end = head + size;
        for (int i = head; i < end; i++) {
            System.out.print(items[i % capacity] + ",");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        CircularQueue<Integer> queue = new CircularQueue<>(4);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);
        queue.enqueue(5);
        queue.print();

        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        queue.print();

        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.print();

        queue.enqueue(4);

        queue.print();
    }
}
