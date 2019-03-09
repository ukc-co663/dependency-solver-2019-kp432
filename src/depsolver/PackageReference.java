package depsolver;

import java.util.regex.*;

public class PackageReference
{
    private final static String PACKAGE_REFERENCE_PATTERN = 
        "([+-]?)([.a-zA-Z0-9-]+)((?:=)|(?:>)|(?:>=)|(?:<)|(?:<=))?(\\d+(?:\\.\\d+)?)?";
    
    private PackageKind packageKind;
    private String packageName;
    private ComparisonOperator operator;
    private String packageVersion;

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

    public String getPackageVersion()
    {
        return packageVersion;
    }
    
    public boolean fits(PackageReference other)
    {
        if (getPackageName().equals(other.getPackageName()))
        {
            if (other.operator == ComparisonOperator.None) return true;
            if (other.operator == ComparisonOperator.Equal && 
                getPackageVersion().compareTo(other.getPackageVersion()) == 0) return true;
            if (other.operator == ComparisonOperator.Greater && 
                getPackageVersion().compareTo(other.getPackageVersion()) > 0) return true;
            if (other.operator == ComparisonOperator.GreaterOrEqual && 
                getPackageVersion().compareTo(other.getPackageVersion()) >= 0) return true;
            if (other.operator == ComparisonOperator.Less && 
                getPackageVersion().compareTo(other.getPackageVersion()) < 0) return true;
            if (other.operator == ComparisonOperator.LessOrEqual && 
                getPackageVersion().compareTo(other.getPackageVersion()) <= 0) return true;
        }
        
        return false;
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
            
            result.packageVersion = version;
            
            return result;
        }

        throw new IllegalArgumentException(input);
    }
    
    public static PackageReference parse(Package _package)
    {
        PackageReference result;
        
        result = new PackageReference();
        result.packageName = _package.getName();
        result.packageVersion = _package.getVersion();
        result.operator = ComparisonOperator.Equal;
        result.packageKind = PackageKind.None;
        
        return result;
    }
}
