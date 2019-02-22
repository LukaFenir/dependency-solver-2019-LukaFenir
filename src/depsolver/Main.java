package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Package {
  private String name;
  private String version;
  private Integer size;
  private List<List<String>> depends = new ArrayList<>();
  private List<String> conflicts = new ArrayList<>();
  private List<List<Package>> dependsParsed = new ArrayList<>();
  private List<Package> conflictsParsed = new ArrayList<>();

  public String getName() { return name; }
  public String getVersion() { return version; }
  public Integer getSize() { return size; }
  public List<List<String>> getDepends() { return depends; }
  public List<String> getConflicts() { return conflicts; }
  public List<List<Package>> getDependsParsed() { return dependsParsed; } //Could remove these two? Won't need strings later
  public List<Package> getConflictsParsed() { return conflictsParsed; }
  // Used by JSON parser
  public void setName(String name) { this.name = name; }
  public void setVersion(String version) { this.version = version; }
  public void setSize(Integer size) { this.size = size; }
  public void setDepends(List<List<String>> depends) { this.depends = depends; }
  public void setConflicts(List<String> conflicts) { this.conflicts = conflicts; }
  public void setDependsParsed(List<List<Package>> depends) { this.dependsParsed = depends; }
  public void setConflictsParsed(List<Package> conflicts) { this.conflictsParsed = conflicts; }

  public void parseDepsCons(List<Package> raw_repo) {
    for (List<String> d3 : getDepends()) {
        parseDC(d3, raw_repo);
    }
  }

  private void parseDC(List<String> deps, List<Package> raw_repo){
      for (String dep : deps) {
          //get deps from raw_repo by name, may use a HashMap for faster search
          //1. Parse version, grab name, operator, and version string
          Pattern r = Pattern.compile("([.+a-zA-Z0-9-]+)(?:(>=|<=|=|<|>)(\\d+(?:\\.\\d+)*))?");
          Matcher m = r.matcher(dep);
          m.find();
          String name_str = m.group(1);
          String operator_str = m.group(2);
          List<Package> list_of_packages = new ArrayList<Package>();
          for (Package p : raw_repo) {
              if((p.getName() == name_str) && (p.getVersion())) {

              }
          }


          // Go through repo, pop that shit
          String version_str = m.group(3);
          int x = 0;
      }
  }
}


public class Main {
    // From https://www.programcreek.com/2014/03/leetcode-compare-version-numbers-java/
    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");
        int i=0;
        while(i<arr1.length || i<arr2.length){
            if(i<arr1.length && i<arr2.length){
                if(Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])){ return -1; }
                else if(Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])){ return 1; }
            } else if(i<arr1.length){
                if(Integer.parseInt(arr1[i]) != 0){ return 1; }
            } else if(i<arr2.length){
                if(Integer.parseInt(arr2[i]) != 0){ return -1; }
            }
            i++;
        }
        return 0;
    }

    public static List<Package> parseDepsNCons(List<Package> raw_repo){
        List<Package> parsed_repo = new ArrayList<Package>();
        // Set up initial repo of packages, needed before inserting dependencies (PackageP reference)
        for (Package d : raw_repo) {
            for (List<String> d2 : d.getDepends()) {
                for (String d3 : d2) {
                    //get deps from raw_repo by name, may use a HashMap for faster search
                    //1. Parse version, grab name, operator, and version string
                    Pattern r = Pattern.compile("([.+a-zA-Z0-9-]+)(?:(>=|<=|=|<|>)(\\d+(?:\\.\\d+)*))?");
                    Matcher m = r.matcher(d3);
                    //https://www.tutorialspoint.com/java/java_regular_expressions.htm
                    Boolean ch = m.find();

                    String c = m.group(1);
                    String e = m.group(2);
                    String b = m.group(3);
                    int x = 0;
                }
            }

        }
        return parsed_repo;
    }

    public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

    // Go through each package and depend to other packages
    for(Package pack : repo) {
        pack.parseDepsCons(repo);
    }
    List<Package> parsed_repo = parseDepsNCons(repo);

    List<List<String>> str_dependencies = repo.get(0).getDepends();
    List<Package> pack_dependencies = new ArrayList<>();
    int x = compareVersion("1.03","1.3.0");

    //Change List<String> to List<Package>

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

    static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
    }
}
