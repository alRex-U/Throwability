package com.alrexu.throwability.common.network.handler;

import com.alrexu.throwability.common.capability.IThrow;
import com.alrexu.throwability.common.capability.capabilities.ThrowProvider;
import com.alrexu.throwability.common.network.ItemThrowMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemThrowMessageHandler {
	@OnlyIn(Dist.CLIENT)
	public static void handleClient(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player;
			if (contextSupplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
				Player player1 = Minecraft.getInstance().player;
				if (player1 == null) return;
				Level world = player1.level;
				player = world.getPlayerByUUID(message.senderID);
			} else {
				player = contextSupplier.get().getSender();
			}
			if (player == null) return;
			IThrow iThrow = ThrowProvider.get(player);
			if (iThrow == null) return;

			iThrow.throwItem(player, message.isAll, message.throwStrength);
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public static void handleServer(ItemThrowMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		contextSupplier.get().enqueueWork(() -> {
			Player player = contextSupplier.get().getSender();
			if (player == null) return;

			IThrow iThrow = ThrowProvider.get(player);
			if (iThrow == null) return;
			iThrow.throwItem(player, message.isAll, message.throwStrength);
		});
		contextSupplier.get().setPacketHandled(true);
	}
}
