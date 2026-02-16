package com.sila.amro.domain.model

enum class SortField { POPULARITY, TITLE, RELEASE_DATE }
enum class SortOrder { DESC, ASC }

data class SortOption(val field: SortField, val order: SortOrder) {
    companion object {
        val Default = SortOption(SortField.POPULARITY, SortOrder.DESC)
    }
}