import java.io.*;
import java.util.*;

public class BlockList {
    private final Set<String> blocked = new HashSet<>();

    public BlockList(String file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim().toLowerCase();

            if (line.isEmpty()) continue;
            if (line.startsWith("#")) continue;

            blocked.add(line);
        }

        reader.close();

        System.out.println("Loaded " + blocked.size() + " blocked domains");
    }

    public boolean isBlocked(String domain) {
        return blocked.contains(domain.toLowerCase());
    }
}