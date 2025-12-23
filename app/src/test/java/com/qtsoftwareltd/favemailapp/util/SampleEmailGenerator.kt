package com.qtsoftwareltd.favemailapp.util

import com.qtsoftwareltd.favemailapp.proto.EmailMessageOuterClass
import java.io.File

/**
 * Utility class to generate sample Protocol Buffer email files for testing
 */
object SampleEmailGenerator {
    
    /**
     * Generate a sample email .pb file with valid data and correct hashes
     * 
     * @param outputPath Path where the .pb file should be created
     * @return The generated File
     */
    fun generateSampleEmail(outputPath: String = "sample_email.pb"): File {
        // Sample email data with Rwandan names
        val senderName = "Jean Nkurunziza"
        val senderEmail = "jean.nkurunziza@qtsoftwareltd.com"
        val subject = "Welcome to FavEmailApp"
        val body = """
            Muraho!
            
            This is a sample email message to test the FavEmailApp.
            The app can load and display Protocol Buffer (.pb) files containing email messages.
            
            Features:
            - Protocol Buffer parsing
            - SHA-256 hash verification
            - Encrypted database storage
            - Beautiful UI with Jetpack Compose
            - Theme support (Light/Dark/System)
            - Localization (English/Kinyarwanda)
            
            Murakoze,
            Jean Nkurunziza
        """.trimIndent()
        
        // Create a simple test image (1x1 pixel PNG)
        // This is a minimal valid PNG file
        val imageBytes = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, // IHDR chunk length
            0x49, 0x48, 0x44, 0x52, // IHDR
            0x00, 0x00, 0x00, 0x01, // Width: 1
            0x00, 0x00, 0x00, 0x01, // Height: 1
            0x08, 0x06, 0x00, 0x00, 0x00, // Bit depth, color type, etc.
            0x1F.toByte(), 0x15.toByte(), 0xC4.toByte(), 0x89.toByte(), // CRC
            0x00, 0x00, 0x00, 0x0A, // IDAT chunk length
            0x49, 0x44, 0x41, 0x54, // IDAT
            0x78, 0x9C.toByte(), 0x63, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01, // Compressed data
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE.toByte(), 0x42, 0x60, 0x82.toByte() // IEND
        )
        
        // Compute SHA-256 hashes
        val bodyHash = HashUtil.computeSha256Hash(body)
        val imageHash = HashUtil.computeSha256Hash(imageBytes)
        
        // Build the Protocol Buffer message
        val emailMessage = EmailMessageOuterClass.EmailMessage.newBuilder()
            .setSenderName(senderName)
            .setSenderEmailAddress(senderEmail)
            .setSubject(subject)
            .setBody(body)
            .setAttachedImage(com.google.protobuf.ByteString.copyFrom(imageBytes))
            .setBodyHash(bodyHash)
            .setImageHash(imageHash)
            .build()
        
        // Serialize to binary file
        val outputFile = File(outputPath)
        outputFile.writeBytes(emailMessage.toByteArray())
        
        return outputFile
    }
    
    /**
     * Generate a sample email with invalid hashes (for testing verification failure)
     */
    fun generateEmailWithInvalidHashes(outputPath: String = "sample_email_invalid.pb"): File {
        val senderName = "Marie Mukamana"
        val senderEmail = "marie.mukamana@qtsoftwareltd.com"
        val subject = "Test Email with Invalid Hashes"
        val body = "This email has incorrect hash values for testing."
        
        val imageBytes = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        
        // Use incorrect hashes
        val bodyHash = "invalid_hash_value"
        val imageHash = "also_invalid_hash"
        
        val emailMessage = EmailMessageOuterClass.EmailMessage.newBuilder()
            .setSenderName(senderName)
            .setSenderEmailAddress(senderEmail)
            .setSubject(subject)
            .setBody(body)
            .setAttachedImage(com.google.protobuf.ByteString.copyFrom(imageBytes))
            .setBodyHash(bodyHash)
            .setImageHash(imageHash)
            .build()
        
        val outputFile = File(outputPath)
        outputFile.writeBytes(emailMessage.toByteArray())
        
        return outputFile
    }
    
    /**
     * Generate an empty email (for testing error handling)
     * This creates a valid protobuf message with all empty fields
     */
    fun generateEmptyEmail(outputPath: String = "sample_email_empty.pb"): File {
        // Create a message with all empty strings - this will trigger NoEmailData error
        val emailMessage = EmailMessageOuterClass.EmailMessage.newBuilder()
            .setSenderName("")
            .setSenderEmailAddress("")
            .setSubject("")
            .setBody("")
            .setAttachedImage(com.google.protobuf.ByteString.EMPTY)
            .setBodyHash("")
            .setImageHash("")
            .build()
        
        val outputFile = File(outputPath)
        outputFile.writeBytes(emailMessage.toByteArray())
        
        return outputFile
    }
}

