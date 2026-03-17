package com.vaultkey.ps5keyboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }

        setContent {
            SenseKeyboardApp(
                onEnableKeyboard = { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) },
                onSelectKeyboard = {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showInputMethodPicker()
                }
            )
        }
    }
}

private val BgColor = Color(0xFF0E1020)
private val SurfaceColor = Color(0xFF1A1D2E)
private val CardColor = Color(0xFF232640)
private val AccentColor = Color(0xFF0070D1)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFF8E91A4)

@Composable
fun SenseKeyboardApp(onEnableKeyboard: () -> Unit, onSelectKeyboard: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Header + tabs in one compact row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceColor)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎮", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("SenseKeyboard", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(24.dp))
            listOf("Setup", "Settings", "Controls", "Test").forEachIndexed { i, label ->
                val isSelected = selectedTab == i
                Box(
                    modifier = Modifier
                        .clickable { selectedTab = i }
                        .background(
                            if (isSelected) AccentColor.copy(alpha = 0.15f) else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        label,
                        color = if (isSelected) AccentColor else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            when (selectedTab) {
                0 -> SetupTab(onEnableKeyboard, onSelectKeyboard)
                1 -> SettingsTab()
                2 -> ControlsTab()
                3 -> TestTab()
            }
        }
    }
}

@Composable
fun SetupTab(onEnableKeyboard: () -> Unit, onSelectKeyboard: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StepCard("1", "Enable Keyboard", "Enable SenseKeyboard in input settings",
            "Open Settings", onEnableKeyboard, Modifier.weight(1f))
        StepCard("2", "Select Keyboard", "Set SenseKeyboard as active",
            "Select", onSelectKeyboard, Modifier.weight(1f))
        StepCard("3", "Type!", "Open any text field. Connect DualSense via Bluetooth.",
            null, null, Modifier.weight(1f))
    }
}

@Composable
fun StepCard(step: String, title: String, desc: String, buttonText: String?, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(24.dp).background(AccentColor, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(step, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            if (buttonText != null && onClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(buttonText, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsTab() {
    val context = LocalContext.current
    val settings = remember { KeyboardSettings(context) }
    var selectedLayout by remember { mutableStateOf(settings.keyboardLayout) }
    var bgOpacity by remember { mutableFloatStateOf(settings.bgOpacity.toFloat()) }
    var kbHeight by remember { mutableFloatStateOf(settings.keyboardHeightPercent.toFloat()) }
    var kbWidth by remember { mutableFloatStateOf(settings.keyboardWidthPercent.toFloat()) }
    var suggestions by remember { mutableStateOf(settings.suggestionsEnabled) }
    var vibrate by remember { mutableStateOf(settings.vibrateEnabled) }
    var hWrap by remember { mutableStateOf(settings.horizontalWrap) }
    var vWrap by remember { mutableStateOf(settings.verticalWrap) }
    var dpadSpeed by remember { mutableFloatStateOf(settings.dpadRepeatRate.toFloat()) }
    var selectedPreset by remember { mutableStateOf("ps5") }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Column 1: Layout + Behavior
        Column(modifier = Modifier.weight(1f)) {
            SectionLabel("Keyboard Layout")
            KeyboardLayouts.ALL_LETTER_LAYOUTS.forEach { layout ->
                val isSelected = selectedLayout == layout.id
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AccentColor.copy(alpha = 0.15f) else CardColor
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        .clickable { selectedLayout = layout.id; settings.keyboardLayout = layout.id }
                        .border(if (isSelected) 2.dp else 0.dp, if (isSelected) AccentColor else Color.Transparent, RoundedCornerShape(6.dp))
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Box(modifier = Modifier.size(18.dp).background(AccentColor, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center) {
                                Text("✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(layout.name, color = if (isSelected) AccentColor else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(layout.rows[0], color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            SectionLabel("Behavior")
            SettingSwitch("Word Suggestions", suggestions) { suggestions = it; settings.suggestionsEnabled = it }
            SettingSwitch("Vibrate on Press", vibrate) { vibrate = it; settings.vibrateEnabled = it }
            SettingSwitch("Horizontal Wrap", hWrap) { hWrap = it; settings.horizontalWrap = it }
            SettingSwitch("Vertical Wrap", vWrap) { vWrap = it; settings.verticalWrap = it }
            SettingSlider("D-pad Repeat Speed", dpadSpeed, 30f, 200f, "ms") {
                dpadSpeed = it; settings.dpadRepeatRate = it.toInt()
            }
            Spacer(modifier = Modifier.height(8.dp))
            var showResetConfirm by remember { mutableStateOf(false) }
            if (!showResetConfirm) {
                Button(
                    onClick = { showResetConfirm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3D4E)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) { Text("Reset Word History", fontSize = 10.sp, color = Color(0xFFFF6B6B)) }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            WordSuggestions(context).resetHistory()
                            showResetConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) { Text("Confirm Reset", fontSize = 10.sp, color = Color.White) }
                    Button(
                        onClick = { showResetConfirm = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A3D4E)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) { Text("Cancel", fontSize = 10.sp) }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            var showReset by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (!showReset) { showReset = true } else {
                        context.getSharedPreferences("word_freq", 0).edit().clear().apply()
                        showReset = false
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showReset) Color(0xFFCC3333) else CardColor
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text(
                    if (showReset) "Confirm Reset" else "Reset Word History",
                    fontSize = 10.sp, color = if (showReset) Color.White else TextSecondary
                )
            }
        }

        // Column 2: Visual
        Column(modifier = Modifier.weight(1f)) {
            SectionLabel("Visual Style")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ps5" to "PS5", "dark" to "Dark", "xbox" to "Xbox", "steam" to "Steam").forEach { (id, name) ->
                    val isSel = selectedPreset == id
                    Box(
                        modifier = Modifier
                            .background(if (isSel) AccentColor else CardColor, RoundedCornerShape(6.dp))
                            .clickable { selectedPreset = id; settings.applyPreset(id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .border(if (isSel) 2.dp else 0.dp, if (isSel) AccentColor else Color.Transparent, RoundedCornerShape(6.dp))
                    ) {
                        Text(name, color = if (isSel) Color.White else TextSecondary, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            SectionLabel("Size & Position")
            SettingSlider("Height", kbHeight, 20f, 60f, "%") {
                kbHeight = it; settings.keyboardHeightPercent = it.toInt()
            }
            SettingSlider("Width", kbWidth, 50f, 100f, "%") {
                kbWidth = it; settings.keyboardWidthPercent = it.toInt()
            }
            SettingSlider("Opacity", bgOpacity, 0f, 100f, "%") {
                bgOpacity = it; settings.bgOpacity = it.toInt()
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(text, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun SettingSwitch(label: String, checked: Boolean, onChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(if (checked) AccentColor.copy(alpha = 0.08f) else Color.Transparent, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (checked) TextPrimary else TextSecondary, fontSize = 11.sp,
            fontWeight = if (checked) FontWeight.Medium else FontWeight.Normal)
        Switch(
            checked = checked, onCheckedChange = onChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentColor,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = CardColor
            ),
            modifier = Modifier.height(20.dp)
        )
    }
}

@Composable
fun SettingSlider(label: String, value: Float, min: Float, max: Float, unit: String, onChanged: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextSecondary, fontSize = 11.sp)
            Text("${value.toInt()}$unit", color = AccentColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        Slider(
            value = value, onValueChange = onChanged,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = AccentColor,
                activeTrackColor = AccentColor,
                inactiveTrackColor = CardColor
            ),
            modifier = Modifier.height(24.dp)
        )
    }
}

@Composable
fun ControlsTab() {
    val controls = listOf(
        "✕ Cross" to "Select", "△ Triangle" to "Space", "□ Square" to "Backspace",
        "○ Circle" to "Close", "D-pad" to "Navigate", "L1 / R1" to "Cursor ←→",
        "L2 hold" to "Shift", "R2" to "Enter", "L2+R2" to "New line",
        "L3" to "Symbols", "R3" to "Voice", "Options" to "Done"
    )

    Column {
        Text("Controller Mapping", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        // 2-column grid
        val rows = controls.chunked(2)
        rows.forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pair.forEach { (button, action) ->
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.background(CardColor, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Text(button, color = AccentColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(action, color = TextSecondary, fontSize = 11.sp)
                    }
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun TestTab() {
    var testText by remember { mutableStateOf("") }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Test Input", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Focus the field below to test", color = TextSecondary, fontSize = 11.sp)
            if (testText.isNotEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Text("${testText.length} chars", color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear", color = AccentColor, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { testText = "" })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = testText,
            onValueChange = { testText = it },
            modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
            placeholder = { Text("Type something here...", color = TextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary,
                cursorColor = AccentColor, focusedBorderColor = AccentColor,
                unfocusedBorderColor = Color(0xFF3A3D4E),
                focusedContainerColor = CardColor, unfocusedContainerColor = CardColor
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}
