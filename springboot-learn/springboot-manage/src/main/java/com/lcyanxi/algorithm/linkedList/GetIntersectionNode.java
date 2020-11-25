package com.lcyanxi.algorithm.linkedList;

/**
 * 两个链表的第一个公共节点
 * 描述：输入两个链表，找出它们的第一个公共节点（节点相同，并非节点的内容相同）。
 * eg:
 * 输入：intersectVal = 8, listA = [4,1,8,4,5], listB = [5,0,1,8,4,5], skipA = 2, skipB = 3
 * 输出：Reference of the node with value = 8
 * 输入解释：相交节点的值为 8 （注意，如果两个列表相交则不能为 0）。从各自的表头开始算起，链表 A 为 [4,1,8,4,5]，
 * 链表 B 为 [5,0,1,8,4,5]。在 A 中，相交节点前有 2 个节点；在 B 中，相交节点前有 3 个节点。
 * @author lichang
 * @date 2020/11/25
 */
public class GetIntersectionNode {
    public static void main(String[] args) {

        Integer[] a1 = {1,2,4};
        Integer[] b1 = {1,3,5};

        ListNode node1 = getListNode(a1);
        ListNode node2 = getListNode(b1);
        System.out.println(getIntersectionNode(node1,node2));


    }
    private static int getIntersectionNode(ListNode headA, ListNode headB){
        if (headA == null || headB == null){
            return 0;
        }
        //统计链表A和链表B的长度
        int lengthA = lengthUtil(headA);
        int lengthB = lengthUtil(headB);
        //如果节点长度不一样，节点多的先走，直到他们的长度一样为止
        while (lengthA != lengthB){
            if (lengthA > lengthB){
                headA = headA.next;
                lengthA --;
            }else {
                headB = headB.next;
                lengthB --;
            }
        }
        //然后开始比较，如果他俩不相等就一直往下走
        while (headA != headB){
            headA = headB.next;
            headB = headB.next;
        }

        return headA == null ? 0 : headA.val;
    }


    /**
     * 获取节点长度
     * @param node 节点
     * @return
     */
    private static int lengthUtil(ListNode node){
        if (node == null){
            return 0;
        }
        int index = 0;
        while (node.next != null){
             node = node.next;
            index ++ ;
        }
        return index;
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
}
