package net.yuurraa.averiummod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ArgonVentBlockEntity extends BlockEntity {
    private int remaining = 5;  // for example

    public ArgonVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARGON_VENT.get(), pos, state);
    }

    public boolean hasArgon() {
        return remaining > 0;
    }

    public void collectOne() {
        if (remaining > 0) remaining--;
        setChanged();              // mark dirty
        // tell clients to re‑request update
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    // Send full NBT to client when chunk data is sent
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Remaining", remaining);
        return tag;
    }

    // Create the packet to send to client when sync is requested
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Read NBT from server → client
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        CompoundTag tag = pkt.getTag();
        assert tag != null;
        if (tag.contains("Remaining")) {
            this.remaining = tag.getInt("Remaining");
        }
    }
}
