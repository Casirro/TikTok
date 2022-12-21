package com.casirro.chat.Models

class Message {
    var messageId: String? = null
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var imageUrl: String? = null
    var timestamp: Long = 0
    var feeling = -1

    constructor(message: String?, senderId: String?, receiverId: String?, timestamp: Long, ) {
        this.message = message
        this.senderId = senderId
        this.receiverId = receiverId
        this.timestamp = timestamp
    }

    constructor() {}
    constructor(messageTxt: String?, senderUid: String?, time: Long) {
        message = messageTxt
        senderId = senderUid
        timestamp = time
    }
}