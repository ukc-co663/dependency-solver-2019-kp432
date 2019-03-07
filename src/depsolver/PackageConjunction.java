package depsolver;

import java.util.*;

public class PackageConjunction
{
    private List<PackageAlternative> conjunctions;
    
    public PackageConjunction()
    {
        conjunctions = new ArrayList<>();
    }
    
    public void add(PackageAlternative alternative)
    {
        conjunctions.add(alternative);
    }

    public List<PackageAlternative> getConjunctions()
    {
        return conjunctions;
    }
}
