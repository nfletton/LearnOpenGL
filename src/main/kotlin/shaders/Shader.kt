package shaders

import org.lwjgl.opengl.GL20.*
import java.io.File

class Shader(vertexPath: String, fragmentPath: String) {
    private val programId: Int
    init {
        val vertexShaderCode = File(vertexPath).readText()
        val fragmentShaderCode = File(fragmentPath).readText()

        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexShaderCode)
        glCompileShader(vertexShader)
        checkCompileErrors(vertexShader, "VERTEX")


        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentShaderCode)
        glCompileShader(fragmentShader)
        checkCompileErrors(fragmentShader, "FRAGMENT")

        programId = glCreateProgram()
        glAttachShader(programId, vertexShader)
        glAttachShader(programId, fragmentShader)
        glLinkProgram(programId)
        checkCompileErrors(programId, "PROGRAM")

        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    fun use() {
        glUseProgram(programId)
    }
    // utility uniform functions
    // ------------------------------------------------------------------------
    fun setBool(name: String, value: Boolean) {
        glUniform1i(glGetUniformLocation(programId, name), if (value) 1 else 0)
    }
    // ------------------------------------------------------------------------
    fun setInt(name: String, value: Int) {
        glUniform1i(glGetUniformLocation(programId, name), value)
    }
    // ------------------------------------------------------------------------
    fun setFloat(name: String, value: Float) {
        glUniform1f(glGetUniformLocation(programId, name), value)
    }

    private fun checkCompileErrors(shader: Int, type: String) {
        val success = IntArray(1)
        if (type != "PROGRAM") {
            glGetShaderiv(shader, GL_COMPILE_STATUS, success)
            if (success[0] == 0) {
                throw RuntimeException(
                    "ERROR::SHADER_COMPILATION_ERROR of type: $type \n ${glGetShaderInfoLog(shader)}\n"
                )
            }
        } else {
            glGetProgramiv(shader, GL_LINK_STATUS, success)
            if (success[0] == 0) {
                throw RuntimeException(
                    "ERROR::PROGRAM_LINKING_ERROR of type: $type \n ${glGetShaderInfoLog(shader)}\n"
                )
            }
        }
    }
}
