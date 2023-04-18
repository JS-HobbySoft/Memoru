
package org.jshobbysoft.memoru

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.jshobbysoft.memoru.databinding.FragmentFirstBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private val mainHandler = Handler(Looper.getMainLooper())

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater,container,false)

        // this creates a vertical layout Manager
        binding.recyclerviewMem.layoutManager = LinearLayoutManager(activity)

        val dataMem = ArrayList<MemoruViewModel>()

        // get all key-value pairs from shared preferences
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireActivity())

        // initialize Shared Preferences if not already created
        if (sharedPreferences?.getString("200",null) == null) {
            for (memNumber in 1..200) {
                sharedPreferences?.edit()?.putString(memNumber.toString(),"0")?.apply()
            }
            sharedPreferences?.edit()?.putString("widget","0")?.apply()
        }

        // create data array
        for (k in 1..200) {
            val memNumbersArray = sharedPreferences?.getString(k.toString(),"0")?.split("^")
            if (memNumbersArray!!.size == 12) {
                val userDateAndTimeFromPickers = memNumbersArray[0] + " " + memNumbersArray[1]
                val newTime = (activity as MainActivity).calculateDateOneLine(userDateAndTimeFromPickers)
                dataMem.add(
                    MemoruViewModel(memNumbersArray[0],memNumbersArray[1],memNumbersArray[2],
                        newTime,memNumbersArray[4],memNumbersArray[5],
                        memNumbersArray[6],memNumbersArray[7],memNumbersArray[8],memNumbersArray[9],
                        memNumbersArray[10],memNumbersArray[11])
                )
            }
        }

        // This will pass the ArrayList to our Adapter
        val adapterMem = MemAdapter(sharedPreferences, requireContext())
        adapterMem.data = dataMem

        // Setting the Adapter with the recyclerview
        binding.recyclerviewMem.adapter = adapterMem

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
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