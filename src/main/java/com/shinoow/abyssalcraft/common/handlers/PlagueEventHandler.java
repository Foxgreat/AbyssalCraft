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
package com.shinoow.abyssalcraft.common.handlers;

import java.util.Collections;
import java.util.List;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;

import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlagueEventHandler {


	@SubscribeEvent
	public void onDeath(LivingDeathEvent event){
		if(event.getEntityLiving().world.isRemote) return;

		EntityLivingBase entity = event.getEntityLiving();
		if(entity.isPotionActive(AbyssalCraftAPI.coralium_plague))
			if(entity.getRNG().nextFloat() > 0.1F) {
				EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(entity.world, entity.posX, entity.posY, entity.posZ);
				entityareaeffectcloud.addEffect(new PotionEffect(AbyssalCraftAPI.coralium_plague, 600));
				entityareaeffectcloud.setRadius(entity.width);
				entityareaeffectcloud.setDuration(100 + entity.getRNG().nextInt(100));
				entityareaeffectcloud.setRadiusPerTick((3F - entityareaeffectcloud.getRadius()) / entityareaeffectcloud.getDuration());

				entity.world.spawnEntity(entityareaeffectcloud);
			} else {

				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(6.0D, 2.0D, 6.0D);
				List<EntityLivingBase> list = entity.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

				if (!list.isEmpty())
					for (EntityLivingBase entitylivingbase : list)
						if (entitylivingbase.canBeHitWithPotion() && !EntityUtil.isEntityCoralium(entitylivingbase))
						{
							double d0 = entity.getDistanceSqToEntity(entitylivingbase);

							if (d0 < 32.0D)
							{
								double d1 = 1.0D - Math.sqrt(d0) / 8.0D;

								PotionEffect potioneffect = entity.getActivePotionEffect(AbyssalCraftAPI.coralium_plague);

								int i = (int)(d1 * potioneffect.getDuration() + 0.5D);

								if (i > 20)
									entitylivingbase.addPotionEffect(new PotionEffect(AbyssalCraftAPI.coralium_plague, i, potioneffect.getAmplifier(), potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
							}
						}
				entity.world.playEvent(2002, new BlockPos(entity), PotionUtils.getPotionColorFromEffectList(Collections.singletonList(entity.getActivePotionEffect(AbyssalCraftAPI.coralium_plague))));

			}
		if(entity.isPotionActive(AbyssalCraftAPI.dread_plague))
			if(entity.getRNG().nextFloat() > 0.1F) {
				EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(entity.world, entity.posX, entity.posY, entity.posZ);
				entityareaeffectcloud.addEffect(new PotionEffect(AbyssalCraftAPI.dread_plague, 600));
				entityareaeffectcloud.setRadius(entity.width);
				entityareaeffectcloud.setDuration(100 + entity.getRNG().nextInt(100));
				entityareaeffectcloud.setRadiusPerTick((3F - entityareaeffectcloud.getRadius()) / entityareaeffectcloud.getDuration());

				entity.world.spawnEntity(entityareaeffectcloud);
			} else {

				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(6.0D, 2.0D, 6.0D);
				List<EntityLivingBase> list = entity.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

				if (!list.isEmpty())
					for (EntityLivingBase entitylivingbase : list)
						if (entitylivingbase.canBeHitWithPotion() && !EntityUtil.isEntityDread(entitylivingbase))
						{
							double d0 = entity.getDistanceSqToEntity(entitylivingbase);

							if (d0 < 32.0D)
							{
								double d1 = 1.0D - Math.sqrt(d0) / 8.0D;

								PotionEffect potioneffect = entity.getActivePotionEffect(AbyssalCraftAPI.dread_plague);

								int i = (int)(d1 * potioneffect.getDuration() + 0.5D);

								if (i > 20)
									entitylivingbase.addPotionEffect(new PotionEffect(AbyssalCraftAPI.dread_plague, i, potioneffect.getAmplifier(), potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
							}
						}
				entity.world.playEvent(2002, new BlockPos(entity), PotionUtils.getPotionColorFromEffectList(Collections.singletonList(entity.getActivePotionEffect(AbyssalCraftAPI.dread_plague))));

			}
	}

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event){
		if(event.getEntityLiving().world.isRemote) return;

		DamageSource source = event.getSource();
		if(source != null && source.getEntity() instanceof EntityLivingBase) {
			EntityLivingBase attacker = (EntityLivingBase) source.getEntity();
			if(attacker.isPotionActive(AbyssalCraftAPI.coralium_plague) && !EntityUtil.isEntityCoralium(event.getEntityLiving()))
				if(event.getEntityLiving().getRNG().nextFloat() > 0.5F)
					event.getEntityLiving().addPotionEffect(getEffect(attacker.getActivePotionEffect(AbyssalCraftAPI.coralium_plague)));
			if(attacker.isPotionActive(AbyssalCraftAPI.dread_plague) && !EntityUtil.isEntityDread(event.getEntityLiving()))
				if(event.getEntityLiving().getRNG().nextFloat() > 0.5F)
					event.getEntityLiving().addPotionEffect(getEffect(attacker.getActivePotionEffect(AbyssalCraftAPI.dread_plague)));
		}
	}

	public static PotionEffect getEffect(PotionEffect effect) {

		int duration = effect.getDuration() >= 600 ? effect.getDuration() : 600;

		return new PotionEffect(effect.getPotion(), duration, effect.getAmplifier());
	}
}