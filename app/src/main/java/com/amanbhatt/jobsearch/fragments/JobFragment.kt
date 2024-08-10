package com.amanbhatt.jobsearch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amanbhatt.jobsearch.adapter.JobsAdapter
import com.amanbhatt.jobsearch.databinding.FragmentJobBinding
import com.amanbhatt.jobsearch.model.JobsModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class JobFragment : Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null
    private var requestQueue: RequestQueue? = null
    private var jobsModelList: MutableList<JobsModel>? = null
    private var jobsAdapter: JobsAdapter? = null
    private var _binding: FragmentJobBinding? = null
    private val binding get() = _binding!!

    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
        requestQueue = Volley.newRequestQueue(requireContext())
        jobsModelList = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.progressBar.visibility = View.VISIBLE
        binding.jobsRv.layoutManager = LinearLayoutManager(context)
        jobsAdapter = jobsModelList?.let { JobsAdapter(requireContext(), it) }
        binding.jobsRv.adapter = jobsAdapter

        // Add scroll listener
        binding.jobsRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        loadMoreJobs()
                    }
                }
            }
        })

        fetchJobsData(currentPage)

        return view
    }

    private fun loadMoreJobs() {
        isLoading = true
        currentPage += 1
        fetchJobsData(currentPage)
    }

    private fun fetchJobsData(page: Int) {
        val url = "https://testapi.getlokalapp.com/common/jobs?page=$page"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val jobsArray = response.getJSONArray("results")
                    if (jobsArray.length() == 0) {
                        isLastPage = true
                    } else {
                        for (i in 0 until jobsArray.length()) {
                            val job = jobsArray.getJSONObject(i)
                            val title = job.optString("title", "N/A")
                            val id = job.optString("id", "N/A")
                            val primaryDetails = job.optJSONObject("primary_details")
                            val location = primaryDetails?.optString("Place", "N/A") ?: "N/A"
                            val salary = primaryDetails?.optString("Salary", "N/A") ?: "N/A"
                            val customLink = job.optString("custom_link", "")
                            val phone = if (customLink.startsWith("tel:")) customLink.replace("tel:", "") else "N/A"
                            jobsModelList!!.add(JobsModel(id, title, location, salary, phone))
                        }
                        jobsAdapter?.notifyDataSetChanged()
                    }
                    binding.progressBar.visibility = View.GONE
                    isLoading = false
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error parsing data", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    isLoading = false
                }
            },
            { error ->
                Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                isLoading = false
            }
        )
        requestQueue?.add(jsonObjectRequest)
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
