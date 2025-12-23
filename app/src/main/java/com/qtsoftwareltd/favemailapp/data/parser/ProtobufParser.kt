package com.qtsoftwareltd.favemailapp.data.parser

import android.util.Log
import com.qtsoftwareltd.favemailapp.data.error.AppError
import com.qtsoftwareltd.favemailapp.data.error.AppErrorException
import com.qtsoftwareltd.favemailapp.data.error.ErrorMapper
import com.qtsoftwareltd.favemailapp.data.error.Result
import com.qtsoftwareltd.favemailapp.data.model.EmailMessage
import com.qtsoftwareltd.favemailapp.proto.EmailMessageOuterClass
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Parses Protocol Buffer email files into EmailMessage objects
 */
object ProtobufParser {
    
    private const val TAG = "ProtobufParser"
    
    /**
     * Parses a .pb file into an EmailMessage
     * 
     * @param file The .pb file to parse
     * @return Result.Success with EmailMessage or Result.Error with AppError
     */
    fun parseEmailFile(file: File): Result<EmailMessage> {
        return try {
            val emailMessage = parseEmailFileInternal(file)
            Result.Success(emailMessage)
        } catch (e: Exception) {
            ErrorMapper.mapToResult(e)
        }
    }
    
    /**
     * Internal parsing method that throws exceptions
     * Wrapped by parseEmailFile which converts exceptions to Result
     * 
     * @param file The .pb file to parse
     * @return Parsed EmailMessage
     * @throws AppErrorException with specific AppError types
     */
    private fun parseEmailFileInternal(file: File): EmailMessage {
        // Check if file is empty
        if (file.length() == 0L) {
            throw AppErrorException(AppError.FileError.Empty)
        }
        
        // Read the file as bytes
        val fileBytes = try {
            FileInputStream(file).use { it.readBytes() }
        } catch (e: IOException) {
            Log.e(TAG, "IO error reading file: ${file.name}", e)
            throw AppErrorException(AppError.FileError.CannotRead)
        }
        
        // Check if file has content
        if (fileBytes.isEmpty()) {
            throw AppErrorException(AppError.FileError.Corrupted)
        }
        
        // Parse the Protocol Buffer message
        val protoMessage = try {
            EmailMessageOuterClass.EmailMessage.parseFrom(fileBytes)
        } catch (e: com.google.protobuf.InvalidProtocolBufferException) {
            Log.e(TAG, "Invalid protobuf format: ${file.name}", e)
            throw AppErrorException(AppError.FileError.InvalidFormat)
        }
        
        // Validate that required fields are present
        if (protoMessage.senderName.isEmpty() && 
            protoMessage.senderEmailAddress.isEmpty() && 
            protoMessage.subject.isEmpty()) {
            throw AppErrorException(AppError.FileError.NoEmailData)
        }
        
        // Convert to our data model
        return EmailMessage(
            senderName = protoMessage.senderName,
            senderEmailAddress = protoMessage.senderEmailAddress,
            subject = protoMessage.subject,
            body = protoMessage.body,
            attachedImageBytes = protoMessage.attachedImage.toByteArray(),
            bodyHash = protoMessage.bodyHash,
            imageHash = protoMessage.imageHash
        )
    }
}

