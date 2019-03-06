package depsolver;

import java.util.*;

public class PackageDisjunction
{
    private List<PackageReference> disjunctions;
    
    public PackageDisjunction()
    {
        disjunctions = new ArrayList<>();
    }
    
    public void add(PackageReference reference)
    {
        disjunctions.add(reference);
    }

    public List<PackageReference> getDisjunctions()
    {
        return disjunctions;
    }
}
