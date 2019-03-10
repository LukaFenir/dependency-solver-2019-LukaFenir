package depsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PackageExpand {
    public PackageExpand(){

    }

    public List<Package> expandPackageString(String depStr, List<Package> raw_repo){
        List<Package> dependencyList = new ArrayList<>();
        Pattern r = Pattern.compile("([.+a-zA-Z0-9-]+)(?:(>=|<=|=|<|>)(\\d+(?:\\.\\d+)*))?");
        Matcher m = r.matcher(depStr);
        m.find();
        PackageConstraint dependency = new PackageConstraint(m.group(1),m.group(2),m.group(3));
        for (Package p : raw_repo) { //get deps from raw_repo by name, may use a HashMap for faster search
            if((p.getName().equals(dependency.getName())) && ((dependency.getOperator().equals("")) || dependency.correctVersion(p.getVersion()))) {
                dependencyList.add(p);
                // Added to deps already,  pop from raw_repo, reduces iteration over repo
            }
        }
        return dependencyList;
    }

    public Package expandInitialString(String depStr, List<Package> raw_repo){
        Pattern r = Pattern.compile("([.+a-zA-Z0-9-]+)(?:(>=|<=|=|<|>)(\\d+(?:\\.\\d+)*))?");
        Matcher m = r.matcher(depStr);
        m.find();
        PackageConstraint dependency = new PackageConstraint(m.group(1),m.group(2),m.group(3));
        for (Package p : raw_repo) { //get deps from raw_repo by name, may use a HashMap for faster search
            if((p.getName().equals(dependency.getName())) && ((dependency.getOperator().equals("")) || dependency.correctVersion(p.getVersion()))) {
                return p;
                // Added to deps already,  pop from raw_repo, reduces iteration over repo
            }
        }
        return new Package(); //TODO erm, if not in repo??
    }

    public List<String> packagesToCommands(State state, State initialState) {
        List<String> commands = new ArrayList<>();
        //compare state0 and initial0, if different, uninstall initial0
        List<Package> packages = state.getPackageList();
        List<Package> initialPackages = initialState.getPackageList();
        for(int i = 0; i < initialState.getPackageList().size(); i++){
            if(packages.get(0) == initialState.getPackageList().get(i)){
                //commands.add("+" + packages.get(0).getName() + "=" + packages.get(0).getVersion());
                packages.remove(0);
            }
            else {
                commands.add("-" + initialPackages.get(i).getName() + "=" + initialPackages.get(i).getVersion());
            }
        }
        for(Package pack : packages) {
            commands.add("+" + pack.getName() + "=" + pack.getVersion()); //How to get from initial state to state
        }
        return commands;
    }
}



class PackageConstraint {

    private String name;
    private String operator;
    private String version;

    public PackageConstraint(String name, String operator, String version) {
        this.name = name;
        if(operator == null){
            this.operator = "";
            this.version = "";
        }
        else {
            this.operator = operator;
            this.version = version;
        }
    }

    public String getName() {
        return name;
    }

    public String getOperator() {
        return operator;
    }

    public String getVersion() {
        return version;
    }

    public Boolean correctVersion(String comparison) {
        int compValue = compareVersion(comparison);
        switch(operator){
            case("<="): return (compValue == 0)||(compValue == -1);
            case("<"): return (compValue == -1);
            case(">="): return (compValue == 0)||(compValue == 1);
            case(">"): return (compValue == 1);
            case("="): return (compValue == 0);
        }
        return false;
    }

    // 0:equal to constraint, -1:smaller than constraint, 1: bigger than constraint
    // From https://www.programcreek.com/2014/03/leetcode-compare-version-numbers-java/
    public int compareVersion(String compareVersion) {
        String[] compareArr = compareVersion.split("\\.");
        String[] versionArr = version.split("\\.");

        int i=0;
        while(i<compareArr.length || i<versionArr.length){
            if(i<compareArr.length && i<versionArr.length){
                if(Integer.parseInt(compareArr[i]) < Integer.parseInt(versionArr[i])){ return -1; }
                else if(Integer.parseInt(compareArr[i]) > Integer.parseInt(versionArr[i])){ return 1; }
            } else if(i<compareArr.length){
                if(Integer.parseInt(compareArr[i]) != 0){ return 1; }
            } else if(i<versionArr.length){
                if(Integer.parseInt(versionArr[i]) != 0){ return -1; }
            }
            i++;
        }
        return 0;
    }
}

public class Package {
    private String name;
    private String version;
    private Integer size;
    private List<List<String>> depends = new ArrayList<>();
    private List<String> conflicts = new ArrayList<>();
    private List<List<Package>> dependsExpanded = new ArrayList<>();
    private List<Package> conflictsExpanded = new ArrayList<>();
    private boolean install = true;

    public String getName() { return name; }
    public String getVersion() { return version; }
    public Integer getSize() { return size; }
    public List<List<String>> getDepends() { return depends; }
    public List<String> getConflicts() { return conflicts; }
    public List<List<Package>> getDependsExpanded() { return dependsExpanded; } //Could remove these two? Won't need strings later
    public List<Package> getConflictsExpanded() { return conflictsExpanded; }
    public boolean installPackage(){ return install; }
    // Used by JSON parser
    public void setName(String name) { this.name = name; }
    public void setVersion(String version) { this.version = version; }
    public void setSize(Integer size) { this.size = size; }
    public void setDepends(List<List<String>> depends) { this.depends = depends; }
    public void setConflicts(List<String> conflicts) { this.conflicts = conflicts; }
    public void addDependsExpanded(List<Package> deps) { dependsExpanded.add(deps); }
    public void addConflictsExpanded(List<Package> conf) { this.conflictsExpanded = conf; }
    public void setUninstall() { install = false; }

    public void expandRepoConstraints(List<Package> raw_repo) {
        for (List<String> dependencies : getDepends()) {
            addDependsExpanded(expandPackageList(dependencies, raw_repo));
        }
        addConflictsExpanded(expandPackageList(conflicts, raw_repo));
    }

    //Input list of Strings and list of Packages (repo)
    //Outputs a list of Packages
    private List<Package> expandPackageList(List<String> deps, List<Package> raw_repo){
        List<Package> dependencyList = new ArrayList<>();
        for (String dep : deps) {
            dependencyList.addAll(expandPackageString(dep, raw_repo));
        }
        return dependencyList;
    }

    public List<Package> expandPackageString(String depStr, List<Package> raw_repo){
        List<Package> dependencyList = new ArrayList<>();
        Pattern r = Pattern.compile("([.+a-zA-Z0-9-]+)(?:(>=|<=|=|<|>)(\\d+(?:\\.\\d+)*))?");
        Matcher m = r.matcher(depStr);
        m.find();
        PackageConstraint dependency = new PackageConstraint(m.group(1),m.group(2),m.group(3));
        for (Package p : raw_repo) { //get deps from raw_repo by name, may use a HashMap for faster search
            if((p.getName().equals(dependency.getName())) && ((dependency.getOperator().equals("")) || dependency.correctVersion(p.getVersion()))) {
                dependencyList.add(p);
                // Added to deps already,  pop from raw_repo, reduces iteration over repo
            }
        }
        return dependencyList;
    }
}
