package backend;

import java.io.*;
import java.util.*;

public class CsvUtil {
    public static List<String[]> read(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return data;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        }
        return data;
    }

    public static void write(String filePath, List<String[]> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }
} 