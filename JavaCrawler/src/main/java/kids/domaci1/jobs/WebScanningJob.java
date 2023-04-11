package kids.domaci1.jobs;

import kids.domaci1.Main;
import kids.domaci1.config.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class WebScanningJob implements ScanningJob, Callable<Map<String, Integer>> {

    private ScanType type;
    private String path;
    private int hops;

    private WebScanningJob parentJob;


    public WebScanningJob(String path, int hops) {
        this.type = ScanType.WEB;
        this.path = path;
        this.hops = hops;
    }

    public WebScanningJob(ScanType type) {
        this.type = type;
    }

    public WebScanningJob(String path, int hops, WebScanningJob parentJob) {
        this.type = ScanType.WEB;
        this.path = path;
        this.hops = hops;
    }

    @Override
    public Map<String, Integer> call() {


//        if(parentJob != null)
//            System.out.println("Starting web scan for web|" + query + " From parent: " + parentJob.getQuery());
//        else
//        System.out.println("Starting web scan for web|" + path);

        Map<String, Integer> keywordCounts = new HashMap<>();

        Map<String, Integer> emptyMap = new HashMap<>();

        for(String s : Config.keywords) {
            emptyMap.put(s, 0);
        }

        Document doc;

        try {
            doc = Jsoup.connect(path).get();

            Elements links = doc.select("a[href]");
            Element body = doc.body();
            String bodyText = body.text();


            for (Element link : links) {
//                URL u = null;
//                try {
//                    if(link.attr("abs:href").contains("#"))
//                        continue;
//                    u = new URL(link.attr("abs:href"));
//                    u.toURI();
//                } catch (MalformedURLException | URISyntaxException e) {
//                    System.out.println("POGRESAN URL : " + link.attr("abs:href") );
//                    continue;
//                } // ovo za test otkomentarisati ali generalno ne moze lepo da se testira sa ovim

                for (String keyword : Config.keywords) {
                    int count = countOccurrences(bodyText, keyword);
                    keywordCounts.put(keyword, count);
                }

                if (hops != 0) {

//                    System.out.println("Adding web job : " + link.attr("abs:href"));

                    ScanningJob webJob = new WebScanningJob(link.attr("abs:href"), hops - 1, this);

//                    synchronized (this) {
//                        Main.resultRetriever.addChildToParent(path, link.attr("abs:href"));
//                        Main.jobQueue.add(webJob);
//                    }
                    Main.jobQueue.add(webJob);
                    //

                }
            }

            //TODO ne kreira
//            System.out.println("Found " + keywordCounts + " for " + query);

            if(keywordCounts.isEmpty())
                return emptyMap;

            return keywordCounts;
        } catch (IOException e) {
//            System.out.println("Failed to connect to: " + query);
        }

        return emptyMap;
    }

    public static int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            if(text.charAt(index - 1) != ' '){
                while(text.charAt(index) != ' '){
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
    public Future<Map<String, Integer>> initiate() {

        if(this.type.equals(ScanType.STOP)) {
            Main.webScanner.getWebScannerThreadPool().shutdown();
            return null;
        }

        return Main.webScanner.getWebScannerThreadPool().submit(this);
    }

    @Override
    public ScanType getType() {
        return this.type;
    }
    @Override
    public String getPath() {
        return this.path;
    }

    public WebScanningJob getParentJob() {
        return parentJob;
    }
}
