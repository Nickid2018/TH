package io.github.nickid2018.tiny2d.buffer;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import io.github.nickid2018.tiny2d.shader.ShaderProgram;
import io.github.nickid2018.tiny2d.util.LazyLoadValue;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {

    private final int framebufferID;
    private final int textureID;
    private final int renderbufferID;

    public static final LazyLoadValue<VertexArray> defaultVAO = new LazyLoadValue<>(() -> {
        VertexArrayBuilder builder = new VertexArrayBuilder(VertexAttributeList.TEXTURE_2D, IndexBufferProvider.DEFAULT);
        builder.pos(-1, -1).uv(0, 0).end();
        builder.pos(1, -1).uv(1, 0).end();
        builder.pos(-1, 1).uv(0, 1).end();
        builder.pos(1, 1).uv(1, 1).end();
        return builder.build();
    });

    @RenderThreadOnly
    public FrameBuffer(int width, int height) {
        framebufferID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureID, 0);

        renderbufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderbufferID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Framebuffer is not complete");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @RenderThreadOnly
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
    }

    @RenderThreadOnly
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @RenderThreadOnly
    public void delete() {
        glDeleteFramebuffers(framebufferID);
        glDeleteTextures(textureID);
        glDeleteRenderbuffers(renderbufferID);
    }

    @RenderThreadOnly
    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    @RenderThreadOnly
    public void renderToScreen() {
        ShaderProgram.getDefaultShader("screen").use();
        bindTexture();
        defaultVAO.get().draw();
    }

    @RenderThreadOnly
    public void processAndRenderToScreen(ShaderProgram shader) {
        shader.use();
        bindTexture();
        defaultVAO.get().draw();
    }

    public int getTexture() {
        return textureID;
    }
}
