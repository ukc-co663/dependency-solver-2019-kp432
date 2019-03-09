package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
