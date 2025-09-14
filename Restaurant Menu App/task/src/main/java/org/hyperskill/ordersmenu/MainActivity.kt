package org.hyperskill.ordersmenu

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperskill.ordersmenu.theme.PlayOrdersMenuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayOrdersMenuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    OrdersPage()
                }
            }
        }
    }
}

@Preview
@Composable
fun OrdersPage() {
    val recipeNames = listOf(
        "Fettuccine",
        "Risotto",
        "Gnocchi",
        "Spaghetti",
        "Lasagna",
        "Steak Parmigiana"
    )

    val context = LocalContext.current
    val recipesNameToStockAmount = remember {
        mutableStateMapOf(
            "Fettuccine" to 5,
            "Risotto" to 6,
            "Gnocchi" to 4,
            "Spaghetti" to 3,
            "Lasagna" to 5,
            "Steak Parmigiana" to 2
        )
    }

    val orders = remember {
        recipesNameToStockAmount.mapValues { mutableIntStateOf(0) }.toMutableMap()
    }

    fun makeOrder() {
        val anyOrdered = orders.any { it.value.intValue > 0 }
        if (anyOrdered) {
            val orderedText = buildString {
                append("Ordered:")
                recipeNames.forEach { item ->
                    val quantity = orders[item]
                    if (quantity != null && quantity.intValue > 0) {
                        append("\n==> $item: ${quantity.intValue}")
                        // Decrease stock
                        recipesNameToStockAmount[item] = (recipesNameToStockAmount[item] ?: 0) - quantity.intValue
                        // Reset order count
                        quantity.intValue = 0
                    }
                }
            }

            Log.d("DEBUG", orderedText)

            Toast.makeText(
                context,
                orderedText,
                Toast.LENGTH_LONG
            ).show()
        }
    }



    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title()
        recipeNames.forEach { recipe ->
            MenuItem(recipe, recipesNameToStockAmount[recipe] ?: 0, orders[recipe] ?: mutableIntStateOf(0))
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = ::makeOrder,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Make Order", color = Color.White, fontSize = 24.sp)
            }
        }
    }

}


@Composable
fun Title() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(fontSize = 48.sp, text = "Orders Menu")
    }
}

@Composable
fun MenuItem(recipeName: String, amountStock: Int, amountOrdered: MutableState<Int>) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        Text(
            textAlign = TextAlign.Start,
            text = recipeName,
            fontSize = 24.sp,
            color = if (amountOrdered.value >= amountStock) Color.Red else Color.Black
        )
        Text(
            "+", fontSize = 24.sp, modifier = Modifier
                .clickable {
                    amountOrdered.value = (amountOrdered.value + 1).coerceIn(0, amountStock)
                }
                .padding(horizontal = 16.dp))
        Text(text = "${amountOrdered.value}", fontSize = 24.sp)
        Text(
            "-", fontSize = 24.sp, modifier = Modifier
                .clickable {
                    amountOrdered.value = (amountOrdered.value - 1).coerceIn(0, amountStock)
                }
                .padding(horizontal = 16.dp))
    }
}