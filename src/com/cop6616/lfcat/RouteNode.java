package com.cop6616.lfcat;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class RouteNode extends Node
{
    int key;

    AtomicReference<Node> left = new AtomicReference<Node>();
    AtomicReference<Node> right = new AtomicReference<Node>();
    AtomicReference<Node> joinID = new AtomicReference<Node>();
    AtomicBoolean valid = new AtomicBoolean();

    public RouteNode()
    {
        type = NodeType.ROUTE;
        valid.set(true);
    }

    public RouteNode(int _key, Node _left, Node _right)
    {
        type = NodeType.ROUTE;

        valid.set(true);

        key = _key;

        left.set(_left);
        right.set(_right);
    }

    public boolean IsReplaceable()
    {
        return false;
    }

    public void HelpIfNeeded(AtomicReference<Node> root){return;}

    public void Print()
    {
        System.out.println("Route Node - Key: " + key);
        System.out.print(" Left: ");
        left.get().Print();
        System.out.print(" Right: ");
        right.get().Print();
    }

    public int Size()
    {
        int size = 0;

        if(left != null)
        {
            size += left.get().Size();
        }

        if(right != null)
        {
            size += right.get().Size();
        }

        return size;
    }
}
