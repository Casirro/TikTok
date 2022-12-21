package com.casirro.tiktokjava.Modules

class Video {
    var videoPath: String? = null
    var username: String? = null
    var caption: String? = null
    var comment: String? = null
    var videoId: String? = null
    var userId: String? = null

    constructor() {}
    constructor(videoPath: String?, caption: String?, videoId: String?, userId: String?){
        this.videoPath = videoPath
        this.caption = caption
        this.videoId = videoId
        this.userId = userId
    }

}