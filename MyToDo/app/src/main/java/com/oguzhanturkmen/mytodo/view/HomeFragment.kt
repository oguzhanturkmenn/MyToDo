package com.oguzhanturkmen.mytodo.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.oguzhanturkmen.mytodo.R
import com.oguzhanturkmen.mytodo.databinding.FragmentHomeBinding
import com.oguzhanturkmen.mytodo.view.adapter.NoteAdapter
import com.oguzhanturkmen.mytodo.view.model.Note
import com.oguzhanturkmen.mytodo.view.util.toast
import com.oguzhanturkmen.mytodo.view.viewmodel.NoteViewModel


class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() =_binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteViewModel = (activity as MainActivity).noteViewModel
        setUpRecyclerView()
        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged", "ShowToast")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                AlertDialog.Builder(activity).apply {
                    setTitle("Delete Note")
                    setMessage("Are you sure you want to permanently delete this note?")
                    setPositiveButton("DELETE") { _, _ ->

                            noteViewModel.deleteNote(noteAdapter.Note(viewHolder.adapterPosition))

                        Snackbar.make(view,"Deleted",Snackbar.LENGTH_SHORT).setAction("UNDO"){
                            noteViewModel.getAllNote().observe(viewLifecycleOwner){
                                noteAdapter.differ.submitList(it)
                                noteAdapter.notifyDataSetChanged()
                            }
                        }.show()
                    }
                    setNegativeButton("CANCEL",){ _, _ ->
                        noteViewModel.getAllNote().observe(viewLifecycleOwner){
                            noteAdapter.differ.submitList(it)
                            noteAdapter.notifyDataSetChanged()
                        }
                    }
                }.create().show()
            }
        }).attachToRecyclerView(binding.recyclerView)
    }


    private fun setUpRecyclerView(){
        noteAdapter = NoteAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = noteAdapter

        }
        activity?.let {
            noteViewModel.getAllNote().observe(viewLifecycleOwner) { note ->

                noteAdapter.differ.submitList(note)
                updateUI(note)
            }
        }
    }
    private fun updateUI(note : List<Note>){
        if (note.isNotEmpty()){
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvNoNoteAvailable.visibility = View.GONE
        }else{
            binding.recyclerView.visibility = View.GONE
            binding.tvNoNoteAvailable.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_menu,menu)
        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        /*if (query != null) {
            searchNote(query)
        }*/
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        if (newText != null) {
            searchNote(newText)
        }
        return true
    }


    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        noteViewModel.searchNote(searchQuery).observe(
            this
        ) { list ->
            noteAdapter.differ.submitList(list)
        }
    }

}