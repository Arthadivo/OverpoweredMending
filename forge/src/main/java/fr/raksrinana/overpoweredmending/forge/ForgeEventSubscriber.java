package fr.raksrinana.overpoweredmending.forge;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Comparator;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(modid = OverpoweredMending.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventSubscriber{
	private static final int DURABILITY_PER_XP = 2;
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerPickupXpEvent(PlayerXpEvent.PickupXp e){
		e.setCanceled(true);
		var player = e.getEntity();
		var xp = e.getOrb();
		var item = getDamagedEnchantedItem(Enchantments.MENDING, player);
		// See EntityXPOrb#onCollideWithPlayer for details.
		// All requirements for picking up XP are met at this point.
		// -> EntityPlayer#xpCooldown is set to 2.
		player.takeXpDelay = 2;
		// -> EntityPlayer#onItemPickup is called with the xp orb and 1 (quantity).
		player.take(xp, 1);
		// -> The mending effect is applied and the xp value is recalculated.
		while(!item.isEmpty() && xp.getValue() > 0){
			var realRepair = Math.min(xp.getValue() * DURABILITY_PER_XP, item.getDamageValue());
			xp.value -= realRepair / DURABILITY_PER_XP;
			item.setDamageValue(item.getDamageValue() - realRepair);
			item = getDamagedEnchantedItem(Enchantments.MENDING, player);
		}
		// -> The XP are added to the player's experience.
		if(xp.getValue() > 0){
			player.giveExperiencePoints(xp.getValue());
		}
		// -> The XP orb is killed.
		xp.discard();
	}
	
	private static ItemStack getDamagedEnchantedItem(Enchantment ench, Player player){
		var playerInventory = player.getInventory();
		return IntStream.range(0, playerInventory.getContainerSize())
				.mapToObj(playerInventory::getItem)
				.filter(is -> !is.isEmpty())
				.filter(ItemStack::isDamageableItem)
				.filter(ItemStack::isDamaged)
				.filter(is -> EnchantmentHelper.getItemEnchantmentLevel(ench, is) > 0)
				.max(Comparator.comparing(ItemStack::getDamageValue))
				.orElse(ItemStack.EMPTY);
	}
}
