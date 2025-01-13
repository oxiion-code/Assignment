package com.oxiion.campuscart_user.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart_user.data.model.Product
import com.oxiion.campuscart_user.data.model.User
import com.oxiion.campuscart_user.navigation.Screens
import com.oxiion.campuscart_user.ui.components.AppBottomBar
import com.oxiion.campuscart_user.ui.components.AppTopBar
import com.oxiion.campuscart_user.ui.components.LoadingDialogTransParent
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.viewmodels.AuthViewModel
import com.oxiion.campuscart_user.viewmodels.CartViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    navigateToScreen: (route: String) -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val productList by authViewModel.productList.collectAsState()
    val userData by authViewModel.userData.collectAsState()
    var currentScreen by remember { mutableStateOf<ScreenState>(ScreenState.ProductList) }
    var isHomeScreen by remember{ mutableStateOf(true) }
    val isAddToCart = remember{ mutableStateOf(false) }
    val addToCartState by cartViewModel.addToCartState.collectAsState()
    BackHandler {
        if (currentScreen is ScreenState.ProductDetails) {
            currentScreen = ScreenState.ProductList
            isHomeScreen=true
        } else {
            isHomeScreen=false
            navigateBack()
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            userData.address?.let {
                AppTopBar(
                    title = "CampusCart",
                    hostelName = it.hostelNumber,
                    isHomeScreen = isHomeScreen,
                    onBackClick = {
                        if (currentScreen is ScreenState.ProductDetails) {
                            isHomeScreen=true
                            currentScreen = ScreenState.ProductList
                        } else {
                            navigateBack()
                        }
                    }
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                currentScreen = Screens.Home.HomeScreen.route,
                onNavigate = { route ->
                    navigateToScreen(route)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            when (val screen = currentScreen) {
                is ScreenState.ProductList -> ProductList(
                    productList = productList,
                    userData = userData,
                    onProductClick = { product ->
                        isHomeScreen=false
                        currentScreen = ScreenState.ProductDetails(product)
                    }
                )
                is ScreenState.ProductDetails -> ProductDetailsScreen(
                    product = screen.product,
                    onAddToCart = {product->
                        cartViewModel.findCartItemByProductId(
                           productId =  product.id,
                            onResult = {result->
                                if (result==null){
                                    cartViewModel.addToCart(product,1)
                                }else{
                                    Toast.makeText(context,"Already added to cart",Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                    },
                )
            }
        }
        when(addToCartState){
            is DataState.Error -> {
                isAddToCart.value=false
                Toast.makeText(context, (addToCartState as DataState.Error).message,Toast.LENGTH_SHORT).show()
                cartViewModel.resetAddToCartState()
            }
            DataState.Idle -> {

            }
            DataState.Loading -> {
                isAddToCart.value=true
                LoadingDialogTransParent(isAddToCart)
            }
            DataState.Success -> {
                isAddToCart.value=false
                Toast.makeText(context, "Product is added to cart",Toast.LENGTH_LONG).show()
                cartViewModel.resetAddToCartState()
            }
        }
    }
}

@Composable
fun ProductList(
    productList: List<Product>,
    userData: User,
    onProductClick: (Product) -> Unit
) {
    Text(
        text = "Welcome, ${
            userData.address?.fullName?.let { fullName ->
                if (fullName.contains(" ")) fullName.substringBefore(" ") else fullName
            } ?: "Bro"
        }!",
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF001E30)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(productList.chunked(2)) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { product ->
                    ProductCard(
                        product = product,
                        onProductClick = onProductClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


// ScreenState to manage different screens within the HomeScreen
sealed class ScreenState {
   data  object ProductList : ScreenState()
    data class ProductDetails(val product: Product) : ScreenState()
}




@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit
) {
    Card(
        modifier = modifier
            .clickable {
                onProductClick(product)
            }
            .aspectRatio(0.75f), // Maintain consistent aspect ratio
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF29638A))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Card
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f), // Adjusted height-to-width ratio for the image
                colors = CardDefaults.cardColors(containerColor = Color(0xFFCBE6FF))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(product.image ),
                    contentDescription = "Product Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "₹${product.price}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = TextDecoration.LineThrough
                )
            )
            Text(
                text = "₹${product.discount}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
