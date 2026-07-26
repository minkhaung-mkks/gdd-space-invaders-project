package gdd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LevelLoader {

    public LevelLoader() {
    }

    public static int[][] loadGrid(String filePath) {

        List<int[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {

                // Skip comment and blank lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] values = line.split(",");
                int[] row = new int[values.length];

                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                }

                rows.add(row);
            }

        } catch (Exception e) {
            System.err.println("Error reading grid file " + filePath + ": " + e.getMessage());
        }

        return rows.toArray(new int[0][]);
    }

    public static HashMap<Integer, SpawnDetails> loadSpawns(String filePath) {

        HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {

                // Skip comment and blank lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] values = line.split(",");

                int frame = Integer.parseInt(values[0]);
                String type = values[1];
                int x = Integer.parseInt(values[2]);
                int y = Integer.parseInt(values[3]);

                spawnMap.put(frame, new SpawnDetails(type, x, y));
            }

        } catch (Exception e) {
            System.err.println("Error reading spawn file " + filePath + ": " + e.getMessage());
        }

        return spawnMap;
    }
}
