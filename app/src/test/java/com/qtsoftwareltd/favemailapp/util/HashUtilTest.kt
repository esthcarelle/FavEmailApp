package com.qtsoftwareltd.favemailapp.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for HashUtil
 */
class HashUtilTest {
    
    @Test
    fun `computeSha256Hash for string returns correct hash`() {
        val input = "Hello, World!"
        val hash = HashUtil.computeSha256Hash(input)
        
        // SHA-256 of "Hello, World!" (verified)
        val expectedHash = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f"
        
        assertEquals(expectedHash, hash)
        assertEquals(64, hash.length) // SHA-256 produces 64 hex characters
    }
    
    @Test
    fun `computeSha256Hash for empty string returns correct hash`() {
        val input = ""
        val hash = HashUtil.computeSha256Hash(input)
        
        // SHA-256 of empty string
        val expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        
        assertEquals(expectedHash, hash)
    }
    
    @Test
    fun `computeSha256Hash for byte array returns correct hash`() {
        val input = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        val hash = HashUtil.computeSha256Hash(input)
        
        assertNotNull(hash)
        assertEquals(64, hash.length) // SHA-256 produces 64 hex characters
    }
    
    @Test
    fun `computeSha256Hash for empty byte array returns correct hash`() {
        val input = ByteArray(0)
        val hash = HashUtil.computeSha256Hash(input)
        
        // SHA-256 of empty byte array
        val expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        
        assertEquals(expectedHash, hash)
    }
    
    @Test
    fun `computeSha256Hash is deterministic`() {
        val input = "Test input"
        val hash1 = HashUtil.computeSha256Hash(input)
        val hash2 = HashUtil.computeSha256Hash(input)
        
        assertEquals(hash1, hash2)
    }
    
    @Test
    fun `computeSha256Hash produces different hashes for different inputs`() {
        val hash1 = HashUtil.computeSha256Hash("Input 1")
        val hash2 = HashUtil.computeSha256Hash("Input 2")
        
        assertNotEquals(hash1, hash2)
    }
    
    @Test
    fun `computeSha256Hash handles unicode characters`() {
        val input = "Hello 世界 🌍"
        val hash = HashUtil.computeSha256Hash(input)
        
        assertNotNull(hash)
        assertEquals(64, hash.length)
    }
}

