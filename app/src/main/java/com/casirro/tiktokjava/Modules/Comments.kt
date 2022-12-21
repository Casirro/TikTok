package com.casirro.tiktokjava.Modules

class Comments {
    private var commentId: String? = null
    var senderId: String? = null
    private var videoId: String? = null
    var comment: String? = null
    private var username: String? = null
    private var profileImage: String? = null

    constructor(commentId: String?, senderId: String?, videoId: String?, comment: String?, username: String?, profileImage: String?) {
        this.commentId = commentId
        this.senderId = senderId
        this.videoId = videoId
        this.comment = comment
        this.username = username
        this.profileImage = profileImage

    }
    constructor()
}