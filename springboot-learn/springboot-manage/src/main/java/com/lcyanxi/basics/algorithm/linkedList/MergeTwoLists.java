package com.lcyanxi.basics.algorithm.linkedList;


/**
 * 合并两个排序的链表
 * 输入两个递增排序的链表，合并这两个链表并使新链表中的节点仍然是递增排序的。
 * eg:
 * 输入：1->2->4, 1->3->4
 * 输出：1->1->2->3->4->4
 * 扩展连个有序数组合并为一个有序数组
 * @author lichang
 * @date 2020/11/18
 */
public class MergeTwoLists {
    public static void main(String[] args) {

        Integer[] a1 = {1,2,4};
        Integer[] b1 = {1,3,5};
        ListNode listNode1 = getListNode(a1);
        ListNode listNode2 = getListNode(b1);
        ListNode node = mergeTwoLists(listNode1,listNode2);
        while (node != null){
            System.out.println(node.val);
            node = node.next;
        }
        Integer[] ab = mergeTwoArray(a1,b1);
        for (Integer integer : ab){
            System.out.print(integer);
        }
        System.out.println();
    }

    private static ListNode getListNode(Integer [] arr){
        ListNode listNode1 = null;
        for (Integer integer : arr){
            ListNode temp = new ListNode(integer);
            temp.next = listNode1;
            listNode1 = temp;
        }
        return listNode1;
    }

    private static ListNode mergeTwoLists(ListNode listNode1,ListNode listNode2){
        ListNode head = new ListNode(-1);
        ListNode current = head;
        // 合并，直至有一个链表为空
        while (listNode1 != null && listNode2 != null) {
            if (listNode1.val >= listNode2.val) {
                current.next = listNode2;
                listNode2 = listNode2.next;
            } else {
                current.next = listNode1;
                listNode1 = listNode1.next;
            }
            current = current.next;
        }
        // 不为空的那条链表的剩余部分 接在 合并链表 的末尾
        current.next = (listNode1 == null) ? listNode2 : listNode1;
        return head.next;
    }

    private static Integer[] mergeTwoArray(Integer[] a1,Integer [] a2){
        Integer[] newArr = new Integer[a1.length + a2.length];
        int l1 = 0;
        int l2 = 0;
        int index = 0;
        while (l1 < a1.length && l2 < a2.length){
            if (a1[l1] <= a2[l2]){
                newArr[index] = a1[l1];
                l1++;
            }else {
                newArr[index] = a2[l2];
                l2++;
            }
            index ++;
        }
        if (l1 < a1.length){
            for (; l1 < a1.length; l1++){
                newArr[l2 + l1] = a1[l1];
            }
        }else {
            for (; l2 < a2.length; l2++){
                newArr[l2 + l1] = a2[l2];
            }
        }
        return  newArr;
    }
}




class ListNode {
     int val;
     ListNode next;
     ListNode(int x) {this.val = x;}
 }
