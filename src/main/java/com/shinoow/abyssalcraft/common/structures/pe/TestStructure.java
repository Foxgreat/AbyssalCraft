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
package com.shinoow.abyssalcraft.common.structures.pe;

import com.shinoow.abyssalcraft.api.block.ACBlocks;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.AmplifierType;
import com.shinoow.abyssalcraft.api.energy.structure.IPlaceOfPower;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureBase;
import com.shinoow.abyssalcraft.api.energy.structure.IStructureComponent;
import com.shinoow.abyssalcraft.common.blocks.BlockACStone;
import com.shinoow.abyssalcraft.common.blocks.BlockACStone.EnumStoneType;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TestStructure implements IPlaceOfPower {

	@Override
	public String getIdentifier() {

		return "test";
	}

	@Override
	public int getBookType() {

		return 0;
	}

	@Override
	public float getAmplifier(AmplifierType type) {

		if(type == AmplifierType.RANGE)
			return 2;
		return 0;
	}

	@Override
	public void construct(World world, BlockPos pos) {
		world.setBlockState(pos, ACBlocks.multi_block.getDefaultState());
		((IStructureBase) world.getTileEntity(pos)).setMultiblock(this);
		if(world.getTileEntity(pos.north()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.north())).setInMultiblock(true);
			((IStructureComponent) world.getTileEntity(pos.north())).setBasePosition(pos);
		}
		if(world.getTileEntity(pos.south()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.south())).setInMultiblock(true);
			((IStructureComponent) world.getTileEntity(pos.south())).setBasePosition(pos);
		}
		if(world.getTileEntity(pos.east()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.east())).setInMultiblock(true);
			((IStructureComponent) world.getTileEntity(pos.east())).setBasePosition(pos);
		}
		if(world.getTileEntity(pos.west()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.west())).setInMultiblock(true);
			((IStructureComponent) world.getTileEntity(pos.west())).setBasePosition(pos);
		}
	}

	@Override
	public void validate(World world, BlockPos pos) {
		boolean valid = false;
		if(world.getBlockState(pos).getBlock() == ACBlocks.multi_block)
			if(world.getBlockState(pos.north()).getBlock() == ACBlocks.statue
			&& world.getBlockState(pos.south()).getBlock() == ACBlocks.statue
			&& world.getBlockState(pos.east()).getBlock() == ACBlocks.statue
			&& world.getBlockState(pos.west()).getBlock() == ACBlocks.statue) {
				pos = pos.down();
				if(isMonolithStone(world.getBlockState(pos)) && isMonolithStone(world.getBlockState(pos.north()))
						&& isMonolithStone(world.getBlockState(pos.south()))
						&& isMonolithStone(world.getBlockState(pos.east()))
						&& isMonolithStone(world.getBlockState(pos.west()))
						&& world.getBlockState(pos.east().south()).getBlock() == ACBlocks.monolith_pillar
						&& world.getBlockState(pos.east().north()).getBlock() == ACBlocks.monolith_pillar
						&& world.getBlockState(pos.west().north()).getBlock() == ACBlocks.monolith_pillar
						&& world.getBlockState(pos.west().south()).getBlock() == ACBlocks.monolith_pillar) {
					pos = pos.down();
					boolean temp = true;
					for(int i = -1; i < 2; i++)
						for(int j = -1; j < 2; j++)
							if(!isMonolithStone(world.getBlockState(pos.add(i, 0, j))))
								temp = false;
					valid = temp;
				}
			}

		if(world.getTileEntity(pos.north()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.north())).setInMultiblock(valid);
			((IStructureComponent) world.getTileEntity(pos.north())).setBasePosition(valid ? pos : null);
		}
		if(world.getTileEntity(pos.south()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.south())).setInMultiblock(valid);
			((IStructureComponent) world.getTileEntity(pos.south())).setBasePosition(valid ? pos : null);
		}
		if(world.getTileEntity(pos.east()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.east())).setInMultiblock(valid);
			((IStructureComponent) world.getTileEntity(pos.east())).setBasePosition(valid ? pos : null);
		}
		if(world.getTileEntity(pos.west()) instanceof IStructureComponent) {
			((IStructureComponent) world.getTileEntity(pos.west())).setInMultiblock(valid);
			((IStructureComponent) world.getTileEntity(pos.west())).setBasePosition(valid ? pos : null);
		}
	}

	@Override
	public boolean canConstruct(World world, BlockPos pos, EntityPlayer player) {

		IBlockState state = world.getBlockState(pos);
		if(isMonolithStone(state))
			if(world.getBlockState(pos.north()).getBlock() == ACBlocks.statue
			&& world.getBlockState(pos.south()).getBlock() == ACBlocks.statue
			&& world.getBlockState(pos.east()).getBlock() == ACBlocks.statue
			&& world.getBlockState(pos.west()).getBlock() == ACBlocks.statue) {
				pos = pos.down();
				if(isMonolithStone(world.getBlockState(pos)) && isMonolithStone(world.getBlockState(pos.north()))
						&& isMonolithStone(world.getBlockState(pos.south()))
						&& isMonolithStone(world.getBlockState(pos.east()))
						&& isMonolithStone(world.getBlockState(pos.west()))
						&& world.getBlockState(pos.east().south()).getBlock() == ACBlocks.monolith_pillar
						&& world.getBlockState(pos.east().north()).getBlock() == ACBlocks.monolith_pillar
						&& world.getBlockState(pos.west().north()).getBlock() == ACBlocks.monolith_pillar
						&& world.getBlockState(pos.west().south()).getBlock() == ACBlocks.monolith_pillar) {
					pos = pos.down();
					boolean temp = true;
					for(int i = -1; i < 2; i++)
						for(int j = -1; j < 2; j++)
							if(!isMonolithStone(world.getBlockState(pos.add(i, 0, j))))
								temp = false;
					return temp;
				}
			}

		return false;
	}

	private static boolean isMonolithStone(IBlockState state) {
		return state.getBlock() == ACBlocks.stone && state.getValue(BlockACStone.TYPE) == EnumStoneType.MONOLITH_STONE;
	}
}
