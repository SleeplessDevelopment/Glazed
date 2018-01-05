package net.insomniakitten.glazed.proxy;

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

import net.insomniakitten.glazed.client.SpecialsParser;
import net.insomniakitten.glazed.client.color.IBlockColorProvider;
import net.insomniakitten.glazed.client.color.IItemColorProvider;
import net.insomniakitten.glazed.compat.tconstruct.TiConHelper;
import net.insomniakitten.glazed.compat.tconstruct.TiConRegistry;
import net.insomniakitten.glazed.tile.TileEntityBase;
import net.insomniakitten.glazed.util.CollectionHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

import static net.insomniakitten.glazed.GlazedRegistry.BLOCK_FACTORY;
import static net.insomniakitten.glazed.GlazedRegistry.ITEM_FACTORY;

@SideOnly(Side.CLIENT)
public final class ClientProxy extends GlazedProxy {

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        super.onPreInit(event);
    }

    @Override
    public void onInit(FMLInitializationEvent event) {
        super.onInit(event);
        if (TiConHelper.isTiConLoaded()) {
            TiConRegistry.onClientInit(event);
        }
    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {
        super.onPostInit(event);
        SpecialsParser.registerResourceListener();
        CollectionHelper.forThoseOf(BLOCK_FACTORY.entries(), IBlockColorProvider.class, IBlockColorProvider::registerColorHandler);
        CollectionHelper.forThoseOf(ITEM_FACTORY.entries(), IItemColorProvider.class, IItemColorProvider::registerColorHandler);
    }

    @Override
    public <T extends TileEntityBase> void bindTESR(Supplier<TileEntitySpecialRenderer<T>> render, Class<T> tile) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, render.get());
    }

}
