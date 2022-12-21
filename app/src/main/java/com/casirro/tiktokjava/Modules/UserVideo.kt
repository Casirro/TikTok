package com.casirro.tiktokjava.Modules

class UserVideo {
    var name: String? = null
    var profileImage: String? = null
    var videos: ArrayList<Video>? = null
    var likesCount: Int? = null

    constructor(name: String?, profileImage: String?,  likesCount: Int?, videos: ArrayList<Video>) {
        this.name = name
        this.profileImage = profileImage
        this.likesCount = likesCount
        this.videos = videos
    }
    constructor()




}