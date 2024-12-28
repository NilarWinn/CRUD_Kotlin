package com.example.bulletinboard.data.dto

import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

data class User(
    val _id: String,
    val name: String,
    val email: String,
    @SerializedName(value = "profile", alternate = ["old_profile"])
    val profile: String,
    val type: String,
    val phone: String,
    val address: String,
    val dob: String,
)

data class UserToRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    var type: String? = null,
    val phone: String? = null,
    val dob: String? = null,
    val address: String? = null,
    val profile: File? = null,
    val old_profile: String? = null,
    val user_id: String? = null,
) : Serializable {
    override fun toString(): String {
        return "[name: $name, email: $email, password: $password, type: $type, " +
                "phone: $phone, dob: $dob, address: $address, user_id: $user_id, " +
                "old_profile: $old_profile]"
    }
}