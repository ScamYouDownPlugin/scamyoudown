package net.runelite.client.plugins.ScamYouDownsyndrome;

import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.Config;

@ConfigGroup("moeyouscam")
public interface MoeYouConfig extends Config
{
    @ConfigItem(keyName = "moeScamString", name = "MoeOverlay", description = "Configure partner in scam. (Ring, Prayer odds)")
    default String moeScamString() {
        return "302 hustlers";
    }

    @ConfigItem(keyName = "moePrayerFlick", name = "moePrayerFlick", description = "Auto flick quick prayers")
    default boolean moePrayerFlick() {
        return true;
    }
}