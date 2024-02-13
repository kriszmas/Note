package com.example.note.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.adapter.NoteAdapter
import com.example.note.databinding.FragmentHomeBinding
import com.example.note.model.Note
import com.example.note.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider {
    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    private var isListView = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        setupHomeRecyclerView()

        binding.addNoteFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }
    }

    private fun setupHomeRecyclerView() {
        noteAdapter = NoteAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        notesViewModel.getAllNotes().observe(viewLifecycleOwner) { note ->
            noteAdapter.differ.submitList(note)
            updateUI(note)
        }
    }

    private fun updateUI(note: List<Note>?) {
        if (note != null) {
            if (note.isNotEmpty()) {
                binding.emptyNotesImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
            } else {
                binding.emptyNotesImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu)
        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.moreOptions -> {
                showPopupMenu()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    private fun showPopupMenu() {
        // Az anchorView meghatározása; itt feltételezzük, hogy a Toolbar része az Activity-nek
        val anchorView = activity?.findViewById<View>(R.id.moreOptions) ?: return
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        val toggleItem = popup.menu.findItem(R.id.action_toggle_view)
        toggleItem.title = if (isListView) "View as a List" else "View as a Gallery"

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.toolbarfirst -> {
                    confirmDeleteAllNotes()
                    true
                }
                R.id.action_toggle_view ->{
                    isListView =!isListView
                    updateUIBasedOnViewType()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
    private fun updateUIBasedOnViewType() {
        val layoutManager = if (isListView) {
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        } else {
            LinearLayoutManager(context)
        }
        binding.homeRecyclerView.layoutManager = layoutManager
        noteAdapter.notifyDataSetChanged()
    }

    private fun confirmDeleteAllNotes() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Delete all notes?")
            setMessage("Are you sure you want to delete all notes?")
            setPositiveButton("Delete") { _, _ ->
                notesViewModel.deleteAllNotes()
            }
            setNegativeButton("Cancel", null)
        }.show()
    }




    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchNote(newText)
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        notesViewModel.searchNote(searchQuery).observe(this) { list ->
            noteAdapter.differ.submitList(list)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }
}