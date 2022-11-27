package me.justahuman.spiritsunchained.managers;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlock;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AltarRecipe;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.AbstractEnergyProvider;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GoldPan;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.data.persistent.PersistentDataAPI;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import me.justahuman.spiritsunchained.SpiritsUnchained;
import me.justahuman.spiritsunchained.implementation.mobs.AbstractCustomMob;
import me.justahuman.spiritsunchained.implementation.multiblocks.Tier1Altar;
import me.justahuman.spiritsunchained.implementation.multiblocks.Tier2Altar;
import me.justahuman.spiritsunchained.implementation.multiblocks.Tier3Altar;
import me.justahuman.spiritsunchained.utils.Keys;
import me.justahuman.spiritsunchained.utils.ParticleUtils;
import me.justahuman.spiritsunchained.utils.PlayerUtils;
import me.justahuman.spiritsunchained.utils.SpiritTraits;
import me.justahuman.spiritsunchained.utils.SpiritUtils;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineFuel;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandManager implements TabExecutor {

    final Set<String> spiritTypes = SpiritsUnchained.getSpiritEntityManager().entityMap.keySet();
    final List<String> entityTypes = SpiritUtils.getTypes();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (! (sender instanceof Player player ) || args.length == 0) {
            return false;
        }
        if (useCommand("TestParticles", player, 0, 2, args)) {
            return testParticles(player, args[1]);
        }
        else if (useCommand("SummonSpirit", player, 0, 2, args)) {
            return summonSpirit(args[1], player, args.length >= 3 ? args[2] : "COW");
        }
        else if (useCommand("GiveSpirit", player, 0, 2, args)) {
            return giveSpirit(player, args[1], args.length >= 3 ? args[2] : "Friendly");
        }
        else if (useCommand("EditItem", player, 0, 2, args)) {
            return editItem(player, args[1], args.length >= 3 ? args[2] : "Blank");
        }
        else if (useCommand("ResetCooldowns", player, 0, 1, args)) {
            return resetCooldown(player, args.length >= 2 ? args[1] : player.getName());
        }
        else if (useCommand("Altar", player, 0, 2, args)) {
            return visualizeAltar(player, args[1]);
        }
        else if (useCommand("GenerateEMI", player, 0, 1, args)) {
            return generateEMI();
        }
        else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (! (sender instanceof Player player ) || args.length == 0) {
            return new ArrayList<>();
        }
        if (command.getName().equalsIgnoreCase("spirits")) {
            final List<String> l = new ArrayList<>();
            final Map<String, Integer> add = new HashMap<>();
            if (args.length == 1) {
                add.put("Altar", 0);
                add.put("TestParticles", 0);
                add.put("GiveSpirit", 0);
                add.put("SummonSpirit", 0);
                add.put("EditItem", 0);
                add.put("ResetCooldowns", 0);
                add.put("GenerateEMI", 0);
            }

            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("TestParticles")) {
                    add.put("Catch", 1);
                    add.put("Bottle", 1);
                    add.put("PassOn", 1);
                }
                else if (args[0].equalsIgnoreCase("Altar")) {
                    add.put("1", 1);
                    add.put("2", 1);
                    add.put("3", 1);
                }
                else if (args[0].equalsIgnoreCase("SummonSpirit")) {
                    for (String string : spiritTypes) {
                        add.put(string, 1);
                    }
                }
                else if (args[0].equalsIgnoreCase("GiveSpirit")) {
                    for (String type : entityTypes) {
                        add.put(type, 1);
                    }
                }
                else if (args[0].equalsIgnoreCase("EditItem")) {
                    add.put("State", 1);
                    add.put("Progress", 1);
                    add.put("Max", 1);
                }
                else if (args[0].equalsIgnoreCase("ResetCooldowns")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        add.put(onlinePlayer.getName(), 1);
                    }
                }
            }

            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("SummonSpirit") && args[1].equalsIgnoreCase("UNIDENTIFIED_SPIRIT")) {
                    for (EntityType type : SpiritsUnchained.getSpiritsManager().getSpiritMap().keySet()) {
                        add.put(String.valueOf(type), 2);
                    }
                }
                else if ((args[0].equalsIgnoreCase("EditItem") && args[1].equalsIgnoreCase("State")) || (args[0].equalsIgnoreCase("GiveSpirit") && entityTypes.contains(args[1]))) {
                    for (String state : SpiritUtils.getStates()) {
                        add.put(state, 2);
                    }
                }
                else if (args[0].equalsIgnoreCase("EditItem") && args[1].equalsIgnoreCase("Progress")) {
                    add.put("1", 2);
                    add.put("10", 2);
                    add.put("25", 2);
                    add.put("50", 2);
                    add.put("75", 2);
                    add.put("100", 2);
                }
            }

            //Change displays based on the current message & Permissions
            for (Map.Entry<String, Integer> entry : add.entrySet()) {
                final String toAdd = entry.getKey();
                final Integer index = entry.getValue();
                if (toAdd.toLowerCase().contains(args[index].toLowerCase()) && (index != 0 || hasPerm(player, toAdd))) {
                    l.add(toAdd);
                }
            }

            return l;
        }
        return Collections.emptyList();
    }

    private boolean useCommand(String command, Player player, int index, int size, String[] args) {
        return args[index].equalsIgnoreCase(command) && args.length >= size && hasPerm(player, command);
    }

    private boolean hasPerm(Player player, String command) {
        return player.isOp() || player.hasPermission("spiritsunchained.*") || player.hasPermission("spiritsunchained." + command.toLowerCase());
    }

    private boolean visualizeAltar(Player player, String altar) {
        final Location location = player.getLocation();
        final World world = player.getWorld();

        if (!(altar.equals("1") || altar.equals("2") || altar.equals("3"))) {
            return sendError(player, "altar.error.altar_tier");
        }

        if (PersistentDataAPI.hasLong(player, Keys.visualizing) && PersistentDataAPI.getLong(player, Keys.visualizing) > System.currentTimeMillis()) {
            return sendError(player, "altar.error.altar_multiple");
        }

        PersistentDataAPI.setLong(player, Keys.visualizing, System.currentTimeMillis() + 30 * 1000L);

        final Map<Vector, Material> altarMap = switch(altar) {
            case "3" -> Tier3Altar.getBlocks();
            case "2" -> Tier2Altar.getBlocks();
            default -> Tier1Altar.getBlocks();
        };

        player.sendMessage(SpiritUtils.getTranslation("messages.commands.altar.use").replace("{altar_tier}", altar));
        player.sendMessage(SpiritUtils.getTranslation("messages.commands.altar.materials"));

        final Set<Material> sent = new HashSet<>();
        for (Material material : altarMap.values()) {
            if (!sent.contains(material)) {
                sent.add(material);
                final String tier = switch(altar) {
                    case "3" -> " III";
                    case "2" -> " II";
                    default -> " I";
                };
                final String slimefunVersion = ChatUtils.humanize(material.name()).replace("Chiseled Quartz", "Charged Core").replace("Quartz", "Charged Quartz").replace("Block", "") + tier;
                player.sendMessage(ChatColors.color("&6" + Collections.frequency(altarMap.values(), material) + " &e" + slimefunVersion));
            }
        }

        int entryIndex = 0;
        for (Map.Entry<Vector, Material> entry : altarMap.entrySet()) {
            final int finalEntryIndex = entryIndex;
            Bukkit.getScheduler().runTaskLater(SpiritsUnchained.getInstance(), () -> {
                final Vector changes = entry.getKey();
                final Location relativeLocation = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5).add(changes);
                final BlockData blockData = entry.getValue().createBlockData();
                if (blockData instanceof Directional directional) {
                    final BlockFace face;
                    if ((Math.max(Math.abs(changes.getX()), Math.abs(changes.getZ()))) == Math.abs(changes.getX())) {
                        face = changes.getX() > 0 ? BlockFace.WEST : BlockFace.EAST;
                    } else {
                        face = changes.getZ() > 0 ? BlockFace.NORTH : BlockFace.SOUTH;
                    }
                    directional.setFacing(face);
                }
                final FallingBlock fallingBlock = world.spawnFallingBlock(relativeLocation, blockData);
                fallingBlock.setVelocity(new Vector(0, 0, 0));
                fallingBlock.setGravity(false);
                fallingBlock.setDropItem(false);
                fallingBlock.setPersistent(true);
                fallingBlock.setInvulnerable(true);
                PersistentDataAPI.setString(fallingBlock, Keys.entityKey, "altar");
                Bukkit.getScheduler().runTaskLater(SpiritsUnchained.getInstance(), () -> {
                    if (fallingBlock != null) {
                        fallingBlock.remove();
                    }
                }, (30 * 20) - (finalEntryIndex * 5L));
            }, entryIndex * 5L);
            entryIndex++;
        }

        return true;
    }

    private boolean testParticles(Player player, String test) {
        final Location location = player.getLocation();
        switch (test.toLowerCase()) {
            case "catch" -> ParticleUtils.catchAnimation(location);
            case "bottle" -> ParticleUtils.bottleAnimation(location);
            case "passon" -> ParticleUtils.passOnAnimation(location);
            default -> sendError(player, "particles.error");
        }
        return true;
    }

    private boolean giveSpirit(Player player, String type, String state) {
        final EntityType spiritType;
        try {
            spiritType = EntityType.valueOf(type);
        } catch (IllegalArgumentException | NullPointerException e) {
            return sendError(player, "give_spirit.error");
        }
        final ItemStack spirit = SpiritUtils.spiritItem(state, SpiritsUnchained.getSpiritsManager().getSpiritMap().get(spiritType));
        SpiritUtils.updateSpiritItemProgress(spirit, 100);
        PlayerUtils.addOrDropItem(player, SpiritUtils.spiritItem(state, SpiritsUnchained.getSpiritsManager().getSpiritMap().get(spiritType)));
        return true;
    }

    private boolean summonSpirit(String spiritId, Player player, String type) {
        final AbstractCustomMob<?> spirit = SpiritsUnchained.getSpiritEntityManager().getCustomClass(null, spiritId);
        if (spirit == null || ! SpiritUtils.canSpawn()) {
            return sendError(player, "summon_spirit.error");
        }
        spirit.spawn(player.getLocation(), player.getWorld(), "Natural", type);
        return true;
    }

    private boolean editItem(Player player, String toChange, String changeValue) {
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!SpiritUtils.isSpiritItem(item)) {
            return sendError(player, "edit_item.error.not_holding");
        }
        final ItemMeta meta = item.getItemMeta();
        if (toChange.equalsIgnoreCase("state")) {
            if (!SpiritUtils.getStates().contains(changeValue)) {
                return sendError(player, "edit_item.error.wrong_state");
            }
            PersistentDataAPI.setString(meta, Keys.spiritStateKey, changeValue);
        } else if (toChange.equalsIgnoreCase("progress")) {
            try {
                PersistentDataAPI.setDouble(meta, Keys.spiritProgressKey, Double.parseDouble(changeValue));
            } catch(NullPointerException | NumberFormatException e) {
                return sendError(player, "edit_item.error.wrong_progress");
            }
        } else if (toChange.equalsIgnoreCase("max")) {
            PersistentDataAPI.setString(meta, Keys.spiritStateKey, "Friendly");
            PersistentDataAPI.setDouble(meta, Keys.spiritProgressKey, 100.0);
        }
        item.setItemMeta(meta);
        SpiritUtils.updateSpiritItemProgress(item, 0);
        return true;
    }

    private boolean resetCooldown(Player sender, String playerName) {
        final Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return sendError(sender, "reset_cooldown.error");
        }
        SpiritTraits.resetCooldown(player);
        return true;
    }

    private boolean generateEMI() {
        final JSONObject jsonObject = getData();
        for (SlimefunItem slimefunItem : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (!(slimefunItem.getAddon() instanceof Slimefun)) {
                continue;
            }
            final JSONArray recipeCategory = (JSONArray) jsonObject.getOrDefault(slimefunItem.getId(), new JSONArray());
            if (slimefunItem instanceof AContainer aContainer) {
                for (MachineRecipe machineRecipe : aContainer.getMachineRecipes()) {
                    if (checkRecipe(null, machineRecipe.getInput())) {
                        continue;
                    }
                    final ItemStack[] input = machineRecipe.getInput();
                    final ItemStack[] output = machineRecipe.getOutput();
                    final int energyUsed = machineRecipe.getTicks() * aContainer.getEnergyConsumption() * -1;
                    final JSONObject recipe = fillRecipe(input, output, energyUsed, machineRecipe.getTicks() * 2);
                    recipeCategory.add(recipe);
                }
            } else if (slimefunItem instanceof MultiBlockMachine multiBlockMachine) {
                final List<ItemStack[]> recipes = multiBlockMachine.getRecipes();
                for (ItemStack[] input : recipes) {
                    if (recipes.indexOf(input) % 2 != 0 || checkRecipe(null, input)) {
                        continue;
                    }
                    final ItemStack[] output = recipes.get(recipes.indexOf(input) + 1);
                    final JSONObject recipe = fillRecipe(input, output, null, null);
                    recipeCategory.add(recipe);
                }
            } else if (slimefunItem instanceof GoldPan goldPan) {
                final List<ItemStack> recipes = goldPan.getDisplayRecipes();
                for (ItemStack input : recipes) {
                    if (recipes.indexOf(input) % 2 != 0 || checkRecipe(Collections.singletonList(input), null)) {
                        continue;
                    }
                    final ItemStack output = recipes.get(recipes.indexOf(input) + 1);
                    final JSONObject recipe = fillRecipe(new ItemStack[]{input}, new ItemStack[]{output}, null, null);
                    recipeCategory.add(recipe);
                }
            } else if (slimefunItem instanceof AbstractEnergyProvider abstractEnergyProvider) {
                final Set<MachineFuel> recipes = abstractEnergyProvider.getFuelTypes();
                for (MachineFuel machineFuel : recipes) {
                    if (checkRecipe(Collections.singletonList(machineFuel.getInput()), null)) {
                        continue;
                    }
                    final ItemStack input = machineFuel.getInput();
                    final ItemStack output = machineFuel.getOutput();
                    final int energy = machineFuel.getTicks() * abstractEnergyProvider.getEnergyProduction();
                    final JSONObject recipe = fillRecipe(new ItemStack[]{input}, new ItemStack[]{output}, energy, machineFuel.getTicks() * 2);
                    recipeCategory.add(recipe);
                }
            } else if (slimefunItem instanceof AncientAltar ancientAltar) {
                final List<AltarRecipe> recipes = ancientAltar.getRecipes();
                for (AltarRecipe altarRecipe : recipes) {
                    if (checkRecipe(altarRecipe.getInput(), null)) {
                        continue;
                    }
                    final List<ItemStack> incorrectOrder = altarRecipe.getInput();
                    final List<ItemStack> listInput = new ArrayList<>();
                    listInput.add(incorrectOrder.get(0));
                    listInput.add(incorrectOrder.get(1));
                    listInput.add(incorrectOrder.get(2));
                    listInput.add(incorrectOrder.get(7));
                    listInput.add(altarRecipe.getCatalyst());
                    listInput.add(incorrectOrder.get(3));
                    listInput.add(incorrectOrder.get(6));
                    listInput.add(incorrectOrder.get(5));
                    listInput.add(incorrectOrder.get(4));
                    ItemStack[] input = new ItemStack[listInput.size()];
                    input = listInput.toArray(input);
                    final ItemStack output = altarRecipe.getOutput();
                    final JSONObject recipe = fillRecipe(input, new ItemStack[]{output}, null, 16);
                    recipeCategory.add(recipe);
                }
            }
            if (! recipeCategory.isEmpty()) {
                jsonObject.put(slimefunItem.getId(), recipeCategory);
            }
        }

        writeJson(SpiritsUnchained.getInstance().getDataFolder().getPath() + "/emi.json", jsonObject.toJSONString());
        return true;
    }

    private boolean checkRecipe(List<ItemStack> checkList, ItemStack[] checkArray) {
        boolean toReturn = false;
        if (checkList != null) {
            for (ItemStack itemStack : checkList) {
                toReturn = toReturn || (SlimefunItem.getByItem(itemStack) != null && SlimefunItem.getByItem(itemStack).getAddon() != Slimefun.instance());
            }
        }
        if (checkArray != null) {
            for (ItemStack itemStack : checkArray) {
                toReturn = toReturn || (SlimefunItem.getByItem(itemStack) != null && SlimefunItem.getByItem(itemStack).getAddon() != Slimefun.instance());
            }
        }
        return toReturn;
    }

    private JSONObject fillRecipe(ItemStack[] input, ItemStack[] output, Integer energy, Integer time) {
        final JSONObject recipe = new JSONObject();
        final List<String> inputs = processList(input);
        final List<String> outputs = processList(output);
        recipe.put("inputs", inputs);
        recipe.put("outputs", outputs);
        if (energy != null) {
            recipe.put("energy", energy);
        }
        if (time != null) {
            recipe.put("time", time);
        }
        return recipe;
    }

    private List<String> processList(ItemStack[] process) {
        final List<String> processed = new ArrayList<>();
        for (ItemStack item : process) {
            if (SlimefunItem.getByItem(item) != null) {
                final SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
                processed.add(slimefunItem.getId() + ":" + item.getAmount());
            } else if (item != null) {
                if (item.getType() == Material.EXPERIENCE_BOTTLE && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    final SlimefunItem slimefunItem = SlimefunItems.FLASK_OF_KNOWLEDGE.getItem();
                    processed.add("FILLED_" + slimefunItem.getId() + ":" + item.getAmount());
                } else {
                    processed.add(item.getType().name().toLowerCase() + ":" + item.getAmount());
                }
            } else {
                processed.add("");
            }
        }
        return processed;
    }

    public static JSONObject getData() {
        //JSON parser object to parse read file
        final JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(SpiritsUnchained.getInstance().getDataFolder().getPath() + "/emi.json"))
        {
            //Return the Player's
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static void writeJson(String path, String write) {
        try (final FileWriter file = new FileWriter(path)) {
            //Update the File
            file.write(write);
            file.flush();

        } catch (IOException e) {
            Log("Could not Write to the File: " + path);
            e.printStackTrace();
        }
    }

    public static void Log(String log) {
        SpiritsUnchained.getInstance().getLogger().info(log);
    }

    private boolean sendError(Player player, String path) {
        player.sendMessage(SpiritUtils.getTranslation("messages.commands." + path));
        return true;
    }
}
