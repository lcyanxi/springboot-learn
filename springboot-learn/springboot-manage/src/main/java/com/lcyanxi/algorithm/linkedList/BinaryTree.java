package com.lcyanxi.algorithm.linkedList;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 二叉树遍历
 * eg:
 *       39
 *     /   \
 *   24     64
 *  / \     /
 * 23  30  53
 *           \
 *           60
 * 先序遍历：根左右 : 39 24 23 30 64 53 60
 * 中序遍历：左根右 : 23 24 30 39 53 60 64
 * 后序遍历：左右根 : 23 30 24 60 53 64 39
 * @author lichang
 * @date 2020/11/25
 */
public class BinaryTree {
    public static void main(String[] args) {
        TreeNode root = null;
        int[] arr = {39,24,64,23,30,53,60};
        for (int e : arr){
            root = buildBinaryTree(root, e);
        }
        List<Integer> list1 = Lists.newArrayList();
        preOrderRecusive(root,list1);
        System.out.print("前序遍历：");
        for (Integer integer : list1){
            System.out.print(integer + " ");
        }
        System.out.println();
        List<Integer> list2 = Lists.newArrayList();
        midOrderRecusive(root,list2);
        System.out.print("中序遍历：");
        for (Integer integer : list2){
            System.out.print(integer + " ");
        }
        System.out.println();
        List<Integer> list3 = Lists.newArrayList();
        latOrderRecusive(root,list3);
        System.out.print("后序遍历：");
        for (Integer integer : list3){
            System.out.print(integer + " ");
        }
        System.out.println();
        System.out.println("层次遍历：" + levelOrderRecusive(root));
    }

    /**
     * 构建二叉树
     * @param root 根节点
     * @param data 数据
     * @return
     */
    private static TreeNode buildBinaryTree(TreeNode root,int data){
        if (root == null){
            root = new TreeNode(data);
            return root;
        }
        TreeNode current = root;
        TreeNode parent;
        while (true){
            parent = current;
            if (current.val > data){
                current = current.left;
                if (current == null){
                    parent.left = new TreeNode(data);
                    break;
                }
            }else {
                current = current.right;
                if (current == null){
                    parent.right = new TreeNode(data);
                    break;
                }
            }
        }
        return root;
    }


    // 先序遍历
    private static void preOrderRecusive(TreeNode node,List<Integer> list){
        if (node == null){
           return;
        }
        list.add(node.val);
        preOrderRecusive(node.left,list);
        preOrderRecusive(node.right,list);
    }

    // 中序遍历
    private static void midOrderRecusive(TreeNode node,List<Integer> list){
        if (node == null){
            return;
        }
        midOrderRecusive(node.left,list);
        list.add(node.val);
        midOrderRecusive(node.right,list);
    }

    // 后序遍历
    private static void latOrderRecusive(TreeNode node,List<Integer> list){
        if (node == null){
            return;
        }
        latOrderRecusive(node.left,list);
        latOrderRecusive(node.right,list);
        list.add(node.val);
    }

    // 层次遍历
    private static List<Integer> levelOrderRecusive(TreeNode node){
        List<Integer> result = Lists.newArrayList();
        Queue<TreeNode> queue = Lists.newLinkedList();
        if (node == null){
            return result;
        }
        queue.add(node);
        while (true){
            int size = queue.size();
            if (size == 0){
                return result;
            }
            TreeNode treeNode = queue.poll();
            result.add(treeNode.val);
            if (treeNode.left != null){
                queue.add(treeNode.left);
            }
            if (treeNode.right != null){
                queue.add(treeNode.right);
            }
        }
    }

}

