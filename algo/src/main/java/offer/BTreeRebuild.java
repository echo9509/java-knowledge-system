package offer;


import java.util.HashMap;
import java.util.Map;

/**
 * @author sh
 * 剑指 Offer 07. 重建二叉树
 * 输入某二叉树的前序遍历和中序遍历的结果，请构建该二叉树并返回其根节点。
 * 假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
 */
public class BTreeRebuild {

    private Map<Integer, Integer> indexMap = new HashMap<>();

    public TreeNode buildTree(int[] preorder, int[] inorder) {

        int length = preorder.length;
        for (int i = 0; i < length; i++) {
            indexMap.put(inorder[i], i);
        }
        return buildTree(preorder, inorder, 0, length - 1, 0, length - 1);
    }

    public TreeNode buildTree(int[] preOrder, int[] inorder, int preorderLeft, int preorderRight, int inorderLeft, int inorderRight) {
        if (preorderLeft > preorderRight) {
            return null;
        }
        int inorderRootIndex = indexMap.get(preOrder[preorderLeft]);
        TreeNode root = new TreeNode(preOrder[preorderLeft]);
        int leftTreeSize = inorderRootIndex - inorderLeft;
        root.left = buildTree(preOrder, inorder, preorderLeft + 1, preorderLeft + leftTreeSize, inorderLeft, inorderRootIndex - 1);
        root.right = buildTree(preOrder, inorder, preorderLeft + leftTreeSize + 1, preorderRight, inorderRootIndex + 1, inorderRight);
        return root;
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public static void main(String[] args) {
        int[] preorder = new int[]{3,9, 20, 15, 7};
        int[] inorder = new int[]{9, 3, 15, 20, 7};
        BTreeRebuild bTreeRebuild = new BTreeRebuild();
        bTreeRebuild.buildTree(preorder, inorder);
    }
}
