package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;
import lombok.Getter;

@Getter
public class SimpleBulletDispenser extends BulletDispenser {

    private final BulletProvider provider;
    private final BulletArgsProvider argsProvider;

    public SimpleBulletDispenser(Playground playground, BulletProvider provider, BulletArgsProvider argsProvider) {
        this(playground, provider, argsProvider, null);
    }

    public SimpleBulletDispenser(Playground playground, BulletProvider provider, BulletArgsProvider argsProvider, Enemy enemy) {
        super(playground, enemy);
        this.provider = provider;
        this.argsProvider = argsProvider;
    }

    @Override
    protected void dispenseBullet(long tickTime) {
        argsProvider.getBullets(this, provider).forEach(playground::addBullet);
    }
}
