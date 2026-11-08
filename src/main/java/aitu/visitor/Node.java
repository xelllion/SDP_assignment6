package aitu.visitor;

public abstract class Node implements Element {
    private final String name;
    private DirectoryNode parent;

    Node(String name) {
        if (name == null || name.isBlank() || name.equals(".") || name.equals("..")
                || name.contains("/") || name.contains("\\")) {
            throw new IllegalArgumentException("Name must be a non-empty single path component.");
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    public final DirectoryNode getParent() {
        return parent;
    }

    // Only the model package can attach nodes; clients use DirectoryNode.add().
    final void attachTo(DirectoryNode parent) {
        this.parent = parent;
    }
}
