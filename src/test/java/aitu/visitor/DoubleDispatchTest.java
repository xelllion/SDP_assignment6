package aitu.visitor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoubleDispatchTest {
    private static class RecordingVisitor implements Visitor {
        FileNode file;
        DirectoryNode directory;

        public void visit(FileNode file) { this.file = file; }
        public void visit(DirectoryNode directory) { this.directory = directory; }
    }

    @Test
    void fileReachesFileOverloadThroughElementReference() {
        FileNode file = new FileNode("a.txt", 8);
        Element element = file;
        RecordingVisitor visitor = new RecordingVisitor();
        element.accept(visitor);
        assertSame(file, visitor.file);
        assertNull(visitor.directory);
    }

    @Test
    void directoryReachesDirectoryOverloadWithoutTraversingChildren() {
        DirectoryNode directory = new DirectoryNode("root");
        directory.add(new FileNode("a.txt", 8));
        Element element = directory;
        RecordingVisitor visitor = new RecordingVisitor();
        element.accept(visitor);
        assertSame(directory, visitor.directory);
        assertNull(visitor.file);
    }
}
