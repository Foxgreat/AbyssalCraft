/**
 * AbyssalCraft
 * Copyright 2012-2015 Shinoow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shinoow.abyssalcraft.client.render.entity;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.shinoow.abyssalcraft.common.entity.anti.EntityAntiCreeper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAntiCreeper extends RenderLiving {

	private static final ResourceLocation creeperTextures = new ResourceLocation("abyssalcraft:textures/model/anti/creeper.png");

	public RenderAntiCreeper()
	{
		super(new ModelCreeper(), 0.5F);
	}

	protected void preRenderCallback(EntityAntiCreeper par1EntityAntiCreeper, float par2)
	{
		float f1 = par1EntityAntiCreeper.getCreeperFlashIntensity(par2);
		float f2 = 1.0F + MathHelper.sin(f1 * 100.0F) * f1 * 0.01F;

		if (f1 < 0.0F)
			f1 = 0.0F;

		if (f1 > 1.0F)
			f1 = 1.0F;

		f1 *= f1;
		f1 *= f1;
		float f3 = (1.0F + f1 * 0.4F) * f2;
		float f4 = (1.0F + f1 * 0.1F) / f2;
		GL11.glScalef(f3, f4, f3);
	}

	protected int getColorMultiplier(EntityAntiCreeper par1EntityAntiCreeper, float par2, float par3)
	{
		float f2 = par1EntityAntiCreeper.getCreeperFlashIntensity(par3);

		if ((int)(f2 * 10.0F) % 2 == 0)
			return 0;
		else
		{
			int i = (int)(f2 * 0.2F * 255.0F);

			if (i < 0)
				i = 0;

			if (i > 255)
				i = 255;

			short short1 = 255;
			short short2 = 255;
			short short3 = 255;
			return i << 24 | short1 << 16 | short2 << 8 | short3;
		}
	}

	protected ResourceLocation getEntityTexture(EntityAntiCreeper par1EntityAntiCreeper)
	{
		return creeperTextures;
	}

	@Override
	protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
	{
		this.preRenderCallback((EntityAntiCreeper)par1EntityLivingBase, par2);
	}

	@Override
	protected int getColorMultiplier(EntityLivingBase par1EntityLivingBase, float par2, float par3)
	{
		return this.getColorMultiplier((EntityAntiCreeper)par1EntityLivingBase, par2, par3);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
	{
		return this.getEntityTexture((EntityAntiCreeper)par1Entity);
	}
}