package installer;

import config.CurseForgeInstanceConfig;

import java.io.File;

public class LoaderInstaller {
    private final CurseForgeInstanceConfig cfInstanceConfig;
    private final File currentDir;

    public LoaderInstaller(CurseForgeInstanceConfig cfInstanceConfig, File currentDir)
    {
        this.cfInstanceConfig = cfInstanceConfig;
        this.currentDir = currentDir;
    }

    public boolean install()
    {
        // Create directory
        File serverPackFile = new File(this.currentDir, this.currentDir.getName() + "-ServerPack");
        if (serverPackFile.exists())
        {
            if (serverPackFile.isFile())
                serverPackFile.delete();
            else
                deleteDir(serverPackFile);
        }
        if (!serverPackFile.exists() && !serverPackFile.mkdirs())
        {
            System.out.println("Can't create server pack directory");
            return false;
        }
        System.out.println("Mod loader will be installed at: " + serverPackFile.getAbsolutePath());

        // Install mod loader
        if (this.cfInstanceConfig.getModLoaderName().toLowerCase().contains("neoforge"))
            return new NeoForgeInstaller(this.cfInstanceConfig.getModLoaderVersion(), serverPackFile).install();

        return false;
    }

    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

}
