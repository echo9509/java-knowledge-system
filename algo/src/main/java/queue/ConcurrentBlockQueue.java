package queue;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentBlockQueue<T> {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    private T[] items;
    private final int capacity;
    private int size;
    private int head;
    private int tail;

    public ConcurrentBlockQueue(int capacity) {
        this.capacity = capacity;
        this.items = (T[]) new Object[capacity];
        this.size = 0;
        this.head = 0;
        this.tail = 0;
    }

    /**
    * 入队
    *
    * @param data
    */
    public void enqueue(T data) throws InterruptedException {
        try {
            lock.lock();
            if (size == capacity) {
                System.out.println("队列已满，等待元素出队>>>>>>>>>>>>>>>>>");
                notFull.await();
            } else {
                items[tail] = data;
                tail = (tail + 1) % capacity;
                size++;
                notEmpty.signalAll();
                System.out.println(Thread.currentThread().getName() + "放入元素：" + data);
                print();
            }
        } finally {
            lock.unlock();
        }
    }

    /** 出队 */
    public T dequeue() throws InterruptedException {
        try {
            lock.lock();
            if (head == tail && size == 0) {
                System.out.println(">>>>>>>>>>>>>>>>>>队列为空，等待元素入队");
                notEmpty.await();
            } else {
                T data = items[head];
                head = (head + 1) % capacity;
                size--;
                notFull.signalAll();
                System.out.println(Thread.currentThread().getName() + "出队元素：" + data);
                print();
                return data;
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    public void print() {
        System.out.println("当前队列内容：");
        int end = head + size;
        for (int i = head; i < end; i++) {
            System.out.print(items[i % capacity] + ",");
        }
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        ConcurrentBlockQueue<Integer> queue = new ConcurrentBlockQueue<>(4);
        Runnable put =
                () -> {
                    try {
                        while (true) {
                            long sleep = new Random().nextInt(2000);
                            Thread.sleep(sleep);
                            int data = new Random().nextInt(10);
                            queue.enqueue(data);
                        }
                    } catch (InterruptedException e) {
                        System.out.println("发生异常");
                    }
                };
        Runnable take =
                () -> {
                    try {
                        while (true) {
                            long sleep = new Random().nextInt(5000);
                            Thread.sleep(sleep);
                            queue.dequeue();
                        }
                    } catch (InterruptedException e) {
                        System.out.println("发生异常");
                    }
                };
        ExecutorService putExecutor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            putExecutor.submit(put);
        }
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            taskExecutor.submit(take);
        }
        Thread.sleep(Integer.MAX_VALUE);
    }
}
