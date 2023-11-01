package learnopengl.helloworld

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.NativeType

const val SCR_WIDTH = 800
const val SCR_HEIGHT = 600

private val frameBufferSizeCallback =
    GLFWFramebufferSizeCallbackI { _, width, height ->
        glViewport(0, 0, width, height)
    }

fun processInput(@NativeType("GLFWwindow *") window: Long) {
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true)
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

    while (!glfwWindowShouldClose(window)) {
        processInput(window)

        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)

        glfwSwapBuffers(window)
        glfwPollEvents()
    }
    glfwTerminate()
}
