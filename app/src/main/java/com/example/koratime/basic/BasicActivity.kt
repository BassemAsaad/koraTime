@file:Suppress("DEPRECATION")

package com.example.koratime.basic

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BasicActivity <DB : ViewDataBinding, VM : BasicViewModel<*>>: AppCompatActivity(){

    lateinit var dataBinding : DB
    lateinit var viewModel: VM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, getLayoutID())

        viewModel = initViewModel()
        subscribeToLiveData()

    }

    abstract fun getLayoutID():Int
    abstract fun initViewModel():VM
    abstract fun initView()

     private fun subscribeToLiveData() {
         viewModel.showLoading.observe(this) { show ->
             if (show) {
                 showLoading()
             } else {
                 hideLoading()
             }
         }
     }


     private var progressDialog: ProgressDialog?=null
     private fun showLoading(){
         progressDialog = ProgressDialog(this)
         progressDialog?.setMessage("Loading...")
         progressDialog?.setCancelable(false)
         progressDialog?.show()
     }
     private fun hideLoading(){
         progressDialog?.dismiss()
         progressDialog = null
     }


}