package com.aduilio.mytasks.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

class LocalDateAdapter : TypeAdapter<LocalDate>() {

    override fun write(writer: JsonWriter?, value: LocalDate?) {
        writer?.value(value?.toString())
    }

    override fun read(reader: JsonReader?): LocalDate? {
        return LocalDate.parse(reader?.nextString())
    }
}