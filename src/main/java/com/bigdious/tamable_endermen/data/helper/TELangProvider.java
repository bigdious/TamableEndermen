package main.java.com.bigdious.dn.data.helper;

import com.bigdious.tamable_endermen.DN;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class DNLangProvider extends LanguageProvider {

	private final PackOutput output;
	public final Map<String, String> upsideDownEntries = new HashMap<>();

	public DNLangProvider(PackOutput output) {
		super(output, DN.MODID, "en_us");
		this.output = output;
	}

	@Override
	public void add(String key, String value) {
		super.add(key, value);
		List<LangFormatSplitter.Component> splitEnglish = LangFormatSplitter.split(value);
		this.upsideDownEntries.put(key, LangConversionHelper.convertComponents(splitEnglish));
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		//generate normal lang file
		CompletableFuture<?> languageGen = super.run(cache);
		ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();
		futuresBuilder.add(languageGen);

		//generate en_ud file
		JsonObject upsideDownFile = new JsonObject();
		this.upsideDownEntries.forEach(upsideDownFile::addProperty);
		futuresBuilder.add(DataProvider.saveStable(cache, upsideDownFile, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(DN.MODID).resolve("lang").resolve("en_ud.json")));

		return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
	}
}
