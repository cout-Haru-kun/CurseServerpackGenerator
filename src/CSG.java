import config.CurseForgeInstanceConfig;
import generator.ServerPackGenerator;
import installer.LoaderInstaller;

import java.io.File;

public class CSG {

    private final File currentDir;
    private final CurseForgeInstanceConfig cfInstanceConfig;

    public CSG(File instanceDir)
    {
        this.currentDir = instanceDir == null ? new File(System.getProperty("user.dir")) : instanceDir;
        this.cfInstanceConfig = new CurseForgeInstanceConfig();
    }

    public boolean start()
    {
        System.out.println("Detecting curseforge profile in " + this.currentDir.getAbsolutePath() + "..");
        // Check if in a curseforge pack instance
        if (!this.detectInstance())
            return false;

        System.out.println("Instance detected! Loading configuration..");
        if (!this.loadCFConfig())
            return false;

        System.out.println("Mod list loaded! Installing mod loader..");
        if (!this.installModLoader())
            return false;

        System.out.println("Mod loader installed! Generating pack..");
        return this.generateServerPack();
    }


    private boolean detectInstance()
    {
        boolean foundInstance = false;
        for (File file : this.currentDir.listFiles())
        {
            if (file.getName().equals("minecraftinstance.json"))
            {
                foundInstance = true;
                break;
            }
        }
        if (!foundInstance)
        {
            System.out.println("Can't find minecraftinstance.json! Aborting!");
            return false;
        }
        return true;
    }

    private boolean loadCFConfig()
    {
        if (!this.cfInstanceConfig.load(new File(this.currentDir, "minecraftinstance.json")))
        {
            System.out.println("Can't load minecraftinstance.json content! Aborting!");
            return false;
        }
        return true;
    }

    private boolean installModLoader()
    {
        if (!new LoaderInstaller(this.cfInstanceConfig, this.currentDir).install())
        {
            System.out.println("Can't install mod loader!");
            return false;
        }
        return true;
    }

    private boolean generateServerPack()
    {
        if (!new ServerPackGenerator(this.cfInstanceConfig, this.currentDir).generate())
        {
            System.out.println("Can't generate server pack!");
            return false;
        }
        return true;
    }
}
