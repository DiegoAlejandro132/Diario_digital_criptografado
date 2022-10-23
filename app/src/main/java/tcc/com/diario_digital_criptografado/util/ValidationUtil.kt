package tcc.com.diario_digital_criptografado.util

import java.util.*

internal object ValidationUtil {

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

        fun validarDataNascimento(dia : Int, mes : Int, ano : Int) : Boolean{

            var valido = false

            val utc = Calendar.getInstance(TimeZone.getTimeZone("GMT-4"))
            val diaAtual = utc.get(Calendar.DAY_OF_MONTH)
            val mesAtual = utc.get(Calendar.MONTH)+1
            val anoAtual = utc.get(Calendar.YEAR)


            if(anoAtual - ano >= 18)
                if(anoAtual - ano > 18)
                    valido = true
                else if(anoAtual - ano == 18)
                        if(mesAtual >= mes)
                            if(mesAtual > mes)
                                valido = true
                            else if(diaAtual >= dia)
                                valido = true

            return valido
        }


}