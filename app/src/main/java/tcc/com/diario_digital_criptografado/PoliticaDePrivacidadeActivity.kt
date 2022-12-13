package tcc.com.diario_digital_criptografado

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_termos_de_uso.*

class PoliticaDePrivacidadeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termos_de_uso)

        btn_voltar_politica_privacidade.setOnClickListener {
            finish()
        }

        val inicio = "Meu Diário, pessoa jurídica de direito privado leva a sua privacidade e intimiade " +
                "como principal ativo a ser protegido e zela pela segurança e proteção de dados de todos os seus clientes, parceiros, " +
                "fornecedores e usuários do aplicativo Meu Diário. " +
                "Esta Política de Privacidade destina-se a informá-lo sobre o modo como nós utilizamos e " +
                "divulgamos informações coletadas ao longo do uso do aplicativo.Esta Política de " +
                "Privacidade aplica-se somente a informações coletadas por meio do aplicativo. " +
                "AO UTILIZAR O APLICATIVO, REGISTRAR INFORMAÇÕES OU FORNECER QUALQUER TIPO DE DADO PESSOAL, " +
                "VOCÊ DECLARA ESTAR CIENTE COM RELAÇÃO AOS TERMOS AQUI PREVISTOS E DE ACORDO COM A POLÍTICA " +
                "DE PRIVACIDADE, A QUAL DESCREVE AS FINALIDADES E FORMAS DE TRATAMENTO DE SEUS DADOS " +
                "PESSOAIS QUE VOCÊ DISPONIBILIZAR NO APLICATIVO. Esta Política de Privacidade fornece uma visão " +
                "geral de nossas práticas de privacidade e das escolhas que você pode fazer, bem como " +
                "direitos que você pode exercer em relação aos Dados Pessoais tratados por nós. Se " +
                "você tiver alguma dúvida sobre o uso de Dados Pessoais, entre em contato com 2020003353@ifam.edu.br. " +
                "Além disso, a Política de Privacidade não se aplica a quaisquer aplicativos, produtos, " +
                "serviços, site ou recursos de mídia social de terceiros que possam ser oferecidos ou " +
                "acessados por meio do aplicativo. O acesso a esses links fará com que você deixe o " +
                "nosso aplicativo e poderá resultar na coleta ou compartilhamento de informações sobre " +
                "você por terceiros. Nós não controlamos, endossamos ou fazemos quaisquer representações " +
                "sobre sites de terceiros ou suas práticas de privacidade, que podem ser diferentes das " +
                "nossas. Recomendamos que você revise a política de privacidade de qualquer site com o " +
                "qual você interaja antes de permitir a coleta e o uso de seus Dados Pessoais. " +
                "Caso você nos envie Dados Pessoais referentes a outras pessoas físicas, você declara " +
                "ter a competência para fazê-lo e declara ter obtido o consentimento necessário para " +
                "autorizar o uso de tais informações nos termos desta Política de Privacidade."

        politica_privacidade_texto_inicial.text = inicio

        val definicoes = "Para os fins desta Política de Privacidade:\n"+
                "1. \"Dados Pessoais\": significa qualquer informação que, direta ou indiretamente, " +
                "identifique ou possa identificar uma pessoa natural, como por exemplo, nome, CPF, " +
                "data de nascimento, endereço IP, dentre outros;\n2. \"Dados Pessoais Sensíveis\": " +
                "significa qualquer informação que revele, em relação a uma pessoa natural, origem " +
                "racial ou étnica, convicção religiosa, opinião política, filiação a sindicato ou a " +
                "organização de caráter religioso, filosófico ou político, dado referente à saúde ou " +
                "à vida sexual, dado genético ou biométrico;\n3. \"Tratamento de Dados Pessoais\": " +
                "significa qualquer operação efetuada no âmbito dos Dados Pessoais, por meio de meios " +
                "automáticos ou não, tal como a recolha, gravação, organização, estruturação, armazenamento, " +
                "adaptação ou alteração, recuperação, consulta, utilização, divulgação por transmissão, " +
                "disseminação ou, alternativamente, disponibilização, harmonização ou associação, restrição, " +
                "eliminação ou destruição. Também é considerado Tratamento de Dados Pessoais qualquer " +
                "outra operação prevista nos termos da legislação aplicável;\n4. \"Leis de Proteção de Dados\": " +
                "significa todas as disposições legais que regulam o Tratamento de Dados Pessoais, " +
                "incluindo, porém sem se limitar, a Lei nº 13.709/18, Lei Geral de Proteção de Dados Pessoais (\"LGPD\").\n"

        politica_privacidade_texto_definicoes.text = definicoes

        val usoDadosPessoais = "Coletamos e usamos Dados Pessoais para gerenciar seu relacionamento " +
                "conosco e poder entregar uma melhor usabilidade por parte do aplicativo, personalizando " +
                "e melhorando sua experiência. Exemplos de como usamos os dados incluem:\n1. Viabilizar " +
                "a procura pela veracidade dos dados de um psicólogo cadastrado;\n2. Para confirmar " +
                "ou corrigir as informações que temos sobre você;\n3. Para um psicólogo poder identificar " +
                "seus pacientes;\n4. Para personalizar sua experiência de uso no aplicativo;\n5. Para " +
                "tornar possível o conhecimento mútuo dos dados entre um psicólogo e um paciente, " +
                "com o objetivo de garantir para ambos que são quem dizem ser;\n6. Para entrarmos " +
                "em contato por um número de telefone e/ou endereço de e-mail fornecido. Podemos entrar " +
                "em contato com você pessoalmente, por mensagem de voz, através de equipamentos de " +
                "discagem automática, por mensagens de texto (SMS), por e-mail, ou por qualquer outro " +
                "meio de comunicação que seu dispositivo seja capaz de receber, nos termos da lei e " +
                "para fins comerciais razoáveis. \nAlém disso, os Dados Pessoais fornecidos também " +
                "podem ser utilizados na forma que julgarmos necessária ou adequada: (a) nos termos " +
                "das Leis de Proteção de Dados; (b) para atender exigências de processo judicial; (c) " +
                "para cumprir decisão judicial, decisão regulatória ou decisão de autoridades competentes, " +
                "incluindo autoridades fora do país de residência; (d) para aplicar nossos Termos e " +
                "Condições de Uso; (e) para proteger nossas operações; (f) para proteger direitos, " +
                "privacidade, segurança nossos, seus ou de terceiros; (g) para detectar e prevenir " +
                "fraude; (h) permitir-nos usar as ações disponíveis ou limitar danos que venhamos a " +
                "sofrer; e (i) de outros modos permitidos por lei.\n"

        politica_privacidade_texto_uso_dados_pessoais.text = usoDadosPessoais


        val naoFornecimentoDadosPessoais = "É obrigatório o compartilhamento de dados pessoais para " +
                "poder fazer uso do aplicativo, pois eles são a base do funcionamento dele.\n"

        politica_privacidade_texto_nao_fornecimento_dados_pessoais.text = naoFornecimentoDadosPessoais

        val dadosColetados = "O público não poderá acessar o aplicativo sem o compartilhamento dos " +
                "dados pessoais solicitados no cadastro. São eles: nome; data de nascimento; CPF; " +
                "gênero; e-mail e número de telefone. Caso o tipo de usuário seja selecionado como " +
                "psicólogo, será necessário fornecer também o estado de inscrição do CPR e o respectivo " +
                "número de inscrição.\nDurante o uso do aplicativo, por parte do usuário comum, " +
                "poderão ser coletados os sentimentos bons e ruins sentidos durante o dia selecionado, " +
                "um registro diário das experiências vividas no dia, uma avaliação geral do dia, um " +
                "título atribuído ao dia e uma foto de perfil para o usuário.\nPara o psicólogo, " +
                "durante o uso do aplicativo, poderão ser coletados quem são seus pacientes e uma foto " +
                "de perfil referente ao usuário.\nOS DADOS COLETADOS REFERENTES À QUALQUER DIA SELECIONADO " +
                "PODEM SER DADOS SENSÍVEIS, PORTANTO AO ACEITAR ESTA POLÍTICA DE PRIVACIDADE, O USUÁRIO " +
                "ESTÁ CIENTE E DE ACORDO DO TRATAMENTO DAS RESPECTIVAS INFORMAÇÕES.\nO OBJETIVO DA POSSÍVEL " +
                "COLETA DE DADOS SENSÍVEIS É PARA CONTROLE DO PRÓPRIO USUÁRIO, UMA VEZ QUE O APLICATIVO " +
                "PODE SER USADO COMO DIÁRIO, E PARA  COMPARTILHAMENTO, MEDIANTE AUTORIZAÇÃO DO USUÁRIO, " +
                "COM O PSICÓLOGO RELACIONADO À CONTA.\n"

        politica_privacidade_texto_dados_coletados.text = dadosColetados


        val compartilhamentoDadosColetados = "Nós poderemos compartilhar seus Dados Pessoais:\n" +
                "1. Com outros usuários, a partir do momento em que é criada uma relação voluntária " +
                "entre psicólogo e paciente(s).\n" +
                "\tOs dados da conta do psicólogo como nome, CPF, número de inscrição, estado de " +
                "registro e foto de perfil serão compartilhados com um usuário do diário, quando o " +
                "psicólogo mandar uma solicitação de acesso de dados ao paciente, com o objetivo do " +
                "paciente identificar quem deseja acessar seus dados. Caso o paciente aceite a solicitação, " +
                "esses mesmos dados continuarão sendo compartilhados até o momento em que a relação " +
                "psicólogo-paciente seja desfeita no aplicativo.\nOs dados do usuário do diário que " +
                "serão compartilhados com um psicólogo autorizado pelo usuário são o nome, telefone " +
                "e e-mail para identificação e contato, e os topicos avaliação geral, sentimentos bons e ruins " +
                "referentes a dias preenchidos pelo usuário do diário com o objetivo de auxiliar o " +
                "psicólogo em um possível acompanhamento psicológico.\n"

        politica_privacidade_compartilhamento_dados.text = compartilhamentoDadosColetados

        val direitosUsuario = "Você pode, a qualquer momento, requerer: (i) confirmação de que seus " +
                "Dados Pessoais estão sendo tratados; (ii) acesso aos seus Dados Pessoais; (iii) " +
                "correções a dados incompletos, inexatos ou desatualizados; (iv) anonimização, bloqueio " +
                "ou eliminação de dados desnecessários, excessivos ou tratados em desconformidade com " +
                "o disposto em lei; (v) portabilidade de Dados Pessoais a outro prestador de serviços, " +
                "contanto que isso não afete nossos segredos industriais e comerciais; (vi) eliminação " +
                "de Dados Pessoais tratados com seu consentimento, na medida do permitido em lei; (vii) " +
                "informações sobre as entidades às quais seus Dados Pessoais tenham sido compartilhados; " +
                "(viii) informações sobre a possibilidade de não fornecer o consentimento e sobre as " +
                "consequências da negativa; e (ix) revogação do consentimento. Os seus pedidos serão " +
                "tratados com especial cuidado de forma a que possamos assegurar a eficácia dos seus " +
                "direitos. Poderá lhe ser pedido que faça prova da sua identidade de modo a assegurar " +
                "que a partilha dos Dados Pessoais é apenas feita com o seu titular.\n" +
                "Você deverá ter em mente que, em certos casos (por exemplo, devido a requisitos legais), " +
                "o seu pedido poderá não ser imediatamente satisfeito, além de que nós poderemos não " +
                "conseguir atendê-lo por conta de cumprimento de obrigações legais.\n"

        politica_privacidade_texto_direitos_usuario.text = direitosUsuario

        val segurancaDadosPessoais = "Buscamos adotar as medidas técnicas e organizacionais previstas " +
                "pelas Leis de Proteção de Dados adequadas para proteção dos Dados Pessoais na nossa " +
                "organização.\n" +
                "Infelizmente, nenhuma transmissão ou sistema de armazenamento de dados tem a garantia " +
                "de serem 100% seguros. Caso tenha motivos para acreditar que sua interação conosco " +
                "tenha deixado de ser segura (por exemplo, caso acredite que a segurança de qualquer " +
                "uma de suas contas foi comprometida), favor nos notificar imediatamente.\n" +
                "A segurança atribuída aos possíveis dados sensíveis informados anteriormente se dará " +
                "por, além das medidas previstas pelas Leis de Proteção de Dados, criptografia dessas " +
                "informações, para que, mesmo que haja uma quebra na integridade da segurança dos meios " +
                "de armazenamento de informações, os dados sensíveis se tornem quase ou até totalmente " +
                "indecifráveis.\n"

        politica_privacidade_seguranca_dados_pessoais.text = segurancaDadosPessoais

        val linksParaOutrosSites = "O aplicativo poderá conter links de hipertexto que vão redirecionar " +
                "você para outros sites, como o site de consulta de cadastro nacional de psicólogos. " +
                "Se você clicar em um desses links para qualquer site, lembre-se que cada site possui " +
                "as suas próprias práticas de privacidade e que não somos responsáveis por essas políticas. " +
                "Consulte as referidas políticas antes de enviar quaisquer Dados Pessoais para esses sites.\n" +
                "Não nos responsabilizamos pelas políticas e práticas de coleta, uso e divulgação " +
                "(incluindo práticas de proteção de dados) de outras organizações, tais como Facebook, " +
                "Apple, Google, Microsoft, ou de qualquer outro desenvolvedor de software ou provedor " +
                "de aplicativo, loja de mídia social, sistema operacional, prestador de serviços de " +
                "internet sem fio ou fabricante de dispositivos, incluindo todos os Dados Pessoais que " +
                "divulgar para outras organizações por meio dos aplicativos, relacionadas a tais " +
                "aplicativos, ou publicadas em nossas páginas em mídias sociais. Nós recomendamos que " +
                "você se informe sobre a política de privacidade de cada site visitado ou de cada " +
                "prestador de serviço utilizado.\n"

        politica_privacidade_links_para_outros_sites.text = linksParaOutrosSites

        val atualizacaoPolitica = "Se modificarmos nossa Política de Privacidade, publicaremos o novo " +
                "texto no aplicativo, com a data de revisão atualizada. Podemos alterar esta Política " +
                "de Privacidade a qualquer momento. Caso haja alteração significativa nos termos desta " +
                "Política de Privacidade, podemos informá-lo por meio das informações de contato que " +
                "tivermos em nosso banco de dados ou por meio de notificação em nosso aplicativo.\n" +
                "Recordamos que nós temos como compromisso não tratar os seus Dados Pessoais de forma " +
                "incompatível com os objetivos descritos acima, exceto se de outra forma requerido por " +
                "lei ou ordem judicial.\n" +
                "O seu uso do aplicativo após as alterações significa que aceitou as Políticas de " +
                "Privacidade revisadas. Caso, após a leitura da versão revisada, você não esteja de " +
                "acordo com seus termos, favor encerrar o acesso ao aplicativo excluindo sua conta.\n"

        politica_privacidade_atualizacao_politica.text = atualizacaoPolitica

        val encarregadoTratamentoDados = "Caso pretenda exercer qualquer um dos direitos previstos, " +
                "inclusive retirar o seu consentimento, nesta Política de Privacidade e/ou nas Leis " +
                "de Proteção de Dados, ou resolver quaisquer dúvidas relacionadas ao Tratamento de " +
                "seus Dados Pessoais, favor contatar-nos em 2020003353@ifam.edu.br.\n"

        politica_privacidade_encarregado_tratamento.text = encarregadoTratamentoDados
    }
}