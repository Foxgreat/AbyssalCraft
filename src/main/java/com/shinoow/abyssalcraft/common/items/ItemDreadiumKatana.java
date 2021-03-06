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
package com.shinoow.abyssalcraft.common.items;

import com.shinoow.abyssalcraft.api.item.ACItems;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;

public class ItemDreadiumKatana extends ItemACSword {

	public static ToolMaterial hilt = EnumHelper.addToolMaterial("dreadiumkatana_hilt", 0, 200, 1, 1, 0).setRepairItem(new ItemStack(ACItems.crystal, 1, 14));
	public static ToolMaterial katana = EnumHelper.addToolMaterial("dreadiumkatana_katana", 0, 1000, 1, 11, 0).setRepairItem(new ItemStack(ACItems.crystal, 1, 14));

	public ItemDreadiumKatana(String par1Str, ToolMaterial mat){
		super(mat, par1Str);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(stack.isItemEnchanted())
			stack.getTagCompound().removeTag("ench");
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}
}
