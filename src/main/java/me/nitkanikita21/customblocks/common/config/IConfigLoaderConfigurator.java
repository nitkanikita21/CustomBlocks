package me.nitkanikita21.customblocks.common.config;

import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;

public interface IConfigLoaderConfigurator {

    ConfigurationLoader<?> configure(File file);

}
