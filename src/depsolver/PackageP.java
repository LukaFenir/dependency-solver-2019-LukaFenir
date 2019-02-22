package depsolver;

import java.util.ArrayList;
import java.util.List;

public class PackageP {
    private String name;
    private String version;
    private Integer size;
    private List<List<PackageP>> depends = new ArrayList<>();
    private List<PackageP> conflicts = new ArrayList<>();

    public PackageP(Package oldP){
        name = oldP.getName();
        version = oldP.getVersion();
        size = oldP.getSize();
    }
    public void insertDepends(List<PackageP> oneOf) {
        depends.add(oneOf);
    }
    //Might change to insert by PackageP
    public void insertConflicts(List<PackageP> confs) {
        depends.add(confs);
    }
    public String getName() { return name; }
    public String getVersion() { return version; }
    public Integer getSize() { return size; }
    public List<List<Package>> getDepends() { return depends; }
    public List<Package> getConflicts() { return conflicts; }
    public void setName(String name) { this.name = name; }
    public void setVersion(String version) { this.version = version; }
    public void setSize(Integer size) { this.size = size; }
    public void setDepends(List<List<Package>> depends) { this.depends = depends; }
    public void setConflicts(List<Package> conflicts) { this.conflicts = conflicts; }

}

