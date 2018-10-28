package com.cop6616.lfcat;

import java.util.concurrent.atomic.AtomicReference;


public class LFCAT <T extends Comparable<T>>
{
    enum Operation { INSERT, REMOVE}

    AtomicReference<Node> root;

    public LFCAT()
    {
        root = new AtomicReference<Node>();
        Node node = (Node) new BaseNode<T>();
        root.set(node);
    }

    public boolean Insert(T x)
    {
        return DoUpdate(Operation.INSERT, x);
    }

    public boolean Remove(T x)
    {
        return DoUpdate(Operation.REMOVE, x);
    }

    public boolean Lookup(T x)
    {
        BaseNode<T> base = (BaseNode<T>) Node.FindBaseNode(root.get(), (Integer)x);
        return base.DataContains(x);
    }

    public boolean RangeQuery(/* low, high, ?? */)
    {
        // TODO: implement RangeQuery
        return false;
    }

    private boolean DoUpdate(Operation op, T x)
    {
        ContentionInfo cnt_info = ContentionInfo.UNCONTESTED;
        int key = (Integer) x;

        while(true)
        {
            BaseNode<T> base = (BaseNode<T>)Node.FindBaseNode(root.get(), (Integer)key);

            if(base.IsReplaceable())
            {
                boolean res = false;

                BaseNode<T> newb = new BaseNode<T>(base.data);
                newb.parent = base.parent;

                if(op == Operation.INSERT)
                {
                    res = newb.DataInsert(x);
                }

                if(op == Operation.REMOVE)
                {
                    res = newb.DataRemove(x);
                }

                newb.statistic = base.NewStat(cnt_info);

                if(Node.TryReplace(root, base, newb))
                {
                    newb.AdaptIfNeeded(root);
                    return res;
                }
            }

            cnt_info = ContentionInfo.CONTESTED;
            this.HelpIfNeeded(base);
        }
    }

    //
    public boolean HelpIfNeeded(Node n)
    {
        Node temp = n;
        if(temp.type == NodeType.JOIN_NEIGHBOR)
        {
            temp = ((JoinNeighborNode) temp).mainNode;
        }
        if(temp.type == NodeType.JOIN_MAIN)
        {
            if (((JoinMainNode) temp).neighbor2.get() == FlagNode.PREPARING)
            {
                ((JoinMainNode) temp).neighbor2.compareAndSet(FlagNode.PREPARING,FlagNode.ABORTED);
            }
            else if(((JoinMainNode) temp).neighbor2.get() == FlagNode.NEEDS_HELP)
            {
                //TODO: complete merge
            }
        }
        else if(temp.type == NodeType.RANGE /*TODO: && ((RangeNode) temp).storage.result.get() == Node.NOT_SET*/)
        {
            //TODO: allInRange
        }

        return true; // default value
    }
}