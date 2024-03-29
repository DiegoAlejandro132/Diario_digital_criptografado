package tcc.com.diario_digital_criptografado.usuarioActivities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_agenda_usuario.*
import kotlinx.android.synthetic.main.header_navigation_drawer.*
import tcc.com.diario_digital_criptografado.MainActivity
import tcc.com.diario_digital_criptografado.MeuPerfilActivity
import tcc.com.diario_digital_criptografado.PoliticaDePrivacidadeActivity
import tcc.com.diario_digital_criptografado.R
import tcc.com.diario_digital_criptografado.adapter.DiaAdapter
import tcc.com.diario_digital_criptografado.model.Diario
import tcc.com.diario_digital_criptografado.psicologoActivities.AdicionarPacienteActivity
import tcc.com.diario_digital_criptografado.util.AuthUtil
import tcc.com.diario_digital_criptografado.util.ConexaoUtil
import tcc.com.diario_digital_criptografado.util.CriptografiaUtil
import tcc.com.diario_digital_criptografado.util.FotoUtil
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class AgendaUsuarioActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    private lateinit var emailUsuarioSelecionado : String
    private lateinit var tipoUsuario : String

    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda_usuario)
        usuarioEstaLogado()

        atualizarContagemDiasRuins()

        supportActionBar?.title = "Agenda"

        if(ConexaoUtil.estaConectado(this)){
            FotoUtil.definirFotoPerfil()
            trazerDadosUsuario()
            listarDias()
        }else{
            Snackbar.make(calendarView_user, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }

        setNavigationDrawer()
        //caso o usuario esteja logado, direciona para a sua pagina ao inves de fazer login de novo
        if(!AuthUtil.usuarioEstaLogado()) {
            startActivity(Intent(this, MainActivity::class.java))
        }


        calendarView_user.setOnDateChangeListener {
                calendarView, i, i2, i3 ->


            val ano = i
            val mes = i2
            val dia = i3

            val calendar = Calendar.getInstance()
            calendar.set(ano, mes, dia)
            val calendarioLong = calendar.timeInMillis
            val dateSelecionado = Date(calendarioLong)
            val localDateSelecionado = dateSelecionado.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val dataCertaSelecionada = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(localDateSelecionado)
            val diaAtual = LocalDate.now()

            val intentEmail = intent
            emailUsuarioSelecionado = if(tipoUsuario == "Psicólogo"){
                intentEmail.getStringExtra("email").toString()
            }else ""

            if(diaAtual.isAfter(localDateSelecionado) || diaAtual.equals(localDateSelecionado)){
                val intent = Intent(this, FormularioDiarioActivity::class.java)
                intent.putExtra("dataSelecionada", dataCertaSelecionada)
                intent.putExtra("dataSelecionadaLong", calendarioLong.toString())
                if(tipoUsuario == "Psicólogo")
                    if(ConexaoUtil.estaConectado(this)){
                        intent.putExtra("emailUsuarioSelecionado", emailUsuarioSelecionado)
                        irDiario(intent)
                    }else{
                        Snackbar.make(calendarView_user, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
                    }
                else
                    irDiario(intent)
            }else{
                Snackbar.make(calendarView_user, "Data futura não é permitida", Snackbar.LENGTH_LONG).show()
            }
        }


        btn_voltar_agenda.setOnClickListener{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if(ConexaoUtil.estaConectado(this)){
            usuarioEstaLogado()
            atualizarDadosUsuario()
            listarDias()
            FotoUtil.definirFotoPerfil()
        }else{
            Snackbar.make(calendarView_user, "Verifique a conexão com a internet", Snackbar.LENGTH_LONG).show()
        }
    }


    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->
    // <---------------------------------------------------- funções ----------------------------------------------------->

    private fun trazerDadosUsuario(){

        try{

            if(progressive_agenda.visibility == View.GONE){
                linear_layout_agenda.visibility = View.GONE
                progressive_agenda.isVisible = true
            }

            database = FirebaseDatabase.getInstance().getReference("users")
            database.get().addOnSuccessListener {
                if(it.exists() && AuthUtil.getCurrentUser() != null){
                    tipoUsuario = it.child("${AuthUtil.getCurrentUser()}/tipo_perfil").value.toString()

                    val nomeUsuario = it.child("${AuthUtil.getCurrentUser()}/nome").value.toString().replaceFirstChar { it.toUpperCase() }
                    val emailUsuario = it.child("${AuthUtil.getCurrentUser()}/email").value.toString()
                    val fotoperfil = it.child("${AuthUtil.getCurrentUser()}/foto_perfil").value.toString()
                    if(fotoperfil != "")
                        Glide.with(applicationContext).load(fotoperfil.toUri()).into(nav_header_foto_perfil)
                    nav_header_nome_usuario.text = nomeUsuario
                    nav_header_email_usuario.text = emailUsuario

                    if(tipoUsuario == "Psicólogo"){
                        val intent = intent
                        emailUsuarioSelecionado = intent.getStringExtra("email").toString()
                        for(user in it.children){
                            if(user.child("email").value == emailUsuarioSelecionado){
                                if(user.child("codigo_psicologo").value != AuthUtil.getCurrentUser() || user.child("tem_psicologo").value == false){
                                    progressive_agenda.visibility = View.GONE
                                    linear_layout_agenda.visibility = View.GONE
                                    lbl_psicologo_nao_autorizado_agenda.isVisible = true
                                }else{
                                    progressive_agenda.visibility = View.GONE
                                    layout_recycler_agenda.visibility = View.GONE
                                    btn_voltar_agenda.isVisible = true
                                    lbl_psicologo_nao_autorizado_agenda.visibility = View.GONE
                                    linear_layout_agenda.isVisible = true
                                }
                            }
                        }
                        navigation_view_agenda.inflateMenu(R.menu.navigation_drawer_psicologo)
                        btn_voltar_agenda.visibility = View.VISIBLE
                    }else if (tipoUsuario == "Usuário do diário"){
                        emailUsuarioSelecionado = ""
                        navigation_view_agenda.inflateMenu(R.menu.navigation_drawer_usuario)
                        progressive_agenda.visibility = View.GONE
                        linear_layout_agenda.isVisible = true
                    }
                }
            }.addOnFailureListener {
                Log.e("trazerDadosUsuario", "Não foi possivel trazer os dados do usuario")
                Snackbar.make(btn_voltar_agenda, "Houve um erro ao trazer os dados do usuário", Snackbar.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            Toast.makeText(this@AgendaUsuarioActivity, "Erro ao verificar o usuário.", Toast.LENGTH_SHORT).show()
            Log.e("verifyUser", e.message.toString())
        }
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean{
       if(toggle.onOptionsItemSelected(item)){
           return true
       }
        return super.onOptionsItemSelected(item)
    }


    private fun listarDias() {
        try{
            val dialist = ArrayList<Diario>()

            val recryclerDias = recycler_dias
            recryclerDias.layoutManager = LinearLayoutManager(this)
            recryclerDias.setHasFixedSize(true)

            database = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!).child("dias")
            database.orderByChild("dias")
            database.get().addOnCompleteListener{
                if(it.isSuccessful){
                    val dias = it.result
                    val dataHoje = LocalDate.now()

                    for(dia in dias.children){
                        val expiraEm = CriptografiaUtil.decrypt(dia.child("modificado_em").value.toString())
                        val dataExpiracaoMili = expiraEm.toLong()
                        val dataExpiracaoDate = Date(dataExpiracaoMili)
                        val dataExpiracao = dataExpiracaoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(5)

                        if(dataHoje <= dataExpiracao){
                            val diaData = Diario()
                            diaData.data = CriptografiaUtil.decrypt(dia.child("data").value.toString())
                            diaData.data_long = CriptografiaUtil.decrypt(dia.child("data_long").value.toString())
                            diaData.avaliacaoDia = CriptografiaUtil.decrypt(if(dia.child("avaliacaoDia").value != null) dia.child("avaliacaoDia").value.toString() else "")
                            diaData.titulo = CriptografiaUtil.decrypt(if(dia.child("titulo").value != null) dia.child("titulo").value.toString() else "" )
                            dialist.add(diaData)
                        }
                    }

                    dialist.reverse()

                    if(dialist.size == 0)
                        lbl_sem_lancamentos_dias.isVisible = true
                    else
                        lbl_sem_lancamentos_dias.visibility = View.GONE

                    var adapter = DiaAdapter(this@AgendaUsuarioActivity, dialist)
                    recycler_dias.adapter = adapter


                    adapter.setOnItemClickListener(object : DiaAdapter.onItemClickListener{
                        override fun visualizarDia(position: Int) {
                            val clickedItem = dialist[position]
                            adapter.notifyItemChanged(position)
                            intent = Intent(this@AgendaUsuarioActivity, FormularioDiarioActivity::class.java)
                            intent.putExtra("dataSelecionadaLong", clickedItem.data_long)
                            intent.putExtra("dataSelecionada", clickedItem.data)
                            startActivity(intent)
                        }
                    })
                }else{
                    Log.e("litarDias", "Não foi possivel listar os dias")
                    Snackbar.make(btn_voltar_agenda, "Houve um erro ao listar os dias", Snackbar.LENGTH_LONG).show()
                }
            }
        }catch (e:Exception){
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            Log.e("listarDias", e.message.toString())
            val intent = Intent(this, MeuPerfilActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setNavigationDrawer(){
        val drawerLayout : DrawerLayout = drawer_layout_agenda_usuario
        val navView : NavigationView = navigation_view_agenda
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                //opções do acesso do usuario comum
                R.id.nav_acesso_perfil_usuario -> irVisualizarPefil()
                R.id.nav_acesso_meu_psicologo -> goMeuPsicologo()
                R.id.nav_acesso_solicitacoes -> goSolicitacoes()
                R.id.nav_logout_usuario -> showDialogLogOut()
                R.id.nav_politica_privacidade_usuario -> irPoliticaPrivacidade()
                //opções de acesso do psicologo
                R.id.nav_acesso_perfil_psicologo -> irVisualizarPefil()
                R.id.nav_adicionar_paciente -> goAdicionarPaciente()
                R.id.nav_logout_psicologo -> showDialogLogOut()
                R.id.nav_politica_privacidade_psicologo -> irPoliticaPrivacidade()
            }
            true
        }
    }

    private fun showDialogLogOut(){
        val dialogBuilder = AlertDialog.Builder(this@AgendaUsuarioActivity)
        dialogBuilder.setMessage("Deseja encerrar a sessão?")
            .setTitle("Encerrar sessão")
            .setPositiveButton("Sim") { dialog, id ->  signOut() }
            .setNegativeButton("Não") { dialog, id ->  dialog.dismiss() }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun signOut(){

        try{
            Firebase.auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }catch (e:Exception){
            Toast.makeText(this@AgendaUsuarioActivity, "Erro ao sair.", Toast.LENGTH_SHORT).show()
            Log.e("signOut", e.message.toString())
        }
    }

    private fun atualizarDadosUsuario(){

        try{

            database = FirebaseDatabase.getInstance().getReference("users")
            database.child(AuthUtil.getCurrentUser()!!).get().addOnSuccessListener {
                if(it.exists()){
                    tipoUsuario = it.child("tipo_perfil").value.toString()
                    val nomeUsuario = "<b>" + it.child("nome").value.toString().replaceFirstChar { it.toUpperCase() } + "</b>"
                    nav_header_nome_usuario.text = Html.fromHtml(nomeUsuario)
                    val fotoPerfil = it.child("foto_perfil").value.toString()
                    if(fotoPerfil != ""){
                        Glide.with(applicationContext).load(fotoPerfil.toUri()).into(nav_header_foto_perfil)
                    }
                    if(tipoUsuario == "Psicólogo"){
                        val intent = intent
                        emailUsuarioSelecionado = intent.getStringExtra("email").toString()
                        btn_voltar_agenda.visibility = View.VISIBLE
                    }else if (tipoUsuario == "Usuário do diário"){
                        emailUsuarioSelecionado = ""
                    }
                }
            }.addOnFailureListener {
                Log.e("updateUserData", it.message.toString())
                Snackbar.make(btn_voltar_agenda, "Houve um erro ao atualizar os dados", Snackbar.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            Toast.makeText(this@AgendaUsuarioActivity, "Erro ao verificar o usuário.", Toast.LENGTH_SHORT).show()
            Log.e("verifyUser", e.message.toString())
        }
    }

    private fun atualizarContagemDiasRuins(){
        database = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!)
        database.get().addOnCompleteListener{
            if(it.isSuccessful && AuthUtil.getCurrentUser() != null){
                val snapshot = it.result
                val hoje = LocalDate.now()
                var qtdDiaRuim = 0
                for (dia in snapshot.child("dias").children){
                    val data = CriptografiaUtil.decrypt(dia.child("data_long").value.toString())
                    val dataDate = Date(data.toLong())
                    val dataLocalDate = dataDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    if((hoje.year == dataLocalDate.year) && (hoje.month == dataLocalDate.month)
                        && (hoje.dayOfMonth - dataLocalDate.dayOfMonth <= 7)){

                        val avaliacaoDia = CriptografiaUtil.decrypt(dia.child("avaliacaoDia").value.toString())
                        val diaRuim = "Péssimo" in avaliacaoDia || "Ruim" in avaliacaoDia
                        if(diaRuim){
                            qtdDiaRuim ++
                        }
                    }
                }
                if (qtdDiaRuim >= 5){
                    val trueString = CriptografiaUtil.encrypt("true")
                    val databaseDiaRuim = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!)
                    databaseDiaRuim.child("diasRuinsConsecutivos").setValue(trueString)
                }else{
                    val falseString = CriptografiaUtil.encrypt("false")
                    val databaseDiaRuim = FirebaseDatabase.getInstance().getReference("users").child(AuthUtil.getCurrentUser()!!)
                    databaseDiaRuim.child("diasRuinsConsecutivos").setValue(falseString)
                }
            }else{
                Snackbar.make(btn_voltar_agenda, "Houve um erro ao atualizar os dados", Snackbar.LENGTH_LONG).show()
            }
        }
    }


    private fun irVisualizarPefil(){
        intent = Intent(this, MeuPerfilActivity::class.java)
        intent.putExtra("tipoUsuario", tipoUsuario)
        startActivity(intent)
    }

    private fun goSolicitacoes(){
        intent = Intent(this, SolicitacoesActivity::class.java)
        startActivity(intent)
    }

    private fun goMeuPsicologo(){
        intent = Intent(this, MeuPsicologoActivity::class.java)
        startActivity(intent)
    }

    private fun goAdicionarPaciente() {
        intent = Intent(this, AdicionarPacienteActivity::class.java)
        startActivity(intent)
    }

    private fun usuarioEstaLogado(){
        if(!AuthUtil.usuarioEstaLogado()){
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun irPoliticaPrivacidade(){
        intent = Intent(this, PoliticaDePrivacidadeActivity::class.java)
        startActivity(intent)
    }

    private fun irDiario(intent : Intent){
        startActivity(intent)
    }


}