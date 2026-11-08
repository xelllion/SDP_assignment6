package aitu.visitor;

public class Main {
    public static void main(String[] args) {
        DirectoryNode root = createDemoTree();
        TotalSizeVisitor total = new TotalSizeVisitor();
        SearchVisitor search = new SearchVisitor(".txt", 100);
        StatisticsVisitor statistics = new StatisticsVisitor();

        total.execute(root);
        search.execute(root);
        statistics.execute(root);

        System.out.println("SDP Assignment 6 - Visitor Pattern");
        System.out.println("Total size: " + total.getTotalSize() + " bytes");
        System.out.println("Search: name contains .txt, size >= 100 bytes");
        for (String path : search.getMatches()) {
            System.out.println("  " + path);
        }
        System.out.println("Files: " + statistics.getFileCount());
        System.out.println("Directories (including root): " + statistics.getDirectoryCount());
        System.out.println("Maximum depth: " + statistics.getMaximumDepth());
    }

    private static DirectoryNode createDemoTree() {
        DirectoryNode root = new DirectoryNode("study");
        DirectoryNode notes = new DirectoryNode("notes");
        DirectoryNode archive = new DirectoryNode("archive");
        DirectoryNode images = new DirectoryNode("images");
        root.add(new FileNode("readme.txt", 120));
        root.add(notes);
        notes.add(new FileNode("lecture.txt", 300));
        notes.add(new FileNode("todo.txt", 40));
        notes.add(archive);
        archive.add(new FileNode("old.txt", 100));
        root.add(images);
        images.add(new FileNode("diagram.png", 2048));
        root.add(new DirectoryNode("empty"));
        return root;
    }
}
