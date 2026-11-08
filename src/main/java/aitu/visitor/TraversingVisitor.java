package aitu.visitor;

import java.util.Objects;

public abstract class TraversingVisitor implements Visitor {
    private String currentPath = "";
    private int currentDepth;

    // Use execute for a complete operation, including every repeated run.
    public final void execute(Element root) {
        Objects.requireNonNull(root, "Root cannot be null.");
        currentPath = "";
        currentDepth = 0;
        reset();
        root.accept(this);
    }

    @Override
    public final void visit(FileNode file) {
        String path = currentPath.isEmpty() ? file.getName() : currentPath;
        onFile(file, path, currentDepth);
    }

    @Override
    public final void visit(DirectoryNode directory) {
        onDirectory(directory, currentPath, currentDepth);
        String parentPath = currentPath;
        int parentDepth = currentDepth;
        try {
            for (Node child : directory.getChildren()) {
                currentPath = parentPath.isEmpty() ? child.getName() : parentPath + "/" + child.getName();
                currentDepth = parentDepth + 1;
                child.accept(this);
            }
        } finally {
            currentPath = parentPath;
            currentDepth = parentDepth;
        }
    }

    protected abstract void reset();
    protected abstract void onFile(FileNode file, String path, int depth);

    // Most operations need no directory-specific action.
    protected void onDirectory(DirectoryNode directory, String path, int depth) {
    }
}
