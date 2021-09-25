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

package com.github.isam.render.gui;

import com.github.isam.phys.AABB;
import com.github.isam.render.Component;
import com.github.isam.render.Renderer;
import com.github.isam.render.texture.Texture;

public class Picture extends Component {

    private Texture texture;

    public Picture(Renderer renderer, AABB position) {
        super(renderer, position);
    }

    @Override
    public void render() {

    }

    @Override
    public void onResize(int sWidth, int sHeight, int reWidth, int reHeight) {

    }
}
