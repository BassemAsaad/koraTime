package com.example.koratime.friends.search

import com.example.koratime.basic.BasicViewModel
import com.example.koratime.chat.ChatViewModel

class SearchViewModel : BasicViewModel<SearchNavigator>(){
    override val TAG: String
        get() = SearchViewModel::class.java.simpleName
}