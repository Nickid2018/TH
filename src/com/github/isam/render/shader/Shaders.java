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
package com.github.isam.render.shader;

import com.github.isam.crash.CrashReport;
import com.github.isam.crash.DetectedCrashException;

public class Shaders {

    public static final ShaderProgram SIMPLE;
    public static final ShaderProgram FONT;

    static {
        Shaders.class.getResource("/assets/shader/simple.vsh");
        try {
            SIMPLE = ShaderProgram.createFromJAR("/assets/shader/simple.vsh", "/assets/shader/simple.fsh",
                    new Uniform("sampler", Uniform.Type.INT_1));
        } catch (Exception e) {
            throw new DetectedCrashException(new CrashReport("Can't create shader 'simple'", e));
        }
        SIMPLE.getUniform("sampler").setInt(0);
        try {
            FONT = ShaderProgram.createFromJAR("/assets/shader/simple.vsh", "/assets/shader/font.fsh",
                    new Uniform("sampler", Uniform.Type.INT_1));
        } catch (Exception e) {
            throw new DetectedCrashException(new CrashReport("Can't create shader 'font'", e));
        }
        FONT.getUniform("sampler").setInt(0);
    }
}
