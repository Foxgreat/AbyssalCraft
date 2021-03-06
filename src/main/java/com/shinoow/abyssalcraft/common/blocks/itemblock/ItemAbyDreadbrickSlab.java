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
package com.shinoow.abyssalcraft.common.blocks.itemblock;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DimensionCondition;
import com.shinoow.abyssalcraft.init.BlockHandler;
import com.shinoow.abyssalcraft.lib.ACLib;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;

public class ItemAbyDreadbrickSlab extends ItemSlabAC {

	public ItemAbyDreadbrickSlab(Block block) {
		super(block, (BlockSlab)ACBlocks.abyssalnite_stone_brick_slab, (BlockSlab)BlockHandler.abydreadbrickslab2);
		setMaxDamage(0);
		setHasSubtypes(true);
		setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id));
	}
}
