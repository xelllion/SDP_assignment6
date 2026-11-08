package aitu.visitor;

public final class StatisticsVisitor extends TraversingVisitor {
    private int fileCount;
    private int directoryCount;
    private int maximumDepth;

    @Override
    protected void reset() {
        fileCount = 0;
        directoryCount = 0;
        maximumDepth = 0;
    }

    @Override
    protected void onFile(FileNode file, String path, int depth) {
        fileCount++;
        maximumDepth = Math.max(maximumDepth, depth);
    }

    @Override
    protected void onDirectory(DirectoryNode directory, String path, int depth) {
        directoryCount++;
        maximumDepth = Math.max(maximumDepth, depth);
    }

    public int getFileCount() { return fileCount; }
    public int getDirectoryCount() { return directoryCount; }
    public int getMaximumDepth() { return maximumDepth; }
}
