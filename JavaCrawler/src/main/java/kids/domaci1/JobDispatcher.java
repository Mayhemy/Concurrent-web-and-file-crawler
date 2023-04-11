package kids.domaci1;

import kids.domaci1.config.Config;
import kids.domaci1.jobs.FileScanningJob;
import kids.domaci1.jobs.ScanType;
import kids.domaci1.jobs.ScanningJob;
import kids.domaci1.jobs.WebScanningJob;

import java.util.ArrayList;
import java.util.List;

public class JobDispatcher extends Thread  {

    List<String> visitedCorpuses = new ArrayList<>();
    long expirationTime;

    public JobDispatcher() {
        this.expirationTime = System.currentTimeMillis() + Config.url_refresh_time;
    }

    @Override
    public void run() {

        boolean shouldStop = false;
        int numberOfIterations = 0;

        while(!shouldStop) {

//            System.out.println("main velicina : " + Main.jobQueue.size());
//            System.out.println("velicina tpa : " + Main.resultRetriever.getResultRetrieverThreadPool().getActiveCount());
            try {
//                System.out.println("PRE");
                ScanningJob job = Main.jobQueue.take();
//                System.out.println("POSLE");

                if(System.currentTimeMillis() >= expirationTime) {
                    visitedCorpuses.clear();
                    expirationTime = System.currentTimeMillis() + Config.url_refresh_time;
                }

                if(job.getType().equals(ScanType.STOP))
                    numberOfIterations++;

                if(numberOfIterations == 2)
                    shouldStop = true;

                if(job.getType().equals(ScanType.FILE) || !visitedCorpuses.contains(job.getPath())) {

                    String query = job.getPath();

                    if(job.getPath() != null) {

                        visitedCorpuses.add(job.getPath());
                        String[] queryComponents = query.split("\\\\");
                        query = queryComponents[queryComponents.length-1];
                    }

                    if(job instanceof WebScanningJob)
                        Main.resultRetriever.addCorpusResult("web|" + query, job.initiate());
                    else if (job instanceof FileScanningJob) {
//                        System.out.println("DA VIDIMO IME FAJLA : file|" + query);
                        Main.resultRetriever.addCorpusResult("file|" + query, job.initiate());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
