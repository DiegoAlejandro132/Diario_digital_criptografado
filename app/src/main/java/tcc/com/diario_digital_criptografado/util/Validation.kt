package tcc.com.diario_digital_criptografado.util

class Validation {

    companion object {
        fun validateDateCalendar(data : String, dataAtual : String) : Boolean{
            var dia : Int
            var mes : Int
            var ano : Int
            if (data[1].toString() == "-"){
                dia = "${data[0]}".toInt()
                if(data[3].toString() == "-"){
                    mes = "${data[2]}".toInt()
                    ano = "${data[4]}${data[5]}${data[6]}${data[7]}".toInt()
                }else{
                    mes = "${data[2]}${data[3]}".toInt()
                    ano = "${data[5]}${data[6]}${data[7]}${data[8]}".toInt()
                }
            }else{
                dia = "${data[0]}${data[1]}".toInt()
                if(data[4].toString() == "-" ){
                    mes = "${data[3]}".toInt()
                    ano = "${data[5]}${data[6]}${data[7]}${data[8]}".toInt()
                }else{
                    mes = "${data[3]}${data[4]}".toInt()
                    ano = "${data[6]}${data[7]}${data[8]}${data[9]}".toInt()
                }
            }
            val total = dia + (mes * 30)  + (ano * 365)

            val diaAtual = "${dataAtual[0]}${dataAtual[1]}".toInt()
            val mesAtual = "${dataAtual[3]}${dataAtual[4]}".toInt()
            val anoAtual = "${dataAtual[6]}${dataAtual[7]}${dataAtual[8]}${dataAtual[9]}".toInt()
            val totalAtual = diaAtual + (mesAtual * 30) + (anoAtual * 365)

            if(total <= totalAtual){
                return true
            }
            return false
        }


    }


}