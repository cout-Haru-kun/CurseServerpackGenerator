package installer;

import java.io.*;
import java.net.URL;

public class NeoForgeInstaller {

    private final File serverPackDir;
    private final String mavenRepo, version;

    public NeoForgeInstaller(String version, File serverPackDir)
    {
        this.mavenRepo = "https://maven.neoforged.net/releases/net/neoforged/neoforge/" + version + "/neoforge-" + version + "-installer.jar";
        this.version = version;
        this.serverPackDir = serverPackDir;
    }

    public boolean install()
    {
        File installerFile = new File(this.serverPackDir, "neoforge-" + version + "-installer.jar");
        try (BufferedInputStream in = new BufferedInputStream(new URL(this.mavenRepo).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(installerFile)) {

            // Download file
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            // Run installer
            System.out.println("Downloaded neoforge mod loader!");
            if (!this.run(installerFile))
                return false;

            installerFile.delete();
            return true;
        }
        // Error
        catch (IOException e) {
            return false;
        }
    }


    public boolean run(File installerFile)
    {
        try {
            // Run installer and wait for exit
            System.out.println("Waiting for server installation, this may take several minutes.. | java -jar " + installerFile.getAbsolutePath() + " --installServer " + serverPackDir.getAbsolutePath());
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-jar",
                    installerFile.getAbsolutePath(),
                    "--installServer",
                    serverPackDir.getAbsolutePath()
            ).inheritIO();

            // Check if installed correctly
            int exitValue = processBuilder.start().waitFor();
            if (exitValue != 0)
            {
                System.out.println("NeoForge installer return value: " + exitValue);
                return false;
            }

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Installed neoforge server!");
        return true;
    }
}
