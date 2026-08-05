package com.whisper.doc

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whisper.doc.sections.howto.HowToSection
import com.whisper.doc.sections.whatif.WhatIfSection
import com.whisper.doc.sections.whatis.WhatIsSection

sealed class DocSection(val title: String) {
    object WhatIs : DocSection("What is?")
    object HowTo : DocSection("How To?")
    object WhatIf : DocSection("What If?")
}

@Composable
fun DocumentationPage(onBack: () -> Unit) {
    var selectedSection by remember { mutableStateOf<DocSection>(DocSection.WhatIs) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val isMobile = maxWidth < 800.dp

        if (isMobile) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Mobile Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF121212))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    ScrollableTabRow(
                        selectedTabIndex = when(selectedSection) {
                            DocSection.WhatIs -> 0
                            DocSection.HowTo -> 1
                            DocSection.WhatIf -> 2
                        },
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedSection == DocSection.WhatIs,
                            onClick = { selectedSection = DocSection.WhatIs },
                            text = { Text("What is?") }
                        )
                        Tab(
                            selected = selectedSection == DocSection.HowTo,
                            onClick = { selectedSection = DocSection.HowTo },
                            text = { Text("How To?") }
                        )
                        Tab(
                            selected = selectedSection == DocSection.WhatIf,
                            onClick = { selectedSection = DocSection.WhatIf },
                            text = { Text("What If?") }
                        )
                    }
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    DocContent(selectedSection)
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                // Sidebar
                Column(
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF121212))
                        .padding(24.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "DOCUMENTATION",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 2.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    DocSidebarItem(DocSection.WhatIs, selectedSection) { selectedSection = it }
                    DocSidebarItem(DocSection.HowTo, selectedSection) { selectedSection = it }
                    DocSidebarItem(DocSection.WhatIf, selectedSection) { selectedSection = it }
                }

                // Content Area
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    DocContent(selectedSection)
                }
            }
        }
    }
}

@Composable
fun DocContent(section: DocSection) {
    when (section) {
        DocSection.WhatIs -> WhatIsSection()
        DocSection.HowTo -> HowToSection()
        DocSection.WhatIf -> WhatIfSection()
    }
}

@Composable
fun DocSidebarItem(
    section: DocSection,
    selectedSection: DocSection,
    onSelect: (DocSection) -> Unit
) {
    val isSelected = section == selectedSection
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect(section) },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
    ) {
        Text(
            text = section.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
