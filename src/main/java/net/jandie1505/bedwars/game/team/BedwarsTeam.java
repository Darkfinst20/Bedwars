package net.jandie1505.bedwars.game.team;

import net.jandie1505.bedwars.game.Game;
import net.jandie1505.bedwars.game.player.PlayerData;
import net.jandie1505.bedwars.lobby.setup.LobbyTeamData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.data.type.Bed;

import java.util.*;

public class BedwarsTeam {
    private final Game game;
    private final String name;
    private final ChatColor chatColor;
    private final Color color;
    private final List<Location> spawnpoints;
    private final List<Location> bedLocations;
    private final List<Location> shopVillagerLocations;
    private final List<Location> upgradesVillagerLocations;
    private int attackDamageUpgrade;
    private int protectionUpgrade;
    private int hasteUpgrade;
    private int forgeUpgrade;
    private int healPoolUpgrade;
    private int dragonBuffUpgrade;

    public BedwarsTeam(Game game, LobbyTeamData teamData) {
        this.game = game;
        this.name = teamData.getName();
        this.chatColor = teamData.getChatColor();
        this.color = teamData.getColor();

        this.spawnpoints = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getSpawnpoints())) {
            this.spawnpoints.add(this.game.buildLocationWithWorld(location));
        }

        this.bedLocations = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getBedLocations())) {
            this.bedLocations.add(this.game.buildLocationWithWorld(location));
        }

        this.shopVillagerLocations = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getShopVillagerLocations())) {
            this.shopVillagerLocations.add(this.game.buildLocationWithWorld(location));
        }

        this.upgradesVillagerLocations = Collections.synchronizedList(new ArrayList<>());
        for (Location location : List.copyOf(teamData.getUpgradesVillagerLocations())) {
            this.upgradesVillagerLocations.add(this.game.buildLocationWithWorld(location));
        }

        this.attackDamageUpgrade = 0;
        this.protectionUpgrade = 0;
        this.hasteUpgrade = 0;
        this.forgeUpgrade = 0;
        this.healPoolUpgrade = 0;
        this.dragonBuffUpgrade = 0;
    }

    public String getName() {
        return name;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Color getColor() {
        return color;
    }

    public List<Location> getSpawnpoints() {
        return List.copyOf(this.spawnpoints);
    }

    public Location getRandomSpawnpoint() {

        if (this.spawnpoints.isEmpty()) {
            return null;
        }

        return this.spawnpoints.get(new Random().nextInt(this.spawnpoints.size()));
    }

    public List<Location> getBedLocations() {
        return List.copyOf(this.bedLocations);
    }

    public List<Location> getShopVillagerLocations() {
        return List.copyOf(this.shopVillagerLocations);
    }

    public List<Location> getUpgradesVillagerLocations() {
        return List.copyOf(this.upgradesVillagerLocations);
    }

    public int getId() {
        return this.game.getTeams().indexOf(this);
    }

    public List<UUID> getPlayers() {
        List<UUID> returnList = new ArrayList<>();

        for (UUID playerId : this.game.getPlayers().keySet()) {
            PlayerData playerData = this.game.getPlayers().get(playerId);

            if (playerData.getTeam() == this.getId()) {
                returnList.add(playerId);
            }

        }

        return List.copyOf(returnList);
    }

    public int hasBed() {
        int beds = 0;

        for (Location bedLocation : this.getBedLocations()) {

            if (bedLocation.getBlock().getBlockData() instanceof Bed) {
                beds++;
            }

        }

        return beds;
    }

    public boolean isAlive() {
        return this.getPlayers().size() > 0;
    }

    public Game getGame() {
        return game;
    }

    public int getAttackDamageUpgrade() {
        return attackDamageUpgrade;
    }

    public void setAttackDamageUpgrade(int attackDamageUpgrade) {
        this.attackDamageUpgrade = attackDamageUpgrade;
    }

    public int getProtectionUpgrade() {
        return protectionUpgrade;
    }

    public void setProtectionUpgrade(int protectionUpgrade) {
        this.protectionUpgrade = protectionUpgrade;
    }

    public int getHasteUpgrade() {
        return hasteUpgrade;
    }

    public void setHasteUpgrade(int hasteUpgrade) {
        this.hasteUpgrade = hasteUpgrade;
    }

    public int getForgeUpgrade() {
        return forgeUpgrade;
    }

    public void setForgeUpgrade(int forgeUpgrade) {
        this.forgeUpgrade = forgeUpgrade;
    }

    public int getHealPoolUpgrade() {
        return healPoolUpgrade;
    }

    public void setHealPoolUpgrade(int healPoolUpgrade) {
        this.healPoolUpgrade = healPoolUpgrade;
    }

    public int getDragonBuffUpgrade() {
        return dragonBuffUpgrade;
    }

    public void setDragonBuffUpgrade(int dragonBuffUpgrade) {
        this.dragonBuffUpgrade = dragonBuffUpgrade;
    }

    public int getTeamUpgrade(TeamUpgrade teamUpgrade) {

        if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getSharpnessUpgrade()) {
            return this.getAttackDamageUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getProtectionUpgrade()) {
            return this.getProtectionUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHasteUpgrade()) {
            return this.getHasteUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getForgeUpgrade()) {
            return this.getForgeUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHealPoolUpgrade()) {
            return this.getHealPoolUpgrade();
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getDragonBuffUpgrade()) {
            return this.getDragonBuffUpgrade();
        } else {
            return -1;
        }

    }

    public void setTeamUpgrade(TeamUpgrade teamUpgrade, int value) {

        if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getSharpnessUpgrade()) {
            this.attackDamageUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getProtectionUpgrade()) {
            this.protectionUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHasteUpgrade()) {
            this.hasteUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getForgeUpgrade()) {
            this.forgeUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getHealPoolUpgrade()) {
            this.healPoolUpgrade = value;
        } else if (teamUpgrade == this.getGame().getTeamUpgradesConfig().getDragonBuffUpgrade()) {
            this.dragonBuffUpgrade = value;
        }

    }
}
