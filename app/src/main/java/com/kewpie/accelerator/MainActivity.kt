package com.kewpie.accelerator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity() {
    var accelerator = Accelerator()
    lateinit var dumpLogBtn: Button
    lateinit var externalReportPath: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        }else{
            initExternalReportPath()
        }
        accelerator.initForArt(BuildConfig.VERSION_CODE, 5000) //从 start 开始触发到5000的数据就 dump 到文件中

        dumpLogBtn = findViewById(R.id.dump_log)

        findViewById<Button>(R.id.btn_start).setOnClickListener(View.OnClickListener {
            accelerator.startAllocationTracker()
            dumpLogBtn.isEnabled = true
        })
        findViewById<Button>(R.id.btn_stop).setOnClickListener(View.OnClickListener {
            accelerator.stopAllocationTracker()
            dumpLogBtn.isEnabled = false
        })


        dumpLogBtn.setOnClickListener { Thread(Runnable { accelerator.dumpAllocationDataInLog() }).start() }
        findViewById<Button>(R.id.gen_obj).setOnClickListener(View.OnClickListener {
            for (i in 0..999) {
                val msg = Message()
                msg.what = i
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initExternalReportPath()
    }

    private fun initExternalReportPath(){
        externalReportPath = File(Environment.getExternalStorageDirectory(),"crashDump")
        if (!externalReportPath.exists()){
            externalReportPath.mkdirs()
        }
        accelerator.setSaveDataDirectory(externalReportPath.absolutePath)
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    companion object {
        // Used to load the 'native-lib' library on application startup.
        const val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100
    }
}
