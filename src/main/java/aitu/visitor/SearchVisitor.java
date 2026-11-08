package aitu.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SearchVisitor extends TraversingVisitor {
    private final String namePart;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();

    public SearchVisitor(String namePart, long minimumSize) {
        if (namePart == null || minimumSize < 0) {
            throw new IllegalArgumentException("Search text cannot be null and minimum size cannot be negative.");
        }
        this.namePart = namePart;
        this.minimumSize = minimumSize;
    }

    @Override
    protected void reset() {
        matches.clear();
    }

    @Override
    protected void onFile(FileNode file, String path, int depth) {
        if (file.getName().contains(namePart) && file.getSize() >= minimumSize) {
            matches.add(path);
        }
    }

    public List<String> getMatches() {
        List<String> sorted = new ArrayList<>(matches);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }
}
