function __simpleBullet__update__(playGround, bullet) {
    bullet.updatePosition();
    if(checkHit(playGround, bullet)){
        hitOnPlayer(playGround, bullet);
        playGround.dispose(bullet);
    } else if (playGround.isItemOutsidePlayground(bullet))
        playGround.dispose(bullet);
}