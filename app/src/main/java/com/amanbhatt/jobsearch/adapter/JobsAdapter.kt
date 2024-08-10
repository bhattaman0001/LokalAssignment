package com.amanbhatt.jobsearch.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amanbhatt.jobsearch.R
import com.amanbhatt.jobsearch.activities.JobDetailActivity
import com.amanbhatt.jobsearch.databinding.JobItemBinding
import com.amanbhatt.jobsearch.model.JobsModel
import org.json.JSONException
import org.json.JSONObject

class JobsAdapter(
    private val context: Context,
    private var jobsModelList: List<JobsModel>
) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("BookmarkedJobs", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = JobItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = jobsModelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jobsModel = jobsModelList[position]
        holder.bind(jobsModel)
    }

    inner class ViewHolder(private val binding: JobItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(jobsModel: JobsModel) {
            binding.titleTv.text = jobsModel.title
            binding.locationTv.text = jobsModel.location
            binding.salaryTv.text = jobsModel.salary
            binding.phoneTv.text = jobsModel.phone

            val articleKey = "article_${jobsModel.title}"
            val isSaved = sharedPreferences.contains(articleKey)
            binding.bookmarkBtn.setImageResource(
                if (isSaved) R.drawable.bookmark_filled_icon else R.drawable.bookmark_blank_icon
            )

            binding.jobCard.setOnClickListener {
                val intent = Intent(context, JobDetailActivity::class.java).apply {
                    putExtra("job_id", jobsModel.id)
                }
                context.startActivity(intent)
            }

            binding.bookmarkBtn.setOnClickListener {
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                if (isSaved) {
                    editor.remove(articleKey).apply()
                    binding.bookmarkBtn.setImageResource(R.drawable.bookmark_blank_icon)
                    Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show()
                } else {
                    try {
                        val jsonObject = JSONObject().apply {
                            put("id", jobsModel.id)
                            put("Title", jobsModel.title)
                            put("Location", jobsModel.location)
                            put("Salary", jobsModel.salary)
                            put("Phone", jobsModel.phone)
                        }
                        editor.putString(articleKey, jsonObject.toString()).apply()
                        binding.bookmarkBtn.setImageResource(R.drawable.bookmark_filled_icon)
                        Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                notifyItemChanged(adapterPosition) // Refresh the specific item
            }
        }
    }
}
