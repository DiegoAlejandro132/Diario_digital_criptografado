package tcc.com.diario_digital_criptografado.model

class Usuario (var nome : String = "", var data_nascimento : String = "", var cpf : String = "",
               var telefone : String = "", var email : String = "",
               var genero : String = "", var tipo_perfil : String = "", var tem_psicologo : Boolean = false, var tem_solicitacao : Boolean = false,
               var codigo_psicologo : String = "", var codigo_psicologo_solicitacao : String = "") {

}