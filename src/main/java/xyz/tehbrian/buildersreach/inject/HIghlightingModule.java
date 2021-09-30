package xyz.tehbrian.buildersreach.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.buildersreach.highlight.BlockHighlightingTask;

public class HIghlightingModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(BlockHighlightingTask.class).asEagerSingleton();
    }

}
