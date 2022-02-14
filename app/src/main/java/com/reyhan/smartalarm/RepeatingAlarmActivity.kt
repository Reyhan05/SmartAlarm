package com.reyhan.smartalarm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.reyhan.data.Alarm
import com.reyhan.data.local.AlarmDB
import com.reyhan.fragment.TimeDialogFragment
import com.reyhan.helper.timeFormatter
import com.reyhan.smartalarm.databinding.ActivityRepeatingAlarmBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingAlarmActivity : AppCompatActivity(), TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding
    private var alarmReceiver: AlarmReceiver? = null

    private val db by lazy { AlarmDB(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repeating_alarm)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmReceiver = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetTime.setOnClickListener {
                val timePickerDialog = TimeDialogFragment()
                timePickerDialog.show(supportFragmentManager, "TimePickerDialog")
            }

            btnAdd.setOnClickListener {
                val time = tvOnceTime.text.toString()
                val message = edtNote.text.toString()

                if (time != "Time") {
                    alarmReceiver?.setRepeatingAlarm(
                        applicationContext,
                        AlarmReceiver.TYPE_REPEATING,
                        time,
                        message
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                "Repeating alarm",
                                time,
                                message,
                                AlarmReceiver.TYPE_REPEATING
                            )
                        )
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@RepeatingAlarmActivity,
                        "Tentukan alarm",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            btnCancel.setOnClickListener {
                finish()
            }

        }
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hour, minute)
    }
}