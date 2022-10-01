package tcc.com.diario_digital_criptografado.models

import android.graphics.Bitmap
import tcc.com.diario_digital_criptografado.R

class Usuario (var nome : String = "", var data_nascimento : String = "", var cpf : String = "",
               var telefone : String = "", var email : String = "",
               var genero : String = "", var tipo_perfil : String = "", var bitmap_foto : Bitmap? = null , var bitmap_string : String = "") {

}