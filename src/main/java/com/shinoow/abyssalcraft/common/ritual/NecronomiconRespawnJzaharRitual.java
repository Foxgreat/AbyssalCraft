package com.shinoow.abyssalcraft.common.ritual;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.common.structures.omothol.StructureJzaharTemple;
import com.shinoow.abyssalcraft.common.util.SpecialTextUtil;

public class NecronomiconRespawnJzaharRitual extends NecronomiconRitual {

	public NecronomiconRespawnJzaharRitual() {
		super("respawnJzahar", 3, AbyssalCraft.configDimId3, 20000F,
				new Object[]{new ItemStack(AbyssalCraft.essence, 1, 2), AbyssalCraft.oblivionshard,
				new ItemStack(AbyssalCraft.essence, 1, 2), AbyssalCraft.oblivionshard,
				new ItemStack(AbyssalCraft.essence, 1, 2), AbyssalCraft.oblivionshard,
				new ItemStack(AbyssalCraft.essence, 1, 2), AbyssalCraft.oblivionshard});

	}

	@Override
	public boolean canCompleteRitual(World world, BlockPos pos, EntityPlayer player) {

		return pos.getX() == 4 && pos.getY() == 54 && pos.getZ() == 85;
	}

	@Override
	protected void completeRitualClient(World world, BlockPos pos, EntityPlayer player) {
		SpecialTextUtil.JzaharGroup(world, I18n.translateToLocalFormatted("message.jzahar.respawn", player.getName()));
	}

	@Override
	protected void completeRitualServer(World world, BlockPos pos, EntityPlayer player) {
		StructureJzaharTemple temple = new StructureJzaharTemple();
		temple.generate(world, world.rand, new BlockPos(4, 53, 7));
		world.getChunkFromBlockCoords(pos).setChunkModified();
	}
}