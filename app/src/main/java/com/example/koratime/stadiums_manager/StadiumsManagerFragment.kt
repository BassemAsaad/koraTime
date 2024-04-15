package com.example.koratime.stadiums_manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.koratime.R
import com.example.koratime.databinding.FragmentStadiumsManagerBinding

class StadiumsManagerFragment : Fragment(),StadiumsManagerNavigator{
    lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var dataBinding : FragmentStadiumsManagerBinding
    lateinit var viewModel : StadiumsManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_stadiums_manager,container,false)
        return dataBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =  ViewModelProvider(this).get(StadiumsManagerViewModel::class.java)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    fun initView() {
        dataBinding.vm = viewModel
        viewModel.navigator=this
        addStadiumActivity()

    }

    override fun addStadiumActivity() {
        dataBinding.createStadiumButton.setOnClickListener {
//            val intent = Intent(requireContext(), AddStadiumActivity::class.java)
//            startActivity(intent)
        }
    }



}