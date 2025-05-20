package com.example.imhere.pages.report

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.imhere.mock_data.AttendanceMockData
import com.example.imhere.model.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
 val attendances = AttendanceMockData.attendanceList

    init {
        Log.d("ReportViewModel",attendances.toString())
    }
}