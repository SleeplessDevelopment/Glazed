package net.insomniakitten.glazed.kiln;

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

import net.insomniakitten.glazed.Glazed;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class BlockKiln extends Block {

    private static final PropertyEnum<KilnHalf> HALF = PropertyEnum
            .create("half", KilnHalf.class);
    private static final PropertyDirection FACING = PropertyDirection
            .create("facing", EnumFacing.Plane.HORIZONTAL);
    private static final PropertyBool ACTIVE = PropertyBool
            .create("active");

    private static final AxisAlignedBB AABB_UPPER = new AxisAlignedBB(
            0.0625, 0, 0.0625, 0.9375, 0.8125, 0.9375);
    private static final AxisAlignedBB AABB_LOWER = new AxisAlignedBB(
            0, 0, 0, 1, 1, 1);

    public BlockKiln() {
        super(Material.ROCK);
        setRegistryName("kiln");
        setUnlocalizedName(Glazed.MOD_ID + ".kiln");
        setCreativeTab(Glazed.TAB);
        setHardness(5.0f);
        setResistance(30.0f);
        setDefaultState(this.getDefaultState()
                .withProperty(HALF, KilnHalf.LOWER)
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(ACTIVE, false));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int half = state.getValue(HALF).ordinal() << 1;
        int facing = state.getValue(FACING).getHorizontalIndex() << 2;
        int active = state.getValue(ACTIVE) ? 1 : 0;
        return half | facing | active;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        KilnHalf half = KilnHalf.values()[((meta & 0b10) >> 1)];
        EnumFacing facing = EnumFacing.getHorizontal(meta >> 2);
        boolean active = (meta & 0b1) != 0;
        return this.getDefaultState()
                .withProperty(HALF, half)
                .withProperty(FACING, facing)
                .withProperty(ACTIVE, active);
    }

    @Override @Nonnull
    public IBlockState getStateForPlacement(
            @Nonnull World world,
            @Nonnull BlockPos pos,
            @Nonnull EnumFacing facing,
            float hitX, float hitY, float hitZ, int meta,
            @Nonnull EntityLivingBase placer,
            EnumHand hand) {
        return this.getDefaultState()
                .withProperty(HALF, KilnHalf.LOWER)
                .withProperty(FACING, placer.getHorizontalFacing().getOpposite())
                .withProperty(ACTIVE, false);
    }

    @Override public boolean isFullCube(@Nonnull IBlockState state) { return false; }
    @Override public boolean isOpaqueCube(@Nonnull IBlockState state) { return false; }

    @Override
    public int getLightValue(
            @Nonnull IBlockState state,
            IBlockAccess world,
            @Nonnull BlockPos pos) {
        return state.getValue(ACTIVE) ?
                8 : 0;
    }

    @Override
    public ItemStack getPickBlock(
            IBlockState state, RayTraceResult target, World world,
            BlockPos pos, EntityPlayer player) {
        return new ItemStack(this);
    }

    @Override
    public boolean onBlockActivated(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityPlayer player,
            EnumHand hand,
            EnumFacing facing,
            float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;
        if (isUpper(state))
            pos = pos.down();
        FMLNetworkHandler.openGui(
                player, Glazed.instance, 0, world,
                pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(
            World world,
            @Nonnull BlockPos pos) {
        boolean canPlaceLower = world.getBlockState(pos)
                .getBlock().isReplaceable(world, pos);

        boolean canPlaceUpper = world.getBlockState(pos.up())
                .getBlock().isReplaceable(world, pos.up());

        return canPlaceLower && canPlaceUpper;
    }

    @Override
    public void onBlockPlacedBy(
            World world,
            BlockPos pos,
            IBlockState state,
            EntityLivingBase placer,
            ItemStack stack) {
        world.setBlockState(pos.up(), state
                .withProperty(HALF, KilnHalf.UPPER));
    }


    @Override
    @SuppressWarnings("ConstantConditions")
    public void breakBlock(@Nonnull World world,
                           @Nonnull BlockPos pos,
                           @Nonnull IBlockState state) {
        final BlockPos tilePos = isUpper(state) ? pos.down() : pos;
        Capability<IItemHandler> items = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
        TileEntity te = world.getTileEntity(tilePos);

        if (te.hasCapability(items, null)) {
            IItemHandler inventory = te.getCapability(items, null);
            boolean tileDrops = world.getGameRules().getBoolean("doTileDrops");

            if (inventory != null && tileDrops) {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty())
                        InventoryHelper.spawnItemStack(
                                world,
                                tilePos.getX(),
                                tilePos.getY(),
                                tilePos.getZ(),
                                stack);
                }
            }
        }

        BlockPos target = isUpper(state) ?
                pos.down() : pos.up();
        world.setBlockToAir(target);
    }

    @Override
    public AxisAlignedBB getBoundingBox(
            IBlockState state,
            IBlockAccess source,
            BlockPos pos) {
        return isUpper(state) ?
                AABB_UPPER : AABB_LOWER;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(HALF)
                .equals(KilnHalf.LOWER);
    }

    @Override @Nullable
    public TileEntity createTileEntity(
            @Nonnull World world,
            @Nonnull IBlockState state) {
        return !isUpper(state) ?
                new TileKiln() : null;
    }

    @Override @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(
                this, HALF, FACING, ACTIVE);
    }

    @Override
    public IBlockState getActualState(
            @Nonnull IBlockState state,
            IBlockAccess world,
            BlockPos pos) {
        if (isUpper(state)) pos = pos.down();
        TileKiln tile = (TileKiln) world.getTileEntity(pos);
        if (tile == null)
            return super.getActualState(state, world, pos);
        else return state.withProperty(ACTIVE, tile.isActive);
    }

    public static boolean isUpper(IBlockState state) {
        return state.getValue(HALF)
                .equals(KilnHalf.UPPER);
    }

    private enum KilnHalf implements IStringSerializable {
        UPPER, LOWER;
        public String getName() { return name().toLowerCase(Locale.ENGLISH); }
    }

}