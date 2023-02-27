package offer;

/**
* @author sh 剑指 Offer 10- I. 斐波那契数列 写一个函数，输入 n ，求斐波那契（Fibonacci）数列的第 n 项（即 F(N)）。斐波那契数列的定义如下： F(0)
*     = 0, F(1) = 1 F(N) = F(N - 1) + F(N - 2), 其中 N > 1.
*/
public class FibonacciSequence {

    public static int fib(int n) {
        if (n <= 1) {
            return n;
        }
        int p = 0, q = 0, r = 1;
        for (int i = 2; i <= n; i++) {
            p = q;
            q = r;
            r = (p + q) % 1000000007;
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println(fib(44));
    }
}
