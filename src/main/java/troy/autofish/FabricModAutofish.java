package troy.autofish;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.lwjgl.glfw.GLFW;
import troy.autofish.config.Config;
import troy.autofish.config.ConfigManager;
import troy.autofish.gui.AutofishScreenBuilder;
import troy.autofish.scheduler.AutofishScheduler;

public class FabricModAutofish implements ClientModInitializer {

    private static FabricModAutofish instance; // Singleton instance
    private Autofish autofish; // The Autofisher logic
    private AutofishScheduler scheduler; // Scheduler for managing tasks
    private KeyBinding autofishGuiKey; // Keybinding for opening the GUI
    private ConfigManager configManager; // Configuration manager

    @Override
    public void onInitializeClient() {
        if (instance == null) instance = this;

        // Initialize ConfigManager with hardcoded values
        this.configManager = new ConfigManager(this);

        // Register Keybinding
        autofishGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.autofish.open_gui", // Keybinding ID
            InputUtil.Type.KEYSYM,  // Keybinding type
            GLFW.GLFW_KEY_V,        // Default key (V)
            "Autofish"              // Category
        ));

        // Register Tick Callback
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);

        // Create Scheduler instance
        this.scheduler = new AutofishScheduler(this);

        // Create Autofisher instance
        this.autofish = new Autofish(this);
    }

    // Called every client tick
    public void tick(MinecraftClient client) {
        if (this.autofish != null) {
            if (autofishGuiKey.wasPressed()) {
                // Open the Autofish GUI screen
                client.setScreen(AutofishScreenBuilder.buildScreen(this, client.currentScreen));
            }
            // Handle tick logic for autofish and scheduler
            autofish.tick(client);
            scheduler.tick(client);
        }
    }

    /**
     * Mixin callback for Sound and EntityVelocity packets (multiplayer detection)
     */
    public void handlePacket(Packet<?> packet) {
        autofish.handlePacket(packet);
    }

    /**
     * Mixin callback for chat packets
     */
    public void handleChat(GameMessageS2CPacket packet) {
        autofish.handleChat(packet);
    }

    /**
     * Mixin callback for catchingFish method of EntityFishHook (singleplayer detection)
     */
    public void tickFishingLogic(Entity owner, int ticksCatchable) {
        autofish.tickFishingLogic(owner, ticksCatchable);
    }

    // Singleton accessor for the mod instance
    public static FabricModAutofish getInstance() {
        return instance;
    }

    // Accessor for Autofish logic
    public Autofish getAutofish() {
        return autofish;
    }

    // Accessor for the configuration manager
    public ConfigManager getConfigManager() {
        return configManager;
    }

    // Accessor for the Config instance (uses hardcoded values)
    public Config getConfig() {
        return configManager.getConfig();
    }

    // Accessor for the AutofishScheduler
    public AutofishScheduler getScheduler() {
        return scheduler;
    }
}
