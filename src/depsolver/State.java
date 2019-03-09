package depsolver;

import java.util.ArrayList;
import java.util.List;

public class State {
    private List<Package> packageList; //list of commands?
    private List<Package> accConstraints;
    private int size;

    public State() {
        packageList = new ArrayList<>();
        accConstraints = new ArrayList<>();
        size = 0;
    }

    public State(List<Package> packages, List<Package> constraints, int size) {
        packageList = new ArrayList<>();
        accConstraints = new ArrayList<>();
        packageList.addAll(packages);
        accConstraints.addAll(constraints);
        this.size = size;
    }

    public void addPackage(Package newPackage) {
        packageList.add(newPackage);
        size += newPackage.getSize();
        List<Package> newConstraints = newPackage.getConflictsExpanded();
        for(Package constr : newConstraints) {
            //if(!accConstraints.contains(constr)){ //TODO this needs to be removed, consequences to be dealt with
                accConstraints.add(constr);
            //}
        }
    }

    public void removePackage(Package remPackage) {
        packageList.remove(remPackage);
        size += 1000000;
        List<Package> newConstraints = remPackage.getConflictsExpanded();
        for(Package constr : newConstraints) {
            accConstraints.remove(constr);
        }
    }

    public void addPackages(List<Package> newPackages) {
        packageList.addAll(newPackages);
    }

    public void addConstraints(List<Package> newConstraints) {
        accConstraints.addAll(newConstraints);
    }

    public List<Package> getPackageList() {
        return packageList;
    }

    public List<Package> getAccumulatedConstraints() {
        return accConstraints;
    }

    public int getSize() { return size; }
    //Add ability to add to packageList/accConstraint
}
