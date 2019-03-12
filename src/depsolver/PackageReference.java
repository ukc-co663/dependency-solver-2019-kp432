package depsolver;

import java.util.regex.*;

public class PackageReference
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
