package com.reyhan.smartalarm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.reyhan.data.Alarm
import com.reyhan.data.local.AlarmDB
import com.reyhan.fragment.DateDialogFragment
import com.reyhan.fragment.TimeDialogFragment
import com.reyhan.helper.timeFormatter
import com.reyhan.smartalarm.databinding.ActivityOneTimeAlarmBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(), DateDialogFragment.DialogDateSetListener,
    TimeDialogFragment.TimeDialogListener {

    private var _binding: ActivityOneTimeAlarmBinding? = null
    private val binding get() = _binding as ActivityOneTimeAlarmBinding

    private val db by lazy { AlarmDB(this) }
    private var alarmService: AlarmReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_time_alarm)

        _binding = ActivityOneTimeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmService = AlarmReceiver()
        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetDateOneTime.setOnClickListener {
                val datePickerFragment = DateDialogFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }
            btnSetTimeOneTime.setOnClickListener {
                val timePickerFragment = TimeDialogFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnAdd.setOnClickListener {
                val date = tvOnceDate.text.toString()
                val time = tvOnceTime.text.toString()
                val message = edtNoteOneTime.text.toString()

                if (date == "Date" && time == "Time") {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.txt_toast_add_alarm),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    alarmService?.setOneTimeAlarm(
                        applicationContext,
                        AlarmReceiver.TYPE_ONE_TIME,
                        date,
                        time,
                        message
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.alarmDao().addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                message,
                                AlarmReceiver.TYPE_ONE_TIME
                            )
                        )
                        Log.i("addAlarm", "Alarm set on: $date $time with message $message")
                        finish()
                    }
                }
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        // untuk mengubah tanggal calendar sekarang menjadi tanggal yang telah dipilih di DatePicker
        calendar.set(year, month, dayOfMonth)
        val dateFormatted = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvOnceDate.text = dateFormatted.format(calendar.time)
    }

    override fun onTimeSetListener(tag: String?, hour: Int, minute: Int) {
        binding.tvOnceTime.text = timeFormatter(hour, minute)
    }
}