package com.example.koratime.stadiums_manager.manageStadium

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.koratime.Constants
import com.example.koratime.DataUtils
import com.example.koratime.R
import com.example.koratime.adapters.TimeSlotsForManagerAdapter
import com.example.koratime.basic.BasicFragment
import com.example.koratime.database.addBookingToFirestore
import com.example.koratime.database.deleteStadiumFromFirestore
import com.example.koratime.database.getBookedTimesFromFirestore
import com.example.koratime.database.getBookingRequestsFromFirestore
import com.example.koratime.database.getMultipleImagesFromFirestore
import com.example.koratime.database.removeBookingFromFirestore
import com.example.koratime.database.uploadMultipleImagesToStorage
import com.example.koratime.databinding.FragmentManageStadiumBinding
import com.example.koratime.home.HomeActivity
import com.example.koratime.model.BookingModel
import com.example.koratime.model.StadiumModel
import com.example.koratime.stadiums_manager.manageStadium.booking_requests.BookingRequestsFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DefaultLocale", "SetTextI18n")
class ManageStadiumFragment : BasicFragment<FragmentManageStadiumBinding, ManageStadiumViewModel>(),
    ManageStadiumNavigator {
    private lateinit var stadiumModel: StadiumModel
    private var adapter = TimeSlotsForManagerAdapter(emptyList(), emptyList())

    private lateinit var timeSlotsList: List<String>
    private lateinit var availableSlots: List<String>
    private lateinit var bookedTimesList: List<String>

    private var selectedDate = SimpleDateFormat("MM_dd_yyyy", Locale.getDefault()).format(Date())
    private val slideImageList = mutableListOf<String>()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    companion object {
        const val TAG = "ManageStadiumActivity"
    }

    override fun getLayoutID(): Int {
        return R.layout.fragment_manage_stadium
    }

    override fun initViewModel(): ManageStadiumViewModel {
        return ViewModelProvider(this)[ManageStadiumViewModel::class.java]
    }

    override fun initView() {
        (activity as AppCompatActivity).setSupportActionBar(dataBinding.toolbar)
        callback()
        getStadiumImages()
    }

    override fun callback() {
        viewModel.apply {
            dataBinding.vm = viewModel
            navigator = this@ManageStadiumFragment
            stadiumModel = arguments?.getParcelable(Constants.STADIUM_MANAGER)!!
            stadium = stadiumModel
        }

        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(true)
            title = stadiumModel.stadiumName
            dataBinding.toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }



        dataBinding.apply {
            getTimeSlots(selectedDate)
            recyclerView.adapter = adapter
            swipeRefresh.setOnRefreshListener {
                dataBinding.swipeRefresh.isRefreshing = false
                getStadiumImages()
            }
            openImagePicker()
            imagePicker.setOnClickListener {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            deleteStadiun.setOnClickListener {
                deleteStadiumFromFirestore(
                    stadiumID = stadiumModel.stadiumID!!,
                    onSuccessListener = {
                        Log.e("Firebase ", " Stadium Removed Successfully from firestore")
                        requireActivity().finish()

                    },
                    onFailureListener = {
                        Log.e("Firebase ", "Error Removing Stadium from firestore")
                    }
                )

            }
            calendarView.minDate = System.currentTimeMillis()
            // Add an OnDateChangeListener to the CalendarView
            calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                selectedDate = String.format("%02d_%02d_%04d", month + 1, dayOfMonth, year)
                Log.e("Firebase", "Date selected: $selectedDate")
                getTimeSlots(selectedDate)
            }
            notificationIc.setOnClickListener {
                val bookingRequestsFragment = BookingRequestsFragment()
                val bundle = Bundle()
                bundle.putParcelable(Constants.STADIUM, stadiumModel)
                bookingRequestsFragment.arguments = bundle
                (activity as HomeActivity).addFragment(bookingRequestsFragment,true)
            }

        }
        // Set up click listener for booking button in the adapter
        adapter.onBookClickListener = object : TimeSlotsForManagerAdapter.OnBookClickListener {
            @SuppressLint("SetTextI18n")
            override fun onclick(
                slot: String,
                holder: TimeSlotsForManagerAdapter.ViewHolder,
                position: Int
            ) {
                holder.dataBinding.apply {
                    tvTimeSlot.setOnLongClickListener {
                        removeBookingFromFirestore(
                            timeSlot = tvTimeSlot.text.toString(),
                            stadiumID = stadiumModel.stadiumID!!,
                            date = selectedDate,
                            onSuccessListener = {
                                Log.e("Firebase", " Book has been Removed successfully")
                                holder.dataBinding.apply {
                                    tvTimeSlot.apply {
                                        isEnabled = true
                                        setTextColor((Color.BLACK))
                                    }
                                    btnBook.apply {
                                        isEnabled = true
                                        text = "Book"
                                        backgroundTintList = null
                                    }
                                }
                                Toast.makeText(
                                    requireContext(),
                                    "${holder.dataBinding.tvTimeSlot.text} Book Removed Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getTimeSlots(selectedDate)
                            },
                            onFailureListener = {
                                Log.e("Firebase", "Error: ", it)
                            }

                        )
                        true
                    }
                    btnBook.setOnClickListener {
                        addBookingToFirestore(
                            timeSlot = holder.dataBinding.tvTimeSlot.text.toString(),
                            stadiumID = stadiumModel.stadiumID!!,
                            date = selectedDate,
                            user = DataUtils.user!!,
                            onSuccessListener = {
                                Log.e(
                                    "Firebase",
                                    " ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} to the stadiumID: ${stadiumModel.stadiumID}"
                                )

                                holder.dataBinding.apply {
                                    tvTimeSlot.apply {
                                        isEnabled = false
                                        setTextColor((Color.GRAY))

                                    }
                                    btnBook.apply {
                                        isEnabled = false
                                        text = "Booked"
                                        backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                                    }
                                }

                                Toast.makeText(requireContext(),
                                    "${holder.dataBinding.tvTimeSlot.text} Booked Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getTimeSlots(selectedDate)

                            },
                            onFailureListener = { e ->
                                Log.e("Firebase", " Error:  ${holder.dataBinding.tvTimeSlot.text} booked on  $selectedDate from userId: ${DataUtils.user!!.id!!} ", e)
                            }
                        )
                    }
                }

            }
        }
    }

    private fun getTimeSlots(date: String) {
        getBookedTimesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            date = date,
            onSuccessListener = { bookedList ->

                bookedTimesList = bookedList
                Log.e(TAG, "BookedSlots List: $bookedTimesList")

                // create opening and closing list
                timeSlotsList = viewModel.createListForOpeningTimes(
                    stadiumModel.opening!!,
                    stadiumModel.closing!!,
                    resources.getStringArray(R.array.time_slots)
                )
                Log.e(TAG, "TimeSlots List: $timeSlotsList")

                availableSlots =
                    viewModel.removeBookedListFromOpeningTimes(timeSlotsList, bookedTimesList)
                Log.e(TAG, "AvailableSlots List: $availableSlots")

                adapter.updateTimeSlots(timeSlotsList, bookedTimesList)
            },
            onFailureListener = { e ->
                Log.e("Firebase", "Error fetching booked times", e)
            }
        )
    }

    private fun getStadiumImages() {
        getMultipleImagesFromFirestore(
            stadiumID = stadiumModel.stadiumID!!,
            onSuccessListener = { urls ->
                slideImageList.clear()
                slideImageList.addAll(urls)
                Log.e("Firebase", " List of $urls")
                val imageList = ArrayList<SlideModel>()
                for (i in slideImageList) {
                    imageList.add(SlideModel(i, ""))
                }

                if (imageList.isNotEmpty()) {
                    dataBinding.stadiumImages.visibility = View.VISIBLE
                    dataBinding.imageSlider.visibility = View.VISIBLE
                    dataBinding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
                }

            },
            onFailureListener = {
                Log.e("Firebase", "Failed To get Images From firestore")
            }
        )
    }

    private fun openImagePicker() {
        Log.e("StadiumID", "${stadiumModel.stadiumID}")
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) { uris ->
                // photo picker
                if (uris.isNotEmpty()) {
                    dataBinding.imagePickerTextView.text = "Uploading Images.."
                    Log.e("PhotoPicker", "Selected URI: $uris")
                    viewModel.showLoading.value = true
                    //upload images to storage
                    uploadMultipleImagesToStorage(uris = uris, stadiumID = stadiumModel.stadiumID!!,
                        onSuccessListener = { imagesList ->
                            Log.e("Firebase", "Images uploaded successfully to storage")
                            viewModel.apply {
                                listOfUrls.value = imagesList
                                addImageUrlsToFirestore()
                            }
                            dataBinding.imagePickerTextView.text = "Images Selected"

                        },
                        onFailureListener = {
                            Log.e("Firebase", "Error uploading images to storage")
                        }
                    )


                } else {
                    dataBinding.imagePickerTextView.text = " No Image Selected"
                    Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
                    Log.e("PhotoPicker", "No image selected")
                }


            }
    }



}

