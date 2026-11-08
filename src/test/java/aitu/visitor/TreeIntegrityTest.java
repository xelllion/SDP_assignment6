package aitu.visitor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TreeIntegrityTest {
    @Test
    void rejectsNegativeSizeButAcceptsZero() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad", -1));
        assertEquals(0, new FileNode("empty", 0).getSize());
    }

    @Test
    void rejectsInvalidNames() {
        for (String name : new String[]{null, "", "  ", ".", "..", "a/b", "a\\b"}) {
            assertThrows(IllegalArgumentException.class, () -> new FileNode(name, 1));
            assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(name));
        }
    }

    @Test
    void duplicateNameRejectionDoesNotChangeEitherNode() {
        DirectoryNode root = new DirectoryNode("root");
        FileNode first = new FileNode("same", 1);
        DirectoryNode duplicate = new DirectoryNode("same");
        root.add(first);
        assertThrows(IllegalArgumentException.class, () -> root.add(duplicate));
        assertEquals(1, root.getChildren().size());
        assertSame(first, root.getChildren().get(0));
        assertNull(duplicate.getParent());
        new DirectoryNode("other").add(duplicate);
    }

    @Test
    void rejectsMultipleParentsAndRepeatedInsertion() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        FileNode file = new FileNode("file", 2);
        a.add(file);
        assertThrows(IllegalArgumentException.class, () -> b.add(file));
        assertThrows(IllegalArgumentException.class, () -> a.add(file));
        assertSame(a, file.getParent());
        assertTrue(b.getChildren().isEmpty());
        assertEquals(1, a.getChildren().size());
    }

    @Test
    void rejectsSelfAndAncestorCyclesButAllowsDeepNesting() {
        DirectoryNode a = new DirectoryNode("a");
        DirectoryNode b = new DirectoryNode("b");
        DirectoryNode c = new DirectoryNode("c");
        a.add(b);
        b.add(c);
        assertThrows(IllegalArgumentException.class, () -> a.add(a));
        assertThrows(IllegalArgumentException.class, () -> c.add(a));
        assertNull(a.getParent());
        assertSame(a, b.getParent());
        assertSame(b, c.getParent());
        assertTrue(c.getChildren().isEmpty());
    }

    @Test
    void childrenCannotBeChangedThroughGetter() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("a", 1));
        assertThrows(UnsupportedOperationException.class, () -> root.getChildren().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> root.getChildren().add(new FileNode("b", 2)));
        assertEquals(1, root.getChildren().size());
    }

    @Test
    void rejectsNullAndAllowsSameNameInDifferentDirectories() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode sub = new DirectoryNode("sub");
        root.add(sub);
        root.add(new FileNode("same.txt", 1));
        sub.add(new FileNode("same.txt", 2));
        assertThrows(IllegalArgumentException.class, () -> root.add(null));
        assertEquals(2, root.getChildren().size());
        assertEquals(1, sub.getChildren().size());
    }
}
