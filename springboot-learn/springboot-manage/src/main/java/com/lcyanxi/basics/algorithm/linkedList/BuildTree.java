package com.lcyanxi.basics.algorithm.linkedList;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * 重建二叉树
 * 描述：输入某二叉树的前序遍历和中序遍历的结果，请重建该二叉树。假设输入的前序遍历和中序遍历的结果中都不含重复的数字。
 * eg：
 * 前序遍历 preorder = [3,9,20,15,7]
 * 中序遍历 inorder = [9,3,15,20,7]
 * 返回如下的二叉树：
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 * @author lichang
 * @date 2020/11/23
 */
public class BuildTree {
    static Map<Integer,Integer> map = Maps.newHashMap();
    public static void main(String[] args) {

        int [] preOrder = {3,9,20,15,7};
        int [] inorder = {9,3,15,20,7};
        TreeNode treeNode = buildTree(preOrder, inorder);

        System.out.println(treeNode);

    }
    private static TreeNode buildTree(int[] preOrder,int [] inorder){
        // 为了记录根节点在中序遍历中的位子
        for (int i = 0; i < inorder.length; i++){
            map.put(inorder[i],i);
        }
        int length = preOrder.length - 1;
        return buildTreeUtil(preOrder,0,length,inorder,0,length);

    }

    private static  TreeNode buildTreeUtil(int[] preOrder, int preStart, int preEnd,int [] inorder,int inStart,int inEnd){
        int rootVal = preOrder[preStart];
        TreeNode rootNode = new TreeNode(rootVal);
        if (preStart == preEnd){
            return rootNode;
        }else {
            /*
             *  * 前序遍历 preorder = [3,9,20,15,7]
             *  * 中序遍历 inorder = [9,3,15,20,7]
             */
            int rootIndex = map.get(rootVal);
            int leftNode = rootIndex - inStart;
            rootNode.left = buildTreeUtil(preOrder,preStart + 1,preStart + leftNode,inorder,inStart,rootIndex -1);
            rootNode.right = buildTreeUtil(preOrder,preStart + leftNode + 1,preEnd,inorder,rootIndex + 1,inEnd);
            return rootNode;
        }
    }

}
class TreeNode {
    int val;
    TreeNode left ;
    TreeNode right ;
    TreeNode(int x){
        this.val = x;
    }
}

