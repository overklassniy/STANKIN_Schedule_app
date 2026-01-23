package com.overklassniy.stankinschedule.core.domain.repository

interface DeviceRepository {

    fun extractFilename(path: String): String?

}