package org.jshobbysoft.memoru

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class MemAdapter (private val prefs: SharedPreferences, private val gaContext: Context): RecyclerView.Adapter<MemAdapter.ViewHolder>() {

    private val mainHandler = Handler(Looper.getMainLooper())

    var data =  arrayListOf<MemoruViewModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    // return the number of the items in the list
    override fun getItemCount() = data.size

    // delete an item
    private fun removeItem (position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val memViewModel = data[position]

        // set the card description and the initial calculated date
        holder.labelView.text = memViewModel.memDesc
        holder.calcDate.text = memViewModel.memCalcDate

        // set the thumbnail image on the side of the card
        val backgroundUri = Uri.parse(memViewModel.memURI)
        holder.imageThumb.setImageURI(backgroundUri)

        // set the string with the target date including using "since" or "until" depending if the date is in the past or future
        val dateStringForHolder = memViewModel.memDate + " " + memViewModel.memTime
        val calculateDateHolder = (gaContext as MainActivity).calculateDateOneLine(dateStringForHolder)
        val dateStringForHolderQualified = if (calculateDateHolder.take(2).toInt() < 0) {
            "until $dateStringForHolder"
        } else {
            "since $dateStringForHolder"
        }
        holder.dateString.text = dateStringForHolderQualified

        // loop the date calculation
        mainHandler.post(object : Runnable {
            override fun run() {
                holder.calcDate.text = (gaContext).calculateDateOneLine(dateStringForHolder)
                mainHandler.postDelayed(this, 1000)
            }
        })

        holder.itemView.setOnClickListener {v ->
            val bundle = bundleOf("prefNum" to memViewModel.memPrefIndex)
            v.findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
        }

        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(gaContext)
            builder.setMessage("Are you sure you want to Delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    // Delete selected note from database
                    removeItem(position)
                    prefs.edit().remove(memViewModel.memPrefIndex).commit()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
            true
        }
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val labelView: TextView = itemView.findViewById(R.id.textViewLabel)
        val calcDate: TextView = itemView.findViewById(R.id.myCalculatedDate)
        val dateString: TextView = itemView.findViewById(R.id.myUpdatedDateString)
        val imageThumb: ImageView = itemView.findViewById(R.id.imageThumb)
    }
}
