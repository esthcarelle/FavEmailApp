package com.qtsoftwareltd.favemailapp.data.repository

import com.qtsoftwareltd.favemailapp.data.local.EmailDao
import com.qtsoftwareltd.favemailapp.data.model.EmailMessage
import com.qtsoftwareltd.favemailapp.util.HashUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Unit tests for EmailRepository
 */
class EmailRepositoryTest {
    
    private lateinit var mockDao: EmailDao
    private lateinit var repository: EmailRepository
    
    @Before
    fun setUp() {
        mockDao = mock()
        repository = EmailRepository(mockDao)
    }
    
    @Test
    fun `saveEmail verifies body hash correctly`() = runTest {
        val body = "Test email body"
        val correctBodyHash = HashUtil.computeSha256Hash(body)
        val incorrectBodyHash = "wrong_hash"
        
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = body,
            attachedImageBytes = ByteArray(0),
            bodyHash = correctBodyHash,
            imageHash = HashUtil.computeSha256Hash(ByteArray(0))
        )
        
        whenever(mockDao.insertEmail(any())).thenAnswer { }
        
        val savedEmail = repository.saveEmail(email)
        
        assertTrue(savedEmail.bodyHashVerified)
        verify(mockDao).insertEmail(savedEmail)
    }
    
    @Test
    fun `saveEmail detects invalid body hash`() = runTest {
        val body = "Test email body"
        val incorrectBodyHash = "wrong_hash"
        
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = body,
            attachedImageBytes = ByteArray(0),
            bodyHash = incorrectBodyHash,
            imageHash = HashUtil.computeSha256Hash(ByteArray(0))
        )
        
        whenever(mockDao.insertEmail(any())).thenAnswer { }
        
        val savedEmail = repository.saveEmail(email)
        
        assertFalse(savedEmail.bodyHashVerified)
        assertTrue(savedEmail.imageHashVerified) // Image hash is correct
        verify(mockDao).insertEmail(savedEmail)
    }
    
    @Test
    fun `saveEmail verifies image hash correctly`() = runTest {
        val imageBytes = byteArrayOf(0x01, 0x02, 0x03)
        val correctImageHash = HashUtil.computeSha256Hash(imageBytes)
        
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = "Test body",
            attachedImageBytes = imageBytes,
            bodyHash = HashUtil.computeSha256Hash("Test body"),
            imageHash = correctImageHash
        )
        
        whenever(mockDao.insertEmail(any())).thenAnswer { }
        
        val savedEmail = repository.saveEmail(email)
        
        assertTrue(savedEmail.imageHashVerified)
        verify(mockDao).insertEmail(savedEmail)
    }
    
    @Test
    fun `saveEmail detects invalid image hash`() = runTest {
        val imageBytes = byteArrayOf(0x01, 0x02, 0x03)
        val incorrectImageHash = "wrong_hash"
        
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = "Test body",
            attachedImageBytes = imageBytes,
            bodyHash = HashUtil.computeSha256Hash("Test body"),
            imageHash = incorrectImageHash
        )
        
        whenever(mockDao.insertEmail(any())).thenAnswer { }
        
        val savedEmail = repository.saveEmail(email)
        
        assertFalse(savedEmail.imageHashVerified)
        assertTrue(savedEmail.bodyHashVerified) // Body hash is correct
        verify(mockDao).insertEmail(savedEmail)
    }
    
    @Test
    fun `saveEmail verifies both hashes correctly`() = runTest {
        val body = "Test body"
        val imageBytes = byteArrayOf(0x01, 0x02, 0x03)
        
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = body,
            attachedImageBytes = imageBytes,
            bodyHash = HashUtil.computeSha256Hash(body),
            imageHash = HashUtil.computeSha256Hash(imageBytes)
        )
        
        whenever(mockDao.insertEmail(any())).thenAnswer { }
        
        val savedEmail = repository.saveEmail(email)
        
        assertTrue(savedEmail.bodyHashVerified)
        assertTrue(savedEmail.imageHashVerified)
        verify(mockDao).insertEmail(savedEmail)
    }
    
    @Test
    fun `getLatestEmail returns flow from dao`() = runTest {
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = "Test",
            attachedImageBytes = ByteArray(0),
            bodyHash = "",
            imageHash = ""
        )
        
        whenever(mockDao.getLatestEmail()).thenReturn(flowOf(email))
        
        val result = repository.getLatestEmail().first()
        
        assertEquals(email, result)
        verify(mockDao).getLatestEmail()
    }
    
    @Test
    fun `isEmailVerified returns true when both hashes verified`() {
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = "Test",
            attachedImageBytes = ByteArray(0),
            bodyHash = "",
            imageHash = "",
            bodyHashVerified = true,
            imageHashVerified = true
        )
        
        assertTrue(repository.isEmailVerified(email))
    }
    
    @Test
    fun `isEmailVerified returns false when body hash not verified`() {
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = "Test",
            attachedImageBytes = ByteArray(0),
            bodyHash = "",
            imageHash = "",
            bodyHashVerified = false,
            imageHashVerified = true
        )
        
        assertFalse(repository.isEmailVerified(email))
    }
    
    @Test
    fun `isEmailVerified returns false when image hash not verified`() {
        val email = EmailMessage(
            senderName = "Test",
            senderEmailAddress = "test@example.com",
            subject = "Test",
            body = "Test",
            attachedImageBytes = ByteArray(0),
            bodyHash = "",
            imageHash = "",
            bodyHashVerified = true,
            imageHashVerified = false
        )
        
        assertFalse(repository.isEmailVerified(email))
    }
}

