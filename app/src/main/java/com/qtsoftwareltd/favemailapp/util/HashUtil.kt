package com.qtsoftwareltd.favemailapp.util

import java.security.MessageDigest

/**
 * Utility class for computing SHA-256 hashes
 * Used to verify the integrity of email body and image data
 */
object HashUtil {
    
    /**
     * Compute SHA-256 hash of a string (like email body)
     * Returns the hash as a hexadecimal string
     */
    fun computeSha256Hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytesToHex(hashBytes)
    }
    
    /**
     * Compute SHA-256 hash of byte array (like image data)
     * Returns the hash as a hexadecimal string
     */
    fun computeSha256Hash(input: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input)
        return bytesToHex(hashBytes)
    }
    
    /**
     * Convert byte array to hexadecimal string
     * This makes the hash readable and easy to compare
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder()
        for (byte in bytes) {
            val hex = Integer.toHexString(0xff and byte.toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }
}

