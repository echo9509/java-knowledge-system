package offer;

/** @author sh 剑指 Offer 05. 替换空格 请实现一个函数，把字符串 s 中的每个空格替换成"%20"。 */
public class SpacesReplace {

    public static String replaceSpace(String s) {
        char[] array = new char[s.length() * 3];
        int size = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch != ' ') {
                array[size++] = ch;
                continue;
            }
            array[size++] = '%';
            array[size++] = '2';
            array[size++] = '0';
        }

        return new String(array, 0, size);
    }

    public static void main(String[] args) {
        System.out.println(replaceSpace("We are happy."));
    }
}
