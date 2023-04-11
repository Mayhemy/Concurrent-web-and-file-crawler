package kids.domaci1.scanners;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WebScanner {

    private final ThreadPoolExecutor webScannerThreadPool;

    public WebScanner() {
        this.webScannerThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    public ThreadPoolExecutor getWebScannerThreadPool() {
        return webScannerThreadPool;
    }
}
