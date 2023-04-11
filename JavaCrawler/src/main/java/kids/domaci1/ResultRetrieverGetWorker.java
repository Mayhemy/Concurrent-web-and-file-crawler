package kids.domaci1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class ResultRetrieverGetWorker implements Callable<Map<String,Integer>> {


    private Future<Map<String, Integer>> valueForOneSubdomain;



    public ResultRetrieverGetWorker(Future<Map<String, Integer>> valueForOneSubdomain) {
        this.valueForOneSubdomain = valueForOneSubdomain;
    }


    @Override
    public Map<String, Integer> call() throws Exception {


        System.out.println("waiting on value map for subdomain");
        Map<String, Integer> temp = valueForOneSubdomain.get();
        return temp;
//        if(temp == null) {
//            return resultMap;
//        }
//
//        for(Map.Entry<String, Integer> entry : temp.entrySet()){
//            resultMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
//        }
//        return null;
//        String[] queryComponents = query.split("\\|");
//
//        if (ResultRetriever.inDepthFinishedCorpuses.containsKey(query)) {
//            System.out.println("Iz memorije");
//            return ResultRetriever.inDepthFinishedCorpuses.get(query);
//        }
//
//
//        if (queryComponents[0].equals("file")) {
//
//            System.out.println(ResultRetriever.shellFinishedCorpuses);
//
//            String path = queryComponents[1];
//            Future<Map<String, Integer>> potentialResult = ResultRetriever.shellFinishedCorpuses.get(path);
//
//            if (potentialResult != null) {
//                try {
//
//                    Map<String, Integer> result = ResultRetriever.shellFinishedCorpuses.get(queryComponents[1]).get();
//
//                    ResultRetriever.inDepthFinishedCorpuses.put(query, result);
//
//                    return result;
//                } catch (InterruptedException | ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            } else {
//                System.out.println("Ne postoji rezultat");
//            }
//        } else if (queryComponents[0].equals("web")) {
//            Map<String, Integer> result = traverseMap(parentChildrenMap, queryComponents[1]);
//
//            ResultRetriever.inDepthFinishedCorpuses.put(query,result);
//
//            return result;
//        }
//        return null;
    }


//    private static Map<String, Integer> traverseMap(Map<String, List<String>> map, String path) {
//        List<String> children = map.get(path);
//        Map<String, Integer> result = new HashMap<>();
//
//        if(children != null){
//            for (String child : children) {
//                if (!map.containsKey(child)) {
//
//                    Future<Map<String, Integer>> resultOfChild = ResultRetriever.shellFinishedCorpuses.get(child);
//                    try {
//                        Map<String, Integer> stringIntegerMap = resultOfChild.get();
//                        for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
//                            String key = entry.getKey();
//                            int value = entry.getValue();
//                            result.put(key, result.getOrDefault(key, 0) + value);
//                        }
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    } catch (ExecutionException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                } else {
//                    Map<String, Integer> childResult = traverseMap(map, child);
//                    for (Map.Entry<String, Integer> entry : childResult.entrySet()) {
//                        String key = entry.getKey();
//                        int value = entry.getValue();
//                        result.put(key, result.getOrDefault(key, 0) + value);
//                    }
//                }
//            }
//        }
//
//
//        Future<Map<String, Integer>> resultOfPath = ResultRetriever.shellFinishedCorpuses.get(path);
//        try {
//            System.out.println(path);
//            Map<String, Integer> stringIntegerMap = resultOfPath.get();
//            for (Map.Entry<String, Integer> entry : stringIntegerMap.entrySet()) {
//                String key = entry.getKey();
//                int value = entry.getValue();
//                result.put(key, result.getOrDefault(key, 0) + value);
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//
//        return result;
//    }
}
