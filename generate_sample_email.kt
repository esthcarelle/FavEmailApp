import com.qtsoftwareltd.favemailapp.proto.EmailMessageOuterClass
import com.qtsoftwareltd.favemailapp.util.HashUtil
import java.io.File

/**
 * Script to generate a sample serialized Protocol Buffer email file
 * 
 * Usage:
 * 1. Build the project first: ./gradlew build
 * 2. Run this script: kotlin generate_sample_email.kt
 * 
 * This will create sample_email.pb in the project root
 */
fun main() {
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
    // PNG header + minimal valid PNG structure
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
    
    println("Generating sample email file...")
    println("Sender: $senderName <$senderEmail>")
    println("Subject: $subject")
    println("Body hash: $bodyHash")
    println("Image hash: $imageHash")
    
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
    val outputFile = File("sample_email.pb")
    outputFile.writeBytes(emailMessage.toByteArray())
    
    println("\nSample email file created: ${outputFile.absolutePath}")
    println("File size: ${outputFile.length()} bytes")
    println("\nReady to use for testing.")
}
