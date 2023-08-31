package by.gto.equipment.account.model

import by.gto.equipment.account.helpers.JsonLocalDateDeserializer
import by.gto.equipment.account.helpers.JsonLocalDateSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDate
import java.util.*

class TestDateModel {
    @JsonProperty("date")
    var date = Date()

    @JsonProperty("localdate")
//         @JsonSerialize(using = JsonLocalDateSerializer::class)
//         @JsonDeserialize(using = JsonLocalDateDeserializer::class)
    var localdate= LocalDate.now()
}