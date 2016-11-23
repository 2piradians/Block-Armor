package twopiradians.blockArmor.client.gui.armorDisplay;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityGuiPlayer extends AbstractClientPlayer
{
	private AbstractClientPlayer player;

	public EntityGuiPlayer(World worldIn, GameProfile playerProfile, AbstractClientPlayer thePlayer) 
	{
		super(worldIn, playerProfile);
		this.player = thePlayer;
	}

	@Override
	public ResourceLocation getLocationSkin()
	{
		if (player != null) {
			NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
			return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
		}
		else 
			return super.getLocationSkin();
	}
}
