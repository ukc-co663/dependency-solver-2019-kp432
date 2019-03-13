package depsolver;

import com.alibaba.fastjson.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Main
{
    public static void main(String[] args) throws IOException
    {        
        if (args.length != 3)
        {
            System.err.println("args.length != 3");
            System.exit(-1);
        }
        
        TypeReference<List<Package>> repoType = new TypeReference<List<Package>>()
        {
        };
        List<Package> repo = JSON.parseObject(readFile(args[0]), repoType);
        TypeReference<List<String>> strListType = new TypeReference<List<String>>()
        {
        };
        List<String> initial = JSON.parseObject(readFile(args[1]), strListType);
        List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);
        List<String> commands = DependencySolver.solve(repo, initial, constraints);
        int i;
        
        for (i = 0; i < commands.size(); i++)
        {
            commands.set(i, "\"" + commands.get(i) + "\"");
        }
        
        System.out.print("[ ");
        System.out.print(String.join("\n, ", commands));
        System.out.println(" ]");
        System.exit(0);
    }

    static String readFile(String filename) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        br.lines().forEach(line -> sb.append(line));
        return sb.toString();
    }
}

enum ComparisonOperator
{
    None,
    Equal,
    Greater,
    GreaterOrEqual,
    Less,
    LessOrEqual
}

class ConflictSolver
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

class DependencySolver
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

class Package
{
    private String name;
    private String version;
    private Integer size;
    private List<List<String>> depends = new ArrayList<>();
    private List<String> conflicts = new ArrayList<>();

    public String getName()
    {
        return name;
    }

    public String getVersion()
    {
        return version;
    }

    public Integer getSize()
    {
        return size;
    }

    public List<List<String>> getDepends()
    {
        return depends;
    }

    public List<String> getConflicts()
    {
        return conflicts;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public void setSize(Integer size)
    {
        this.size = size;
    }

    public void setDepends(List<List<String>> depends)
    {
        this.depends = depends;
    }

    public void setConflicts(List<String> conflicts)
    {
        this.conflicts = conflicts;
    }
}

enum PackageKind
{
    None,
    Positive,
    Negative,
}

class PackageReference
{
    private final static String PACKAGE_REFERENCE_PATTERN = 
        "([+-]?)([.a-zA-Z0-9-+]+)((?:=)|(?:>)|(?:>=)|(?:<)|(?:<=))?([0-9.]+?)?";
    
    private PackageKind packageKind;
    private String packageName;
    private ComparisonOperator operator;
    private PackageVersion packageVersion;
    
    private PackageReference() { }

    public PackageKind getPackageKind()
    {
        return packageKind;
    }
    
    public String getPackageName()
    {
        return packageName;
    }

    public ComparisonOperator getOperator()
    {
        return operator;
    }

    public PackageVersion getPackageVersion()
    {
        return packageVersion;
    }
    
    public boolean fits(PackageReference other)
    {
        if (getPackageName().equals(other.getPackageName()))
        {
            if (other.operator == ComparisonOperator.None) return true;
            if (other.operator == ComparisonOperator.Equal && 
                packageVersion.getTotal() == other.packageVersion.getTotal()) return true;
            if (other.operator == ComparisonOperator.Greater && 
                packageVersion.getTotal() > other.packageVersion.getTotal()) return true;
            if (other.operator == ComparisonOperator.GreaterOrEqual && 
                packageVersion.getTotal() >= other.packageVersion.getTotal()) return true;
            if (other.operator == ComparisonOperator.Less && 
                packageVersion.getTotal() < other.packageVersion.getTotal()) return true;
            if (other.operator == ComparisonOperator.LessOrEqual && 
                packageVersion.getTotal() <= other.packageVersion.getTotal()) return true;
        }
        
        return false;
    }
    
    @Override
    public String toString()
    {
        if (operator != ComparisonOperator.None)
        {
            return packageName + getOperatorText(operator) + packageVersion;
        }
        
        return packageName;
    }
    
    private String getOperatorText(ComparisonOperator operator)
    {
        if (operator == ComparisonOperator.Equal) return "=";
        if (operator == ComparisonOperator.Greater) return ">";
        if (operator == ComparisonOperator.GreaterOrEqual) return ">=";
        if (operator == ComparisonOperator.Less) return "<";
        if (operator == ComparisonOperator.LessOrEqual) return "<=";
        
        return null;
    }
    
    public static PackageReference parse(String input)
    {
        PackageReference result;
        Pattern pattern;
        Matcher matcher;
        String kind, name, operator, version;
        
        result = new PackageReference();
        pattern = Pattern.compile(PACKAGE_REFERENCE_PATTERN);
        matcher = pattern.matcher(input);
        
        if (matcher.matches())
        {
            kind = matcher.group(1);
            name = matcher.group(2);
            operator = matcher.group(3);
            version = matcher.group(4);
            
            if (kind == null)
            {
                result.packageKind = PackageKind.None;
            }
            else if (kind.equals("+"))
            {
                result.packageKind = PackageKind.Positive;
            }
            else
            {
                result.packageKind = PackageKind.Negative;
            }
            
            result.packageName = name;
            
            if (operator != null)
            {
                if (operator.equals("=")) result.operator = ComparisonOperator.Equal;
                else if (operator.equals(">")) result.operator = ComparisonOperator.Greater;
                else if (operator.equals(">=")) result.operator = ComparisonOperator.GreaterOrEqual;
                else if (operator.equals("<")) result.operator = ComparisonOperator.Less;
                else result.operator = ComparisonOperator.LessOrEqual;
            }
            else
            {
                result.operator = ComparisonOperator.None;
            }
            
            if (version == null)
            {
                version = "0";
            }
            
            result.packageVersion = PackageVersion.parse(version);
            
            return result;
        }

        throw new IllegalArgumentException(input);
    }
    
    public static PackageReference parse(Package _package)
    {
        PackageReference result;
        
        result = new PackageReference();
        result.packageName = _package.getName();
        result.packageVersion = PackageVersion.parse(_package.getVersion());
        result.operator = ComparisonOperator.Equal;
        result.packageKind = PackageKind.None;
        
        return result;
    }
}

class PackageVersion
{
    private final static String VERSION_PATTERN = 
        "(\\d+)\\.?(\\d+)?\\.?(\\d+)?\\.?(\\d+)?";
    
    private long major;
    private long minor;
    private long release;
    private long build;

    public PackageVersion(long major, long minor, long release, long build)
    {
        this.major = major;
        this.minor = minor;
        this.release = release;
        this.build = build;
    }
    
    public long getTotal()
    {
        return major << 24 | minor << 16 | release << 8 | build;
    }
    
    @Override
    public String toString()
    {                
        if (minor > 0)
        {
            if (release > 0)
            {
                if (build > 0)
                {
                    return String.format("%d.%d.%d.%d", major, minor, release, build);
                }

                return String.format("%d.%d.%d", major, minor, release);
            }

            return String.format("%d.%d", major, minor);
        }

        return String.format("%d", major);
    }
    
    public static PackageVersion parse(String input)
    {
        Pattern pattern;
        Matcher matcher;
        long major, minor, release, build;
        
        pattern = Pattern.compile(VERSION_PATTERN);
        matcher = pattern.matcher(input);
        
        if (matcher.matches())
        {
            major = stringToLong(matcher.group(1));
            minor = stringToLong(matcher.group(2));
            release = stringToLong(matcher.group(3));
            build = stringToLong(matcher.group(4));
            
            return new PackageVersion(major, minor, release, build);
        }
        
        throw new IllegalArgumentException(input);
    }
    
    private static long stringToLong(String text)
    {
        if (text == null)
        {
            return 0;
        }
        
        return Long.parseLong(text);
    }
}