package com.example.note.adapter

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.note.databinding.NoteLayoutBinding
import com.example.note.fragments.HomeFragmentDirections
import com.example.note.model.Note
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NoteAdapter() : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var searchQuery: String = ""
    

    fun setSearchQuery(query: String) {
        searchQuery = query
        notifyDataSetChanged() // Frissíti az adaptert a keresési lekérdezés változása után
    }

    class NoteViewHolder(val itemBinding: NoteLayoutBinding): RecyclerView.ViewHolder(itemBinding.root){
        fun highlightText(query: String) {
            val titleText = itemBinding.noteTitle.text.toString()
            val descText = itemBinding.noteDesc.text.toString()
            if (query.isNotEmpty()) {
                    highlightTextInTextView(query, itemBinding.noteTitle, titleText)
                    highlightTextInTextView(query, itemBinding.noteDesc, descText)
            } else {
                    // Ha a query üres, visszaállítja a szövegeket az eredeti állapotukra
                    itemBinding.noteTitle.text = titleText
                    itemBinding.noteDesc.text = descText
            }
        }

        private fun highlightTextInTextView(query: String, textView: TextView, originalText: String) {
            val spannableString = SpannableString(originalText)
            val startIndex = originalText.indexOf(query, ignoreCase = true)

            if (startIndex != -1) {
                val endIndex = startIndex + query.length
                val foregroundColorSpan = ForegroundColorSpan(Color.rgb(33,73,96))
                spannableString.setSpan(foregroundColorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = spannableString
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteDesc == newItem.noteDesc &&
                    oldItem.noteTitle == newItem.noteTitle
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        with(holder.itemBinding){
            holder.itemBinding.noteTitle.text = currentNote.noteTitle
            holder.itemBinding.noteDesc.text = currentNote.noteDesc

            if (searchQuery.isNotEmpty()) {
                holder.highlightText(searchQuery)
            }


            val wordCount = currentNote.noteDesc.trim().split("\\s+".toRegex()).count{it.isNotEmpty()}
            wordCountTextView.text = wordCount.toString()+" words"

            val lastModifiedDate = Date(currentNote.lastModified)
            val today = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val formattedDate = if (dateFormat.format(lastModifiedDate) == dateFormat.format(today)) {
                "Today at ${timeFormat.format(lastModifiedDate)}"
            } else {
                dateFormat.format(lastModifiedDate)
            }

            noteModifiedDateTextView.text = formattedDate
        }
        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }

        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }
    }
}