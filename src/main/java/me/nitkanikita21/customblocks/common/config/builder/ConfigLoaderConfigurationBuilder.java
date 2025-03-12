package me.nitkanikita21.customblocks.common.config.builder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConfigLoaderConfigurationBuilder
        <L extends AbstractConfigurationLoader<?>, B extends AbstractConfigurationLoader.Builder<B, L>>
{
    Supplier<B> builderSupplier;
    List<Consumer<B>> builderAppliers = new ArrayList<>();

    private ConfigLoaderConfigurationBuilder<L, B> apply(Consumer<B> consumer) {
        builderAppliers.add(consumer);
        return this;
    }

    public ConfigLoaderConfigurationBuilder<L, B>  defaultOptions(ConfigurationOptions defaultOptions) {
        return apply(b -> b.defaultOptions(defaultOptions));
    }

    public ConfigLoaderConfigurationBuilder<L, B> defaultOptions(UnaryOperator<ConfigurationOptions> defaultOptions) {
        return apply(b -> b.defaultOptions(defaultOptions));
    }

    public ConfigLoaderConfigurationBuilder<L, B> peekBuilder(Consumer<B> consumer) {
        return apply(consumer);
    }

    public static ConfigLoaderConfigurationBuilder<YamlConfigurationLoader, YamlConfigurationLoader.Builder> yaml() {
        return new ConfigLoaderConfigurationBuilder<>(YamlConfigurationLoader::builder);
    }

    public static ConfigLoaderConfigurationBuilder<HoconConfigurationLoader, HoconConfigurationLoader.Builder> hocon() {
        return new ConfigLoaderConfigurationBuilder<>(HoconConfigurationLoader::builder);
    }

}
