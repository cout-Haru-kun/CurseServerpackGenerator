package generator;

import config.CurseForgeInstanceConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerPackGenerator {

    private final Map<String, List<String>> unallowedFiles;


    private final CurseForgeInstanceConfig cfInstanceConfig;
    private final File currentDir;

    public ServerPackGenerator(CurseForgeInstanceConfig cfInstanceConfig, File currentDir)
    {
        this.unallowedFiles = new HashMap<>();
        // Minecraft base files
        this.unallowedFiles.put("minecraft", new ArrayList<>());
        this.unallowedFiles.get("minecraft").add("crash-reports");
        this.unallowedFiles.get("minecraft").add("logs");
        this.unallowedFiles.get("minecraft").add("options.txt");
        this.unallowedFiles.get("minecraft").add("usercache.json");
        this.unallowedFiles.get("minecraft").add("usernamecache.json");
        this.unallowedFiles.get("minecraft").add("user-prefs.json");
        this.unallowedFiles.get("minecraft").add("realms_persistence.json");
        this.unallowedFiles.get("minecraft").add("command_history.txt");

        // Forge base files
        this.unallowedFiles.put("forge", new ArrayList<>());
        this.unallowedFiles.get("forge").add(".mixin.out");

        // Curse forge base files
        this.unallowedFiles.put("curseforge", new ArrayList<>());
        this.unallowedFiles.get("curseforge").add("downloads");
        this.unallowedFiles.get("curseforge").add("minecraftinstance.json");
        this.unallowedFiles.get("curseforge").add(".curseclient");

        this.cfInstanceConfig = cfInstanceConfig;
        this.currentDir = currentDir;
    }

    public boolean generate()
    {
        // Create directory
        File serverPackFile = new File(this.currentDir, this.currentDir.getName() + "-ServerPack");
        if (!serverPackFile.exists() && !serverPackFile.mkdirs())
        {
            System.out.println("Can't create server pack directory");
            return false;
        }
        System.out.println("Server pack will be generated at: " + serverPackFile.getAbsolutePath());

        // Copy folders and files
        File cpyFile;
        for (File file : this.currentDir.listFiles())
        {
            try {
                cpyFile = new File(serverPackFile, file.getAbsolutePath().replaceAll("\\\\", "/").replaceFirst(this.currentDir.getAbsolutePath().replaceAll("\\\\", "/"), "")); // Get path of server pack, add the file (remove root to have the same directory tree)
                if (!isUnallowed(file.getName()) && !file.getName().equals(serverPackFile.getName()))
                {
                    System.out.println("Copy " + file.getName() + " to " + cpyFile.getAbsolutePath());
                    if (file.isDirectory())
                        this.copyFolder(file, cpyFile);
                    else
                        Files.copy(file.toPath(), cpyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (IOException e)
            {
                System.out.println("Can't copy " + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean isUnallowed(String fileName)
    {
        // Check unallowed
        for (Map.Entry<String, List<String>> entry : this.unallowedFiles.entrySet())
        {
            for (String unallow : entry.getValue())
            {
                if (fileName.equals(unallow))
                {
                    System.out.println("File " + fileName + " ignored, base file from " + entry.getKey());
                    return true;
                }
            }
        }

        // Check empty
        File file = new File(this.currentDir, fileName);
        if (file.isDirectory() && file.listFiles().length == 0)
        {
            System.out.println("Directory " + fileName + " ignored, empty");
            return true;
        }
        return false;
    }

    private void copyFolder(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        } else
        {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0)
                    out.write(buffer, 0, length);
            } catch (Exception e) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
