package depsolver;

import java.util.*;

public class DependencySolver
{
    public static List<String> solve(
        List<Package> repository, List<String> initial, List<String> constraints)
    {
        List<String> commands;
        PackageReference packageReference, constraintReference;
        
        commands = new ArrayList<>();

        for (String constraint : constraints)
        {
            constraintReference = PackageReference.parse(constraint);
            
            for (Package _package : repository)
            {
                packageReference = PackageReference.parse(_package);
                
                if (_package.getName().equals(constraintReference.getPackageName()) && 
                    tryInstall(repository, packageReference, _package.getDepends(), 
                    _package.getConflicts(), initial, commands, constraintReference))
                {
                    break;
                }
            }
        }
        
        return commands;
    }
    
    private static boolean tryInstall(
        List<Package> repository,
        PackageReference packageReference,
        List<List<String>> dependencies,
        List<String> conflicts,
        List<String> initial, 
        List<String> commands, 
        PackageReference constraintReference)
    {
        PackageReference alternativeReference;
        boolean installed;
        
        if (dependencies != null)
        {
            for (List<String> alternatives : dependencies)
            {
                installed = false;

                for (String alternative : alternatives)
                {
                    alternativeReference = PackageReference.parse(alternative);

                    if (tryInstall(repository, alternativeReference, null, null, 
                        initial, commands, constraintReference))
                    {
                        installed = true;
                        break;
                    }

                    if (!installed)
                    {
                        return false;
                    }
                }
            }
        }
        
        if (!contains(initial, constraintReference) && 
            !contains(commands, constraintReference))
        {
            if (packageReference.fits(constraintReference))
            {
                commands.add("+" + packageReference.getPackageName() 
                    + "=" + packageReference.getPackageVersion());
                
                return true;
            }
        }
        
        return false;
    }
    
    private static void tryUninstall(
        List<Package> repository, 
        List<String> initial, 
        List<String> commands, 
        PackageReference constraintReference)
    {
        
    }
    
    private static boolean contains(
        List<String> packages, PackageReference constraintReference)
    {
        PackageReference reference;
        
        for (String _package : packages)
        {
            reference = PackageReference.parse(_package);
            
            if (reference.fits(constraintReference))
            {
                return true;
            }
        }
        
        return false;
    }
}
