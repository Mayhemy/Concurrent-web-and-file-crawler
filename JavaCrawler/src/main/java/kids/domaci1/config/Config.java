package kids.domaci1.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {
    public static String[] keywords;
    public static String file_corpus_prefix;
    public static int dir_crawler_sleep_time;
    public static int file_scanning_size_limit;
    public static int hop_count;
    public static long url_refresh_time;

    public static void loadConfigFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    switch (key) {
                        case "keywords":
                            keywords = value.split(",");
                            break;
                        case "file_corpus_prefix":
                            file_corpus_prefix = value;
                            break;
                        case "dir_crawler_sleep_time":
                            dir_crawler_sleep_time = Integer.parseInt(value);
                            break;
                        case "file_scanning_size_limit":
                            file_scanning_size_limit = Integer.parseInt(value);
                            break;
                        case "hop_count":
                            hop_count = Integer.parseInt(value);
                            break;
                        case "url_refresh_time":
                            url_refresh_time = Long.parseLong(value);
                            break;
                        default:
                            System.out.println("Unknown key: " + key);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading config file: " + e.getMessage());
        }
    }

}