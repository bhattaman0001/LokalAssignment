package com.amanbhatt.jobsearch.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amanbhatt.jobsearch.adapter.JobsAdapter
import com.amanbhatt.jobsearch.databinding.FragmentBookmarkBinding
import com.amanbhatt.jobsearch.model.JobsModel
import org.json.JSONException
import org.json.JSONObject

class BookmarkFragment : Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null
    private var jobsModelList: MutableList<JobsModel>? = null
    private var jobsAdapter: JobsAdapter? = null
    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        val view = binding.root

        jobsModelList = ArrayList()
        binding.bookmarkRv.layoutManager = LinearLayoutManager(context)
        jobsAdapter = JobsAdapter(requireContext(), jobsModelList as ArrayList<JobsModel>)
        binding.bookmarkRv.adapter = jobsAdapter

        loadBookmarked()

        return view
    }

    private fun loadBookmarked() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("BookmarkedJobs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all
        if (allEntries.isEmpty()) {
            binding.noBookmarksTv.visibility = View.VISIBLE
            binding.bookmarkRv.visibility = View.GONE
        } else {
            binding.noBookmarksTv.visibility = View.GONE
            binding.bookmarkRv.visibility = View.VISIBLE
            for ((_, value) in allEntries) {
                try {
                    val jsonString = value as String
                    val articleObject = JSONObject(jsonString)
                    val title = articleObject.getString("Title")
                    val location = articleObject.getString("Location")
                    val salary = articleObject.getString("Salary")
                    val phone = articleObject.getString("Phone")
                    val id = articleObject.getString("id")
                    val job = JobsModel(id, title, location, salary, phone)
                    jobsModelList!!.add(job)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            jobsAdapter?.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}
