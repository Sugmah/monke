package troy.autofish.config;

import troy.autofish.FabricModAutofish;

public class ConfigManager {

    private Config config; // The hardcoded config instance

    public ConfigManager(FabricModAutofish modAutofish) {
        this.config = new Config(); // Directly initialize with hardcoded values
    }

    // Removed the readConfig and writeConfig methods since file handling is no longer needed

    public Config getConfig() {
        return config; // Always return the hardcoded config
    }
}
