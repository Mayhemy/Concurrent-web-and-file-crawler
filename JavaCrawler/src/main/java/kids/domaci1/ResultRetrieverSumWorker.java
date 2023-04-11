package kids.domaci1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ResultRetrieverSumWorker implements Callable<Map<String,Integer>> {

    private List<Future<Map<String, Integer>>> resultsOfGet;

    private Map<String,Integer> resultMap;


    public ResultRetrieverSumWorker(List<Future<Map<String, Integer>>> resultsOfGet, Map<String,Integer> resultMap) {
        this.resultsOfGet = resultsOfGet;
        this.resultMap = resultMap;
    }


    @Override
    public Map<String, Integer> call() throws Exception {
        for(Future<Map<String, Integer>> future : resultsOfGet){

            try {

                Map<String, Integer> result = future.get();

//                System.out.println("ADDING UP result of one entry " + result.get("one"));

                resultMap.replaceAll((key, value) -> value + result.get(key));

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return resultMap;
    }

}
