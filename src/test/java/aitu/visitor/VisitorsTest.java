package aitu.visitor;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VisitorsTest {
    private DirectoryNode tree() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode docs = new DirectoryNode("docs");
        DirectoryNode deep = new DirectoryNode("deep");
        root.add(new FileNode("z.txt", 100));
        root.add(docs);
        docs.add(new FileNode("a.txt", 40));
        docs.add(deep);
        deep.add(new FileNode("b.txt", 60));
        deep.add(new FileNode("image.png", 200));
        root.add(new DirectoryNode("empty"));
        return root;
    }

    @Test
    void sumsEveryNestedFileExactlyOnce() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        visitor.execute(tree());
        assertEquals(400, visitor.getTotalSize());
    }

    @Test
    void searchCombinesFilenameAndInclusiveSizeConditions() {
        SearchVisitor visitor = new SearchVisitor(".txt", 60);
        visitor.execute(tree());
        assertEquals(List.of("docs/deep/b.txt", "z.txt"), visitor.getMatches());
    }

    @Test
    void searchSortsRootRelativePathsRegardlessOfInsertionOrder() {
        SearchVisitor visitor = new SearchVisitor("", 0);
        visitor.execute(tree());
        assertEquals(List.of("docs/a.txt", "docs/deep/b.txt", "docs/deep/image.png", "z.txt"),
                visitor.getMatches());
    }

    @Test
    void emptyDirectoryAndNoMatchesProduceEmptyResults() {
        TotalSizeVisitor total = new TotalSizeVisitor();
        total.execute(new DirectoryNode("empty"));
        assertEquals(0, total.getTotalSize());
        SearchVisitor search = new SearchVisitor("missing", 0);
        search.execute(tree());
        assertTrue(search.getMatches().isEmpty());
        search.execute(new DirectoryNode("empty"));
        assertTrue(search.getMatches().isEmpty());
    }

    @Test
    void repeatedRunsAndDifferentRootsResetAllResults() {
        DirectoryNode root = tree();
        TotalSizeVisitor total = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(".txt", 60);
        for (int run = 0; run < 2; run++) {
            search.execute(root);
            total.execute(root);
            assertEquals(400, total.getTotalSize());
            assertEquals(List.of("docs/deep/b.txt", "z.txt"), search.getMatches());
        }
        total.execute(new FileNode("small.txt", 3));
        search.execute(new DirectoryNode("empty"));
        assertEquals(3, total.getTotalSize());
        assertTrue(search.getMatches().isEmpty());
    }

    @Test
    void singleFileRootUsesItsNameAndSize() {
        FileNode file = new FileNode("one.txt", 50);
        SearchVisitor search = new SearchVisitor("one", 50);
        TotalSizeVisitor total = new TotalSizeVisitor();
        search.execute(file);
        total.execute(file);
        assertEquals(List.of("one.txt"), search.getMatches());
        assertEquals(50, total.getTotalSize());
    }

    @Test
    void selectedSubtreeIgnoresAncestorsAndSiblingFiles() {
        DirectoryNode outer = new DirectoryNode("outer");
        DirectoryNode chosen = new DirectoryNode("chosen");
        outer.add(chosen);
        outer.add(new FileNode("outside.txt", 999));
        chosen.add(new FileNode("inside.txt", 7));
        SearchVisitor search = new SearchVisitor("", 0);
        TotalSizeVisitor total = new TotalSizeVisitor();
        search.execute(chosen);
        total.execute(chosen);
        assertEquals(List.of("inside.txt"), search.getMatches());
        assertEquals(7, total.getTotalSize());
    }

    @Test
    void searchIsCaseSensitiveAndChecksOnlyFilename() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode docs = new DirectoryNode("match");
        root.add(docs);
        docs.add(new FileNode("other.txt", 1));
        root.add(new FileNode("Match.txt", 1));
        SearchVisitor search = new SearchVisitor("match", 0);
        search.execute(root);
        assertTrue(search.getMatches().isEmpty());
    }

    @Test
    void searchResultsAreImmutableSnapshots() {
        SearchVisitor search = new SearchVisitor(".txt", 100);
        search.execute(tree());
        List<String> previous = search.getMatches();
        assertThrows(UnsupportedOperationException.class, () -> previous.clear());
        search.execute(new DirectoryNode("empty"));
        assertEquals(List.of("z.txt"), previous);
    }

    @Test
    void rejectsInvalidSearchConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor("", -1));
    }

    @Test
    void sizeOverflowFailsInsteadOfReturningAWrongNegativeTotal() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("large", Long.MAX_VALUE));
        root.add(new FileNode("extra", 1));
        TotalSizeVisitor total = new TotalSizeVisitor();
        assertThrows(ArithmeticException.class, () -> total.execute(root));
        total.execute(new FileNode("ok", 2));
        assertEquals(2, total.getTotalSize());
    }

    @Test
    void statisticsCountAllNodesIncludingRoot() {
        StatisticsVisitor stats = new StatisticsVisitor();
        stats.execute(tree());
        assertEquals(4, stats.getFileCount());
        assertEquals(4, stats.getDirectoryCount());
        assertEquals(3, stats.getMaximumDepth());
    }

    @Test
    void statisticsResetForRepeatedRunsEmptyDirectoryAndFileRoot() {
        StatisticsVisitor stats = new StatisticsVisitor();
        DirectoryNode root = tree();
        for (int run = 0; run < 2; run++) {
            stats.execute(root);
            assertEquals(4, stats.getFileCount());
            assertEquals(4, stats.getDirectoryCount());
            assertEquals(3, stats.getMaximumDepth());
        }
        stats.execute(new DirectoryNode("empty"));
        assertEquals(0, stats.getFileCount());
        assertEquals(1, stats.getDirectoryCount());
        assertEquals(0, stats.getMaximumDepth());
        stats.execute(new FileNode("only", 0));
        assertEquals(1, stats.getFileCount());
        assertEquals(0, stats.getDirectoryCount());
        assertEquals(0, stats.getMaximumDepth());
    }

    @Test
    void depthIncludesEmptyDirectoriesAndStartsAtChosenRoot() {
        DirectoryNode outside = new DirectoryNode("outside");
        DirectoryNode chosen = new DirectoryNode("chosen");
        DirectoryNode nested = new DirectoryNode("nested");
        outside.add(chosen);
        chosen.add(nested);
        nested.add(new DirectoryNode("empty"));
        StatisticsVisitor stats = new StatisticsVisitor();
        stats.execute(chosen);
        assertEquals(0, stats.getFileCount());
        assertEquals(3, stats.getDirectoryCount());
        assertEquals(2, stats.getMaximumDepth());
    }
}
