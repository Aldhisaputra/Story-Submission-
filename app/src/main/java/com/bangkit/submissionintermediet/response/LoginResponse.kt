package com.bangkit.submissionintermediet.response

data class LoginResponse(
    val loginResult: LoginResult? = null,
    val error: Boolean? = null,
    val message: String? = null
)

data class LoginResult(
    val name: String? = null,
    val userId: String? = null,
    val token: String? = null
)

