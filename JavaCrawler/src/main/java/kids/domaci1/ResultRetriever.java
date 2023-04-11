package kids.domaci1;

import kids.domaci1.config.Config;
import kids.domaci1.jobs.ScanType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ResultRetriever {

    private ThreadPoolExecutor threadPoolExecutor;


    public static Map<String, Future<List<Map<String, Integer>>>> parentAndChildrenListMap;

    public Map<String, Map<String,Integer>> inDepthFinishedCorpuses;

    public ConcurrentMap<String,Future<Map<String,Integer>>> shellFinishedCorpuses;

    private Map<String, Integer> resultOfWebSearches;
    private Map<String, Integer> resultOfFileSearches;

//    private ConcurrentMap<String, List<String>> childrenForParent;

//    public static Map<String, Future<Map<String,Integer>>> cacheForDomainAndSub; //ovde sam razmisljao listu da stavim za FUture<List<Map... ali ipak ce to biti jedan for za svaki thread;

//    public static Map<String, Future<Map<String, Integer>>>computedCorpuses;



    public ResultRetriever() {
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
        shellFinishedCorpuses = new ConcurrentHashMap<>();
        inDepthFinishedCorpuses = new HashMap<>();
        this.resultOfWebSearches = new HashMap<>();
        this.resultOfFileSearches = new HashMap<>();
//        childrenForParent = new ConcurrentHashMap<>();
    }


//    public Map<String,Integer> getResult(String query) {
//
//        cacheForDomainAndSub.put(query,threadPoolExecutor.submit((new ResultRetrieverWorker(query,parentAndChildrenListMap))));
//
//        try {
//            return cacheForDomainAndSub.get(query).get();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }

    public Map<String, Integer> getResult(String query) {
//        System.out.println("u getu smo");

        String[] typeOfFileAndPath = query.split("\\|");

        if(inDepthFinishedCorpuses.containsKey(query)){
            System.out.println("I've computed that!");
            return inDepthFinishedCorpuses.get(query);
        }

        String typeOfFile = typeOfFileAndPath[0];
        String path = typeOfFileAndPath[1];

        if(typeOfFile.equals("file")){


            System.out.println(shellFinishedCorpuses);

            System.out.println(query);

            Future<Map<String, Integer>> resultOfSearch = shellFinishedCorpuses.get(query);

            if(resultOfSearch != null){
                try {

                    Map<String, Integer> result = resultOfSearch.get();

                    inDepthFinishedCorpuses.put(query, result);

                    return result;
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("No result");
            }
        } else if(typeOfFile.equals("web")) {

            System.out.println("Path to WEB job " + path);
            List<Future<Map<String, Integer>>> sameDomainAsInputFuturesList = new ArrayList<>();
            for (ConcurrentMap.Entry<String, Future<Map<String, Integer>>> oneShellFinishedCorpus : shellFinishedCorpuses.entrySet()) {

                if (oneShellFinishedCorpus.getKey().contains(path)) {
                    System.out.println("Adding same domain " + oneShellFinishedCorpus.getKey());
                    sameDomainAsInputFuturesList.add(oneShellFinishedCorpus.getValue());
                }
            }


            Map<String, Integer> resultMap = new HashMap<>();
            List<Future<Map<String, Integer>>> resultsInFuture = new ArrayList<>();
            Future<Map<String, Integer>> resultInFuture;

//            List<Callable<Map<String, Integer>>> callableTasks = new ArrayList<>();
            for (Future<Map<String, Integer>> sameDomainEntry : sameDomainAsInputFuturesList) {
                resultInFuture = threadPoolExecutor.submit(new ResultRetrieverGetWorker(sameDomainEntry));

                resultsInFuture.add(resultInFuture);
            }

//            List<Future<Map<String, Integer>>> resultsInFuture = null;
//            try {
//                resultsInFuture = threadPoolExecutor.invokeAll(callableTasks);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            System.out.println("SVE GETOVE SMO SACEKALI I IDEMO DALJE");

            for (String key : Config.keywords) {
                resultMap.put(key, 0);
            }

            Future<Map<String, Integer>> getOverallResult = threadPoolExecutor.submit(new ResultRetrieverSumWorker(resultsInFuture,resultMap));
            try {
                Map<String, Integer> finalMap = getOverallResult.get();
                inDepthFinishedCorpuses.put(query, finalMap);
                return finalMap;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void queryResult(String query) {
//        System.out.println("u queriju smo");

        String[] typeOfFileAndPath = query.split("\\|");

//        System.out.println(shellFinishedCorpuses);
//        System.out.println(typeOfFileAndPath[1]);

        if(typeOfFileAndPath[0].equals("file") && !shellFinishedCorpuses.containsKey(query)){
            System.out.println("This corpus doesnt exist");
            return;
        }
        if (inDepthFinishedCorpuses.containsKey(query)) {
            System.out.println("I've computed that already!");
            System.out.println(inDepthFinishedCorpuses.get(query));
            return;
        } else if ((!shellFinishedCorpuses.containsKey(query))) {
            System.out.println("This task hasn't been started yet");
        }else if (shellFinishedCorpuses.get(query).isDone()) {
            System.out.println("This task hasn't been finished yet");
        } else {
            System.out.println("This task hasn't been started yet");
        }
    }
    public void queryResult1(String query) {

        String[] typeOfFileAndPath = query.split("\\|");

        if(inDepthFinishedCorpuses.containsKey(query)){
            System.out.println("I've computed that!");
            System.out.println(inDepthFinishedCorpuses.get(query));
            return ;
        }


        String typeOfFile = typeOfFileAndPath[0];
        String path = typeOfFileAndPath[1];

        if(typeOfFile.equals("file")){


            System.out.println(shellFinishedCorpuses);

            System.out.println(query);

            if(!shellFinishedCorpuses.containsKey(query)){
                System.out.println("This job hasn't been started yet.");
                return ;
            }


            Future<Map<String, Integer>> resultOfSearch = shellFinishedCorpuses.get(query);

            if(resultOfSearch != null){
                try {

                    if(resultOfSearch.isDone()) {
                        Map<String, Integer> result = resultOfSearch.get();

                        inDepthFinishedCorpuses.put(query, result);
                        System.out.println(result);
                    }else{
                        System.out.println("This job hasn't been finished yet.");
                    }

                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Result doesn't exist.");
            }
        } else if(typeOfFile.equals("web")) {

            System.out.println("Path to WEB job " + path);
            if(!shellFinishedCorpuses.containsKey(query)){
                System.out.println("This job hasn't been started yet.");
                return ;
            }
            List<Future<Map<String, Integer>>> sameDomainAsInputFuturesList = new ArrayList<>();
            for (ConcurrentMap.Entry<String, Future<Map<String, Integer>>> oneShellFinishedCorpus : shellFinishedCorpuses.entrySet()) {

                if (oneShellFinishedCorpus.getKey().contains(path)) {
                    System.out.println("Adding same domain " + oneShellFinishedCorpus.getKey());
                    sameDomainAsInputFuturesList.add(oneShellFinishedCorpus.getValue());
                }
            }


            Map<String, Integer> resultMap = new HashMap<>();
            List<Future<Map<String, Integer>>> resultsInFuture = new ArrayList<>();
            Future<Map<String, Integer>> resultInFuture;

//            List<Callable<Map<String, Integer>>> callableTasks = new ArrayList<>();
            for (Future<Map<String, Integer>> sameDomainEntry : sameDomainAsInputFuturesList) {
                resultInFuture = threadPoolExecutor.submit(new ResultRetrieverGetWorker(sameDomainEntry));

                resultsInFuture.add(resultInFuture);
            }

//            List<Future<Map<String, Integer>>> resultsInFuture = null;
//            try {
//                resultsInFuture = threadPoolExecutor.invokeAll(callableTasks);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }


            for (String key : Config.keywords) {
                resultMap.put(key, 0);
            }

            Future<Map<String, Integer>> getOverallResult = threadPoolExecutor.submit(new ResultRetrieverSumWorker(resultsInFuture, resultMap));
            try {
                Map<String, Integer> finalMap = null;
                if (getOverallResult.isDone()) {
                    finalMap = getOverallResult.get();
//                    System.out.println("TU SMO I FINAL MAP JE : + " + finalMap);
                    inDepthFinishedCorpuses.put(query, finalMap);
                    System.out.println(finalMap);
                } else {
                    System.out.println("ELSE GRANA");
                    System.out.println("This job hasn't been finished yet.");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void clearSummary(ScanType type){

        if(type.equals(ScanType.WEB)){
            resultOfWebSearches.clear();
        }else if(type.equals(ScanType.FILE)){
            resultOfFileSearches.clear();
        }
    }
    public Map<String, Integer> getSummary(ScanType type){

        if(type.equals(ScanType.WEB) && !resultOfWebSearches.isEmpty()){
            return resultOfWebSearches;
        }

        if(type.equals(ScanType.FILE) && !resultOfFileSearches.isEmpty()){
            return resultOfFileSearches;
        }

        List<Future<Map<String, Integer>>> listOfSummaryResults = new ArrayList<>();

        for(Map.Entry<String, Future<Map<String, Integer>>> queryFuturePair : shellFinishedCorpuses.entrySet()){
            if(queryFuturePair.getKey().startsWith(type.toString().toLowerCase() + "|")) {


                Future<Map<String,Integer>> oneFutureResult = threadPoolExecutor.submit(new ResultRetrieverGetWorker(queryFuturePair.getValue()));

                listOfSummaryResults.add(oneFutureResult);
            }
        }

        Map<String, Integer> resultMap = new HashMap<>();

        for(String keyword : Config.keywords) {
            resultMap.put(keyword, 0);
        }
        Future<Map<String, Integer>> getOverallResult = threadPoolExecutor.submit(new ResultRetrieverSumWorker(listOfSummaryResults,resultMap));
        try {
            if(type.equals(ScanType.WEB)) {
                resultOfWebSearches = getOverallResult.get();
//                System.out.println(resultOfWebSearches);
                return resultOfWebSearches;
            }else if(type.equals(ScanType.FILE)){
                resultOfFileSearches = getOverallResult.get();
                return resultOfFileSearches;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public Map<String, Integer> querySummary(ScanType summaryType){
        if(summaryType.equals(ScanType.FILE) && !resultOfFileSearches.isEmpty())
            return resultOfFileSearches;

        if(summaryType.equals(ScanType.WEB) && !resultOfWebSearches.isEmpty())
            return resultOfWebSearches;

        System.out.println(resultOfFileSearches);


        System.out.println("Summary not available.");
        return null;
    }

    public void addCorpusResult(String corpusName, Future<Map<String, Integer>> corpusResult){


        String nullCheck = corpusName.split("\\|")[1];
        if(!nullCheck.equals("null")) {
//            System.out.println("CORPUS NAME : " + corpusName);
            shellFinishedCorpuses.put(corpusName, corpusResult);
        }
//        System.out.println("NAME " + corpusName);

    }
//    public void addPendingQuery(String corpusName){
//        futureQueryMap.put(corpusName,null);
//    }


    public ThreadPoolExecutor getResultRetrieverThreadPool() {
        return threadPoolExecutor;
    }

//    public void addChildToParent(String parent, String child) {
//        if(parent == null){
//            return;
//        }
//        if(!childrenForParent.containsKey(parent)) {
//            childrenForParent.put(parent,new ArrayList<>());
//        }
////        System.out.println("PARENT : " + parent + " DETE : " + child);
//        List<String> children = childrenForParent.get(parent);
//        children.add(child);
//        childrenForParent.put(parent,children);
//    }

    public Map<String, Integer> getResultOfWebSearches() {
        return resultOfWebSearches;
    }

    public Map<String, Integer> getResultOfFileSearches() {
        return resultOfFileSearches;
    }
}
