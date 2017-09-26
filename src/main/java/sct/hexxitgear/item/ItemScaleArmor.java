/*
 * HexxitGear
 * Copyright (C) 2013  Ryan Cohen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package sct.hexxitgear.item;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sct.hexxitgear.HexxitGear;
import sct.hexxitgear.model.ModelScaleHelmet;
import sct.hexxitgear.util.FormatCodes;

public class ItemScaleArmor extends ItemHexxitArmor {

	public ItemScaleArmor(int renderIndex, int slot) {
		super(ArmorMaterial.DIAMOND, renderIndex, slot);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, java.lang.String type) {
		if (slot == 0) return "hexxitgear:textures/maps/ScaleHelmet.png";

		if (stack.getItem() == HexxitGear.scaleLeggings) return "hexxitgear:textures/armor/scale2.png";

		return "hexxitgear:textures/armor/scale.png";
	}

	@SideOnly(Side.CLIENT)
	private static ModelScaleHelmet scaleHelmet;

	@SideOnly(Side.CLIENT)
	private ModelBiped getHelmet() {
		if (scaleHelmet == null) scaleHelmet = new ModelScaleHelmet();
		return scaleHelmet;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
		if (armorSlot == 0) {
			ModelBiped helmet = getHelmet();
			helmet.isSneak = entityLiving.isSneaking();
			return helmet;
		}
		return null;
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List infoList, boolean par4) {
		infoList.add(FormatCodes.Indigo.format + StatCollector.translateToLocal("gui.hexxitgear.set.scale"));
	}
}
