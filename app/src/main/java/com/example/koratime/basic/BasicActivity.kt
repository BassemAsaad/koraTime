package com.example.koratime.basic

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer

abstract class BasicActivity <DB : ViewDataBinding, VM : BasicViewModel<*>>: AppCompatActivity(){

    lateinit var dataBinding : DB
    lateinit var viewModel: VM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, getLayoutID())

        viewModel = initViewModel()
        Subscribeto_LiveData()

    }

    abstract fun getLayoutID():Int
    abstract fun initViewModel():VM
    abstract fun initView()

     private fun Subscribeto_LiveData() {
         viewModel.messageLiveData.observe(this, Observer { message ->
             showDialog(message, "ok")
         })
         viewModel.showLoading.observe(this, Observer { show ->
             if (show) {
                 showLoading()
             } else {
                 hideLoading()
             }
         })
     }
     var alertDialog : AlertDialog?=null
     fun showDialog(message : String?=null,
                    posActionName : String?=null,
                    posAction : DialogInterface.OnClickListener?=null,
                    negativeActionName : String?=null,
                    negativeAction : DialogInterface.OnClickListener?=null,
                    cancelable:Boolean=true) {
         val defaultAction = object : DialogInterface.OnClickListener{
             override fun onClick(dialog: DialogInterface?, which: Int) {
                 dialog?.dismiss()
             }
         }
         val builder = AlertDialog.Builder(this).setMessage(message)
         if (posActionName!=null){
             builder.setPositiveButton(posActionName,posAction?:defaultAction)
         }
         if (negativeActionName!=null){
             builder.setNegativeButton(negativeActionName,negativeAction?:defaultAction)
         }
         builder.setCancelable(cancelable)
         alertDialog = builder.show()
     }

     fun hideAlertDialog(){
         alertDialog?.dismiss()
         alertDialog = null
     }

     var progressDialog: ProgressDialog?=null
     fun showLoading(){
         progressDialog = ProgressDialog(this)
         progressDialog?.setMessage("Loading...")
         progressDialog?.setCancelable(false)
         progressDialog?.show()
     }
     fun hideLoading(){
         progressDialog?.dismiss()
         progressDialog = null
     }


}