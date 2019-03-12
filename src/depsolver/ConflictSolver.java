package depsolver;

import java.util.*;

public class ConflictSolver
{
    // dowolny konflikt z tablicą dependencies, initial lub commands zwraca false
    // przy czym dependencies są rozwijane, tak, żeby pobrać ich listę konfliktów
    // lista conflicts należy do reference i należy ją porównać z initial i commands
    public static boolean hasConflict(
        List<Package> repository, 
        PackageReference packageReference, 
        List<List<String>> dependencies, 
        List<String> conflicts, 
        List<String> initial,  
        List<String> commands)
    {
        PackageReference dependencyReference, conflictReference;
        
        for (List<String> alternatives : dependencies)
        {
            for (String alternative : alternatives)
            {
                for (Package _package : repository)
                {
                    dependencyReference = PackageReference.parse(alternative);
                    
                    if (packageReference.fits(dependencyReference))
                    {
                        for (String conflict : _package.getConflicts())
                        {
                            conflictReference = PackageReference.parse(conflict);
                            
                            if (packageReference.fits(conflictReference))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        
        return false;
    }
}
