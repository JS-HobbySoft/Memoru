package org.jshobbysoft.memoru

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import org.jshobbysoft.memoru.databinding.FragmentSecondBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "prefNum"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var prefIndex: String? = null
    private var param2: String? = null

    private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val memEntry = arrayOf("0","0","0","0","0","0","0","0","0","0","999","false")
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            prefIndex = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // if this fragment receives an index number, use the stored values for this index from shared preferences
        if (prefIndex != null) {

            val sharedPreferences =
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireActivity()/* Activity context */)
            val memNumbersArray = sharedPreferences?.getString(prefIndex.toString(),"0")?.split("^")
            if (memNumbersArray!!.size == 12) {

                sharedPreferences.edit().putString("userDateKey",memNumbersArray[0]).apply()
                sharedPreferences.edit().putString("userTimeKey",memNumbersArray[1]).apply()
                sharedPreferences.edit().putString("background_uri_key",memNumbersArray[2]).apply()
                sharedPreferences.edit().putString("textColorKey",memNumbersArray[4]).apply()
                sharedPreferences.edit().putString("positionKey",memNumbersArray[5]).apply()
                sharedPreferences.edit().putString("textViewSizeKey",memNumbersArray[6]).apply()
                sharedPreferences.edit().putString("scaleTypeKey",memNumbersArray[7]).apply()
                sharedPreferences.edit().putString("widgetTextColorKey",memNumbersArray[8]).apply()
                sharedPreferences.edit().putString("textDescKey",memNumbersArray[9]).apply()
                sharedPreferences.edit().putBoolean("widgetUseKey",memNumbersArray[11].toBoolean()).apply()

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        imageView = binding.imageView3

        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireActivity()/* Activity context */)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    imageUri = data?.data
                    val contentResolver = requireActivity().contentResolver
                    val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    imageUri?.let { contentResolver.takePersistableUriPermission(it, takeFlags) }
                    imageView.setImageURI(imageUri)
                    val sharedPref =
                        androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
                    with(sharedPref.edit()) {
                        putString("background_uri_key", imageUri.toString())
                        apply()
                    }
                }
            }

        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_settings, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        // Kill looper to avoid creating multiple looper instances
                        mainHandler.removeCallbacksAndMessages(null)
                        val settingsIntent = Intent(requireContext(), SettingsActivity::class.java)
                        startActivity(settingsIntent)
                        true
                    }
                    R.id.action_cancel -> {
                        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                        true
                    }
                    R.id.action_save -> {
                        var memNumSP = "0"

                        // If the preference index is specified, save the new data under that index
                        // If there is no index, save the data under a new index
                        if (prefIndex != null) {
                            memNumSP = prefIndex.toString()
                        } else {
                            // Find the lowest numbered shared preference and save the data under that key
                            for (j in 1..200) {
                                if (sharedPreferences.getString(j.toString(),"null") == "0") {
                                    memNumSP = j.toString()
                                    break
                                }
                            }
                        }
                        memEntry[10] = memNumSP
                        val memEntryString = memEntry.joinToString("^")
                        sharedPreferences.edit().putString(memNumSP,memEntryString).commit()

                        // If we are using this memorial for the widget, save it in its own line
                        if (sharedPreferences.getBoolean("widgetUseKey",false)) {
                            sharedPreferences.edit().putString("widget",memEntryString).commit()
                        }

                        // set the widget use key back to false
                        sharedPreferences.edit().putBoolean("widgetUseKey",false)

                        // Go back to the recyclerview
                        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val tV: TextView = binding.textView
        val tV2: TextView = binding.textView2

        val sharedPreferences =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireActivity()/* Activity context */)

        if (sharedPreferences.getString("userDateKey", null).isNullOrEmpty()) {
            sharedPreferences.edit().putString("userDateKey", "02/02/2022").apply()
        }

        if (sharedPreferences.getString("userTimeKey", null).isNullOrEmpty()) {
            sharedPreferences.edit().putString("userTimeKey", "22:22:22").apply()
        }

        memEntry[0] = sharedPreferences.getString("userDateKey","02/02/2002")!!
        memEntry[1] = sharedPreferences.getString("userTimeKey", "02:02:00")!!

        var userDateAndTimeFromPickers = memEntry[0] + " " + memEntry[1]

//      Process date and time picked by user
        childFragmentManager.setFragmentResultListener("requestKeyDate",this) { _, bundle ->
            val result = bundle.getString("bundleKeyDate")
            if (result!!.isNotEmpty()) {
                sharedPreferences.edit().putString("userDateKey", result.toString()).apply()
                userDateAndTimeFromPickers = result.toString() + " " + sharedPreferences.getString("userTimeKey", "02:02:00")
                (activity as MainActivity).updateDateString(userDateAndTimeFromPickers, tV2)
                memEntry[0] = result
            }
        }

        childFragmentManager.setFragmentResultListener("requestKeyTime",this) { _, bundle ->
            val result = bundle.getString("bundleKeyTime")
            if (result!!.isNotEmpty()) {
                sharedPreferences.edit().putString("userTimeKey", result.toString()).apply()
                userDateAndTimeFromPickers = sharedPreferences.getString(
                    "userDateKey",
                    "02/02/2002"
                ) + " " + result.toString()
                (activity as MainActivity).updateDateString(userDateAndTimeFromPickers, tV2)
                memEntry[1] = result
            }
        }

//      Set up button to choose a picture
        val button = binding.buttonLoadPicture
        button.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(gallery)
        }

//      Set up buttons to launch the date and time pickers
        val buttonDate = binding.buttonPickDate
        buttonDate.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(childFragmentManager, "datePicker")
        }

        val buttonTime = binding.buttonPickTime
        buttonTime.setOnClickListener {
            val newFragment = TimePickerFragment()
            newFragment.show(childFragmentManager, "timePicker")
        }

//      update string with "since" or "until" date
        (activity as MainActivity).updateDateString(userDateAndTimeFromPickers, tV2)

//      change text view size based on preference settings
        val params = tV.layoutParams as ConstraintLayout.LayoutParams
        params.height = sharedPreferences.getString("textViewSizeKey", "1000")!!.toInt()
        memEntry[6] = sharedPreferences.getString("textViewSizeKey", "1000")!!

//      update text position based on preference settings
        when (val positionOfText = sharedPreferences.getString("positionKey", "")) {
            "top" -> {
                params.topToTop = ConstraintLayout.LayoutParams.MATCH_PARENT
                params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            }
            "middle" -> {
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
            "bottom" -> {
                params.topToTop = ConstraintLayout.LayoutParams.UNSET
                params.bottomToTop = tV2.id
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            }
            else -> println(positionOfText)
        }
        tV.layoutParams = params
        memEntry[5] = sharedPreferences.getString("positionKey", "")!!

//      use default image if no shared preference has been saved
        val backgroundUriString = sharedPreferences.getString("background_uri_key", "")
        if (backgroundUriString == "") {
            imageView.setImageResource(R.drawable.ic_settings_test_foreground)
        } else {
            val backgroundUri = Uri.parse(backgroundUriString)
            imageView.setImageURI(backgroundUri)
            memEntry[2] = backgroundUriString!!
        }

        fun colorChangeTV(newColor: Int): Boolean {
            for (item: TextView? in listOf(tV, tV2)) {
                item?.setTextColor(ContextCompat.getColor(requireActivity(), newColor))
            }
            return true
        }

//      update text colors based on preference settings
        val colorOfText = sharedPreferences.getString("textColorKey", "red")
        memEntry[4] = colorOfText!!
        when (colorOfText) {
            "red" -> colorChangeTV(R.color.red)
            "green" -> colorChangeTV(R.color.green)
            "blue" -> colorChangeTV(R.color.blue)
            "black" -> colorChangeTV(R.color.black)
            "white" -> colorChangeTV(R.color.white)
            else -> println(colorOfText)
        }

        val colorOfWidgetText = sharedPreferences.getString("widgetTextColorKey", "red")
        memEntry[8] = colorOfWidgetText!!

//      update image scaling based on preference settings
        val scaleOfImage = sharedPreferences.getString("scaleTypeKey", "center")
        memEntry[7] = scaleOfImage!!
        when (scaleOfImage) {
            "center" -> imageView.scaleType = ImageView.ScaleType.CENTER
            "center_crop" -> imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            "center_inside" -> imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            "fit_center" -> imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            "fit_start" -> imageView.scaleType = ImageView.ScaleType.FIT_START
            "fit_end" -> imageView.scaleType = ImageView.ScaleType.FIT_END
            else -> println(scaleOfImage)
        }

        val descKey = sharedPreferences.getString("textDescKey", "Change this in the Settings menu")
        memEntry[9] = descKey!!

        val widgetUseKeyValue = sharedPreferences.getBoolean("widgetUseKey",false)
        memEntry[11] = widgetUseKeyValue.toString()

        memEntry[3] = (activity as MainActivity).calculateDateOneLine(userDateAndTimeFromPickers)

//      Loop the date/time calculator to get the time interval
//        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                tV.text = (activity as MainActivity).calculateDate(userDateAndTimeFromPickers)
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Kill looper to avoid NPE when navigating
        mainHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
}