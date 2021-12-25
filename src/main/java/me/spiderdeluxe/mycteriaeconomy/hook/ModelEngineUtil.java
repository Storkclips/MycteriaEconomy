package me.spiderdeluxe.mycteriaeconomy.hook;

import com.ticxo.modelengine.api.ModelEngineAPI;

import java.util.Set;

public class ModelEngineUtil {

	/**
	 * Stores registered Models in the server
	 */
	public static Set<String> registeredModels = ModelEngineAPI.api.getModelManager().getModelRegistry().getRegisteredModel().keySet();
}
