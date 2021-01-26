package com.testapp.utils

internal object ScriptHelper {
    fun getGitCommit(): String? {
        return try {
            Runtime.getRuntime().exec("git rev-parse HEAD").inputStream.bufferedReader().use { it.readLine() }
        } catch (t: Throwable) {
            null
        }
    }

    fun getGitBranch(): String? {
        return try {
            Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD").inputStream.bufferedReader()
                .use { it.readLine() }
        } catch (t: Throwable) {
            null
        }
    }
}