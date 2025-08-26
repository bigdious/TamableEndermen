package com.bigdious.tamable_endermen.data;

import com.bigdious.tamable_endermen.data.helper.TELangProvider;
import com.bigdious.tamable_endermen.init.TEEntities;
import net.minecraft.data.PackOutput;


public class TELangGenerator extends TELangProvider {

	public TELangGenerator(PackOutput output) {
		super(output);
	}
	@Override
	protected void addTranslations() {
		this.addEntityType(TEEntities.TAMED_ENDERMAN, "Tamed Enderman");
	}
}
