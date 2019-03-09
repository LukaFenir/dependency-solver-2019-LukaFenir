package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static List<State> bruteForce(State state, List<Package> repo, FinalConstraints finalState, List<State> solutions){
        if(!state.getPackageList().isEmpty()) { //If state is empty AND couldn't find solution
            //oooo I could create my own package with a size of 1,000,000
            //Or i could use the state's inbuilt size mechanism
            repo.removeAll(state.getPackageList());
        }
        removeRecursive(state, repo, finalState, solutions);
        return solutions;
    }

    /**
     * If initial state isn't empty, try with current state, then remove one by one
     * @param state
     * @param repo
     * @param finalState
     * @param solutions
     * @return
     */
    public static List<State> removeRecursive(State state, List<Package> repo, FinalConstraints finalState, List<State> solutions){
        List<State> solutions2 = new ArrayList<>();
        solutions2 = addRecursive(state, repo, finalState, solutions);
        if(!state.getPackageList().isEmpty()){
            for(Package p : state.getPackageList()){
                //
                removeRecursive(removeFromState(state,p), addToRepo(repo,p), finalState, solutions);
            }
        }
        return solutions2;
    }

    public static List<State> addRecursive(State state, List<Package> repo, FinalConstraints finalState, List<State> solutions){
        if(!isValid(state)){
            return solutions;
        }
        /**
         * Checks if state is valid final state, then stops without checking for other solutions
         * Might there be a smaller solution if we keep checking?
         * eg. State[A1.3], but State[A1.3,A1.4] is never found
         */
        if(isFinal(state.getPackageList(), finalState)){ //If IsFinal
            //System.out.println("Solution found!");
            if(solutions.size() == (0) || state.getSize() < solutions.get(solutions.size() - 1).getSize()) {
                solutions.add(state); //Don't bother adding if previous is smaller?
            }
            return solutions;
            //print list of commands to reach
        }
        //List<Package>[] stateCopy = state;
        for(Package p : repo) {
            //solution
            addRecursive(addToState(state, p),removeFromRepo(repo, p), finalState, solutions); //Can't just add to state, need to create new object every test
        }
        //try uninstalling initial state?
        //for(Package p : )
        return solutions;
    }

    //Gotta initialise a brand new state object each time
    public static State addToState(State originalState, Package newPackage){
       State newState = new State(originalState.getPackageList(), originalState.getAccumulatedConstraints(), originalState.getSize());
        /*State newState = new State();
        newState.addPackages(originalState.getPackageList());
        newState.addConstraints(originalState.getAccumulatedConstraints());*/
        newState.addPackage(newPackage);
        return newState;
    }

    //Gotta initialise a brand new state object each time
    public static State removeFromState(State originalState, Package remPackage){
        State newState = new State(originalState.getPackageList(), originalState.getAccumulatedConstraints(), originalState.getSize());
        /*State newState = new State();
        newState.addPackages(originalState.getPackageList());
        newState.addConstraints(originalState.getAccumulatedConstraints());*/
        //remPackage.setUninstall();
        newState.removePackage(remPackage);
        return newState;
    }

    public static List<Package> removeFromRepo(List<Package> originalRepo, Package pack){
        List<Package> newRepo = new ArrayList<>();
        newRepo.addAll(originalRepo);
        newRepo.remove(pack);
        return newRepo;
    }

    public static List<Package> addToRepo(List<Package> originalRepo, Package pack){
        List<Package> newRepo = new ArrayList<>();
        newRepo.addAll(originalRepo);
        newRepo.add(pack);
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

    /**
     * State is valid if: it's empty, all dependencies are present,
     * all conflicts are absent (new and old)
     *
     * @param state State to be tested
     * @return      Is state valid
     */
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

    /**
     * Compares state with finalState
     * Does state contain at least 1 of the packages in each constraint?
     * [ +[A1,A2,A3] , +[D] ]
     * @param state         State to be tested
     * @param finalState    Constraints of a final state
     * @return              Is state a valid final state
     */
    public static boolean isFinal(List<Package> state, FinalConstraints finalState){
        //Does state contains positive, and doesn't contain negative
        for(List<Package> requiredPack : finalState.getPositivePackages()) {
            if (Collections.disjoint(state, requiredPack)) {
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

    public static State chooseSolution(List<State> possibleStates) {
        State smallestState = null;
        for(State solution : possibleStates) {
            smallestState = ((smallestState == null)||(solution.getSize() < smallestState.getSize())) ? solution : smallestState;
        }
        return smallestState; //What happens if no solutions???
    }

    /**
     * Take the chosen solution and return the JSON of commands
     * @param solution
     * @param initialState
     */
    public static String printCommands(State solution, State initialState) { //Need initial state? (for uninstall paths)
        PackageExpand expander = new PackageExpand();
        List<String> commands = expander.packagesToCommands(solution, initialState);
        String res = JSON.toJSONString(commands, true);
        return res;
    }

    public static void main(String[] args) throws IOException {
        TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
        List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
        TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
        List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
        //List<String> initial = new ArrayList<>();
        //initial.add("B=3.2"); //Artificial starting state
        List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

        // Go through each package and parse string constraints into Package references
        for(Package pack : repo) {
            pack.expandRepoConstraints(repo);
        }

        FinalConstraints finalConstraints = new FinalConstraints(constraints,repo); //is this the right structure?
        State initState = new State();
        PackageExpand expander = new PackageExpand();
        for(String init : initial){
            initState.addPackage(expander.expandInitialString(init, repo));
        }

        //List<State> solutions = removeRecursive(initState, repo, finalConstraints, new ArrayList<State>());
        //Expanded initial state, expanded repo, constraints of a final state
        List<State> solutions = bruteForce(initState, repo, finalConstraints, new ArrayList<State>());
        System.out.println(printCommands(chooseSolution(solutions), initState));
        //Return minimal solution
        }

        private static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        br.lines().forEach(line -> sb.append(line));
        return sb.toString();
    }
}