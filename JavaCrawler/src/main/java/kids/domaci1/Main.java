package kids.domaci1;

import kids.domaci1.config.Config;
import kids.domaci1.jobs.FileScanningJob;
import kids.domaci1.jobs.ScanType;
import kids.domaci1.jobs.ScanningJob;
import kids.domaci1.jobs.WebScanningJob;
import kids.domaci1.scanners.FileScanner;
import kids.domaci1.scanners.WebScanner;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static BlockingQueue<ScanningJob> jobQueue = new LinkedBlockingQueue<>();
    public static final WebScanner webScanner = new WebScanner();
    public static final FileScanner fileScanner = new FileScanner();
    public static final ResultRetriever resultRetriever = new ResultRetriever();

    public static final Validator validator = new Validator();
    static Object lock = new Object();

    public static void main(String[] args) {

        Config.loadConfigFromFile("src\\main\\java\\kids\\domaci1\\config\\config.txt");

        JobDispatcher jobDispatcher = new JobDispatcher();
        jobDispatcher.start();

        DirectoryCrawler directoryCrawler = new DirectoryCrawler();
        directoryCrawler.start();

        Scanner sc = new Scanner(System.in);

        while (true) {
            String ulaz = sc.nextLine();
            if(!validator.isValid(ulaz)) {
                continue;
            }

            String[] inputTokens;

            inputTokens = ulaz.split(" ");

            switch (inputTokens[0]) {
                case "aw" -> {
                    URL u = null;
                    try {
                        u = new URL(inputTokens[1]);
                        u.toURI();
                    } catch (MalformedURLException | URISyntaxException e) {
                        System.out.println("POGRESAN URL : " + inputTokens[1] );
                        continue;
                    }
                    ScanningJob job = new WebScanningJob(inputTokens[1], Config.hop_count);
                    jobQueue.add(job);
                }
                case "ad" -> {
                    String relativePath = "src/main/resources/";
                    directoryCrawler.getDirectoriesToScan().add(relativePath + inputTokens[1]);
                }
                case "cfs" -> {
                    resultRetriever.clearSummary(ScanType.FILE);
                    System.out.println("File summary cleared.");
                }
                case "cws" -> {
                    resultRetriever.clearSummary(ScanType.WEB);
                    System.out.println("Web summary cleared.");
                }
                case "get" -> {
                    if (inputTokens[1].equals("file|summary"))
                        System.out.println(resultRetriever.getSummary(ScanType.FILE));
                    else if (inputTokens[1].equals("web|summary"))
                        System.out.println(resultRetriever.getSummary(ScanType.WEB));
                    else {
                        System.out.println(resultRetriever.getResult(inputTokens[1]));
                        System.out.println("You can ask again.");
                    }
                }
                case "query" -> {
                    Map<String, Integer> out;
                    if (inputTokens[1].equals("file|summary")) {
                        if ((out = resultRetriever.querySummary(ScanType.FILE)) != null) {
                            System.out.println(out);
                        }
                    } else if (inputTokens[1].equals("web|summary")) {
                        if ((out = resultRetriever.querySummary(ScanType.WEB)) != null) {
                            System.out.println(out);
                        }
                    } else {
                        resultRetriever.queryResult(inputTokens[1]);
                    }
                }
            }

            if (inputTokens[0].equalsIgnoreCase("stop")) {
                System.out.println("STOP");

                ScanningJob webStopJob = new WebScanningJob(ScanType.STOP);
                jobQueue.add(webStopJob);
                ScanningJob fileStopJob = new FileScanningJob(ScanType.STOP);
                jobQueue.add(fileStopJob);

                directoryCrawler.getDirectoriesToScan().add("STOP");
                resultRetriever.getResultRetrieverThreadPool().shutdown();
                break;
            }
        }
    }
}