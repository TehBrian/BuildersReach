package xyz.tehbrian.buildersreach.inject;

import com.google.inject.AbstractModule;
import xyz.tehbrian.buildersreach.ScoreboardService;
import xyz.tehbrian.buildersreach.user.UserService;

/**
 * Guice module which provides bindings for {@link UserService}.
 */
public class UserModule extends AbstractModule {

    /**
     * Binds {@link UserService} as an eager singleton.
     */
    @Override
    protected void configure() {
        this.bind(UserService.class).asEagerSingleton();
        this.bind(ScoreboardService.class).asEagerSingleton();
    }

}
