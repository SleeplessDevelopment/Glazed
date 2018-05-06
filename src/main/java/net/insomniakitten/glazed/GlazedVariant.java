package net.insomniakitten.glazed;

/*
 *  Copyright 2018 InsomniaKitten
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

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;
import java.util.function.Function;

public enum GlazedVariant implements IStringSerializable {
    GAIA(0.3F, 1.5F),
    RADIANT(0.3F, 1.5F, 15),
    IRIDESCENT(0.3F, 1.5F, 8),
    ENERGETIC(0.3F, 1.5F),
    SHADOWED(0.3F, 1.5F),
    VOIDIC(0.3F, 1.5F),
    QUILTED(0.3F, 1.5F),
    REINFORCED(0.3F, 1.5F),
    SLIMY(0.3F, 1.5F),
    AURORIC(0.3F, 1.5F, 10);

    public static final GlazedVariant[] VARIANTS = values();

    public static final PropertyEnum<GlazedVariant> PROPERTY = PropertyEnum.create(
            "variant", GlazedVariant.class
    );

    protected static final Function<ItemStack, String> NAME_MAPPER = GlazedVariant::getName;

    private final float hardness;
    private final float resistance;
    private final int lightLevel;

    GlazedVariant(float hardness, float resistance, int lightLevel) {
        this.hardness = hardness;
        this.resistance = resistance;
        this.lightLevel = lightLevel;
    }

    GlazedVariant(float hardness, float resistance) {
        this(hardness, resistance, 0);
    }

    public static boolean isValid(int ordinal) {
        return ordinal >= 0 && ordinal < VARIANTS.length;
    }

    public static String getName(ItemStack stack) {
        final int meta = stack.getMetadata();
        return isValid(meta) ? VARIANTS[meta].getName() : "unknown";
    }

    public static String getName(IBlockState state) {
        if (state.getPropertyKeys().contains(PROPERTY)) {
            return state.getValue(PROPERTY).getName();
        } else return "unknown";
    }

    @SideOnly(Side.CLIENT)
    public static String getDescription(ItemStack stack) {
        return I18n.format("tooltip.glazed.variant." + getName(stack));
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistance() {
        return resistance;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public void onEntityWalk(IBlockAccess access, BlockPos pos, Entity entity) {

    }

    public void onEntityCollide(IBlockAccess access, BlockPos pos, Entity entity) {

    }

    public boolean onPlayerInteract(IBlockAccess access, BlockPos pos, EntityPlayer player, EnumFacing side) {
        return false;
    }
}
