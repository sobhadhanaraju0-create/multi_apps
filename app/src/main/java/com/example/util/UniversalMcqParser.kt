package com.example.util

import com.example.model.Question
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class FieldMappingConfig(
    val questionTextKey: String = "",
    val optionKeys: List<String> = emptyList(), // e.g. ["A", "B", "C", "D"] or ["optionA", "optionB", ...]
    val optionsFormat: String = "AUTO", // "AUTO", "MAP", "ARRAY", "SEPARATE_KEYS"
    val correctAnswerKey: String = "",
    val explanationKey: String = "",
    val setNameKey: String = "",
    val difficultyKey: String = ""
)

data class ParsedMcqItem(
    val rawIndex: Int,
    val question: Question,
    val isValid: Boolean,
    val errorMessages: List<String>,
    val isDuplicate: Boolean = false,
    val duplicateReason: String? = null,
    val detectedHierarchy: Map<String, String> = emptyMap()
)

data class JsonParseResult(
    val totalFound: Int,
    val validCount: Int,
    val duplicateCount: Int,
    val errorCount: Int,
    val parsedItems: List<ParsedMcqItem>,
    val detectedKeys: List<String>,
    val detectedStructure: String, // "Array of MCQs", "Single MCQ Object", "Nested Container", "Hierarchy JSON"
    val sampleRawObject: JSONObject?
)

object UniversalMcqParser {

    private val QUESTION_TEXT_KEYS = listOf(
        "questionText", "question_text", "question", "q", "text", "stem", "title", "mcq", "prompt", "body", "q_text"
    )

    private val CORRECT_ANSWER_KEYS = listOf(
        "correctOption", "correctAnswer", "correct_option", "correct_answer", "answer", "correct", "key", "ans",
        "rightAnswer", "solution_key", "right_option"
    )

    private val EXPLANATION_KEYS = listOf(
        "explanation", "solution", "exp", "rationale", "desc", "explanation_text", "details", "reason", "answer_explanation"
    )

    private val SET_NAME_KEYS = listOf(
        "setName", "set_name", "set", "mcqSet", "mcq_set", "group", "test_set", "setNameTitle"
    )

    private val DIFFICULTY_KEYS = listOf(
        "difficulty", "level", "difficultyLevel", "difficulty_level"
    )

    fun parseJsonString(
        jsonString: String,
        appId: String = "appsc-group2",
        targetSubjectId: String,
        targetUnitId: String = "",
        targetTopicId: String = "",
        targetSubtopic: String = "",
        targetSetName: String = "",
        overrideWithAdminDestination: Boolean = true,
        existingQuestions: List<Question> = emptyList(),
        importBatchId: String = "batch-${System.currentTimeMillis()}",
        customConfig: FieldMappingConfig? = null
    ): JsonParseResult {
        val rawItemsList = mutableListOf<Triple<JSONObject, Map<String, String>, String>>()
        var detectedStructure = "Unknown"
        val allDetectedKeys = mutableSetOf<String>()
        var sampleObject: JSONObject? = null

        val trimmed = jsonString.trim()

        try {
            if (trimmed.startsWith("[")) {
                // Root is JSONArray
                val jsonArray = JSONArray(trimmed)
                detectedStructure = "Array of ${jsonArray.length()} MCQs"
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.optJSONObject(i)
                    if (obj != null) {
                        if (sampleObject == null) sampleObject = obj
                        collectKeys(obj, allDetectedKeys)
                        rawItemsList.add(Triple(obj, emptyMap(), targetSetName))
                    }
                }
            } else if (trimmed.startsWith("{")) {
                val rootObj = JSONObject(trimmed)
                sampleObject = rootObj
                collectKeys(rootObj, allDetectedKeys)

                // Check if root is a single MCQ object
                if (isSingleMcqObject(rootObj)) {
                    detectedStructure = "Single MCQ Object"
                    rawItemsList.add(Triple(rootObj, emptyMap(), targetSetName))
                } else if (rootObj.has("subjects") || rootObj.has("units") || rootObj.has("topics") || rootObj.has("category")) {
                    // Category/Hierarchy JSON
                    detectedStructure = "Nested Syllabus Hierarchy JSON"
                    extractFromHierarchy(rootObj, rawItemsList, targetSetName, allDetectedKeys)
                } else {
                    // Check for nested question containers ("questions", "mcqs", "data", "items", "quiz", "list")
                    val containerKey = listOf("questions", "mcqs", "data", "items", "quiz", "list", "mcq_list", "set_questions")
                        .find { rootObj.has(it) && rootObj.optJSONArray(it) != null }

                    if (containerKey != null) {
                        val array = rootObj.getJSONArray(containerKey)
                        val embeddedSetName = rootObj.optString("setName", rootObj.optString("set", targetSetName))
                        detectedStructure = "Container Object ('$containerKey': ${array.length()} items)"
                        for (i in 0 until array.length()) {
                            val obj = array.optJSONObject(i)
                            if (obj != null) {
                                collectKeys(obj, allDetectedKeys)
                                rawItemsList.add(Triple(obj, emptyMap(), embeddedSetName))
                            }
                        }
                    } else {
                        // Fallback single object
                        detectedStructure = "Single Object"
                        rawItemsList.add(Triple(rootObj, emptyMap(), targetSetName))
                    }
                }
            }
        } catch (e: Exception) {
            return JsonParseResult(
                totalFound = 0,
                validCount = 0,
                duplicateCount = 0,
                errorCount = 1,
                parsedItems = emptyList(),
                detectedKeys = emptyList(),
                detectedStructure = "Invalid JSON Syntax: ${e.localizedMessage}",
                sampleRawObject = null
            )
        }

        // Process candidate MCQs
        val parsedResults = mutableListOf<ParsedMcqItem>()
        var validCount = 0
        var duplicateCount = 0
        var errorCount = 0

        val normalizedExistingText = existingQuestions.map {
            it.questionText.trim().lowercase().replace(Regex("[^a-zA-Z0-9\u0C00-\u0C7F]"), "")
        }.toSet()

        rawItemsList.forEachIndexed { index, triple ->
            val (itemObj, hierarchyMeta, itemSetName) = triple
            val errors = mutableListOf<String>()

            // Detect Question Text
            val questionTextKey = customConfig?.questionTextKey.takeIf { !it.isNullOrBlank() }
                ?: QUESTION_TEXT_KEYS.find { itemObj.has(it) && !itemObj.optString(it).isNullOrBlank() }
                ?: "questionText"

            val rawQuestionText = itemObj.optString(questionTextKey, "").trim()
            if (rawQuestionText.isEmpty()) {
                errors.add("Missing question text (Key: '$questionTextKey')")
            }

            // Detect Options
            val optionsMap = extractOptionsMap(itemObj, customConfig)
            if (optionsMap.size < 2) {
                errors.add("Insufficient options found (Found ${optionsMap.size}, min 2 required)")
            }

            // Detect Correct Option
            val correctAnsKey = customConfig?.correctAnswerKey.takeIf { !it.isNullOrBlank() }
                ?: CORRECT_ANSWER_KEYS.find { itemObj.has(it) }
                ?: "correctOption"

            val rawCorrectAns = itemObj.optString(correctAnsKey, "").trim()
            val correctOption = sanitizeCorrectAnswer(rawCorrectAns, optionsMap)
            if (correctOption.isEmpty() || !optionsMap.containsKey(correctOption)) {
                errors.add("Invalid correct option '$rawCorrectAns' (Mapped: '$correctOption'). Available keys: ${optionsMap.keys}")
            }

            // Detect Explanation
            val expKey = customConfig?.explanationKey.takeIf { !it.isNullOrBlank() }
                ?: EXPLANATION_KEYS.find { itemObj.has(it) }
            val explanation = expKey?.let { itemObj.optString(it, null) }

            // Detect Set Name
            val setKey = customConfig?.setNameKey.takeIf { !it.isNullOrBlank() }
                ?: SET_NAME_KEYS.find { itemObj.has(it) }
            val parsedSetName = if (!overrideWithAdminDestination && setKey != null && itemObj.has(setKey)) {
                itemObj.optString(setKey)
            } else {
                itemSetName.ifEmpty { targetSetName }
            }

            // Detect Difficulty
            val diffKey = customConfig?.difficultyKey.takeIf { !it.isNullOrBlank() }
                ?: DIFFICULTY_KEYS.find { itemObj.has(it) }
            val difficulty = diffKey?.let { itemObj.optString(it, "Medium") } ?: "Medium"

            // Custom fields extraction
            val customFields = mutableMapOf<String, String>()
            val keysIter = itemObj.keys()
            while (keysIter.hasNext()) {
                val k = keysIter.next()
                if (k != questionTextKey && k != correctAnsKey && expKey != k && setKey != k && diffKey != k && k != "options") {
                    val v = itemObj.opt(k)
                    if (v != null && v !is JSONObject && v !is JSONArray) {
                        customFields[k] = v.toString()
                    }
                }
            }

            // Determine Hierarchy
            val finalSubjectId = if (overrideWithAdminDestination || hierarchyMeta["subjectId"].isNullOrEmpty()) targetSubjectId else hierarchyMeta["subjectId"]!!
            val finalUnitId = if (overrideWithAdminDestination || hierarchyMeta["unitId"].isNullOrEmpty()) targetUnitId else hierarchyMeta["unitId"]!!
            val finalTopicId = if (overrideWithAdminDestination || hierarchyMeta["topicId"].isNullOrEmpty()) targetTopicId else hierarchyMeta["topicId"]!!
            val finalSubtopic = if (overrideWithAdminDestination || hierarchyMeta["subtopic"].isNullOrEmpty()) targetSubtopic else hierarchyMeta["subtopic"]!!

            val qId = itemObj.optString("id", "mcq-imp-${UUID.randomUUID().toString().take(8)}")

            val question = Question(
                id = qId,
                appId = appId,
                subjectId = finalSubjectId,
                unitId = finalUnitId,
                topicId = finalTopicId,
                subtopic = finalSubtopic,
                setName = parsedSetName,
                questionText = rawQuestionText,
                options = optionsMap,
                correctOption = correctOption,
                explanation = explanation,
                difficulty = difficulty,
                customFields = customFields,
                importBatchId = importBatchId
            )

            // Duplicate Detection
            val normalizedText = rawQuestionText.lowercase().replace(Regex("[^a-zA-Z0-9\u0C00-\u0C7F]"), "")
            val isDuplicate = normalizedExistingText.contains(normalizedText) || existingQuestions.any { it.id == qId }
            val duplicateReason = if (isDuplicate) "Exact question text or ID already exists in system" else null

            val isValid = errors.isEmpty()
            if (isValid) {
                if (isDuplicate) duplicateCount++ else validCount++
            } else {
                errorCount++
            }

            parsedResults.add(
                ParsedMcqItem(
                    rawIndex = index + 1,
                    question = question,
                    isValid = isValid,
                    errorMessages = errors,
                    isDuplicate = isDuplicate,
                    duplicateReason = duplicateReason,
                    detectedHierarchy = mapOf(
                        "subject" to finalSubjectId,
                        "unit" to finalUnitId,
                        "topic" to finalTopicId,
                        "subtopic" to finalSubtopic,
                        "set" to parsedSetName
                    )
                )
            )
        }

        return JsonParseResult(
            totalFound = rawItemsList.size,
            validCount = validCount,
            duplicateCount = duplicateCount,
            errorCount = errorCount,
            parsedItems = parsedResults,
            detectedKeys = allDetectedKeys.toList().sorted(),
            detectedStructure = detectedStructure,
            sampleRawObject = sampleObject
        )
    }

    private fun isSingleMcqObject(obj: JSONObject): Boolean {
        val hasQuestion = QUESTION_TEXT_KEYS.any { obj.has(it) }
        val hasOptions = obj.has("options") || obj.has("optionA") || obj.has("optA") || obj.has("a")
        return hasQuestion && hasOptions
    }

    private fun collectKeys(obj: JSONObject, keysSet: MutableSet<String>) {
        val keys = obj.keys()
        while (keys.hasNext()) {
            keysSet.add(keys.next())
        }
    }

    private fun extractOptionsMap(obj: JSONObject, customConfig: FieldMappingConfig?): Map<String, String> {
        val result = mutableMapOf<String, String>()

        // 1. Check if "options" key exists as JSONObject or JSONArray
        if (obj.has("options")) {
            val optVal = obj.get("options")
            if (optVal is JSONObject) {
                val keys = optVal.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    val upperK = k.uppercase()
                    result[upperK] = optVal.optString(k, "")
                }
                return result
            } else if (optVal is JSONArray) {
                val labels = listOf("A", "B", "C", "D", "E", "F")
                for (i in 0 until optVal.length()) {
                    if (i < labels.size) {
                        result[labels[i]] = optVal.optString(i, "")
                    }
                }
                return result
            }
        }

        // 2. Check individual option keys (optionA, optionB, optA, optB, a, b, c, d)
        val optionKeyPairs = listOf(
            Pair("A", listOf("optionA", "optA", "a", "option_a", "opt_a", "o1")),
            Pair("B", listOf("optionB", "optB", "b", "option_b", "opt_b", "o2")),
            Pair("C", listOf("optionC", "optC", "c", "option_c", "opt_c", "o3")),
            Pair("D", listOf("optionD", "optD", "d", "option_d", "opt_d", "o4"))
        )

        optionKeyPairs.forEach { (label, candidates) ->
            val matchedKey = candidates.find { obj.has(it) && !obj.optString(it).isNullOrBlank() }
            if (matchedKey != null) {
                result[label] = obj.optString(matchedKey, "")
            }
        }

        return result
    }

    private fun sanitizeCorrectAnswer(rawAnswer: String, optionsMap: Map<String, String>): String {
        if (rawAnswer.isBlank()) return ""

        val upper = rawAnswer.trim().uppercase()
        if (optionsMap.containsKey(upper)) return upper

        // Handle numbers: "1" -> "A", "2" -> "B", "3" -> "C", "4" -> "D"
        when (upper) {
            "1" -> if (optionsMap.containsKey("A")) return "A"
            "2" -> if (optionsMap.containsKey("B")) return "B"
            "3" -> if (optionsMap.containsKey("C")) return "C"
            "4" -> if (optionsMap.containsKey("D")) return "D"
            "0" -> if (optionsMap.containsKey("A")) return "A"
        }

        // Handle option text match
        val matchedEntry = optionsMap.entries.find {
            it.value.trim().equals(rawAnswer.trim(), ignoreCase = true)
        }
        if (matchedEntry != null) return matchedEntry.key

        return ""
    }

    private fun extractFromHierarchy(
        rootObj: JSONObject,
        outList: MutableList<Triple<JSONObject, Map<String, String>, String>>,
        defaultSetName: String,
        detectedKeys: MutableSet<String>
    ) {
        val subjectsArray = rootObj.optJSONArray("subjects")
        if (subjectsArray != null) {
            for (i in 0 until subjectsArray.length()) {
                val subjObj = subjectsArray.optJSONObject(i) ?: continue
                val subjId = subjObj.optString("id", subjObj.optString("name"))
                val unitsArray = subjObj.optJSONArray("units") ?: continue
                for (j in 0 until unitsArray.length()) {
                    val unitObj = unitsArray.optJSONObject(j) ?: continue
                    val unitId = unitObj.optString("id", unitObj.optString("name"))
                    val topicsArray = unitObj.optJSONArray("topics") ?: continue
                    for (k in 0 until topicsArray.length()) {
                        val topicObj = topicsArray.optJSONObject(k) ?: continue
                        val topicId = topicObj.optString("id", topicObj.optString("name"))
                        val questionsArray = topicObj.optJSONArray("questions") ?: topicObj.optJSONArray("mcqs") ?: continue
                        for (q in 0 until questionsArray.length()) {
                            val qObj = questionsArray.optJSONObject(q) ?: continue
                            collectKeys(qObj, detectedKeys)
                            val meta = mapOf(
                                "subjectId" to subjId,
                                "unitId" to unitId,
                                "topicId" to topicId,
                                "subtopic" to topicObj.optString("subtopic", "General Practice")
                            )
                            outList.add(Triple(qObj, meta, defaultSetName))
                        }
                    }
                }
            }
        }
    }

    // --- PRESET TEMPLATES FOR ADMIN PREVIEW & TEST ---

    val PRESET_FLAT_ARRAY = """
[
  {
    "id": "mcq-demo-01",
    "questionText": "ఆంధ్రప్రదేశ్ శాసన మండలి (Legislative Council) ఎంత మంది సభ్యులను కలిగి ఉంటుంది?",
    "options": {
      "A": "50 మంది",
      "B": "58 మంది",
      "C": "60 మంది",
      "D": "175 మంది"
    },
    "correctOption": "B",
    "explanation": "ఆంధ్రప్రదేశ్ శాసన మండలి మొత్తం సభ్యుల సంఖ్య 58 (50 మంది ఎన్నికైనవారు + 8 మంది నామినేటెడ్).",
    "difficulty": "Exam Standard",
    "setName": "Set 01 - AP State Polity"
  },
  {
    "id": "mcq-demo-02",
    "questionText": "Which article of the Constitution relates to the establishment of the Finance Commission of India?",
    "options": {
      "A": "Article 260",
      "B": "Article 280",
      "C": "Article 324",
      "D": "Article 356"
    },
    "correctOption": "B",
    "explanation": "Article 280 requires the President of India to constitute a Finance Commission every 5 years.",
    "difficulty": "Medium",
    "setName": "Set 01 - Indian Polity"
  },
  {
    "id": "mcq-demo-03",
    "questionText": "విజయనగర సామ్రాజ్యాన్ని సందర్శించిన ప్రముఖ పోర్చుగీస్ యాత్రికుడు నికోలో డీ కాంటీ ఏ రాజు కాలంలో వచ్చాడు?",
    "options": {
      "A": "మొదటి దేవరాయలు",
      "B": "రెండవ దేవరాయలు",
      "C": "శ్రీకృష్ణదేవరాయలు",
      "D": "అచ్యుతదేవరాయలు"
    },
    "correctOption": "A",
    "explanation": "ఇటాలియన్ యాత్రికుడు నికోలో కాంటీ 1420-21లో మొదటి దేవరాయల పాలనా కాలంలో విజయనగరాన్ని సందర్శించాడు.",
    "difficulty": "Hard",
    "setName": "Set 01 - Vijayanagara Empire"
  }
]
""".trimIndent()

    val PRESET_SINGLE_OBJECT = """
{
  "id": "mcq-single-101",
  "questionText": "ఇటీవల వార్తల్లో నిలిచిన 'చంద్రయాన్-3' విక్రమ్ ల్యాండర్ ల్యాండ్ అయిన ప్రదేశానికి భారతదేశం పెట్టిన అధికారిక పేరు ఏమిటి?",
  "optionA": "తిరంగా పాయింట్",
  "optionB": "శివశక్తి పాయింట్",
  "optionC": "శక్తి పాయింట్",
  "optionD": "జవాహర్ పాయింట్",
  "correctAnswer": "B",
  "explanation": "ఆగస్టు 23న చంద్రయాన్-3 విజయవంతంగా ల్యాండ్ అయిన ప్రదేశానికి ప్రధాని మోదీ 'శివశక్తి పాయింట్' అని నామకరణం చేశారు.",
  "difficulty": "Easy",
  "setName": "Set 02 - Current Affairs",
  "year": "2026",
  "exam": "APPSC Group 2 Prelims",
  "marks": "1.0",
  "source": "ISRO Press Release"
}
""".trimIndent()

    val PRESET_SET_BASED = """
{
  "setName": "APPSC Group 2 Special Grand Practice Set 01",
  "subject": "subj-ap-history",
  "questions": [
    {
      "q": "శాతవాహనుల నాణేలపై ఓడ (Ship) బొమ్మను ముద్రించిన ప్రసిద్ధ రాజు ఎవరు?",
      "optA": "మొదటి శాతకర్ణి",
      "optB": "గౌతమీపుత్ర శాతకర్ణి",
      "optC": "యజ్ఞశ్రీ శాతకర్ణి",
      "optD": "హాలుడు",
      "ans": "C",
      "exp": "సముద్ర వ్యాపారానికి ప్రసిద్ధి చెందిన యజ్ఞశ్రీ శాతవాహనుల నాణేలపై ద్విపాత తోరణం ఉన్న ఓడ బొమ్మను ముద్రించాడు."
    },
    {
      "q": "కాకతీయుల కాలంలో ప్రసిద్ధి చెందిన ఓడరేవు ఏది?",
      "optA": "మోటుపల్లి",
      "optB": "కలింగపట్నం",
      "optC": "మచిలీపట్నం",
      "optD": "కోరింగ",
      "ans": "A",
      "exp": "గణపతిదేవుడు మోటుపల్లి వద్ద 'అభయ శాసనం' వేయించి విదేశీ వ్యాపారానికి రక్షణ కల్పించాడు."
    }
  ]
}
""".trimIndent()

    val PRESET_NESTED_HIERARCHY = """
{
  "subjects": [
    {
      "id": "subj-indian-history",
      "name": "Indian history",
      "units": [
        {
          "id": "unit-ih-ancient",
          "topics": [
            {
              "id": "topic-ih-ancient-civ",
              "questions": [
                {
                  "questionText": "సింధు నాగరికత ప్రజలు పూజించిన ప్రధాన పురుష దేవుడు ఎవరు?",
                  "options": {
                    "A": "పశుపతి మహాదేవుడు",
                    "B": "ఇంద్రుడు",
                    "C": "విష్ణువు",
                    "D": "వరుణుడు"
                  },
                  "correctOption": "A",
                  "explanation": "మొహెంజొదారోలో లభించిన ముద్రికపై పశుపతి (శివుని తొలి రూపం) బొమ్మ గుర్తించబడింది."
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}
""".trimIndent()
}
