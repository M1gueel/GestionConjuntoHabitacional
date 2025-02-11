package com.example.parroquia_tienda


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.parroquia_tienda.model.ConjuntoDatabase
import com.example.parroquia_tienda.model.DeudorDao
import com.example.parroquia_tienda.model.DeudorEntity
import com.example.parroquia_tienda.model.EventoDao
import com.example.parroquia_tienda.model.EventoEntity
import com.example.parroquia_tienda.model.ResidenteDao
import com.example.parroquia_tienda.model.ResidenteEntity
import com.example.parroquia_tienda.model.VehiculoDao
import com.example.parroquia_tienda.model.VehiculoEntity
import com.example.parroquia_tienda.ui.theme.ResidenteVehiculoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = ConjuntoDatabase.getDatabase(this)
        val residenteDao = database.residenteDao()
        val vehiculoDao = database.vehiculoDao()

        setContent {
            ResidenteVehiculoTheme {
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryAsState()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        Icons.Default.LocationCity,
                                        contentDescription = "Residentes"
                                    )
                                },
                                label = { Text("Residentes") },
                                selected = currentDestination?.destination?.route == "residentes",
                                onClick = {
                                    navController.navigate("residentes") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        Icons.Default.Event,
                                        contentDescription = "Eventos"
                                    )
                                },
                                label = { Text("Eventos") },
                                selected = currentDestination?.destination?.route == "eventos",
                                onClick = {
                                    navController.navigate("eventos") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        Icons.Default.PersonPin,
                                        contentDescription = "Deudores"
                                    )
                                },
                                label = { Text("Deudores") },
                                selected = currentDestination?.destination?.route == "deudores",
                                onClick = {
                                    navController.navigate("deudores") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "residentes",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Existing routes
                        composable("residentes") {
                            ResidenteList(navController, residenteDao)
                        }

                        // Mostrar lista de vehiculos de un residente específico
                        composable("vehiculos/{residenteId}") { backStackEntry ->
                            val residenteId =
                                backStackEntry.arguments?.getString("residenteId")?.toInt() ?: 0
                            VehiculoList(
                                residenteId,
                                navController,
                                vehiculoDao,
                                residenteDao = residenteDao
                            )
                        }

                        // Crear un nuevo residente
                        composable("crearResidente") {
                            CrearResidenteScreen(residenteDao, navController)
                        }

                        // Editar un residente existente
                        composable("editarResidente/{residenteId}") { backStackEntry ->
                            val residenteId =
                                backStackEntry.arguments?.getString("residenteId")?.toInt() ?: 0
                            EditarResidenteScreen(residenteId, residenteDao, navController)
                        }

                        // Crear nuevo vehiculo en un residente específico
                        composable("crearVehiculo/{residenteId}") { backStackEntry ->
                            val residenteId =
                                backStackEntry.arguments?.getString("residenteId")?.toInt() ?: 0
                            CrearVehiculoScreen(residenteId, vehiculoDao, navController)
                        }

                        // Editar vehiculo existente
                        composable("editarVehiculo/{residenteId}/{residenteId}") { backStackEntry ->
                            val residenteId =
                                backStackEntry.arguments?.getString("residenteId")?.toInt() ?: 0
                            val vehiculoId =
                                backStackEntry.arguments?.getString("residenteId")?.toInt() ?: 0
                            EditarVehiculoScreen(vehiculoId, residenteId, vehiculoDao, navController)
                        }

                        composable("eventos") {
                            EventoList(navController, database.eventoDao())
                        }

                        composable("deudores") {
                            DeudoresList(navController, database.deudorDao())
                        }

                        // Crear Evento Screen
                        composable("crearEvento") {
                            CrearEventoScreen(
                                eventoDao = database.eventoDao(),
                                navController = navController
                            )
                        }

                        // Editar Evento Screen
                        composable("editarEvento/{eventoId}") { backStackEntry ->
                            val eventoId = backStackEntry.arguments?.getString("eventoId")?.toInt() ?: 0
                            EditarEventoScreen(
                                eventoId = eventoId,
                                eventoDao = database.eventoDao(),
                                navController = navController
                            )
                        }

                        // Crear Deudor Screen
                        composable("crearDeudor") {
                            CrearDeudorScreen(
                                deudorDao = database.deudorDao(),
                                navController = navController
                            )
                        }

                        // Editar Deudor Screen
                        composable("editarDeudor/{deudorId}") { backStackEntry ->
                            val deudorId = backStackEntry.arguments?.getString("deudorId")?.toInt() ?: 0
                            EditarDeudorScreen(
                                deudorId = deudorId,
                                deudorDao = database.deudorDao(),
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ResidenteList(navController: NavHostController, residenteDao: ResidenteDao) {
        val residentes by residenteDao.getAllResidentes().collectAsState(initial = emptyList())
        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Lista de Residentes",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
                ) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF445B8F) // Color Azul
            )) },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("crearResidente") }) {
                    Text("+")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(residentes) { residente ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            // Fila superior con icono e información
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Residente",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 16.dp)
                                )

                                // Información del residente
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    //InfoResidente("ID", residente.id.toString())
                                    InfoResidente("Nombre", residente.nombre)
                                    InfoResidente("Número de casa", residente.numeroDeCasa)
                                    InfoResidente("Fecha de Registro", residente.fechaRegistro)
                                    InfoResidente(
                                        "Estado de vivienda",
                                        if (residente.esPropietario) "Propietario" else "Arrendatario"
                                    )
                                    InfoResidente(
                                        "Número de residentes",
                                        "${residente.numeroDeMiembros} residentes"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Botones en la parte inferior
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = { navController.navigate("vehiculos/${residente.id}") }) {
                                    Text("Ver")
                                }
                                Button(onClick = { navController.navigate("editarResidente/${residente.id}") }) {
                                    Text("Editar")
                                }
                                Button(
                                    onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            residenteDao.deleteResidente(residente)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun VehiculoList(
        residenteId: Int,
        navController: NavHostController,
        vehiculoDao: VehiculoDao,
        residenteDao: ResidenteDao
    ) {
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        val vehiculos by vehiculoDao.getVehiculosByResidente(residenteId)
            .collectAsState(initial = emptyList())
        val residente by residenteDao.getResidenteById(residenteId)
            .collectAsState(initial = null)

        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = residente?.let { "Vehículos de ${it.nombre}" } ?: "Lista de Vehículos",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, "Regresar", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF445B8F) // Color Azul
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("crearVehiculo/$residenteId") }
                ) {
                    Icon(Icons.Default.Add, "Agregar vehículo")
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isLoading && vehiculos.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else if (vehiculos.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay vehículos registrados",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = vehiculos,
                            key = { it.id }
                        ) { vehiculo ->
                            VehiculoCard(
                                vehiculo = vehiculo,
                                onEditClick = {
                                    navController.navigate("editarVehiculo/$residenteId/${vehiculo.id}")
                                },
                                onDeleteClick = {
                                    scope.launch {
                                        try {
                                            withContext(Dispatchers.IO) {
                                                vehiculoDao.deleteVehiculo(vehiculo)
                                            }
                                        } catch (e: Exception) {
                                            error = "Error al eliminar: ${e.localizedMessage}"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                // Mostrar error si existe
                error?.let { errorMessage ->
                    Snackbar(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { error = null }) {
                                Text("CERRAR")
                            }
                        }
                    ) {
                        Text(errorMessage)
                    }
                }
            }
        }
    }

    @Composable
    private fun VehiculoCard(
        vehiculo: VehiculoEntity,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Vehículo",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 16.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = vehiculo.marca,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Modelo: ${vehiculo.modelo}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Placa: ${vehiculo.placa}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Estacionamiento N°: ${vehiculo.numeroDeEstacionamiento}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onEditClick
                    ) {
                        Text("Editar")
                    }
                    Button(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }

    // Componente auxiliar para mostrar la información en formato etiqueta-valor
    @Composable
    fun InfoResidente(etiqueta: String, valor: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "$etiqueta: ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearResidenteScreen(residenteDao: ResidenteDao, navController: NavHostController) {
        var nombre by remember { mutableStateOf("") }
        var numeroDeCasa by remember { mutableStateOf("") }
        var fechaRegistro by remember { mutableStateOf("") }
        var esPropietario by remember { mutableStateOf(false) }
        var numeroDeMiembros by remember { mutableStateOf("") }

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Nuevo Residente",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del Residente") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = numeroDeCasa,
                            onValueChange = { numeroDeCasa = it },
                            label = { Text("Numero de casa") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = fechaRegistro,
                            onValueChange = { fechaRegistro = it },
                            label = { Text("Fecha de Registro (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = numeroDeMiembros,
                            onValueChange = { numeroDeMiembros = it },
                            label = { Text("Número de miembros") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = esPropietario,
                                onCheckedChange = { esPropietario = it }
                            )
                            Text("¿Es propietario?")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    residenteDao.insertResidente(
                                        ResidenteEntity(
                                            nombre = nombre,
                                            numeroDeCasa = numeroDeCasa,
                                            fechaRegistro = fechaRegistro,
                                            esPropietario = esPropietario,
                                            numeroDeMiembros = numeroDeMiembros.toIntOrNull() ?: 0
                                        )
                                    )
                                }
                                navController.popBackStack()
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditarResidenteScreen(
        residenteId: Int,
        residenteDao: ResidenteDao,
        navController: NavHostController
    ) {
        val residente by residenteDao.getResidenteById(residenteId).collectAsState(initial = null)

        if (residente == null) {
            CircularProgressIndicator()
            return
        }

        var nombre by remember { mutableStateOf(residente!!.nombre) }
        var numeroDeCasa by remember { mutableStateOf(residente!!.numeroDeCasa) }
        var fechaRegistro by remember { mutableStateOf(residente!!.fechaRegistro) }
        var esPropietario by remember { mutableStateOf(residente!!.esPropietario) }
        var numeroDeMiembros by remember { mutableStateOf(residente!!.numeroDeMiembros.toString()) }

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Editar Residente",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = numeroDeCasa,
                            onValueChange = { numeroDeCasa = it },
                            label = { Text("Número de casa") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = fechaRegistro,
                            onValueChange = { fechaRegistro = it },
                            label = { Text("Fecha de Registro (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = numeroDeMiembros,
                            onValueChange = { numeroDeMiembros = it },
                            label = { Text("Número de Residentes") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = esPropietario,
                                onCheckedChange = { esPropietario = it }
                            )
                            Text("¿Es Propietario?")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                val residenteEditada = residente!!.copy(
                                    nombre = nombre,
                                    numeroDeCasa = numeroDeCasa,
                                    fechaRegistro = fechaRegistro,
                                    esPropietario = esPropietario,
                                    numeroDeMiembros = numeroDeMiembros.toIntOrNull() ?: 0
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    residenteDao.updateResidente(residenteEditada)
                                }
                                navController.popBackStack()
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearVehiculoScreen(
        residenteId: Int,
        vehiculoDao: VehiculoDao,
        navController: NavHostController
    ) {
        var marca by remember { mutableStateOf("") }
        var modelo by remember { mutableStateOf("") }
        var placa by remember { mutableStateOf("") }
        var numeroDeEstacionamiento by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }

        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Nuevo Vehiculo",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = marca,
                            onValueChange = {
                                error = null
                                marca = it
                            },
                            label = { Text("Marca del Vehiculo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = modelo,
                            onValueChange = {
                                error = null
                                modelo = it
                            },
                            label = { Text("Modelo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = placa,
                            onValueChange = {
                                error = null
                                placa = it
                            },
                            label = { Text("Placa") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = numeroDeEstacionamiento,
                            onValueChange = {
                                error = null
                                numeroDeEstacionamiento = it.filter { char -> char.isDigit() }
                            },
                            label = { Text("Numero de estacionamiento") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        error?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            // Validate fields
                                            when {
                                                marca.isBlank() -> {
                                                    error = "La marca es requerida"
                                                    return@launch
                                                }
                                                modelo.isBlank() -> {
                                                    error = "El modelo es requerido"
                                                    return@launch
                                                }
                                                placa.isBlank() -> {
                                                    error = "La placa es requerida"
                                                    return@launch
                                                }
                                                numeroDeEstacionamiento.isBlank() -> {
                                                    error = "El número de estacionamiento es requerido"
                                                    return@launch
                                                }
                                            }

                                            // Parse parking number
                                            val parkingNumber = numeroDeEstacionamiento.toIntOrNull()
                                            if (parkingNumber == null) {
                                                error = "Número de estacionamiento inválido"
                                                return@launch
                                            }

                                            // Create and insert vehicle
                                            val vehiculo = VehiculoEntity(
                                                residenteId = residenteId,
                                                marca = marca.trim(),
                                                modelo = modelo.trim(),
                                                placa = placa.trim(),
                                                numeroDeEstacionamiento = parkingNumber
                                            )

                                            withContext(Dispatchers.IO) {
                                                vehiculoDao.insertVehiculo(vehiculo)
                                            }
                                            navController.popBackStack()
                                        } catch (e: Exception) {
                                            error = "Error al guardar: ${e.localizedMessage ?: "Error desconocido"}"
                                        }
                                    }
                                }
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditarVehiculoScreen(
        vehiculoId: Int,
        residenteId: Int,
        vehiculoDao: VehiculoDao,
        navController: NavHostController
    ) {
        val vehiculo by vehiculoDao.getVehiculoById(vehiculoId).collectAsState(initial = null)

        if (vehiculo == null) {
            CircularProgressIndicator()
            return
        }

        var marca by remember { mutableStateOf(vehiculo!!.marca) }
        var modelo by remember { mutableStateOf(vehiculo!!.modelo) }
        var placa by remember { mutableStateOf(vehiculo!!.placa) }
        var numeroDeEstacionamiento by remember { mutableStateOf(vehiculo!!.numeroDeEstacionamiento.toString()) }

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Editar Vehiculo",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = marca,
                            onValueChange = { marca = it },
                            label = { Text("Marca del Vehiculo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = modelo,
                            onValueChange = { modelo = it },
                            label = { Text("Modelo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = placa,
                            onValueChange = { placa = it },
                            label = { Text("Placa") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = numeroDeEstacionamiento,
                            onValueChange = { numeroDeEstacionamiento = it },
                            label = { Text("Numero de estacionamiento") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                val vehiculoEditado = vehiculo!!.copy(
                                    marca = marca,
                                    modelo = modelo,
                                    placa = placa,
                                    numeroDeEstacionamiento = numeroDeEstacionamiento.toIntOrNull()
                                        ?: vehiculo!!.numeroDeEstacionamiento
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    vehiculoDao.updateVehiculo(vehiculoEditado)
                                }
                                navController.popBackStack()
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EventoList(navController: NavHostController, eventoDao: EventoDao) {
        val eventos by eventoDao.getAllEventos().collectAsState(initial = emptyList())

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Lista de Eventos",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            ) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF445B8F) // Color Azul
                )) },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("crearEvento") }) {
                    Text("+")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(eventos) { evento ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventNote,
                                    contentDescription = "Evento",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 16.dp)
                                )
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    InfoResidente("Nombre", evento.nombre)
                                    InfoResidente("Responsable", evento.personaResponsable)
                                    InfoResidente("Fecha", evento.fechaEvento)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = { navController.navigate("editarEvento/${evento.id}") }) {
                                    Text("Editar")
                                }
                                Button(onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        eventoDao.deleteEvento(evento)
                                    }
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DeudoresList(navController: NavHostController, deudorDao: DeudorDao) {
        val deudores by deudorDao.getAllDeudores().collectAsState(initial = emptyList())

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Lista de Deudores",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            ) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF445B8F) // Color Azul
                )) },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate("crearDeudor") }) {
                    Text("+")
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deudores) { deudor ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Deudor",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 16.dp)
                                )
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    InfoResidente("Nombre", deudor.nombre)
                                    InfoResidente("Meses de Deuda", deudor.mesesDeuda.toString())
                                    InfoResidente("Deuda Total", String.format("$ %.2f", deudor.deudaTotal))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = { navController.navigate("editarDeudor/${deudor.id}") }) {
                                    Text("Editar")
                                }
                                Button(onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        deudorDao.deleteDeudor(deudor)
                                    }
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearEventoScreen(
        eventoDao: EventoDao,
        navController: NavHostController
    ) {
        var nombre by remember { mutableStateOf("") }
        var personaResponsable by remember { mutableStateOf("") }
        var fechaEvento by remember { mutableStateOf("") }

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Nuevo Evento",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del Evento") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = personaResponsable,
                            onValueChange = { personaResponsable = it },
                            label = { Text("Persona Responsable") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = fechaEvento,
                            onValueChange = { fechaEvento = it },
                            label = { Text("Fecha del Evento (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    eventoDao.insertEvento(
                                        EventoEntity(
                                            nombre = nombre,
                                            personaResponsable = personaResponsable,
                                            fechaEvento = fechaEvento
                                        )
                                    )
                                }
                                navController.popBackStack()
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditarEventoScreen(
        eventoId: Int,
        eventoDao: EventoDao,
        navController: NavHostController
    ) {
        val evento by eventoDao.getEventoById(eventoId).collectAsState(initial = null)

        if (evento == null) {
            CircularProgressIndicator()
            return
        }

        var nombre by remember { mutableStateOf(evento!!.nombre) }
        var personaResponsable by remember { mutableStateOf(evento!!.personaResponsable) }
        var fechaEvento by remember { mutableStateOf(evento!!.fechaEvento) }

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Editar Evento",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del Evento") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = personaResponsable,
                            onValueChange = { personaResponsable = it },
                            label = { Text("Persona Responsable") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = fechaEvento,
                            onValueChange = { fechaEvento = it },
                            label = { Text("Fecha del Evento (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                val eventoEditado = evento!!.copy(
                                    nombre = nombre,
                                    personaResponsable = personaResponsable,
                                    fechaEvento = fechaEvento
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    eventoDao.updateEvento(eventoEditado)
                                }
                                navController.popBackStack()
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CrearDeudorScreen(
        deudorDao: DeudorDao,
        navController: NavHostController
    ) {
        var nombre by remember { mutableStateOf("") }
        var mesesDeuda by remember { mutableStateOf("") }
        var deudaTotal by remember { mutableStateOf("") }

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Nuevo Deudor",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del Deudor") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = mesesDeuda,
                            onValueChange = { mesesDeuda = it },
                            label = { Text("Meses de Deuda") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = deudaTotal,
                            onValueChange = { deudaTotal = it },
                            label = { Text("Deuda Total") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    deudorDao.insertDeudor(
                                        DeudorEntity(
                                            nombre = nombre,
                                            mesesDeuda = mesesDeuda.toIntOrNull() ?: 0,
                                            deudaTotal = deudaTotal.toDoubleOrNull() ?: 0.0
                                        )
                                    )
                                }
                                navController.popBackStack()
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditarDeudorScreen(
        deudorId: Int,
        deudorDao: DeudorDao,
        navController: NavHostController
    ) {
        val deudor by deudorDao.getDeudorById(deudorId).collectAsState(initial = null)

        if (deudor == null) {
            CircularProgressIndicator()
            return
        }

        var nombre by remember { mutableStateOf(deudor!!.nombre) }
        var mesesDeuda by remember { mutableStateOf(deudor!!.mesesDeuda.toString()) }
        var deudaTotal by remember { mutableStateOf(deudor!!.deudaTotal.toString()) }

        Scaffold(
            topBar = { SmallTopAppBar(title = { Text("Editar Deudor",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center) }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(320.dp)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre del Deudor") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = mesesDeuda,
                            onValueChange = { mesesDeuda = it },
                            label = { Text("Meses de Deuda") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = deudaTotal,
                            onValueChange = { deudaTotal = it },
                            label = { Text("Deuda Total") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                val deudorEditado = deudor!!.copy(
                                    nombre = nombre,
                                    mesesDeuda = mesesDeuda.toIntOrNull() ?: 0,
                                    deudaTotal = deudaTotal.toDoubleOrNull() ?: 0.0
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    deudorDao.updateDeudor(deudorEditado)
                                }
                                navController.popBackStack()
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}