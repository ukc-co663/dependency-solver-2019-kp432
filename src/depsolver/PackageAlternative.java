package depsolver;

import java.util.*;

public class PackageAlternative
{
    private List<PackageConjunction> alternatives;
    
    public PackageAlternative()
    {
        alternatives = new ArrayList<>();
    }
    
    public void add(PackageConjunction conjunction)
    {
        alternatives.add(conjunction);
    }

    public List<PackageConjunction> getAlternatives()
    {
        return alternatives;
    }
}
