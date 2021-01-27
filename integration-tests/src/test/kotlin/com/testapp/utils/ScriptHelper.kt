package com.testapp.utils

import com.testapp.extensions.tryOptional

internal object ScriptHelper {
    fun getGitCommit(): String? {
        return tryOptional {
            Runtime.getRuntime().exec("git rev-parse HEAD").inputStream.bufferedReader().use { it.readLine() }
        }
    }

    fun getGitBranch(): String? {
        return tryOptional {
            Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD").inputStream.bufferedReader()
                .use { it.readLine() }
        }
    }
}