package com.example.dismisstoswipereplace

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.dismisstoswipereplace.ui.theme.DismissToSwipeReplaceTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    val viewModel = MyViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DismissToSwipeReplaceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyCompose(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyCompose(viewModel: MyViewModel) {

    val myListState = viewModel.listFlow.collectAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            viewModel.newList()
        }) {
            Text(text = "Generate New List")
        }

        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(
                items = myListState.value,
                key = { todoItem -> todoItem.id }
            ) { item ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        Log.d("Track", "$item\n${myListState.value.toMutableList()}")
                        viewModel.removeItem(item)
                        true
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    background = {
                        dismissState.dismissDirection ?: return@SwipeToDismiss
                        Box(modifier = Modifier.fillMaxSize().background(Color.Red))
                    },
                    dismissContent = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f).padding(8.dp),
                                text = item.id.toString()
                            )
                            Text(
                                modifier = Modifier.weight(5f).padding(8.dp),
                                text = item.title
                            )
                        }
                    }
                )
            }
        }
    }
}

class MyViewModel : ViewModel() {
    private var myList = mutableStateListOf<MyListItem>()
    val listFlow = MutableStateFlow(myList)

    fun newList() {
        val mutableList = mutableStateListOf<MyListItem>()
        repeat(2) {
            mutableList.add(MyListItem(it, randomWord()))
        }
        myList = mutableList
        listFlow.value = mutableList
    }

    fun removeItem(item: MyListItem) {
        val index = myList.indexOf(item)
        myList.remove(myList[index])
    }

    private fun randomWord(): String {
        val random = Random
        val sb = StringBuilder()
        for (i in 1..random.nextInt(10) + 5) {
            sb.append(('a' + random.nextInt(26)))
        }
        return sb.toString()
    }
}

data class MyListItem(val id: Int, val title: String)
