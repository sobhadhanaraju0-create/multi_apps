package com.example.util

import com.example.model.Subject
import com.example.model.UnitModel
import com.example.model.TopicModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

sealed class SyllabusParseResult {
    data class Subjects(val subjects: List<Subject>) : SyllabusParseResult()
    data class Units(val units: List<UnitModel>) : SyllabusParseResult()
    data class Topics(val topics: List<TopicModel>) : SyllabusParseResult()
    data class Error(val message: String) : SyllabusParseResult()
}

object SyllabusJsonParser {

    fun parseJson(jsonString: String, targetAppId: String): SyllabusParseResult {
        try {
            val json = jsonString.trim()
            if (json.startsWith("{")) {
                val jsonObj = JSONObject(json)
                if (jsonObj.has("subjects")) {
                    return parseSubjects(jsonObj.getJSONArray("subjects"), targetAppId)
                } else if (jsonObj.has("units")) {
                    return parseUnits(jsonObj.getJSONArray("units"), targetAppId)
                } else if (jsonObj.has("topics")) {
                    return parseTopics(jsonObj.getJSONArray("topics"), targetAppId)
                } else {
                    // Try to guess if it's a single object of some kind
                    if (jsonObj.has("units")) return SyllabusParseResult.Subjects(listOf(parseSingleSubject(jsonObj, targetAppId)))
                    if (jsonObj.has("topics")) return SyllabusParseResult.Units(listOf(parseSingleUnit(jsonObj, targetAppId)))
                    return SyllabusParseResult.Topics(listOf(parseSingleTopic(jsonObj, targetAppId)))
                }
            } else if (json.startsWith("[")) {
                val jsonArray = JSONArray(json)
                if (jsonArray.length() == 0) return SyllabusParseResult.Error("Empty array")
                
                val firstObj = jsonArray.getJSONObject(0)
                if (firstObj.has("units")) {
                    return parseSubjects(jsonArray, targetAppId)
                } else if (firstObj.has("topics")) {
                    return parseUnits(jsonArray, targetAppId)
                } else {
                    return parseTopics(jsonArray, targetAppId)
                }
            }
            return SyllabusParseResult.Error("Invalid JSON format")
        } catch (e: Exception) {
            return SyllabusParseResult.Error("JSON Parsing Error: ${e.message}")
        }
    }

    private fun parseSubjects(array: JSONArray, appId: String): SyllabusParseResult.Subjects {
        val list = mutableListOf<Subject>()
        for (i in 0 until array.length()) {
            list.add(parseSingleSubject(array.getJSONObject(i), appId))
        }
        return SyllabusParseResult.Subjects(list)
    }

    private fun parseUnits(array: JSONArray, appId: String): SyllabusParseResult.Units {
        val list = mutableListOf<UnitModel>()
        for (i in 0 until array.length()) {
            list.add(parseSingleUnit(array.getJSONObject(i), appId))
        }
        return SyllabusParseResult.Units(list)
    }

    private fun parseTopics(array: JSONArray, appId: String): SyllabusParseResult.Topics {
        val list = mutableListOf<TopicModel>()
        for (i in 0 until array.length()) {
            list.add(parseSingleTopic(array.getJSONObject(i), appId))
        }
        return SyllabusParseResult.Topics(list)
    }

    private fun parseSingleSubject(obj: JSONObject, appId: String): Subject {
        val units = if (obj.has("units")) {
            val uArray = obj.getJSONArray("units")
            (0 until uArray.length()).map { parseSingleUnit(uArray.getJSONObject(it), appId) }
        } else emptyList()

        return Subject(
            id = obj.optString("id", "subj-${UUID.randomUUID()}"),
            appId = appId,
            name = obj.optString("name", "Untitled Subject"),
            category = obj.optString("category", "prelims"),
            icon = obj.optString("icon", "BookOpen"),
            imageUrl = obj.optString("imageUrl", ""),
            heroQuote = obj.optString("heroQuote", ""),
            color = obj.optString("color", "from-blue-600 to-blue-800"),
            units = units
        )
    }

    private fun parseSingleUnit(obj: JSONObject, appId: String): UnitModel {
        val topics = if (obj.has("topics")) {
            val tArray = obj.getJSONArray("topics")
            (0 until tArray.length()).map { parseSingleTopic(tArray.getJSONObject(it), appId) }
        } else emptyList()

        return UnitModel(
            id = obj.optString("id", "unit-${UUID.randomUUID()}"),
            appId = appId,
            name = obj.optString("name", "Untitled Unit"),
            unitNumber = obj.optInt("unitNumber", 1),
            subtitle = obj.optString("subtitle", ""),
            tagline = obj.optString("tagline", ""),
            quote = obj.optString("quote", ""),
            slogan = obj.optString("slogan", ""),
            description = obj.optString("description", ""),
            imageUrl = obj.optString("imageUrl", ""),
            estimatedMcqs = obj.optString("estimatedMcqs", ""),
            completionPercentage = if (obj.has("completionPercentage")) obj.optInt("completionPercentage") else 0,
            topics = topics
        )
    }

    private fun parseSingleTopic(obj: JSONObject, appId: String): TopicModel {
        val subtopics = if (obj.has("subtopics")) {
            val sArray = obj.getJSONArray("subtopics")
            (0 until sArray.length()).map { sArray.getString(it) }
        } else emptyList()

        return TopicModel(
            id = obj.optString("id", "topic-${UUID.randomUUID()}"),
            appId = appId,
            name = obj.optString("name", "Untitled Topic"),
            subtitle = obj.optString("subtitle", ""),
            tagBadge = obj.optString("tagBadge", ""),
            description = obj.optString("description", ""),
            imageUrl = obj.optString("imageUrl", ""),
            estimatedMcqs = obj.optString("estimatedMcqs", ""),
            practiceSetsCount = obj.optInt("practiceSetsCount", 3),
            difficulty = obj.optString("difficulty", "Medium"),
            completionPercentage = if (obj.has("completionPercentage")) obj.optInt("completionPercentage") else 0,
            notesContent = obj.optString("notesContent", ""),
            subtopics = subtopics
        )
    }
}
