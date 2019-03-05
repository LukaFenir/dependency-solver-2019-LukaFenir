package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void bruteForce(List<Package>[] state, List<Package> repo, FinalConstraints finalState){
        if(!isValid(state)){
            return;
        }
        if(isFinal(state[0], finalState)){ //If IsFinal
            System.out.println("Solution found!");
        }
        //List<Package>[] stateCopy = state;
        for(Package p : repo) {
            bruteForce(addToState(state, p),removeFromRepo(repo, p), finalState);
        }
    }

    //Gotta initialise a brand new state object each time
    public static List<Package>[] addToState(List<Package>[] originalState, Package newPackage){
        List<Package>[] newState = new List[2];
        newState[0] = new ArrayList<>();
        newState[1] = new ArrayList<>();
        newState[0].addAll(originalState[0]);
        newState[0].add(newPackage);
        newState[1].addAll(originalState[1]);
        newState[1].addAll(newPackage.getConflictsExpanded());
        return newState;
    }

    public static List<Package> removeFromRepo(List<Package> originalRepo, Package pack){
        List<Package> newRepo = new ArrayList<>();
        newRepo.addAll(originalRepo);
        newRepo.remove(pack);
        return newRepo;
    }

    /*
    search(x):
  if not valid(x) return
  if x seen, return
  make x seen
  if final(x):
    solution found!
  for each package p in repo:
    obtain y from x by flipping state of p (installed<->uninstalled)
    search(y)
     */

    //[List<Package> packages, List<Package> grouped conflicts]
    public static boolean isValid(List<Package>[] state){
        if(state[0].isEmpty()){
            return true;
        }
        Package x = state[0].get(state[0].size()-1); //last element
        List<List<Package>> deps = x.getDependsExpanded(); // [B3.2,C][D]
        if(!deps.isEmpty()) {
            //depsAreInState
            //for each List, is at least 1 package in state?
            for(List<Package> dep : deps){
                if(!atLeastOne(state[0], dep)) { //If even one dep is not satisfied
                    return false;
                }
            }
        }
        List<Package> confs = x.getConflictsExpanded();
        if(!confs.isEmpty()) {
            for(Package pack : confs){
                if(state[0].contains(pack)){
                    return false; //Conflicting package is in state
                }
            }
        }
        if(!state[1].isEmpty()){
            if(state[1].contains(state[0].get(state[0].size()-1))){
                return false;
            }
        }
        return true;
    }

    public static boolean isFinal(List<Package> state, FinalConstraints finalState){
        //Does state contains positive, and doesn't contain negative
        for(Package requiredPack : finalState.getPositivePackages()) {
            if(!state.contains(requiredPack)) {
                return false;
            }
        }
        return true;
    }

    public static boolean atLeastOne(List<Package> packages, List<Package> dep){
        for(Package pack : dep){
            if(packages.contains(pack)){
                return true; //Package is in state
            }
        }
        return false; //No package was in state
    }

    public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    //List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
        List<String> initial = new ArrayList<>();
        initial.add("A=2.01");
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);



    List<Package> initialState = new ArrayList<>();

    // Go through each package and parse string constraints into Package references
    for(Package pack : repo) {
        pack.expandRepoConstraints(repo);
    }

    PackageExpand expander = new PackageExpand();
    for(String init : initial){
        initialState.add(expander.expandInitialString(init, repo));
    }

    FinalConstraints finalConstraints = new FinalConstraints(constraints,repo); //is this the right structure?

    //initialState = expandInitialState(initial, repo);

    /////// Testing validity
    List<Package>[] testState = new List[2];
    testState[0] = new ArrayList<>();
    testState[1] = new ArrayList<>();
    //List<Package> testState = new ArrayList<>();
    /*testState.add(repo.get(3)); //C
    testState.add(repo.get(4)); //D
    testState.add(repo.get(0)); //A -> C/B3.2 and D*/
    testState[0].add(repo.get(3)); //C
        testState[1].addAll(repo.get(3).getConflictsExpanded());
    testState[0].add(repo.get(2)); //B3.0
        testState[1].addAll(repo.get(2).getConflictsExpanded());
    Boolean x = isValid(testState);
    ////////
    State initState = new State();
    List<Package>[] initStateAndConstraints= new List[2];
    initStateAndConstraints[0] = new ArrayList<>();
    initStateAndConstraints[1] = new ArrayList<>();

    //initStateAndConstraints = [State list, Constraints list]
    //bruteForce(initState, repo, finalConstraints); // Change bruteForce() to accept State instead of List[2]
    bruteForce(initStateAndConstraints, repo, finalConstraints);


    //Take a final constraint, check it for children

    // CHANGE CODE BELOW:
    // using repo, initial and constraints, compute a solution and print the answer
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
