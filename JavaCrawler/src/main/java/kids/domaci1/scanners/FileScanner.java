package kids.domaci1.scanners;

import java.util.concurrent.ForkJoinPool;

public class FileScanner {

    private final ForkJoinPool fileScannerThreadPool;

    public FileScanner() {
        this.fileScannerThreadPool = new ForkJoinPool();
    }

    public ForkJoinPool getFileScannerThreadPool() {
        return fileScannerThreadPool;
    }
}
