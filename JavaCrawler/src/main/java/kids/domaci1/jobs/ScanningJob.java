package kids.domaci1.jobs;

import java.util.Map;
import java.util.concurrent.Future;

public interface ScanningJob {

    ScanType getType();

    String getPath();

    Future<Map<String, Integer>> initiate();
}
