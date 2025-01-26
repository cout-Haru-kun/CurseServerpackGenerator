package config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CurseForgeInstanceConfig {

    private final List<CurseForgeModConfig> mods, serverOnlyMods;
    private String modLoaderName, modLoaderVersion;

    public CurseForgeInstanceConfig()
    {
        this.mods = new ArrayList<>();
        this.serverOnlyMods = new ArrayList<>();
    }

    public boolean load(File file)
    {
        CurseForgeModConfig mod = null;
        try {
            String jsonString = Files.readString(file.toPath());
            JSONObject obj = new JSONObject(jsonString);

            // Get mod loader
            JSONObject modLoader = obj.getJSONObject("baseModLoader");
            this.modLoaderName = modLoader.getString("name");
            this.modLoaderVersion = modLoader.getString("forgeVersion");

            // Get mods
            JSONArray modList = obj.getJSONArray("installedAddons");
            if (modList == null)
                return false;

            // Loop all
            for (int i = 0; i < modList.length(); i++)
            {
                mod = new CurseForgeModConfig(modList.getJSONObject(i));
                if (!mod.isCorrect())
                {
                    System.out.println("Incorrect data: " + modList.getJSONObject(i));
                    continue;
                }
                this.mods.add(mod);
                if (mod.isServer())
                    this.serverOnlyMods.add(mod);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        System.out.println("Loaded " + this.mods.size() + "|" + this.serverOnlyMods.size() + " mods!");
        return true;
    }


    public String getModLoaderName() {
        return modLoaderName;
    }

    public String getModLoaderVersion() {
        return modLoaderVersion;
    }
}
