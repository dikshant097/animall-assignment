package com.animall.assignemnt.views.activitites

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.animall.assignemnt.R
import com.animall.assignemnt.adapters.SmsAdapter
import com.animall.assignemnt.databinding.ActivityMainBinding
import com.animall.assignemnt.model.SMS
import com.animall.assignemnt.utils.Utility
import com.animall.assignemnt.viewmodel.SmsViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_READ_SMS = 1001
    }

    private lateinit var smsAdapter: SmsAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var smsViewModel: SmsViewModel

    private var loading = false
    private var offset = 0
    private var senderId: String? = ""
    private var timeStamp = System.currentTimeMillis() - 86400000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        smsViewModel = ViewModelProviders.of(this).get(SmsViewModel::class.java)
        binding.lifecycleOwner = this

        handleIntent(intent)
        init()
    }

    private fun init() {

        smsViewModel.getLoading().observe(this,
            Observer { loading ->
                binding.loading = loading
                this@MainActivity.loading = loading
            })

        smsViewModel.getSmsList().observe(this, object : Observer<ArrayList<SMS>?> {
            override fun onChanged(smsList: ArrayList<SMS>?) {
                if (smsList.isNullOrEmpty()) {

                    if (offset == 0) {
                        Toast.makeText(this@MainActivity, "No Messages found", Toast.LENGTH_LONG)
                            .show()
                    }

                    offset = -1
                    return
                }

                smsAdapter.addSms(smsList)
                offset += 10
            }
        })

        offset = 0

        smsAdapter = SmsAdapter(this, senderId)
        val layoutManager = LinearLayoutManager(this)
        binding.smsList.layoutManager = layoutManager
        binding.smsList.adapter = smsAdapter
        binding.smsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItem = layoutManager.findLastVisibleItemPosition()
                if (lastItem == -1 || offset == -1) {
                    return
                }

                if (lastItem == smsAdapter.itemCount - 1 && !loading) {
                    smsViewModel.getSms(this@MainActivity, offset, timeStamp)
                }
            }
        })

        val isReadPermissionGranted =
            Utility.isPermissionGranted(this, Manifest.permission.READ_SMS) && Utility.isPermissionGranted(this, Manifest.permission.RECEIVE_SMS)
        if (isReadPermissionGranted) {
            smsViewModel.getSms(this@MainActivity, offset, timeStamp)
        } else {
            //ignoring the permission deny case for now
            Utility.requestPermission(
                this,
                REQUEST_CODE_READ_SMS, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
            )
        }
    }

    private fun handleIntent(intent: Intent?) {
        val newIntent = intent ?: return
        senderId = newIntent.getStringExtra("sender_id");
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_READ_SMS && grantResults.isNotEmpty()
            && grantResults.get(0) == PackageManager.PERMISSION_GRANTED
        ) {
            smsViewModel.getSms(this@MainActivity, offset, timeStamp)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
        init()
    }
}