package twopiradians.blockArmor.client.gui;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;

public class EntityGuiPlayer extends AbstractClientPlayerEntity {
	
	private AbstractClientPlayerEntity player;
	private static ScorePlayerTeam team;

	public EntityGuiPlayer(ClientWorld worldIn, GameProfile playerProfile, AbstractClientPlayerEntity player) {
		super(worldIn, playerProfile);
		this.player = player;
		
		if (team == null) {
			team = Minecraft.getInstance().world.getScoreboard().createTeam("");
			team.setNameTagVisibility(Team.Visible.NEVER);
		}
	}

	@Override
	public Team getTeam() {
		return team;
	}

	@Override
	public ResourceLocation getLocationSkin() {
		if (player != null) {
			NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
			return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
		}
		else 
			return super.getLocationSkin();
	}
}
