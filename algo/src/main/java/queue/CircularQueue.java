package queue;

public class CircularQueue<T> {

    private T[] items;
    private final int capacity;
    private int head;
    private int tail;

    public CircularQueue(int capacity) {
        this.capacity = capacity + 1;
        this.items = (T[]) new Object[capacity + 1];
    }

    /**
     * 入队
     * @param data
     */
    public void enqueue(T data) {
        if ((tail + 1) % capacity == head) {
            System.out.println("队列满了，无法入队");
            return;
        }
        items[tail] = data;
        tail = (tail + 1) % capacity;
    }

    /**
     * 出队
     */
    public T dequeue() {
        if (head == tail) {
            System.out.println("队列已空，无法出队");
            return null;
        }
        T data = items[head];
        head = (head + 1) % capacity;
        return data;
    }

    public void print() {
        System.out.println("当前队列内容：");
        for (int i = head % capacity; i != tail; i = (i + 1) % capacity) {
            System.out.print(items[i] + ",");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        CircularQueue<Integer> queue = new CircularQueue<>(4);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);

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
