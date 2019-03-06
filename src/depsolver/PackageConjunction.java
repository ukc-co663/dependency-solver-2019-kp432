package depsolver;

import java.util.*;

public class PackageConjunction
{
    private List<PackageReference> conjunctions;
    
    public PackageConjunction()
    {
        conjunctions = new ArrayList<>();
    }
    
    public void add(PackageReference reference)
    {
        conjunctions.add(reference);
    }

    public List<PackageReference> getConjunctions()
    {
        return conjunctions;
    }
}
