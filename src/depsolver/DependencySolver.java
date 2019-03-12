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
                
                if (packageReference.fits(constraintReference) &&
                    tryInstallPackage(repository, packageReference, 
                    _package.getDepends(), _package.getConflicts(), initial, commands))
                {
                    break;
                }
            }
        }
        
        return commands;
    }
    
    private static boolean tryInstallPackage(
        List<Package> repository,
        PackageReference packageReference,
        List<List<String>> dependencies,
        List<String> conflicts,
        List<String> initial, 
        List<String> commands)
    {
        if (tryInstallDependencies(
            repository, dependencies, conflicts, initial, commands))
        {
            return install(commands, initial, packageReference);
        }
        
        return false;
    }
    
    private static boolean tryInstallDependencies(
        List<Package> repository,
        List<List<String>> dependencies,
        List<String> conflicts,
        List<String> initial, 
        List<String> commands)
    {
        PackageReference alternativeReference, packageReference;
        boolean installed;
        
        for (List<String> alternatives : dependencies)
        {
            installed = false;
            
            for (String alternative : alternatives)
            {
                alternativeReference = PackageReference.parse(alternative);
                
                for (Package _package : repository)
                {
                    packageReference = PackageReference.parse(_package);
                    
                    if (packageReference.fits(alternativeReference))
                    {
                        if (!ConflictSolver.hasConflict(repository, 
                            packageReference, dependencies, conflicts, initial, commands))
                        {
                            install(commands, initial, packageReference);
                            installed = true;

                            break;
                        }
                    }
                }
                
                if (installed)
                {
                    break;
                }
            }
            
            if (!installed)
            {
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean install(List<String> commands, 
        List<String> initial, PackageReference packageReference)
    {
        if (!contains(commands, packageReference) &&
            !contains(initial, packageReference))
        {
            commands.add("+" + packageReference.toString());
            
            return true;
        }
        
        return false;
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
