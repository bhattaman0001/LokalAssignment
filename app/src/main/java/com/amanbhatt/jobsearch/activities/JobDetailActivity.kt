package com.amanbhatt.jobsearch.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amanbhatt.jobsearch.databinding.ActivityJobDetailBinding
import com.amanbhatt.jobsearch.model.NetworkUtils
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class JobDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val jobId: String? = intent.getStringExtra("job_id")

        if (!NetworkUtils.isConnectedToInternet(this)) {
            Toast.makeText(this, "No internet connection. Please connect to the internet first.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        jobId?.let { fetchJobDetails(it) }

        binding.backBtn.setOnClickListener { finish() }
    }

    private fun fetchJobDetails(jobId: String) {
        val url = "https://testapi.getlokalapp.com/common/jobs"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val jobsArray = response.getJSONArray("results")
                    for (i in 0 until jobsArray.length()) {
                        val job = jobsArray.getJSONObject(i)
                        val id = job.optString("id", "N/A")
                        if (id == jobId) {
                            val title = job.optString("title", "N/A")
                            val primaryDetails = job.optJSONObject("primary_details")
                            val location = primaryDetails?.optString("Place", "N/A") ?: "N/A"
                            val salary = primaryDetails?.optString("Salary", "N/A") ?: "N/A"
                            val jobType = primaryDetails?.optString("Job_Type", "N/A") ?: "N/A"
                            val experience = primaryDetails?.optString("Experience", "N/A") ?: "N/A"
                            val qualification = primaryDetails?.optString("Qualification", "N/A") ?: "N/A"
                            val customLink = job.optString("custom_link", "")
                            val phone = if (customLink.startsWith("tel:")) customLink.replace("tel:", "") else "N/A"
                            val companyName = job.optString("company_name", "")
                            val whatsappNo = job.optString("whatsapp_no", "N/A")
                            val expireOn = job.optString("expire_on", "N/A")
                            val jobHours = job.optString("job_hours", "N/A")
                            val openingsCount = job.optString("openings_count", "N/A")
                            val otherDetails = job.optString("other_details", "N/A")
                            val jobCategory = job.optString("job_category", "N/A")
                            val numApplications = job.optString("num_applications", "N/A")
                            var content = job.optString("content", "N/A")
                            val contactPreference = job.optJSONObject("contact_preference")
                            val whatsappLink = contactPreference?.optString("whatsapp_link", "N/A") ?: "N/A"
                            val preferredCallStartTime = contactPreference?.optString("preferred_call_start_time", "N/A") ?: "N/A"
                            val preferredCallEndTime = contactPreference?.optString("preferred_call_end_time", "N/A") ?: "N/A"

                            content = decodeUnicode(content)
                            val contentJson = JSONObject(content)
                            val contentBuilder = StringBuilder()
                            val it = contentJson.keys()
                            while (it.hasNext()) {
                                val key = it.next()
                                contentBuilder.append(contentJson.getString(key)).append("\n")
                            }

                            binding.jobTitleTv.text = title
                            binding.companyName.text = companyName
                            binding.locationTv.text = location
                            binding.salaryTv.text = salary
                            binding.jobTypeTv.text = jobType
                            binding.expTv.text = experience
                            binding.qualificationTv.text = qualification
                            binding.phoneNoTv.text = phone
                            binding.numberOfOpenings.text = openingsCount
                            binding.numberOfApplicationsTv.text = numApplications
                            binding.workingHrsTv.text = jobHours
                            binding.progressBar.visibility = View.GONE
                            binding.scrollView.visibility = View.VISIBLE

                            binding.whatsappLink.setOnClickListener {
                                if (whatsappLink != "N/A") {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(whatsappLink)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "No WhatsApp link available", Toast.LENGTH_SHORT).show()
                                }
                            }

                            binding.phoneNoTv.setOnClickListener {
                                if (phone != "N/A") {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:$phone")
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "No phone number available", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Handle error
                error.printStackTrace()
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        )
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun decodeUnicode(unicodeStr: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < unicodeStr.length) {
            val ch = unicodeStr[i]
            if (ch == '\\' && i + 1 < unicodeStr.length && unicodeStr[i + 1] == 'u') {
                // Read the unicode value
                val unicodeValue = unicodeStr.substring(i + 2, i + 6)
                sb.append(unicodeValue.toInt(16).toChar())
                i += 6
            } else {
                sb.append(ch)
                i++
            }
        }
        return sb.toString()
    }
}
