package net.jandie1505.bedwars.lobby.setup;

import java.util.List;

public class MapData {
    private final String name;
    private final String world;
    private final int respawnCooldown;
    private final int maxTime;
    private final List<LobbyTeamData> teams;
    private final List<LobbyGeneratorData> globalGenerators;
    private final List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions;

    public MapData(String name, String world, int respawnCooldown, int maxTime, List<LobbyTeamData> teams, List<LobbyGeneratorData> globalGenerators, List<LobbyGeneratorUpgradeTimeActionData> generatorUpgradeTimeActions) {
        this.name = name;
        this.world = world;
        this.respawnCooldown = respawnCooldown;
        this.maxTime = maxTime;
        this.teams = teams;
        this.globalGenerators = globalGenerators;
        this.generatorUpgradeTimeActions = generatorUpgradeTimeActions;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public int getRespawnCooldown() {
        return respawnCooldown;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public List<LobbyTeamData> getTeams() {
        return teams;
    }

    public List<LobbyGeneratorData> getGlobalGenerators() {
        return globalGenerators;
    }

    public List<LobbyGeneratorUpgradeTimeActionData> getGeneratorUpgradeTimeActions() {
        return generatorUpgradeTimeActions;
    }
}
