package textures

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.opengl.ARBFramebufferObject.glGenerateMipmap
import org.lwjgl.opengl.ARBVertexArrayObject.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.NativeType
import shaders.Shader
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer


private const val SCR_WIDTH = 800
private const val SCR_HEIGHT = 600

var mixValue = 0.2f

private val frameBufferSizeCallback =
    GLFWFramebufferSizeCallbackI { _, width, height ->
        glViewport(0, 0, width, height)
    }

private fun processInput(@NativeType("GLFWwindow *") window: Long) {
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true)
    if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
        if (mixValue < 1.0) mixValue += 0.025f
    if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
        if (mixValue > 0.0) mixValue -= 0.025f
}

fun main() {
    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    val window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LearnOpenGL", 0, 0)
    if (window <= 0) {
        glfwTerminate()
        throw RuntimeException("Failed to create the GLFW window")
    }
    glfwMakeContextCurrent(window)
    glfwSetFramebufferSizeCallback(window, frameBufferSizeCallback)
    GL.createCapabilities()

    // build and compile our shader program
    // ------------------------------------
    val ourShader = Shader(
        "src/main/kotlin/textures/step1.vert",
        "src/main/kotlin/textures/exercise4.frag"
    )

    val vao = glGenVertexArrays()
    val vbo: Int
    val ebo: Int

    glBindVertexArray(vao)

    val vertices: FloatBuffer
    stackPush().use { stack ->
        val verticesArray = floatArrayOf(
            // positions          // colors           // texture coordinates
            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f
        )
        vertices = stack.mallocFloat(verticesArray.size)
        verticesArray.forEach {
            vertices.put(it)
        }
        vertices.flip()
        vbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    }

    val indices: IntBuffer
    stackPush().use { stack ->
        val indicesArray = intArrayOf(
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
        )
        indices = stack.mallocInt(indicesArray.size)
        indicesArray.forEach {
            indices.put(it)
        }
        indices.flip()
        ebo = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
    }

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.SIZE_BYTES, 0)
    glEnableVertexAttribArray(0)

    glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.SIZE_BYTES, 3L * Float.SIZE_BYTES)
    glEnableVertexAttribArray(1)

    glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.SIZE_BYTES, 6L * Float.SIZE_BYTES)
    glEnableVertexAttribArray(2)

    val texture1 = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, texture1)

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

    stackPush().use { stack ->
        val data: ByteBuffer?
        val w = stack.mallocInt(1)
        val h = stack.mallocInt(1)
        val c = stack.mallocInt(1)
        data = stbi_load("src/main/kotlin/textures/container.jpg", w, h, c, 0)
        if (data != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w[0], h[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data)
            glGenerateMipmap(GL_TEXTURE_2D)
            stbi_image_free(data)
        } else {
            throw RuntimeException("Failed to load texture\n")
        }
    }

    val texture2 = glGenTextures()
    glBindTexture(GL_TEXTURE_2D, texture2)

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    stackPush().use { stack ->
        val data: ByteBuffer?
        val w = stack.mallocInt(1)
        val h = stack.mallocInt(1)
        val c = stack.mallocInt(1)
        stbi_set_flip_vertically_on_load(true)
        data = stbi_load("src/main/kotlin/textures/awesomeface.png", w, h, c, 0)
        if (data != null) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w[0], h[0], 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
            glGenerateMipmap(GL_TEXTURE_2D)
            stbi_image_free(data)
        } else {
            throw RuntimeException("Failed to load texture\n")
        }
    }

    ourShader.use()
    ourShader.setInt("texture1", 0)
    ourShader.setInt("texture2", 1)

    while (!glfwWindowShouldClose(window)) {
        // input
        // -----
        processInput(window)

        // render
        // ------
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture1)
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, texture2)

        ourShader.use()
        ourShader.setFloat("mixValue", mixValue)
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0)

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(vao)
    glDeleteBuffers(vbo)
    glDeleteBuffers(ebo)
    glDeleteTextures(texture1)
    ourShader.delete()

    // glfw: terminate, clearing all previously allocated GLFW resources.
    // ------------------------------------------------------------------
    glfwTerminate()
}

