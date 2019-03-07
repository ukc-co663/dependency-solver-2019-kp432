package depsolver;

import java.util.regex.*;

public class PackageReference
{
    private final static String PACKAGE_REFERENCE_PATTERN = 
        "(\\w)((?:=)|(?:>)|(?:>=)|(?:<)|(?:<=))?(\\d+(?:\\.\\d+)?)?";
    
    private String packageName;
    private ComparisonOperator operator;
    private double packageVersion;

    private PackageReference() { }
    
    public String getPackageName()
    {
        return packageName;
    }

    public ComparisonOperator getOperator()
    {
        return operator;
    }

    public double getPackageVersion()
    {
        return packageVersion;
    }
    
    public static PackageReference parse(String input)
    {
        PackageReference result;
        Pattern pattern;
        Matcher matcher;
        String name, operator, version;
        
        result = new PackageReference();
        pattern = Pattern.compile(PACKAGE_REFERENCE_PATTERN);
        matcher = pattern.matcher(input);
        
        if (matcher.matches())
        {
            name = matcher.group(1);
            operator = matcher.group(2);
            version = matcher.group(3);
            
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
                version = "0.0";
            }
            
            result.packageVersion = Double.parseDouble(version);
            
            return result;
        }

        throw new IllegalArgumentException(input);
    }
}
