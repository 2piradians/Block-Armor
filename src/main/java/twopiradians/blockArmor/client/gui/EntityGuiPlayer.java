package twopiradians.blockArmor.client.gui;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class EntityGuiPlayer extends AbstractClientPlayer {
	
	private AbstractClientPlayer player;
	private static PlayerTeam team;

	public EntityGuiPlayer(ClientLevel worldIn, GameProfile playerProfile, AbstractClientPlayer player) {
		super(worldIn, playerProfile);
		this.player = player;
		
		if (team == null) {
			team = Minecraft.getInstance().level.getScoreboard().addPlayerTeam("");
			team.setNameTagVisibility(Team.Visibility.NEVER);
		}
	}

	@Override
	public Team getTeam() {
		return team;
	}

	@Override
	public ResourceLocation getSkinTextureLocation() {
		if (player != null) {
			PlayerInfo networkplayerinfo = this.getPlayerInfo();
			return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUUID()) : networkplayerinfo.getSkinLocation();
		}
		else 
			return super.getSkinTextureLocation();
	}
}
