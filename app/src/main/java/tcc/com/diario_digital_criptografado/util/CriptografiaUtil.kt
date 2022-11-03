package tcc.com.diario_digital_criptografado.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object CriptografiaUtil {

    fun decrypt(cipherText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec("1234567890123456".toByteArray(), "AES"), IvParameterSpec(ByteArray(16)))
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }

    fun encrypt(inputText: String): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec("1234567890123456".toByteArray(), "AES"), IvParameterSpec(ByteArray(16)))
        val cipherText = cipher.doFinal(inputText.toByteArray())
        return Base64.getEncoder().encodeToString(cipherText)
    }

//    val inputText = "abcdefghigklmnopqrstuvwxyz0123456789"
//    val algorithm = "AES/CBC/PKCS5Padding"
//    val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
//    val iv = IvParameterSpec(ByteArray(16))
//
//    val cipherText = encrypt(algorithm, inputText, key, iv)
//    val plainText = decrypt(algorithm, cipherText, key, iv)
//
//    assert(inputText == plainText)
}