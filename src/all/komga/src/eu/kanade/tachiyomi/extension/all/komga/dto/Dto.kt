package eu.kanade.tachiyomi.extension.all.komga.dto

data class LibraryDto(
    val id: String,
    val name: String
)

data class SeriesDto(
    val id: String,
    val libraryId: String,
    val name: String,
    val created: String?,
    val lastModified: String?,
    val fileLastModified: String,
    val booksCount: Int,
    val metadata: SeriesMetadataDto
)

data class SeriesMetadataDto(
    val status: String,
    val created: String?,
    val lastModified: String?,
    val title: String,
    val titleSort: String,
    val summary: String,
    val summaryLock: Boolean,
    val readingDirection: String,
    val readingDirectionLock: Boolean,
    val publisher: String,
    val publisherLock: Boolean,
    val ageRating: Int?,
    val ageRatingLock: Boolean,
    val language: String,
    val languageLock: Boolean,
    val genres: Set<String>?, // TODO: remove optional in next iteration
    val genresLock: Boolean,
    val tags: Set<String>?, // TODO: remove optional in next iteration
    val tagsLock: Boolean
)

data class BookDto(
    val id: String,
    val seriesId: String,
    val name: String,
    val number: Float,
    val created: String?,
    val lastModified: String?,
    val fileLastModified: String,
    val sizeBytes: Long,
    val size: String,
    val media: MediaDto,
    val metadata: BookMetadataDto
)

data class MediaDto(
    val status: String,
    val mediaType: String,
    val pagesCount: Int
)

data class PageDto(
    val number: Int,
    val fileName: String,
    val mediaType: String
)

data class BookMetadataDto(
    val title: String,
    val titleLock: Boolean,
    val summary: String,
    val summaryLock: Boolean,
    val number: String,
    val numberLock: Boolean,
    val numberSort: Float,
    val numberSortLock: Boolean,
    val releaseDate: String?,
    val releaseDateLock: Boolean,
    val authors: List<AuthorDto>,
    val authorsLock: Boolean
)

data class AuthorDto(
    val name: String,
    val role: String
)

data class CollectionDto(
    val id: String,
    val name: String,
    val ordered: Boolean,
    val seriesIds: List<Long>,
    val createdDate: String,
    val lastModifiedDate: String,
    val filtered: Boolean
)
