package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void bruteForce(){

    }

    public static boolean isValid(List<Package> state){
        if(state.isEmpty()){
            return true;
        }
        Package x = state.get(state.size()-1); //last element
        List<List<Package>> deps = x.getDependsExpanded(); // [B3.2,C][D]
        if(!deps.isEmpty()) {
            //depsAreInState
            //for each List, is at least 1 package in state?
            for(List<Package> dep : deps){
                if(!atLeastOne(state, dep)) { //If even one dep is not satisfied
                    return false;
                }
            }
        }
        List<Package> confs = x.getConflictsExpanded();
        if(!confs.isEmpty()) {
            for(Package pack : confs){
                if(state.contains(pack)){
                    return false; //Conflicting package is in state
                }
            }
        }
        return true;
    }

    public static boolean atLeastOne(List<Package> state, List<Package> dep){
        for(Package pack : dep){
            if(state.contains(pack)){
                return true; //Package is in state
            }
        }
        return false; //No package was in state
    }

    public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);



    List<Package> initialState = new ArrayList<>();

    // Go through each package and parse string constraints into Package references
    for(Package pack : repo) {
        pack.expandRepoConstraints(repo);
    }
    List<Package> testState = new ArrayList<>();
    /*testState.add(repo.get(3)); //C
    testState.add(repo.get(4)); //D
    testState.add(repo.get(0)); //A -> C/B3.2 and D*/
    testState.add(repo.get(3)); //C
    testState.add(repo.get(2)); //B3.0
    Boolean x = isValid(testState);

    FinalConstraints finalConstraints = new FinalConstraints(constraints,repo);

    //Take a final constraint, check it for children

    // CHANGE CODE BELOW:
    // using repo, initial and constraints, compute a solution and print the answer
    System.out.println("I like bananas");
    for (Package p : repo) {
      System.out.printf("package %s version %s\n", p.getName(), p.getVersion());
      for (List<String> clause : p.getDepends()) {
        System.out.printf("  dep:");
        for (String q : clause) {
          System.out.printf(" %s", q);
        }
        System.out.printf("\n");
      }
    }

    }




    private static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
    }
}
