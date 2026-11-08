package aitu.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DirectoryNode extends Node {
    private final List<Node> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void add(Node child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null.");
        }
        if (child.getParent() != null) {
            throw new IllegalArgumentException("Child already belongs to a directory.");
        }
        // An ancestor cannot become its own descendant.
        for (Node ancestor = this; ancestor != null; ancestor = ancestor.getParent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("Adding this child would create a cycle.");
            }
        }
        for (Node existing : children) {
            if (existing.getName().equals(child.getName())) {
                throw new IllegalArgumentException("Sibling names must be unique.");
            }
        }
        children.add(child);
        child.attachTo(this);
    }

    public List<Node> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
