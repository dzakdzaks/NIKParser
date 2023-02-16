package com.dzakdzaks.nikparser

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.dzakdzaks.nikparser.model.NIKDistrict
import com.dzakdzaks.nikparser.model.NIKParserResponse
import com.dzakdzaks.nikparser.model.NIKProvince
import com.dzakdzaks.nikparser.model.NIKRegency
import com.dzakdzaks.nikparser.util.capitalized
import com.dzakdzaks.nikparser.util.fromJsonMapAdapter
import com.dzakdzaks.nikparser.util.readFromAssets
import com.squareup.moshi.Moshi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NIKParserImpl(
    private val context: Context,
    private val isDataReadyToAction: (Boolean) -> Unit,
) : NIKParser {

    private var provinceList: List<NIKProvince> = emptyList()
    private var regencyList: List<NIKRegency> = emptyList()
    private var districtList: List<NIKDistrict> = emptyList()

    private val locale: Locale by lazy { Locale.getDefault() }

    /**
     * use PARSER_DATE_FORMAT format to achieve leap year
     * @see PARSER_DATE_FORMAT
     * */
    private val sdfParser: SimpleDateFormat by lazy {
        SimpleDateFormat(PARSER_DATE_FORMAT, locale).apply {
            isLenient = false // false to achieve strict year check 31, 30, and leap year
        }
    }

    private val sdfData: SimpleDateFormat by lazy {
        SimpleDateFormat(
            DATA_DATE_FORMAT,
            locale
        )
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            isDataReadyToAction(false)
            parseJsonData()
        }
    }

    override fun parseNik(nik: String): NIKParserResponse {
        if (nik.isEmpty() || nik.isBlank() || nik.length != 16 || !nik.isDigitsOnly()) {
            return NIKParserResponse(nik = nik, isValid = false)
        }

        val province = getProvince(nik)
        val regency = getRegency(nik)
        val district = getDistrict(nik)
        val birthDate = getBirthDate(nik)
        val birthMonth = getBirthMonth(nik)
        val birthYear = getBirthYear(nik)
        val gender = getGender(nik)
        val uniqueCode = getUniqueCode(nik)
        val isLastFourDigitMoreThanZero = isLastFourDigitMoreThanZero(nik)

        if (province == null || regency == null || district == null ||
            !isLastFourDigitMoreThanZero || birthDate == "" || birthMonth == "" || birthYear == ""
        ) {
            return NIKParserResponse(nik = nik, isValid = false)
        }

        val fullBirthDateString = "$birthMonth$birthDate$birthYear"

        try {
            sdfParser.parse(fullBirthDateString)?.let {
                return NIKParserResponse(
                    nik = nik,
                    isValid = true,
                    province = province,
                    regency = regency,
                    district = district,
                    birthDate = sdfData.format(it),
                    gender = gender,
                    uniqueCode = uniqueCode,
                )
            } ?: kotlin.run {
                return NIKParserResponse(nik = nik, isValid = false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return NIKParserResponse(nik = nik, isValid = false)
        }
    }

    /**
     * @param nik
     * Check if Indonesian province code from Nomor Induk Kependudukan(NIK) is valid.
     *
     * if NIK is 3576447103910003.
     * Province code is index 0 and 1.
     * From the NIK example above, the province code is 35.
     *
     * @return true if province code is valid.
     */
    private fun getProvince(nik: String): NIKProvince? {
        val provinceCode = nik.substring(0, 2)
        return provinceList.find { provinceCode == it.id }
    }

    /**
     * @param nik
     * Check if Indonesian regency code from Nomor Induk Kependudukan(NIK) is valid.
     *
     * if NIK is 3576447103910003.
     * Regency code is index 0 - 3.
     * From the NIK example above, the regency code is 3576.
     *
     * @return true if regency code is valid.
     */
    private fun getRegency(nik: String): NIKRegency? {
        val regencyCode = nik.substring(0, 4)
        return regencyList.find { regencyCode == it.id }
    }

    /**
     * @param nik
     * Check if Indonesian district code from Nomor Induk Kependudukan(NIK) is valid.
     *
     * if NIK is 3576447103910003.
     * District code is index 0 - 5.
     * From the NIK example above, the district code is 357644.
     *
     * @return true if district code is valid.
     */
    private fun getDistrict(nik: String): NIKDistrict? {
        val districtCode = nik.substring(0, 6)
        return districtList.find { districtCode == it.id }
    }

    /**
     * @param nik
     * Get birth date from Nomor Induk Kependudukan(NIK)
     *
     * if NIK is 3576447103910003.
     * Birth date is index 6 and 7.
     * Valid birth date is 01 until 71 (male -> 01 until 31 and female -> 41 until 71)
     * From the NIK example above, the birth date is 71.
     *
     * @return String date ex:01,05,10,20,28,etc. if invalid return empty string
     */
    private fun getBirthDate(nik: String): String {
        val birthDate = nik.substring(6, 8).toInt()
        if (birthDate !in 1..71) return ""

        val isFemale = birthDate > 40

        val resultBirthDate = if (isFemale) birthDate - 40 else birthDate

        return if (resultBirthDate < 10) {
            "0$resultBirthDate"
        } else {
            resultBirthDate.toString()
        }
    }

    /**
     * @param nik
     * Get birth month from Nomor Induk Kependudukan(NIK).
     *
     * if NIK is 3576447103910003.
     * Birth month date is index 8 and 9.
     * Valid birth month date is 01 until 12
     * From the NIK example above, the birth month date is 03.
     *
     * @return string month ex:01,02,05,10,12,etc. if invalid return empty string
     */
    private fun getBirthMonth(nik: String): String {
        val birthMonth = nik.substring(8, 10).toInt()
        if (birthMonth !in 1..12) return ""

        return if (birthMonth < 10) {
            "0$birthMonth"
        } else {
            birthMonth.toString()
        }
    }

    /**
     * @param nik
     * Get birth year from Nomor Induk Kependudukan(NIK)
     *
     * if NIK is 3576447103910003.
     * Birth year is index 10 and 11.
     * Valid birth year is current year until 65 year before current
     * From the NIK example above, the birth year is 91.
     *
     * @return String year ex:1980,1990,2000,2010,2022,etc. if invalid return empty string
     */
    private fun getBirthYear(nik: String): String {
        val birthYear = nik.substring(10, 12).toInt()
        val currentDate = Calendar.getInstance()

        val lastTwoDigitYear = "${currentDate.get(Calendar.YEAR).toString()[2]}${
            currentDate.get(Calendar.YEAR).toString()[3]
        }".toInt()

        val resultBirthYear = if (birthYear < lastTwoDigitYear) {
            2000 + birthYear
        } else {
            1900 + birthYear
        }

        val sixtyFiveYearBefore = Calendar.getInstance().also {
            it.set(Calendar.YEAR, currentDate.get(Calendar.YEAR) - 65)
        }

        return if (resultBirthYear < sixtyFiveYearBefore.get(Calendar.YEAR)) {
            ""
        } else {
            resultBirthYear.toString()
        }
    }

    /**
     * @param nik
     * Get gender from Nomor Induk Kependudukan(NIK)
     *
     * if NIK is 3576447103910003.
     * Birth date is index 6 and 7.
     * Valid birth date is 01 until 71 (male -> 01 until 31 and female -> 41 until 71)
     * From the NIK example above, the gender is female
     *
     * @see MALE
     * @see FEMALE
     * @return String gender(male/female). if invalid return empty string
     */
    private fun getGender(nik: String): String {
        val idNumberBirthDate = nik.substring(6, 8).toInt()
        if (idNumberBirthDate !in 1..71) return ""

        val isFemale = idNumberBirthDate > 40

        return if (isFemale) FEMALE else MALE
    }

    /**
     * @param nik
     * Get unique code from Nomor Induk Kependudukan(NIK)
     *
     * if NIK is 3576447103910003.
     * unique code is last 4 digit from NIK.
     * Valid unique code is if the last digit is not zero
     * From the NIK example above, the unique code is 0003
     *
     * @return String 4 digit unique code
     */
    private fun getUniqueCode(nik: String): String {
        return nik.substring(nik.length - 4)
    }

    /**
     * @param nik
     * Check if last four digit is more than zero
     *
     * @return true if more thank zero
     * */
    private fun isLastFourDigitMoreThanZero(nik: String): Boolean {
        return nik.substring(nik.length - 4).toInt() > 0
    }

    private suspend fun parseJsonData() {
        withContext(Dispatchers.IO) {
            val moshi = Moshi.Builder().build()

            provinceList = moshi.fromJsonMapAdapter(context.readFromAssets("provinces.json")).map {
                NIKProvince(it.key, it.value.capitalized())
            }

            regencyList = moshi.fromJsonMapAdapter(context.readFromAssets("regencies.json")).map {
                NIKRegency(it.key, it.value.capitalized())
            }

            districtList = moshi.fromJsonMapAdapter(context.readFromAssets("districts.json")).map {
                val name = it.value.split(" -- ")[0].capitalized()
                val zipCode = it.value.split(" -- ")[1].replace("-", "")
                NIKDistrict(it.key, name, zipCode)
            }

            isDataReadyToAction(true)
        }
    }

    companion object {
        const val MALE = "male"
        const val FEMALE = "female"

        const val PARSER_DATE_FORMAT = "MMddyyyy"
        const val DATA_DATE_FORMAT = "yyyy-MM-dd"
    }
}
