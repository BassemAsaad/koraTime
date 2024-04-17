package com.example.koratime.messages

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityMessagesBinding

class MessagesActivity : BasicActivity<ActivityMessagesBinding,MessagesViewModel>(),MessagesChatNavigator {


    override fun getLayoutID(): Int {
        return R.layout.activity_messages
    }

    override fun initViewModel(): MessagesViewModel {
        return ViewModelProvider(this)[MessagesViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
    }

    override fun initView() {
        viewModel.navigator = this
        dataBinding.vm = viewModel

    }
}