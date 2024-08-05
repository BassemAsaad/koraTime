@file:Suppress("DEPRECATION")

package com.example.koratime.basic

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BasicFragment <DB : ViewDataBinding, VM : BasicViewModel<*>>: Fragment() {
    private lateinit var _dataBinding : DB
    private lateinit var _viewModel: VM
    protected val dataBinding get() = _dataBinding
    protected val viewModel get() = _viewModel
    private var progressDialog: ProgressDialog?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _dataBinding = DataBindingUtil.inflate(inflater, getLayoutID(),container,false)
        return _dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewModel = initViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToLiveData()
    }

    abstract fun initViewModel():VM
    abstract fun initView()
    abstract fun getLayoutID():Int


    private fun subscribeToLiveData() {
        _viewModel.showLoading.observe(viewLifecycleOwner) { show ->
            if (show) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }
    private fun showLoading(){
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.apply {
            setMessage("Loading...")
            setCancelable(false)
            show()
        }
    }
    private fun hideLoading(){
        progressDialog?.dismiss()
        progressDialog = null
    }

}