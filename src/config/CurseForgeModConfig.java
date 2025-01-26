package config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class CurseForgeModConfig {

    // Mod's information
    private String modName;
    private boolean client, server;
    private File modFile;

    // Is correct mod
    private final boolean correct;



    public CurseForgeModConfig(JSONObject mobObj)
    {
        this.modName = "";
        this.client = false;
        this.server = false;

        // Check
        this.correct = parse(mobObj);
    }

    public boolean isCorrect()
    {
        return this.correct;
    }

    public boolean isServer()
    {
        // Count as server file if: detected server string, or no client or server detecte at all
        return this.server || !this.client;
    }



    private boolean parse(JSONObject modObj)
    {
        this.modName = modObj.getString("name");

        // Get file
        JSONObject installedFile = modObj.getJSONObject("installedFile");
        if (installedFile == null)
            return false;

        // Get game version
        JSONArray gameVersion = installedFile.getJSONArray("gameVersion");
        if (gameVersion == null)
            return false;
        for (int i = 0; i < gameVersion.length(); i++)
        {
            if (gameVersion.getString(i).equals("Server"))
                this.server = true;
            if (gameVersion.getString(i).equals("Client"))
                this.client = true;
        }

        // Get installed path
        File path = new File(modObj.getString("modFolderPath"));
        if (!path.exists() || !path.isDirectory())
            return false;
        path = new File(path, installedFile.getString("fileName"));
        if (!path.exists() || !path.isFile())
            return false;
        this.modFile = path;

        // Good
        System.out.println("Serialized mod " + modName + " [" + (this.client ?  "Client|" : "") + (this.server ? "Server" : "") + "]");
        return true;
    }
}
