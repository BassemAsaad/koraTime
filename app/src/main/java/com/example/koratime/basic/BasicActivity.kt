@file:Suppress("DEPRECATION")

package com.example.koratime.basic

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BasicActivity<DB : ViewDataBinding, VM : BasicViewModel<*>> : AppCompatActivity() {

    private lateinit var _dataBinding: DB
    private lateinit var _viewModel: VM
    protected val dataBinding get() = _dataBinding
    protected val viewModel get() = _viewModel
    abstract val TAG: String
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _dataBinding = DataBindingUtil.setContentView(this, getLayoutID())
        _viewModel = initViewModel()
        subscribeToLiveData()
        initView()
    }

    abstract fun getLayoutID(): Int
    abstract fun initViewModel(): VM
    abstract fun initView()
    abstract fun callback()
    private fun subscribeToLiveData() {
        _viewModel.showLoading.observe(this) { show ->
            if (show) {
                showLoading()
            } else {
                hideLoading()
            }
        }
    }

    protected fun log(value: Any) {
        Log.e(TAG, value.toString())
    }
    private fun showLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog?.apply {
            setMessage("Loading...")
            setCancelable(false)
            show()
        }
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }


}