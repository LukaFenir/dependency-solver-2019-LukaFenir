package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<State> bruteForce(State state, List<Package> repo, FinalConstraints finalState, List<State> solutions){
        if(!isValid(state)){
            return solutions;
        }
        if(isFinal(state.getPackageList(), finalState)){ //If IsFinal
            System.out.println("Solution found!");
            solutions.add(state);
            return solutions;
            //print list of commands to reach
        }
        //List<Package>[] stateCopy = state;
        for(Package p : repo) {
            //solution
            bruteForce(addToState(state, p),removeFromRepo(repo, p), finalState, solutions); //Can't just add to state, need to create new object every test
        }
        return solutions;
    }

    //Gotta initialise a brand new state object each time
    public static State addToState(State originalState, Package newPackage){
       State newState = new State(originalState.getPackageList(), originalState.getAccumulatedConstraints());
        /*State newState = new State();
        newState.addPackages(originalState.getPackageList());
        newState.addConstraints(originalState.getAccumulatedConstraints());*/
        newState.addPackage(newPackage);
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
    //State is not empty, New package dependencies are met, New package does not conflict with current packages
    public static boolean isValid(State state){
        if(state.getPackageList().isEmpty()){
            return true;
        }
        Package lastPackage = state.getPackageList().get(state.getPackageList().size()-1); //last element
        List<List<Package>> deps = lastPackage.getDependsExpanded(); // [B3.2,C][D]
        if(!deps.isEmpty()) {
            //depsAreInState
            //for each List, is at least 1 package in state?
            for(List<Package> dep : deps){
                if(!atLeastOne(state.getPackageList(), dep)) { //If even one dep is not satisfied
                    return false;
                }
            }
        }
        List<Package> confs = lastPackage.getConflictsExpanded();
        if(!confs.isEmpty()) {
            for(Package pack : confs){
                if(state.getPackageList().contains(pack)){
                    return false; //Conflicting package is in state
                }
            }
        }
        if(!state.getAccumulatedConstraints().isEmpty()){
            if(state.getAccumulatedConstraints().contains(state.getPackageList().get(state.getPackageList().size()-1))){ //fix this
                return false;
            }
        }
        return true;
    }

    public static boolean isFinal(List<Package> state, FinalConstraints finalState){
        //Does state contains positive, and doesn't contain negative
        for(Package requiredPack : finalState.getPositivePackages()) {
            if (!state.contains(requiredPack)) {
                return false;
            }
        }
        for(Package refusedPack : finalState.getNegativePackages()) {
            if(state.contains(refusedPack)) {
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
    //initial.add("A=2.01"); Artificial starting state
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

    // Go through each package and parse string constraints into Package references
    for(Package pack : repo) {
        pack.expandRepoConstraints(repo);
    }

    /**
     * // Expand the initial state string to Packages
    PackageExpand expander = new PackageExpand();
    for(String init : initial){
        initialState.add(expander.expandInitialString(init, repo));
    }*/

    FinalConstraints finalConstraints = new FinalConstraints(constraints,repo); //is this the right structure?
    State initState = new State();
    PackageExpand expander = new PackageExpand();
    for(String init : initial){
        initState.addPackage(expander.expandInitialString(init, repo));
    }

    List<State> solutions = bruteForce(initState, repo, finalConstraints, new ArrayList<State>());
    //Return minimal soluti
        int sssss = 0;
    int ss = 0;

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
