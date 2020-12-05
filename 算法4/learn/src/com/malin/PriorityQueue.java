package com.malin;

// https://www.bilibili.com/video/BV1ti4y1879c?from=search&seid=3409589889006736711
public class PriorityQueue {

    static class Node {
        int value;
        int priority;
        Node next;

        public Node(int value, int priority) {
            this.value = value;
            this.priority = priority;
        }
    }

    Node head = null;

    public void push(int value, int priority) {
        if (head == null) {
            head = new Node(value, priority);
            return;
        }
        Node currentNode = head;
        Node newNode = new Node(value, priority);
        if (head.priority < priority) {
            newNode.next = head;
            this.head = newNode;
        } else {
            while (currentNode.next != null && currentNode.next.priority > priority) {
                currentNode = currentNode.next;
            }
            newNode.next = currentNode.next;
            currentNode.next = newNode;
        }
    }

    public Node peek() {
        return head;
    }

    public Node pop() {
        if (head == null) {
            return null;
        }
        Node temp = head;
        head = head.next;
        return temp;
    }

    public boolean isEmpty() {
        return head == null;
    }

}
