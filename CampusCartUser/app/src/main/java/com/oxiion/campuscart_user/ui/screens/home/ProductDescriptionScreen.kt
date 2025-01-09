package com.oxiion.campuscart_user.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart_user.data.model.Product

@Composable
fun ProductDetailsScreen(product: Product, onAddToCart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // LazyColumn for scrolling content
        LazyColumn(
            modifier = Modifier.weight(1f) // This makes the column take up all available space
        ) {
            item {
                // Product Name
                Text(
                    text = product.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2, // Limit to 2 lines
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Product Image with Padding
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.25f) // Ensures a square Box
                        .background(Color(0xFFC1C7CE))
                        .padding(16.dp) // Add padding inside the Box
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(product.image),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Product Price
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "₹${product.price}",
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.bodySmall.copy(
                            textDecoration = TextDecoration.LineThrough
                        ),
                        color = Color(0xFF2D3135)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₹${product.discount}",
                        fontSize = 20.sp,
                        color = Color(0xFF000000),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Product Category
                Text(
                    text = product.category,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Product Description
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    shape = MaterialTheme.shapes.medium,
                   colors = CardDefaults.cardColors(
                       containerColor = Color(0xFFD7DADF)
                   ) // Light grey background color
                ) {
                    Text(
                        text = "The perfect companion for students and professionals alike, this premium-quality assignment copy offers a seamless writing experience with its smooth 70 GSM paper that prevents ink bleeding and ensures clean, crisp notes every time. Its sturdy binding guarantees durability, allowing you to flip through pages effortlessly without worrying about tearing or loose sheets. Designed for practicality, this copy includes a convenient margin for annotations and a user-friendly index at the front, making it ideal for organizing projects, assignments, or lecture notes. The eco-friendly cover features a sleek, modern design and is made from recycled materials, reflecting your commitment to sustainability. Whether you’re preparing detailed reports, brainstorming creative ideas, or jotting down quick calculations, this assignment copy is a must-have for anyone who values quality and organization. With 200 ruled pages, it’s perfect for extended use, ensuring you never run out of space when inspiration strikes!",
                        fontSize = 16.sp,
                        maxLines = 4, // Limit to 3 lines
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(16.dp) // Padding inside the card
                    )
                }

            }
        }
        // Spacer to push button to bottom
        Spacer(modifier = Modifier.weight(0.1f))
        Text(
            text = if (product.quantity > 0) "Available" else "Not Available",
            fontSize = 16.sp,
            color = if (product.quantity > 0) Color.Green else Color.Red,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        // Add to Cart Button
        Button(
            onClick = { onAddToCart() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF29638A),
                contentColor = Color.White
            ),
            enabled = product.quantity > 0 // Disable button if product is not available
        ) {
            Text(
                text = "Add to Cart",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}


