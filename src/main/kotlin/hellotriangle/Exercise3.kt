package hellotriangle

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI
import org.lwjgl.opengl.ARBVertexArrayObject.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.NativeType

private const val SCR_WIDTH = 800
private const val SCR_HEIGHT = 600

private const val vertexShaderSource = """
#version 330 core
layout (location = 0) in vec3 aPos;
void main()
{
    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
}
"""

private const val fragmentShaderSource1 = """
#version 330 core 
out vec4 FragColor;
void main()
{
    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);
}
"""

private const val fragmentShaderSource2 = """
#version 330 core 
out vec4 FragColor;
void main()
{
    FragColor = vec4(0.5f, 0.5f, 0.2f, 1.0f);
}
"""


private val frameBufferSizeCallback =
    GLFWFramebufferSizeCallbackI { _, width, height ->
        glViewport(0, 0, width, height)
    }

private fun processInput(@NativeType("GLFWwindow *") window: Long) {
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true)
}

fun main() {
    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    val window = glfwCreateWindow(
        SCR_WIDTH, SCR_HEIGHT,
        "LearnOpenGL", 0, 0)
    if (window <= 0) {
        glfwTerminate()
        throw RuntimeException("Failed to create the GLFW window")
    }
    glfwMakeContextCurrent(window)
    glfwSetFramebufferSizeCallback(window, frameBufferSizeCallback)
    GL.createCapabilities()

    // build and compile our shader program
    // ------------------------------------
    // vertex shader
    val vertexShader = glCreateShader(GL_VERTEX_SHADER)
    glShaderSource(vertexShader, vertexShaderSource)
    glCompileShader(vertexShader)
    // check for shader compile errors
    val success = IntArray(1)
    glGetShaderiv(vertexShader, GL_COMPILE_STATUS, success)
    if (success[0] == 0) {
        throw RuntimeException(
            "ERROR::SHADER::VERTEX::COMPILATION_FAILED\n ${
                glGetShaderInfoLog(
                    vertexShader
                )
            }"
        )
    }
    // fragment shader
    val fragmentShader1 = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(fragmentShader1, fragmentShaderSource1)
    glCompileShader(fragmentShader1)
    // check for shader compile errors
    glGetShaderiv(fragmentShader1, GL_COMPILE_STATUS, success)
    if (success[0] == 0) {
        throw RuntimeException(
            "ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n ${
                glGetShaderInfoLog(
                    fragmentShader1
                )
            }"
        )
    }
    val fragmentShader2 = glCreateShader(GL_FRAGMENT_SHADER)
    glShaderSource(fragmentShader2, fragmentShaderSource2)
    glCompileShader(fragmentShader2)
    // check for shader compile errors
    glGetShaderiv(fragmentShader2, GL_COMPILE_STATUS, success)
    if (success[0] == 0) {
        throw RuntimeException(
            "ERROR::SHADER::FRAGMENT::COMPILATION_FAILED\n ${
                glGetShaderInfoLog(
                    fragmentShader2
                )
            }"
        )
    }
    // link shaders
    val shaderProgram1 = glCreateProgram()
    glAttachShader(shaderProgram1, vertexShader)
    glAttachShader(shaderProgram1, fragmentShader1)
    glLinkProgram(shaderProgram1)
    // check for linking errors
    glGetProgramiv(shaderProgram1, GL_LINK_STATUS, success)
    if (success[0] == 0) {
        throw RuntimeException(
            "ERROR::SHADER::PROGRAM::LINKING_FAILED\n ${
                glGetShaderInfoLog(
                    shaderProgram1
                )
            }"
        )
    }
    val shaderProgram2 = glCreateProgram()
    glAttachShader(shaderProgram2, vertexShader)
    glAttachShader(shaderProgram2, fragmentShader2)
    glLinkProgram(shaderProgram2)
    // check for linking errors
    glGetProgramiv(shaderProgram2, GL_LINK_STATUS, success)
    if (success[0] == 0) {
        throw RuntimeException(
            "ERROR::SHADER::PROGRAM::LINKING_FAILED\n ${
                glGetShaderInfoLog(
                    shaderProgram2
                )
            }"
        )
    }
    glDeleteShader(vertexShader)
    glDeleteShader(fragmentShader1)
    glDeleteShader(fragmentShader2)

    // set up vertex data (and buffer(s)) and configure vertex attributes
    // ------------------------------------------------------------------
    val firstTriangle = floatArrayOf(
        -0.95f, -0.5f, 0.0f, // left
        0.0f, -0.5f, 0.0f, // right
        -0.45f, 0.5f, 0.0f  // top
    )
    val secondTriangle = floatArrayOf(
        0.0f, -0.5f, 0.0f, // left
        0.95f, -0.5f, 0.0f, // right
        0.45f, 0.5f, 0.0f  // top
    )

    val VAOs = IntArray(2)
    val VBOs = IntArray(2)
    glGenVertexArrays(VAOs)
    glGenBuffers(VBOs)
    // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
    glBindVertexArray(VAOs[0])
    glBindBuffer(GL_ARRAY_BUFFER, VBOs[0])
    glBufferData(GL_ARRAY_BUFFER, firstTriangle, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(0)

    glBindVertexArray(VAOs[1])
    glBindBuffer(GL_ARRAY_BUFFER, VBOs[1])
    glBufferData(GL_ARRAY_BUFFER, secondTriangle, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
    glEnableVertexAttribArray(0)

    // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex
    // attribute's bound vertex buffer object so afterward we can safely unbind
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    // You can unbind the VAO afterward so other VAO calls won't accidentally modify this VAO,
    // but this rarely happens. Modifying other VAOs requires a call to glBindVertexArray anyway,
    // so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
    glBindVertexArray(0)


    // uncomment this call to draw in wireframe polygons.
    // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)

    // render loop
    // -----------
    while (!glfwWindowShouldClose(window)) {
        // input
        // -----
        processInput(window)

        // render
        // ------
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)

        // draw our first triangle
        glUseProgram(shaderProgram1)
        glBindVertexArray(VAOs[0]) // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
        glDrawArrays(GL_TRIANGLES, 0, 3)
        glUseProgram(shaderProgram2)
        glBindVertexArray(VAOs[1]) // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
        glDrawArrays(GL_TRIANGLES, 0, 3)
        // glBindVertexArray(0); // no need to unbind it every time

        // glfw: swap buffers and poll IO events (keys pressed/released, mouse moved etc.)
        // -------------------------------------------------------------------------------
        glfwSwapBuffers(window)
        glfwPollEvents()
    }

    // optional: de-allocate all resources once they've outlived their purpose:
    // ------------------------------------------------------------------------
    glDeleteVertexArrays(VAOs)
    glDeleteBuffers(VBOs)
    glDeleteProgram(shaderProgram1)
    glDeleteProgram(shaderProgram2)

    // glfw: terminate, clearing all previously allocated GLFW resources.
    // ------------------------------------------------------------------
    glfwTerminate()
}


