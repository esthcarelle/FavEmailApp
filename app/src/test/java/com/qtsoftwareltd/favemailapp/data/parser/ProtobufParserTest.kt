package com.qtsoftwareltd.favemailapp.data.parser

import com.qtsoftwareltd.favemailapp.data.error.AppError
import com.qtsoftwareltd.favemailapp.data.error.Result
import com.qtsoftwareltd.favemailapp.util.HashUtil
import com.qtsoftwareltd.favemailapp.util.SampleEmailGenerator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests for ProtobufParser
 */
class ProtobufParserTest {
    
    private lateinit var tempDir: File
    private lateinit var validEmailFile: File
    private lateinit var invalidHashFile: File
    private lateinit var emptyEmailFile: File
    
    @Before
    fun setUp() {
        // Create temporary directory for test files
        tempDir = File(System.getProperty("java.io.tmpdir"), "protobuf_parser_test")
        tempDir.mkdirs()
        
        // Generate test files
        validEmailFile = SampleEmailGenerator.generateSampleEmail(
            File(tempDir, "valid_email.pb").absolutePath
        )
        invalidHashFile = SampleEmailGenerator.generateEmailWithInvalidHashes(
            File(tempDir, "invalid_hash_email.pb").absolutePath
        )
        emptyEmailFile = SampleEmailGenerator.generateEmptyEmail(
            File(tempDir, "empty_email.pb").absolutePath
        )
    }
    
    @Test
    fun `parseEmailFile with valid file returns Success`() {
        val result = ProtobufParser.parseEmailFile(validEmailFile)
        
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
        
        val email = result.getOrNull()
        assertNotNull(email)
        assertEquals("Jean Nkurunziza", email!!.senderName)
        assertEquals("jean.nkurunziza@qtsoftwareltd.com", email.senderEmailAddress)
        assertEquals("Welcome to FavEmailApp", email.subject)
        assertTrue(email.body.isNotEmpty())
        assertTrue(email.attachedImageBytes.isNotEmpty())
    }
    
    @Test
    fun `parseEmailFile with empty file returns Error`() {
        val emptyFile = File(tempDir, "empty.pb")
        emptyFile.writeBytes(ByteArray(0))
        
        val result = ProtobufParser.parseEmailFile(emptyFile)
        
        assertTrue(result.isError())
        assertFalse(result.isSuccess())
        
        val error = (result as Result.Error).error
        assertTrue(error is AppError.FileError.Empty)
    }
    
    @Test
    fun `parseEmailFile with non-existent file returns Error`() {
        val nonExistentFile = File(tempDir, "non_existent.pb")
        
        val result = ProtobufParser.parseEmailFile(nonExistentFile)
        
        assertTrue(result.isError())
        val error = (result as Result.Error).error
        assertTrue(error is AppError.FileError)
    }
    
    @Test
    fun `parseEmailFile with empty email data returns Error`() {
        val result = ProtobufParser.parseEmailFile(emptyEmailFile)
        
        assertTrue(result.isError())
        val error = (result as Result.Error).error
        // Empty email should trigger a FileError (could be NoEmailData or other validation)
        assertTrue(error is AppError.FileError)
    }
    
    @Test
    fun `parseEmailFile with invalid protobuf format returns Error`() {
        val invalidFile = File(tempDir, "invalid.pb")
        // Create a file with data that looks like protobuf but has invalid structure
        // Use a sequence that will cause InvalidProtocolBufferException
        // Protobuf wire format: field_number << 3 | wire_type
        // Using field number 0 (invalid) or malformed varint will cause parsing to fail
        invalidFile.writeBytes(byteArrayOf(
            0x08, 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x01 // Invalid varint
        ))
        
        val result = ProtobufParser.parseEmailFile(invalidFile)
        
        assertTrue(result.isError())
        val error = (result as Result.Error).error
        // Invalid protobuf should trigger a FileError (likely InvalidFormat or Corrupted)
        assertTrue("Error should be a FileError, got: ${error.javaClass.simpleName}", error is AppError.FileError)
    }
    
    @Test
    fun `parseEmailFile extracts all fields correctly`() {
        val result = ProtobufParser.parseEmailFile(validEmailFile)
        val email = (result as Result.Success).data
        
        assertEquals("Jean Nkurunziza", email.senderName)
        assertEquals("jean.nkurunziza@qtsoftwareltd.com", email.senderEmailAddress)
        assertEquals("Welcome to FavEmailApp", email.subject)
        assertTrue(email.body.contains("FavEmailApp"))
        assertTrue(email.attachedImageBytes.isNotEmpty())
        assertTrue(email.bodyHash.isNotEmpty())
        assertTrue(email.imageHash.isNotEmpty())
    }
    
    @Test
    fun `parseEmailFile preserves hash values`() {
        val result = ProtobufParser.parseEmailFile(validEmailFile)
        val email = (result as Result.Success).data
        
        // Verify that hashes are preserved (they should match computed hashes)
        val computedBodyHash = HashUtil.computeSha256Hash(email.body)
        val computedImageHash = HashUtil.computeSha256Hash(email.attachedImageBytes)
        
        assertEquals(computedBodyHash, email.bodyHash)
        assertEquals(computedImageHash, email.imageHash)
    }
}

