package depsolver;

import java.util.*;

public class PackageAlternative
{
    private List<PackageReference> alternatives;
    
    public PackageAlternative()
    {
        alternatives = new ArrayList<>();
    }
    
    public void add(PackageReference reference)
    {
        alternatives.add(reference);
    }

    public List<PackageReference> getAlternatives()
    {
        return alternatives;
    }
}
