package com.casirro.tiktokjava.Modules

class User {
    var uid: String? = null
    var name: String? = null
    private var mail: String? = null
    private var password: String? = null
    var profileImage: String? = null
    var bio: String? = null
    private var token: String? = null
    var username: String? = null

    constructor(
        uid: String?,
        name: String?,
        password: String?,
        mail: String?,
        profileImage: String?,
        token: String?
    ) {
        this.uid = uid
        this.name = name
        this.mail = mail
        this.password = password
        this.profileImage = profileImage
        this.token = token
    }

    constructor(uid: String?, name: String?, mail: String?, password: String?, imageUrl: String?) {
        this.uid = uid
        this.name = name
        this.mail = mail
        this.password = password
        profileImage = imageUrl
    }

    constructor() {}
    constructor(uid: String?, mail: String?, username: String?, password: String?) {
        this.uid = uid
        this.mail = mail
        this.username = username
        this.password = password
    }
}