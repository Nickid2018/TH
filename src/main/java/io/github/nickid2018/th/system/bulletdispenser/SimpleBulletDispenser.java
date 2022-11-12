package io.github.nickid2018.th.system.bulletdispenser;

import io.github.nickid2018.th.system.bullet.BulletProvider;
import io.github.nickid2018.th.system.bulletdispenser.args.BulletArgsProvider;
import io.github.nickid2018.th.system.bulletdispenser.variant.BulletVariantProvider;
import io.github.nickid2018.th.system.compute.Playground;
import io.github.nickid2018.th.system.enemy.Enemy;
import lombok.Getter;

@Getter
public class SimpleBulletDispenser extends BulletDispenser {

    private final BulletVariantProvider variantProvider;
    private final BulletArgsProvider argsProvider;
    private final BulletProvider bulletProvider;

    public SimpleBulletDispenser(Playground playground, BulletVariantProvider variantProvider,
                                 BulletArgsProvider argsProvider, BulletProvider bulletProvider, Enemy enemy) {
        super(playground, enemy);
        this.variantProvider = variantProvider;
        this.argsProvider = argsProvider;
        this.bulletProvider = bulletProvider;
    }

    @Override
    protected void dispenseBullet(long tickTime) {
        argsProvider.getBullets(this, variantProvider, bulletProvider).forEach(playground::addBullet);
    }
}
