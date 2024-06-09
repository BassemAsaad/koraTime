package com.example.koratime.model

data class PlayersSearchModel(
    var counter : Int?=null,
    var playersIdList: MutableList<String>?=null,
    var playersNameList: MutableList<String>?=null
)
