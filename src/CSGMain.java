import java.io.File;
import java.util.List;

public class CSGMain {

    public static void main(String[] args) throws InterruptedException {
        final long time = System.currentTimeMillis();

        System.out.println("Welcome to CSG, starting server pack generation!");
        System.out.println("=============");

        // Parse args
        List<String> split;
        File instanceDir = null;
        for (String arg : args)
        {
            split = List.of(arg.split("="));
            if (split.size() == 2)
            {
                switch (split.getFirst())
                {
                    case "--instanceDir" -> {
                        instanceDir = new File(split.getLast());
                    }
                }
            }
        }

        // Run
        if (new CSG(instanceDir).start())
        {
            System.out.println("=============");
            System.out.println("Generation success, generated in " + ((double) (System.currentTimeMillis() - time) / 1000) + " seconds");
            System.out.println("=============");
        }
        else
        {
            System.out.println("=============");
            System.out.println("Generation failed, generated in " + ((double) (System.currentTimeMillis() - time) / 1000) + " seconds");
            System.out.println("=============");
        }
    }
}