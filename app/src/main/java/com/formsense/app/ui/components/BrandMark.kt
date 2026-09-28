package com.formsense.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.formsense.app.ui.theme.FormBlue

@Composable
fun BrandMark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(width = 32.dp, height = 36.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 28.dp, height = 16.dp)
                    .background(FormBlue, RoundedCornerShape(5.dp, 12.dp, 12.dp, 5.dp)),
            )
            Box(
                modifier = Modifier
                    .size(11.dp)
                    .offset(x = 1.dp, y = 22.dp)
                    .background(Color(0xFF72AEFF), CircleShape),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "FormSense",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
