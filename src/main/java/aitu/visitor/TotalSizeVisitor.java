package aitu.visitor;

public final class TotalSizeVisitor extends TraversingVisitor {
    private long totalSize;

    @Override
    protected void reset() {
        totalSize = 0;
    }

    @Override
    protected void onFile(FileNode file, String path, int depth) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }

    public long getTotalSize() {
        return totalSize;
    }
}
