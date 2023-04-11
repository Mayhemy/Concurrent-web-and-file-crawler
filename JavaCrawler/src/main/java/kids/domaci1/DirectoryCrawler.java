package kids.domaci1;

import kids.domaci1.config.Config;
import kids.domaci1.jobs.FileScanningJob;
import kids.domaci1.jobs.ScanningJob;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class DirectoryCrawler extends Thread {

    private ConcurrentLinkedQueue<String> directoriesToScan;
    private Map<String, Long> pathToFileAndLastModified;
    private final String corpusPrefix;
    private final long pauseDuration;

    public DirectoryCrawler() {
        this.corpusPrefix = Config.file_corpus_prefix;
        this.pauseDuration = Config.dir_crawler_sleep_time;
        this.pathToFileAndLastModified = new HashMap<>();
        this.directoriesToScan = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        boolean toBreak = false;
        try {
            while (true) {
                for (String directory : directoriesToScan) {
                    traverseDirectory(new File(directory));
                    if(directory.equalsIgnoreCase("STOP")){
                        toBreak = true;
                        break;
                    }
                }
                if(toBreak){
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(pauseDuration);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void traverseDirectory(File directory) throws IOException {
        boolean filesWereModified = false;
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        boolean hasNewJob = false;
        for (File file : files) {
//            int lengthOfFolder = 0;
            if (file.isDirectory() && file.getName().startsWith(corpusPrefix)) {
                // corpus - create new job
                for(File txtFile: file.listFiles()){
//                    lengthOfFolder += txtFile.length()-1; // zbog EOF oduzimamo 1
                    if(txtFile.getName().endsWith(".txt")){
                        if(pathToFileAndLastModified.get(txtFile.getPath())==null || (pathToFileAndLastModified.get(txtFile.getPath())!=null && !pathToFileAndLastModified.get(txtFile.getPath()).equals(txtFile.lastModified()))){
                            filesWereModified = true;
                            pathToFileAndLastModified.put(txtFile.getPath(), txtFile.lastModified());
                        }
                    }
                }
                if(filesWereModified) {
//                    if(file.getPath().equalsIgnoreCase("src\\main\\resources\\data\\corpus_sagan"))
//                    System.out.println("VELICINA FOLDERA : " + lengthOfFolder);
//                    ScanningJob fileJob = new FileScanningJob(file.getPath(), 0, lengthOfFolder);
                    ScanningJob fileJob = new FileScanningJob(file.getPath());

                    Main.jobQueue.add(fileJob);

//                    System.out.println("VALJDA SE ADDOVAO");
//                    System.out.println("QUERY " + fileJob.getQuery() + " TIP " +fileJob.getType());
                    hasNewJob = true;
                }
            }
        }
        if (hasNewJob) {
            return;
        }
        for (File subdirectory : files) {
            if (subdirectory.isDirectory()) {
                traverseDirectory(subdirectory);
            }
        }
    }

    public ConcurrentLinkedQueue<String> getDirectoriesToScan() {
        return directoriesToScan;
    }

}