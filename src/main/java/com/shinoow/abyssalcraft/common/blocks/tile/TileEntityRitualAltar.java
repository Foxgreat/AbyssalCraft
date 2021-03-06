/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2018 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import java.util.List;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.IEnergyTransporterItem;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionHandler;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.event.ACEvents.DisruptionEvent;
import com.shinoow.abyssalcraft.api.event.ACEvents.RitualEvent;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.items.ItemNecronomicon;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.RitualMessage;
import com.shinoow.abyssalcraft.lib.ACSounds;
import com.shinoow.abyssalcraft.lib.util.blocks.IRitualAltar;
import com.shinoow.abyssalcraft.lib.util.blocks.IRitualPedestal;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TileEntityRitualAltar extends TileEntity implements ITickable, IRitualAltar {

	private int ritualTimer;
	private ItemStack[] offers = new ItemStack[8];
	private boolean[] hasOffer = new boolean[8];
	private int[][] offerData = new int[8][2];
	private NecronomiconRitual ritual;
	private ItemStack item = ItemStack.EMPTY;
	private int rot;
	private EntityPlayer user;
	private float consumedEnergy;
	private boolean isDirty;
	private EntityLiving sacrifice;

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		NBTTagCompound nbtItem = nbttagcompound.getCompoundTag("Item");
		item = new ItemStack(nbtItem);
		rot = nbttagcompound.getInteger("Rot");
		ritualTimer = nbttagcompound.getInteger("Cooldown");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		NBTTagCompound nbtItem = new NBTTagCompound();
		if(!item.isEmpty())
			item.writeToNBT(nbtItem);
		nbttagcompound.setTag("Item", nbtItem);
		nbttagcompound.setInteger("Rot", rot);
		nbttagcompound.setInteger("Cooldown", ritualTimer);

		return nbttagcompound;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void update()
	{
		if(isDirty || isPerformingRitual()){
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
			isDirty = false;
		}

		if(isPerformingRitual()){
			if(ritualTimer == 1){
				SoundEvent chant = getRandomChant();
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), chant, SoundCategory.PLAYERS, 1, 1, true);
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), chant, SoundCategory.PLAYERS, 1, 1, true);
				world.playSound(pos.getX(), pos.getY(), pos.getZ(), chant, SoundCategory.PLAYERS, 1, 1, true);
			}
			ritualTimer++;

			if(ritual != null){
				if(sacrifice != null && sacrifice.isEntityAlive())
					world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, sacrifice.posX, sacrifice.posY + sacrifice.getEyeHeight(), sacrifice.posZ, 0, 0, 0);
				if(!world.isRemote)
					if(user != null)
						collectPEFromPlayer();
					else user = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, true);
				if(ritualTimer == 200)
					if(user != null && !world.isRemote){
						if(!MinecraftForge.EVENT_BUS.post(new RitualEvent.Post(user, ritual, world, pos))){
							collectPEFromPlayer();
							if(consumedEnergy == ritual.getReqEnergy() && (sacrifice == null || !sacrifice.isEntityAlive()))
								ritual.completeRitual(world, pos, user);
							else
								triggerDisruption();
							reset();
						}
					} else {
						if(!world.isRemote)
							triggerDisruption();
						reset();
					}
			} else ritualTimer = 0;

			world.spawnParticle(EnumParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0,0,0);

			double n = 0.5;

			if(hasOffer[0])
				spawnParticles(-2.5, 0.5, n, 0, offerData[0]);
			if(hasOffer[1])
				spawnParticles(0.5, -2.5, 0, n, offerData[1]);
			if(hasOffer[2])
				spawnParticles(3.5, 0.5, -n, 0, offerData[2]);
			if(hasOffer[3])
				spawnParticles(0.5, 3.5, 0, -n, offerData[3]);
			if(hasOffer[4])
				spawnParticles(-1.5, 2.5, n, -n, offerData[4]);
			if(hasOffer[5])
				spawnParticles(-1.5, -1.5, n, n, offerData[5]);
			if(hasOffer[6])
				spawnParticles(2.5, 2.5, -n, -n, offerData[6]);
			if(hasOffer[7])
				spawnParticles(2.5, -1.5, -n, n, offerData[7]);
		}

		if(rot == 360)
			rot = 0;
		if(!item.isEmpty())
			rot++;
	}

	private void spawnParticles(double xOffset, double zOffset, double velX, double velZ, int[] data) {
		world.spawnParticle(EnumParticleTypes.ITEM_CRACK, pos.getX() + xOffset, pos.getY() + 0.95, pos.getZ() + zOffset, velX,.15,velZ, data);
		world.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, 0,0,0);
		world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + xOffset, pos.getY() + 1.05, pos.getZ() + zOffset, 0,0,0);
	}

	private void reset() {
		ritualTimer = 0;
		user = null;
		ritual = null;
		consumedEnergy = 0;
		isDirty = true;
		sacrifice = null;
	}

	private void collectPEFromPlayer() {
		for(ItemStack stack : user.inventory.mainInventory)
			if(!stack.isEmpty() && stack.getItem() instanceof IEnergyTransporterItem &&
					((IEnergyTransporterItem) stack.getItem()).canTransferPEExternally(stack) &&
					(stack.getItem() instanceof ItemNecronomicon && ((ItemNecronomicon)stack.getItem()).isOwner(user, stack) || !(stack.getItem() instanceof ItemNecronomicon))){
				consumedEnergy += ((IEnergyTransporterItem) stack.getItem()).consumeEnergy(stack, ritual.getReqEnergy()/200);
				break;
			}
	}

	private void triggerDisruption() {
		world.addWeatherEffect(new EntityLightningBolt(world, pos.getX(), pos.getY() + 1, pos.getZ(), true));
		DeityType deity = DeityType.values()[world.rand.nextInt(DeityType.values().length)];
		List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(16, 16, 16));
		if(user != null) {
			RitualEvent.Failed event = new RitualEvent.Failed(user, ritual, DisruptionHandler.instance().getRandomDisruption(deity, world), world, pos);
			if(!MinecraftForge.EVENT_BUS.post(event)) {
				DisruptionEntry disruption = event.getDisruption();
				PacketDispatcher.sendToAllAround(new RitualMessage(ritual.getUnlocalizedName().substring("ac.ritual.".length()), pos, true, disruption.getUnlocalizedName()), user, 5);
				if(!MinecraftForge.EVENT_BUS.post(new DisruptionEvent(deity, world, pos, players, disruption)))
					disruption.disrupt(world, pos, players);
				AbyssalCraftAPI.getInternalMethodHandler().sendDisruption(deity, disruption.getUnlocalizedName().substring("ac.disruption.".length()), pos, world.provider.getDimension());
			} else DisruptionHandler.instance().generateDisruption(deity, world, pos, players);
		} else DisruptionHandler.instance().generateDisruption(deity, world, pos, players);
	}

	@Override
	public boolean canPerform(){

		if(checkSurroundings(world, pos)) return true;
		return false;
	}

	@Override
	public boolean checkSurroundings(World world, BlockPos pos){
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		TileEntity ped1 = world.getTileEntity(new BlockPos(x - 3, y, z));
		TileEntity ped2 = world.getTileEntity(new BlockPos(x, y, z - 3));
		TileEntity ped3 = world.getTileEntity(new BlockPos(x + 3, y, z));
		TileEntity ped4 = world.getTileEntity(new BlockPos(x, y, z + 3));
		TileEntity ped5 = world.getTileEntity(new BlockPos(x - 2, y, z + 2));
		TileEntity ped6 = world.getTileEntity(new BlockPos(x - 2, y, z - 2));
		TileEntity ped7 = world.getTileEntity(new BlockPos(x + 2, y, z + 2));
		TileEntity ped8 = world.getTileEntity(new BlockPos(x + 2, y, z - 2));
		if(ped1 instanceof IRitualPedestal && ped2 instanceof IRitualPedestal && ped3 instanceof IRitualPedestal
				&& ped4 instanceof IRitualPedestal && ped5 instanceof IRitualPedestal && ped6 instanceof IRitualPedestal
				&& ped7 instanceof IRitualPedestal && ped8 instanceof IRitualPedestal){
			offers[0] = ((IRitualPedestal)ped1).getItem();
			offers[1] = ((IRitualPedestal)ped2).getItem();
			offers[2] = ((IRitualPedestal)ped3).getItem();
			offers[3] = ((IRitualPedestal)ped4).getItem();
			offers[4] = ((IRitualPedestal)ped5).getItem();
			offers[5] = ((IRitualPedestal)ped6).getItem();
			offers[6] = ((IRitualPedestal)ped7).getItem();
			offers[7] = ((IRitualPedestal)ped8).getItem();
			for(int i = 0; i < 8; i++) {
				ItemStack stack = offers[i];
				if(!stack.isEmpty()) {
					offerData[i] = new int[]{Item.getIdFromItem(stack.getItem()), stack.getMetadata()};
					hasOffer[i] = true;
				}
			}
			if(offers[0].isEmpty() && offers[1].isEmpty() && offers[2].isEmpty() && offers[3].isEmpty() && offers[4].isEmpty() &&
					offers[5].isEmpty() && offers[6].isEmpty() && offers[7].isEmpty()) return false;
			else return true;
		}
		return false;
	}

	@Override
	public void resetPedestals(World world, BlockPos pos){
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		TileEntity ped1 = world.getTileEntity(new BlockPos(x-3, y, z));
		TileEntity ped2 = world.getTileEntity(new BlockPos(x, y, z - 3));
		TileEntity ped3 = world.getTileEntity(new BlockPos(x + 3, y, z));
		TileEntity ped4 = world.getTileEntity(new BlockPos(x, y, z + 3));
		TileEntity ped5 = world.getTileEntity(new BlockPos(x - 2, y, z + 2));
		TileEntity ped6 = world.getTileEntity(new BlockPos(x - 2, y, z - 2));
		TileEntity ped7 = world.getTileEntity(new BlockPos(x + 2, y, z + 2));
		TileEntity ped8 = world.getTileEntity(new BlockPos(x + 2, y, z - 2));
		if(ped1 instanceof IRitualPedestal && ped2 instanceof IRitualPedestal && ped3 instanceof IRitualPedestal
				&& ped4 instanceof IRitualPedestal && ped5 instanceof IRitualPedestal && ped6 instanceof IRitualPedestal
				&& ped7 instanceof IRitualPedestal && ped8 instanceof IRitualPedestal){
			((IRitualPedestal)ped1).setItem(getStack(((IRitualPedestal)ped1).getItem()));
			((IRitualPedestal)ped2).setItem(getStack(((IRitualPedestal)ped2).getItem()));
			((IRitualPedestal)ped3).setItem(getStack(((IRitualPedestal)ped3).getItem()));
			((IRitualPedestal)ped4).setItem(getStack(((IRitualPedestal)ped4).getItem()));
			((IRitualPedestal)ped5).setItem(getStack(((IRitualPedestal)ped5).getItem()));
			((IRitualPedestal)ped6).setItem(getStack(((IRitualPedestal)ped6).getItem()));
			((IRitualPedestal)ped7).setItem(getStack(((IRitualPedestal)ped7).getItem()));
			((IRitualPedestal)ped8).setItem(getStack(((IRitualPedestal)ped8).getItem()));
		}
	}

	private ItemStack getStack(ItemStack stack){
		if(!stack.isEmpty() && stack.getItem().hasContainerItem(stack))
			return stack.getItem().getContainerItem(stack);
		else return ItemStack.EMPTY;
	}

	@Override
	public void performRitual(World world, BlockPos pos, EntityPlayer player){

		if(!isPerformingRitual()){
			ItemStack item = player.getHeldItemMainhand();
			if(item.getItem() instanceof ItemNecronomicon && ((ItemNecronomicon)item.getItem()).isOwner(player, item))
				if(RitualRegistry.instance().canPerformAction(world.provider.getDimension(), ((ItemNecronomicon)item.getItem()).getBookType()))
					if(canPerform()){
						ritual = RitualRegistry.instance().getRitual(world.provider.getDimension(), ((ItemNecronomicon)item.getItem()).getBookType(), offers, this.item);
						if(ritual != null)
							if(ritual.requiresSacrifice()){
								if(!world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos).grow(4, 4, 4)).isEmpty())
									for(EntityLiving mob : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos).grow(4, 4, 4)))
										if(canBeSacrificed(mob))
											if(ritual.canCompleteRitual(world, pos, player))
												if(!MinecraftForge.EVENT_BUS.post(new RitualEvent.Pre(player, ritual, world, pos))){
													sacrifice = mob;
													ritualTimer = 1;
													resetPedestals(world, pos);
													user = player;
													consumedEnergy = 0;
													isDirty = true;
													return;
												}
							} else if(ritual.canCompleteRitual(world, pos, player))
								if(!MinecraftForge.EVENT_BUS.post(new RitualEvent.Pre(player, ritual, world, pos))){
									ritualTimer = 1;
									resetPedestals(world, pos);
									user = player;
									consumedEnergy = 0;
									isDirty = true;
								}
					}
		}
	}

	/**
	 * Checks if a certain Entity can be sacrificed
	 * @param entity Entity to potentially sacrifice
	 * @return True if the Entity can be sacrificed, otherwise false
	 */
	private boolean canBeSacrificed(EntityLiving entity){
		return (EntityUtil.isShoggothFood(entity) || entity instanceof EntityVillager) &&
				entity.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD &&
				entity.isEntityAlive() && !entity.isChild();
	}

	public SoundEvent getRandomChant(){
		SoundEvent[] chants = {ACSounds.cthulhu_chant, ACSounds.yog_sothoth_chant_1, ACSounds.yog_sothoth_chant_2,
				ACSounds.hastur_chant_1, ACSounds.hastur_chant_2, ACSounds.sleeping_chant, ACSounds.cthugha_chant};
		return chants[world.rand.nextInt(chants.length)];
	}

	@Override
	public int getRitualCooldown(){
		return ritualTimer;
	}

	@Override
	public boolean isPerformingRitual(){
		return ritualTimer < 200 && ritualTimer > 0;
	}

	@Override
	public int getRotation(){
		return rot;
	}

	@Override
	public ItemStack getItem(){
		return item;
	}

	@Override
	public void setItem(ItemStack item){
		this.item = item;
		isDirty = true;
	}
}
