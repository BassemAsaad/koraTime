package com.example.koratime.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BasicFragment<DB : ViewDataBinding, VM : ViewModel> : Fragment() {

    lateinit var dataBinding: DB
    lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using data binding
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize view model
        viewModel = ViewModelProvider(this).get(viewModelClass())
        // Perform any additional initialization
        initView()
    }

    abstract fun getLayoutId(): Int

    abstract fun viewModelClass(): Class<VM>

    abstract fun initView()
}