package io.github.nickid2018.tiny2d.buffer;

import io.github.nickid2018.tiny2d.RenderThreadOnly;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class VertexArray {

    private final int arrayID;
    private final int indexID;
    private final int bufferID;

    private final int vertexCount;

    private final VertexAttributeList list;

    public VertexArray(VertexAttributeList list, int vertexCount, int arrayID, int indexID, int bufferID) {
        this.list = list;
        this.vertexCount = vertexCount;
        this.arrayID = arrayID;
        this.indexID = indexID;
        this.bufferID = bufferID;
    }

    @RenderThreadOnly
    public VertexArray(VertexAttributeList list, FloatList bufferData, IntList indices) {
        this.list = list;
        this.vertexCount = indices.size();
        arrayID = glGenVertexArrays();
        indexID = glGenBuffers();
        bufferID = glGenBuffers();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(bufferData.size());
            buffer.put(bufferData.toFloatArray());
            buffer.flip();

            IntBuffer index = stack.mallocInt(indices.size());
            index.put(indices.toIntArray());
            index.flip();

            glBindVertexArray(arrayID);
            glBindBuffer(GL_ARRAY_BUFFER, bufferID);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, index, GL_STATIC_DRAW);
        }

        VertexAttribute[] attributes = list.getAttributes();
        int[] offsets = list.getOffsets();
        for (int i = 0; i < attributes.length; i++) {
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, attributes[i].size, GL_FLOAT, false,
                    list.getStride() * 4, offsets[i] * 4L);
        }

        glBindVertexArray(0);
    }

    @RenderThreadOnly
    public void bind() {
        glBindVertexArray(arrayID);
    }

    @RenderThreadOnly
    public void unbind() {
        glBindVertexArray(0);
    }

    @RenderThreadOnly
    public void draw() {
        bind();
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        unbind();
    }

    @RenderThreadOnly
    public void destroy() {
        glDeleteBuffers(bufferID);
        glDeleteBuffers(indexID);
        glDeleteVertexArrays(arrayID);
    }
}
