package xyz.tehbrian.buildersreach.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.buildersreach.ScoreboardService;
import xyz.tehbrian.buildersreach.config.ConfigConfig;
import xyz.tehbrian.buildersreach.config.LangConfig;
import xyz.tehbrian.buildersreach.highlight.BlockHighlightingTask;
import xyz.tehbrian.buildersreach.user.UserService;

public final class SingletonModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(ConfigConfig.class).asEagerSingleton();
        this.bind(LangConfig.class).asEagerSingleton();

        this.bind(BlockHighlightingTask.class).asEagerSingleton();
        this.bind(UserService.class).asEagerSingleton();
        this.bind(ScoreboardService.class).asEagerSingleton();
    }

}
