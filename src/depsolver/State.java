/**
 * Commands(Instruction, Package) requires getting a list of packages each time.
 * Why not keep packageList and commandList together?
 */
package depsolver;

import java.util.ArrayList;
import java.util.List;

enum Instruction {
    INSTALL,
    UNINSTALL
}

class Command {

    private Instruction instruction;
    private Package pack;

    public Command(Instruction instruction, Package pack){
        this.instruction = instruction;
        this.pack = pack;
    }

    public Instruction getInstruction(){
        return instruction;
    }

    public Package getPackage(){
        return pack;
    }

}

public class State {
    private List<Package> packageList; //list of commands?
    private List<Command> commandList;
    private List<Package> accConstraints;
    private int size;

    public State() {
        packageList = new ArrayList<>();
        commandList = new ArrayList<>();
        accConstraints = new ArrayList<>();
        size = 0;
    }

    /**
     * For checking new states
     */
    public State(List<Package> packages, List<Command> commands, List<Package> constraints, int size) {
        packageList = new ArrayList<>();
        commandList = new ArrayList<>();
        accConstraints = new ArrayList<>();
        packageList.addAll(packages);
        commandList.addAll(commands);
        accConstraints.addAll(constraints);
        this.size = size;
    }

    /**
     * Minus commands
     * @param newPackage
     */
    public void addPackage(Package newPackage){
        packageList.add(newPackage);
        size += newPackage.getSize();
        List<Package> newConstraints = newPackage.getConflictsExpanded();
        for(Package constr : newConstraints) {
            //if(!accConstraints.contains(constr)){ //TODO this needs to be removed, consequences to be dealt with
            accConstraints.add(constr);
            //}
        }
    }

    public void installPackage(Package newPackage) {
        packageList.add(newPackage);
        commandList.add(new Command(Instruction.INSTALL, newPackage));
        size += newPackage.getSize();
        List<Package> newConstraints = newPackage.getConflictsExpanded();
        for(Package constr : newConstraints) {
            //if(!accConstraints.contains(constr)){ //TODO this needs to be removed, consequences to be dealt with
                accConstraints.add(constr);
            //}
        }
    }

    public void uninstallPackage(Package remPackage) {
        packageList.remove(remPackage);
        commandList.add(new Command(Instruction.UNINSTALL, remPackage));
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

    public List<Command> getCommandList() {
        return commandList;
    }

    public List<Package> getAccumulatedConstraints() {
        return accConstraints;
    }

    public int getSize() { return size; }
    //Add ability to add to packageList/accConstraint
}
