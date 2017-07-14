package net.insomniakitten.glazed.client;

/*
 *  Copyright 2017 InsomniaKitten
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import net.insomniakitten.glazed.kiln.ContainerKiln;
import net.insomniakitten.glazed.kiln.GuiKiln;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(
            int ID, EntityPlayer player,
            World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (ID == 0) return new ContainerKiln(
                world.getTileEntity(pos), player);
        else return null;
    }

    @Override @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(
            int ID, EntityPlayer player,
            World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (ID == 0) return new GuiKiln(
                world.getTileEntity(pos), player);
        else return null;
    }

}