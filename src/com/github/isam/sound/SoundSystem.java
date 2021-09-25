/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.isam.sound;

import com.github.isam.phys.Vec3f;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import org.lwjgl.openal.AL11;

import javax.annotation.Nullable;
import java.util.Queue;
import java.util.Set;

public class SoundSystem implements Runnable {

    private static volatile boolean running = true;
    private static final Queue<Runnable> runningQueue = Queues.newConcurrentLinkedQueue();
    private static final Set<SoundInstance> sounds = Sets.newHashSet();
    private static final Set<SoundInstance> toDelete = Sets.newHashSet();
    private static float volume = 1;
    private static final Listener listener = new Listener();

    private static String alVersion;

    public static void init() {
        new Thread(new SoundSystem(), "Sound Engine").start();
    }

    public static void enqueue(Runnable operation) {
        runningQueue.offer(operation);
    }

    @Nullable
    public static SoundInstance create() {
        SoundInstance instance = SoundInstance.create();
        if(instance == null)
            return null;
        instance.setTotalVolume(volume);
        sounds.add(instance);
        return instance;
    }

    public static String getAlVersion(){
        return alVersion == null ? "<Unknown>" : alVersion;
    }

    public static void setTotalVolume(float value) {
        enqueue(() -> {
            for (SoundInstance instance : sounds)
                instance.setTotalVolume(value);
            volume = value;
        });
    }

    public static void setListenerPos(Vec3f pos) {
        enqueue(() -> listener.setListenerPosition(pos));
    }

    public static void setListenerVelocity(Vec3f v) {
        enqueue(() -> listener.setListenerVelocity(v));
    }

    public static void setGain(float gain) {
        enqueue(() -> listener.setGain(gain));
    }

    public static void setOrientation(Vec3f at, Vec3f up) {
        enqueue(() -> listener.setOrientation(at, up));
    }

    public static Listener getListener() {
        return listener;
    }

    public static void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            SoundEngine.initEngine(null);
        } catch (Exception e) {
            SoundEngine.LOGGER.info("Error has occurred in initializing, game will go into silent mode");
            return;
        }
        alVersion = AL11.alGetString(AL11.AL_VERSION);
        while (running) {
            while (!runningQueue.isEmpty())
                runningQueue.poll().run();
            for (SoundInstance instance : sounds)
                if (instance.doTick())
                    toDelete.add(instance);
            sounds.removeAll(toDelete);
            toDelete.clear();
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
        SoundEngine.stopEngine();
    }
}
