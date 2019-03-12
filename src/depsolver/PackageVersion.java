package depsolver;

import java.util.regex.*;

public class PackageVersion
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
