package depsolver;

public class PackageReference
{
    private String packageName;
    private ComparisonOperator operator;
    private double packageVersion;
    
    public PackageReference(String input)
    {
        parseInput(input);
    }

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
    
    private void parseInput(String input)
    {
        throw new UnsupportedOperationException();
    }
}
