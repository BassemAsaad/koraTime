package com.example.koratime.stadiums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.basic.BasicActivity
import com.example.koratime.databinding.ActivityStadiumsBinding

class StadiumsActivity : BasicActivity <ActivityStadiumsBinding,StadiumsViewModel> (),StadiumsNavigator{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

    }

    override fun getLayoutID(): Int {
        return R.layout.activity_stadiums
    }

    override fun initViewModel(): StadiumsViewModel {
        return ViewModelProvider(this).get(StadiumsViewModel::class.java)
    }

    override fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator = this
    }
}