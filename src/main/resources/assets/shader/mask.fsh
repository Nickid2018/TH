#version 330 core

out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D texture1;

void main() {
    vec4 colorSource = texture(texture1, TexCoord);
    if (colorSource.a < 0.1)
        discard;
    FragColor = vec4(0.0, 0.0, 0.0, 0.0);
}