package ru.ifmo.ctddev.parsing;

import java.util.Arrays;
import java.util.List;

/**
 * @author Моклев Вячеслав
 */
public class Tree {
    private NodeType node;
    private List<Tree> children;

    public Tree(NodeType node, Tree... children) {
        this.node = node;
        this.children = Arrays.asList(children);
    }

    public NodeType getNode() {
        return node;
    }

    public List<Tree> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(node).append("{");
        for (Tree child: children) {
            sb.append(child).append(", ");
        }
        if (!children.isEmpty()) {
            sb.replace(sb.length() - 2, sb.length(), "}");
        } else {
            sb.append("}");
        }
        return sb.toString();
    }
}
