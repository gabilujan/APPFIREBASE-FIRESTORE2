package com.example.ativ_firebase

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import com.example.ativ_firebase.ui.theme.Ativ_firebaseTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ativ_firebaseTheme  {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App(db)
                }
            }
        }
    }
}

@Composable
fun App(db: FirebaseFirestore) {
    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var clientes by remember { mutableStateOf(listOf<Map<String, String>>()) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Nome: Gabriela Lujan Alves Severo dos Santos 3DS A", modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(20.dp))



        val image: Painter = painterResource(id = R.drawable.eu)
        Image(painter = image, contentDescription = "Imagem personalizada", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))


        Row(
            Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.fillMaxWidth(0.3f)
            ) {
                Text(text = "Nome:")
            }
            Column {
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Row(
            Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.fillMaxWidth(0.3f)
            ) {
                Text(text = "Telefone:")
            }
            Column {
                TextField(
                    value = telefone,
                    onValueChange = { telefone = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                if (nome.isNotEmpty() && telefone.isNotEmpty()) {
                    val pessoa = hashMapOf(
                        "nome" to nome,
                        "telefone" to telefone
                    )
                    db.collection("Clientes").add(pessoa)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firestore", "Cliente adicionado com sucesso: ${documentReference.id}")
                            nome = ""
                            telefone = ""
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Erro ao adicionar cliente", e)
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cadastrar")
        }

        Spacer(modifier = Modifier.height(20.dp))


        LaunchedEffect(Unit) {
            db.collection("Clientes")
                .get()
                .addOnSuccessListener { documents ->
                    val listaClientes = mutableListOf<Map<String, String>>()
                    for (document in documents) {
                        listaClientes.add(
                            mapOf(
                                "id" to document.id,
                                "nome" to "${document.data["nome"]}",
                                "telefone" to "${document.data["telefone"]}"
                            )
                        )
                    }
                    clientes = listaClientes
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Erro ao buscar clientes: ", exception)
                }
        }


        LazyColumn {
            items(clientes) { cliente ->
                Row(Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(Modifier.weight(0.5f)) {
                        Text(text = "Nome: ${cliente["nome"]}")
                    }
                    Column(Modifier.weight(0.5f)) {
                        Text(text = "Telefone: ${cliente["telefone"]}")
                    }
                }
            }
        }
    }
}