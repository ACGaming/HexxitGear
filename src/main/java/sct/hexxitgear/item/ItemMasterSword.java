package sct.hexxitgear.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.gui.HexTab;
import sct.hexxitgear.init.HexRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMasterSword extends ItemSword {

    private final boolean inactive;
    private boolean highUp;

    public ItemMasterSword(String regname, ToolMaterial material) {
        super(material);
        setCreativeTab(HexTab.INSTANCE);
        setRegistryName(HexxitGear.MODID, regname);
        setTranslationKey(HexxitGear.MODID + "." + regname);
        inactive = regname.contains("inactive");
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return inactive && highUp;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);

        if (!world.isRemote && inactive && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.posY > 128) {
                highUp = true;
                if (isSelected && world.isThundering()) {
                    world.addWeatherEffect(new EntityLightningBolt(world, player.posX, player.posY, player.posZ, true));
                    player.inventory.deleteStack(stack);
                    player.inventory.add(itemSlot, new ItemStack(HexRegistry.HEXICAL_MASTER_SWORD));
                    InventoryHelper.spawnItemStack(world, player.posX, player.posY + 1.0D, player.posZ, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cqrepoured:sword_walker"))));
                    InventoryHelper.spawnItemStack(world, player.posX, player.posY + 1.0D, player.posZ, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("mowziesmobs:wrought_axe"))));
                    InventoryHelper.spawnItemStack(world, player.posX, player.posY + 1.0D, player.posZ, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("mod_lavacow:skeletonking_mace"))));
                    world.playSound(null, player.posX, player.posY + 1.0D, player.posZ, HexRegistry.HEXICAL_MASTER_SWORD_ACTIVATION_SOUND, SoundCategory.PLAYERS, 0.5F, 1.0F);
                    if (FMLLaunchHandler.side().isClient()) {
                        displayItemActivation();
                    }
                }
            } else {
                highUp = false;
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.hexxitgear.hexical_master_sword_inactive.1"));
        tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.hexxitgear.hexical_master_sword_inactive.2"));
        tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.hexxitgear.hexical_master_sword_inactive.3"));
        tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.hexxitgear.hexical_master_sword_inactive.4"));
    }

    @SideOnly(Side.CLIENT)
    public void displayItemActivation() {
        Minecraft.getMinecraft().entityRenderer.displayItemActivation(new ItemStack(HexRegistry.HEXICAL_MASTER_SWORD_ACTIVATION));
    }
}
