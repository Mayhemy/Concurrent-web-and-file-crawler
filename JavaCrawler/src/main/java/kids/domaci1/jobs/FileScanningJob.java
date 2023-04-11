package kids.domaci1.jobs;

import kids.domaci1.Main;
import kids.domaci1.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class FileScanningJob extends RecursiveTask<Map<String, Integer>> implements ScanningJob {

    private ScanType type;
    private String path;
    private long start ;
    private long stop ;
    private String content;

    public FileScanningJob(String query) throws IOException {
        this.type = ScanType.FILE;
        this.path = query;
        this.start = 0;
        this.stop = calculateTextFilesLenght(query);
    }

    public FileScanningJob(String content, long start, long stop) {
        this.content = content;
        this.start = start;
        this.stop = stop;
    }

    public FileScanningJob(ScanType type) {
        this.type = type;
    }

    public long calculateTextFilesLenght(String query) throws IOException {
        File f = new File(query);
        content = "";
        for (File txtFile : f.listFiles()) {
            if(txtFile.getName().endsWith(".txt"))
                content = content.concat(Files.readString(txtFile.toPath(),StandardCharsets.UTF_8)); // access denied exception
            else{
                System.out.println("PROBLEMATICAN FAJL " + txtFile.getName());
            }
        }

        return content.length();
    }

    public static int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            if(text.charAt(index - 1) != ' '){
                while(index < text.length() && text.charAt(index) != ' '){
                    index++;
                }
                continue;
            }
            count++;
            index += pattern.length();
        }
        return count;
    }

    @Override
    public ScanType getType() {
        return this.type;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Future<Map<String, Integer>> initiate() {

        if(this.type.equals(ScanType.STOP)) {
            Main.fileScanner.getFileScannerThreadPool().shutdown();
            return null;
        }

        return Main.fileScanner.getFileScannerThreadPool().submit(this);
    }

    @Override
    protected Map<String, Integer> compute() {

        if(path != null)
            System.out.println("Starting file scan for file|" + path);

        Map<String, Integer> keywordOccurrences = new HashMap<>();

        if (stop - start < Config.file_scanning_size_limit) {
            try {
                for (String searchWord : Config.keywords) {
                    keywordOccurrences.put(searchWord, countOccurrences(content.substring((int) start, (int) stop ), searchWord));
                }
            } catch (Exception e1){
//                System.out.println("START : " + start + " STOP : "+stop);
//                System.out.println(Arrays.toString(e1.getStackTrace()));
                throw new StringIndexOutOfBoundsException();
            }
        } else {
            long mid = ((stop - start) / 2) + start;

            FileScanningJob left = new FileScanningJob(content, start, mid);
            FileScanningJob right = new FileScanningJob(content, mid, stop);

            left.fork();

            HashMap<String, Integer> resultOfRightSearch = (HashMap<String, Integer>) right.compute();
            HashMap<String, Integer> resultOfLeftSearch = (HashMap<String, Integer>) left.join();

            for (String key : resultOfRightSearch.keySet()) {
                keywordOccurrences.put(key, keywordOccurrences.getOrDefault(key, 0) + resultOfRightSearch.get(key));
            }
            for (String key : resultOfLeftSearch.keySet()) {
                keywordOccurrences.put(key, keywordOccurrences.getOrDefault(key, 0) + resultOfLeftSearch.get(key));
            }

        }
//        for(Map.Entry<String,Integer> keyword: keywordOccurrences.entrySet()){
//            System.out.println("REC : " + keyword.getKey() + "         BROJ POJAVLJIVANJA : " + keyword.getValue() + " " +Thread.currentThread() + " PUTANJA: " + query);
//        }
        return keywordOccurrences;
    }
}
