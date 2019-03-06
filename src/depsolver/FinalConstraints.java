package depsolver;

import java.util.ArrayList;
import java.util.List;

public class FinalConstraints {
    private List<Package> positiveConstraints;
    private List<Package> negativeConstraints;

    public FinalConstraints(List<String> constraintsString, List<Package> raw_repo) {
        List<List<Package>> expanded = expandConstraints(constraintsString, raw_repo);
        positiveConstraints = expanded.get(0);
        negativeConstraints = expanded.get(1);
    }

    private List<List<Package>> expandConstraints(List<String> constraintsString, List<Package> raw_repo) {
        List<List<Package>> constraintsPosNeg = new ArrayList<>();
        constraintsPosNeg.add(new ArrayList<>());
        constraintsPosNeg.add(new ArrayList<>()); //change to loop?

        PackageExpand expander = new PackageExpand();
        for(String s : constraintsString) {
            String sign = s.substring(0,1);
            List<Package> packages = expander.expandPackageString(s.substring(1), raw_repo);
            switch(sign) {
                case ("+"):
                    constraintsPosNeg.get(0).addAll(packages); break;
                case ("-"):
                    constraintsPosNeg.get(1).addAll(packages); break;
            }
        }
        return constraintsPosNeg;
    }

    public List<Package> getPositivePackages() {
        return positiveConstraints;
    }

    public List<Package> getNegativePackages() {
        return negativeConstraints;
    }
}
