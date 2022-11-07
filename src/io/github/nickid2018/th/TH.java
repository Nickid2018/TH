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
package io.github.nickid2018.th;

import io.github.nickid2018.th.crash.CrashReport;
import io.github.nickid2018.th.crash.DetectedCrashError;
import io.github.nickid2018.th.gui.GameScreen;
import io.github.nickid2018.th.pack.FileDataPack;
import io.github.nickid2018.th.pack.InternalDataPack;
import io.github.nickid2018.th.pack.PackManager;
import io.github.nickid2018.th.system.script.ScriptRunner;
import io.github.nickid2018.tiny2d.font.VectorFont;
import io.github.nickid2018.tiny2d.sound.SoundEngine;
import io.github.nickid2018.tiny2d.window.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class TH {

    public static final Logger LOGGER = LoggerFactory.getLogger("TH System");

    public static void main(String[] args) throws IOException {
        //#if DEBUG
        //$LOGGER.info("Debug mode is enabled.");
        //#endif
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (e instanceof DetectedCrashError crashError) {
                LOGGER.error(crashError.getReport().populateReport());
                crashError.getReport().writeToFile();
            } else {
                CrashReport report = new CrashReport("Uncaught Exception", e);
                LOGGER.error(report.populateReport());
                report.writeToFile();
            }
        });
        PackManager.setPackList(List.of(new InternalDataPack(), new FileDataPack(new File("test"))));
        ScriptRunner.init();
        VectorFont font = new VectorFont(new FileInputStream("C:\\Windows\\Fonts\\Arial.ttf"));
        Window window = new Window("TH - Temp Runner", 1200, 916, font, false);
        SoundEngine.initEngine(null);
        window.setMaxFPS(60);
        window.switchScreen(new GameScreen(window));
        window.run(null);
        SoundEngine.stopEngine();
    }
}
