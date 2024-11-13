package com.hyuuny.resellshop.config

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.hyuuny.resellshop.core.common.exception.ResellShopException
import com.hyuuny.resellshop.core.common.response.ResellShopResponse
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("Invalid message format", ex)
        val message = when (val cause = ex.cause) {
            is InvalidFormatException -> "Invalid format for field: ${cause.path.last().fieldName}"
            is MismatchedInputException -> "${cause.path.joinToString() { it.fieldName }} is null"
            else -> cause?.message ?: "Invalid request format"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResellShopResponse.error(message))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("Validation failed for request", ex)

        val errorMessages = ex.bindingResult.fieldErrors
            .groupBy { it.field }
            .mapValues { (_, errors) ->
                errors.joinToString(", ") { it.defaultMessage.orEmpty() }
            }

        val errorMessage = errorMessages.entries.joinToString("; ") { (field, message) ->
            "$field: $message"
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResellShopResponse.error(errorMessage))
    }

    @ExceptionHandler(ResellShopException::class)
    fun handleResellShopException(exception: ResellShopException): ResponseEntity<ResellShopResponse<Unit>> {
        logger.error("Error occurred", exception)
        val errorMessage = exception.message ?: "An unexpected error occurred"
        val errorCode = exception.code.name
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResellShopResponse.error(errorMessage, errorCode))
    }

    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class)
    fun handleBadRequestException(exception: RuntimeException): ResponseEntity<ResellShopResponse<Unit>> {
        logger.error("Bad request error", exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResellShopResponse.error(exception.message ?: "Bad request"))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFoundException(exception: EntityNotFoundException): ResponseEntity<ResellShopResponse<Unit>> {
        logger.error("Entity not found", exception)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResellShopResponse.error(exception.message ?: "Entity not found"))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(exception: NoSuchElementException): ResponseEntity<ResellShopResponse<Unit>> {
        logger.error("Element not found", exception)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResellShopResponse.error(exception.message ?: "Element not found"))
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(exception: Exception): ResponseEntity<ResellShopResponse<Unit>> {
        logger.error("Internal server error", exception)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResellShopResponse.error(exception.message ?: "An unexpected error occurred"))
    }
}
