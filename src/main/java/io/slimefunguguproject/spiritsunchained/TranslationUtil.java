package io.slimefunguguproject.spiritsunchained;

import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;

import javax.annotation.Nonnull;
import java.util.Locale;

public final class TranslationUtil {

    @Nonnull
    public static String getSpiritState(@Nonnull String state) {
        return switch (state.toLowerCase(Locale.ROOT)) {
            case "hostile" -> "敌对";
            case "aggressive" -> "挑衅";
            case "passive" -> "被动";
            case "gentle" -> "温和";
            case "friendly" -> "友好";
            default -> ChatUtils.humanize(state);
        };
    }

    @Nonnull
    public static String getTraitType(@Nonnull String type) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "use item" -> "使用物品";
            case "passive" -> "被动";
            default -> ChatUtils.humanize(type);
        };
    }
}
