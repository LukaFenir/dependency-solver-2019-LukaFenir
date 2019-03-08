package depsolver;

import java.util.ArrayList;
import java.util.List;

public class FinalConstraints {
    private List<List<Package>> positiveConstraints; //List of lists of packages
    private List<Package> negativeConstraints; //List of lists of packages

    public FinalConstraints(List<String> constraintsString, List<Package> raw_repo) {
        expandConstraints(constraintsString, raw_repo);
    }

    private void expandConstraints(List<String> constraintsString, List<Package> raw_repo) {
        List<List<Package>> positive = new ArrayList<>();
        List<Package> negative = new ArrayList<>();

        PackageExpand expander = new PackageExpand();
        for(String s : constraintsString) {
            String sign = s.substring(0,1);
            //At least one of these
            List<Package> packages = expander.expandPackageString(s.substring(1), raw_repo);
            switch(sign) {
                case ("+"):
                    positive.add(packages); break; //Need to make list of lists
                case ("-"):
                    negative.addAll(packages); break;
            }
        }
        positiveConstraints = positive;
        negativeConstraints = negative;
    }

    public List<List<Package>> getPositivePackages() {
        return positiveConstraints;
    }

    public List<Package> getNegativePackages() {
        return negativeConstraints;
    }
}
