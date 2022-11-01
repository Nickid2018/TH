#version 330 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec2 TexCoord;

uniform mat4 transform[100];

void main() {
    gl_Position = transform[gl_InstanceID] * vec4(aPos, 1.0, 1.0);
    TexCoord = aTexCoord;
}