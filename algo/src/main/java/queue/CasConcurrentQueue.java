package queue;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class CasConcurrentQueue<T> {

    private AtomicReferenceArray<T> items;
    private final int capacity;
    private AtomicInteger size;
    private AtomicInteger head;
    private AtomicInteger tail;

    public CasConcurrentQueue(int capacity) {
        this.capacity = capacity;
        this.items = new AtomicReferenceArray<>(capacity);
        this.size = new AtomicInteger(0);
        this.head = new AtomicInteger(0);
        this.tail = new AtomicInteger(0);
    }

    /**
    * 入队
    *
    * @param data
    */
    public boolean enqueue(T data) {
        if (size.get() == capacity) {
            return false;
        }
        int index = tail.get();
        T oldData = items.get(index);
        while (!items.compareAndSet(tail.get(), oldData, data)
                || !tail.compareAndSet(index, (index + 1) % capacity)) {
            return enqueue(data);
        }
        size.incrementAndGet();
        System.out.println(Thread.currentThread().getName() + "入队元素：" + data);
        print();
        return true;
    }

    /** 出队 */
    public T dequeue() {
        if (head.get() == tail.get() && size.get() == 0) {
            return null;
        }
        int index = head.get();
        T data = items.get(index);
        while (!items.compareAndSet(index, data, null)
                || !head.compareAndSet(index, (index + 1) % capacity)) {
            return dequeue();
        }
        size.decrementAndGet();
        System.out.println(Thread.currentThread().getName() + "出队元素：" + data);
        print();
        return data;
    }

    public synchronized void print() {
        System.out.print("当前队列内容：");
        int end = head.get() + size.get();
        for (int i = head.get(); i < end; i++) {
            System.out.print(items.get(i % capacity) + ",");
        }
        System.out.println();
    }

    public static void main(String[] args) throws InterruptedException {
        CasConcurrentQueue<Integer> queue = new CasConcurrentQueue<>(4);
        Runnable put =
                () -> {
                    try {
                        while (true) {
                            long sleep = new Random().nextInt(3000);
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
