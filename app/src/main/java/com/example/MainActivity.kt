package com.example

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.horizontalScroll
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TourBooking
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: TourViewModel by viewModels {
        TourViewModelFactory(applicationContext as Application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel)
            }
        }
    }
}

enum class NavigationTab(val route: String, val title: String, val icon: ImageVector) {
    HOME("home", "Home", Icons.Default.Home),
    ATTRACTIONS("attractions", "Darshan", Icons.Default.Explore),
    PACKAGES("packages", "Packages", Icons.Default.Tour),
    BOOKINGS("bookings", "Bookings", Icons.Default.ReceiptLong),
    WORSHIP("worship", "Worship", Icons.Default.TempleHindu),
    AI_GUIDE("ai_guide", "AI Guide", Icons.Default.AutoAwesome)
}

@Composable
fun OfferMarqueeBanner(lang: AppLanguage) {
    val offerText = if (lang == AppLanguage.HINDI) {
        "🔥 सावन स्पेशल ऑफर: मात्र ₹5100 में विद्वान ब्राह्मणों द्वारा भव्य रुद्राभिषेक कराएं! 🪷 | 12 ज्योतिर्लिंग दर्शन एवं गंगा आरती यात्रा पैकेज पर 30% की विशेष छूट! 🌸 | अभी कॉल करें: +91 8423340923 📞 "
    } else {
        "🔥 Savan Special Offer: Conduct sacred Rudrabhishek starting at just ₹5100! 🪷 | Special 30% discount on 12 Temples Darshan & Ganga Aarti Tour Package! 🌸 | Call us now to book: +91 8423340923 📞 "
    }

    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.maxValue) {
        if (scrollState.maxValue > 0) {
            while (true) {
                scrollState.animateScrollTo(
                    value = scrollState.maxValue,
                    animationSpec = tween(
                        durationMillis = scrollState.maxValue * 12,
                        easing = LinearEasing
                    )
                )
                delay(400)
                scrollState.scrollTo(0)
                delay(400)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFE65100), // Rich Saffron Orange
                        Color(0xFFFF8F00), // Saffron Amber
                        Color(0xFFE65100)  // Rich Saffron Orange
                    )
                )
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "badge_blink")
        val badgeAlpha by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "badge_alpha"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // "OFFER" Saffron Badge
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .padding(end = 10.dp)
                    .alpha(badgeAlpha)
            ) {
                Text(
                    text = if (lang == AppLanguage.HINDI) "विशेष ऑफर" else "SPECIAL OFFER",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            // Marquee Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(scrollState, enabled = false)
            ) {
                Text(
                    text = offerText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TourViewModel) {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    var showBookingDialog by remember { mutableStateOf(false) }
    var showCopyrightDialog by remember { mutableStateOf(false) }
    var selectedTourPackage by remember { mutableStateOf<TourPackage?>(null) }
    
    val context = LocalContext.current
    val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val savedPlaces by viewModel.allSavedPlaces.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                .clickable {
                                    viewModel.toggleLanguage()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.ENGLISH) "En" else "हिं",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TempleHindu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Locales.getString("app_title", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.TempleHindu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    actions = {
                        IconButton(
                            onClick = {
                                val shareMessage = if (currentLanguage == AppLanguage.HINDI) {
                                    "यात्रा काशी धाम (Yatra Kashi Dham) - वाराणसी की पावन और आध्यात्मिक यात्रा का अनुभव करें। 12 प्रमुख मंदिरों के दर्शन, पवित्र गंगा आरती, नौका विहार, बनारसी खान-पान और पर्सनल एआई ट्रैवल गाइड की सुविधा! ऐप देखें: https://ais-pre-2xdki2rjevthotzsrlklc3-504542751224.asia-southeast1.run.app"
                                } else {
                                    "Yatra Kashi Dham - Experience the spiritual journey of Varanasi. Explore 12 major temples, sacred Ganga Aarti, boat ride, Banarasi food, and a personal AI Travel Guide! Visit app: https://ais-pre-2xdki2rjevthotzsrlklc3-504542751224.asia-southeast1.run.app"
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, if (currentLanguage == AppLanguage.HINDI) "ऐप साझा करें" else "Share App")
                                context.startActivity(shareIntent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
                OfferMarqueeBanner(currentLanguage)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    NavigationTab.values().forEach { tab ->
                        val tabTitle = when (tab) {
                            NavigationTab.HOME -> Locales.getString("tab_home", currentLanguage)
                            NavigationTab.ATTRACTIONS -> Locales.getString("tab_attractions", currentLanguage)
                            NavigationTab.PACKAGES -> Locales.getString("tab_packages", currentLanguage)
                            NavigationTab.BOOKINGS -> Locales.getString("tab_bookings", currentLanguage)
                            NavigationTab.WORSHIP -> Locales.getString("tab_worship", currentLanguage)
                            NavigationTab.AI_GUIDE -> Locales.getString("tab_ai_guide", currentLanguage)
                        }
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tabTitle
                                )
                            },
                            label = {
                                Text(
                                    text = tabTitle,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        letterSpacing = (-0.3).sp
                                    ),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.route}")
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCopyrightDialog = true }
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Copyright,
                        contentDescription = "Copyright",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "२०२६ यात्रा काशी धाम | सर्वाधिकार सुरक्षित 🙏" else "2026 Yatra Kashi Dham | All Rights Reserved 🙏",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                NavigationTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onBookTourClick = { tour ->
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:8423340923"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not launch dialer", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onNavigateToTab = { tab ->
                        selectedTab = tab
                    }
                )
                NavigationTab.ATTRACTIONS -> AttractionsScreen(
                    viewModel = viewModel
                )
                NavigationTab.PACKAGES -> PackagesScreen(
                    viewModel = viewModel,
                    onBookTourClick = { tour ->
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:8423340923"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not launch dialer", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                NavigationTab.BOOKINGS -> BookingsSavedScreen(
                    viewModel = viewModel
                )
                NavigationTab.WORSHIP -> WorshipScreen(
                    viewModel = viewModel
                )
                NavigationTab.AI_GUIDE -> AiGuideScreen(
                    viewModel = viewModel
                )
            }

            if (showBookingDialog && selectedTourPackage != null) {
                BookingDialog(
                    tourPackage = selectedTourPackage!!,
                    onDismiss = {
                        showBookingDialog = false
                        selectedTourPackage = null
                    },
                    onConfirm = { name, phone, date, persons ->
                        viewModel.bookTour(
                            tourName = selectedTourPackage!!.name,
                            tourPrice = selectedTourPackage!!.price,
                            userName = name,
                            userPhone = phone,
                            travelDate = date,
                            personsCount = persons,
                            onSuccess = {
                                showBookingDialog = false
                                selectedTourPackage = null
                                Toast.makeText(
                                    context,
                                    if (currentLanguage == AppLanguage.HINDI) "यात्रा सफलतापूर्वक बुक हो गई! 🙏 'बुकिंग्स' टैब देखें।" else "Yatra booked successfully! 🙏 Check 'Bookings' tab.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    },
                    lang = currentLanguage
                )
            }

            if (showCopyrightDialog) {
                CopyrightDialog(
                    onDismiss = { showCopyrightDialog = false },
                    lang = currentLanguage
                )
            }
        }
    }
}

// --- HOME SCREEN ---
data class HomeTabItem(
    val nameEn: String,
    val nameHi: String,
    val category: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun HomeScreen(
    viewModel: TourViewModel,
    onBookTourClick: (TourPackage) -> Unit,
    onNavigateToTab: (NavigationTab) -> Unit
) {
    var selectedPlaceForDetails by remember { mutableStateOf<Place?>(null) }
    val savedPlaces by viewModel.allSavedPlaces.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val placesList by viewModel.placesList.collectAsStateWithLifecycle()
    val tourPackages by viewModel.tourPackages.collectAsStateWithLifecycle()

    val homeAllowedIds = listOf("dashashwamedh_ghat", "kashi_vishwanath", "durga_kund", "ramnagar_fort", "sarnath")
    val homePlacesList = remember(placesList) {
        val mapped = placesList.associateBy { it.id }
        homeAllowedIds.mapNotNull { mapped[it] }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Image Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_yatra_kashi_dham_banner),
                    contentDescription = "Yatra Kashi Dham Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Introduction text
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "काशी में आपका स्वागत है 🕉️" else "Welcome to Kashi 🕉️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Locales.getString("welcome_desc_kashi", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Visual Category Tab Bar (Aarti Time, Temples, Ghats, Heritage, Food)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "त्वरित श्रेणियाँ 🌟" else "Quick Categories 🌟",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val homeTabs = listOf(
                        HomeTabItem(
                            nameEn = "Aarti Time",
                            nameHi = "आरती समय",
                            category = "Aarti",
                            icon = Icons.Default.AccessTime,
                            color = Color(0xFFFF5722)
                        ),
                        HomeTabItem(
                            nameEn = "Temples",
                            nameHi = "मंदिर",
                            category = "Temple",
                            icon = Icons.Default.TempleHindu,
                            color = Color(0xFFFF9800)
                        ),
                        HomeTabItem(
                            nameEn = "Ghats",
                            nameHi = "घाट",
                            category = "Ghat",
                            icon = Icons.Default.DirectionsBoat,
                            color = Color(0xFF03A9F4)
                        ),
                        HomeTabItem(
                            nameEn = "Heritage",
                            nameHi = "धरोहर",
                            category = "Heritage",
                            icon = Icons.Default.AccountBalance,
                            color = Color(0xFF9C27B0)
                        ),
                        HomeTabItem(
                            nameEn = "Food",
                            nameHi = "भोजन",
                            category = "Food",
                            icon = Icons.Default.RestaurantMenu,
                            color = Color(0xFF4CAF50)
                        )
                    )

                    homeTabs.forEach { tab ->
                        val label = if (currentLanguage == AppLanguage.HINDI) tab.nameHi else tab.nameEn
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.setSelectedCategory(tab.category)
                                    onNavigateToTab(NavigationTab.ATTRACTIONS)
                                }
                                .padding(vertical = 8.dp)
                                .testTag("home_category_tab_${tab.category}"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(tab.color.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = label,
                                    tint = tab.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 1,
                                fontSize = 11.sp,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Spiritual Attractions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "पवित्र काशी का अन्वेषण करें 🚩" else "Explore Sacred Kashi 🚩",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Render all the Places of Interest!
        items(homePlacesList) { place ->
            PlaceHomeCard(
                place = place,
                onClick = { selectedPlaceForDetails = place }
            )
        }

        // Section Title: Tour Packages
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "विशेष यात्रा पैकेज ⛵" else "Special Tour Packages ⛵",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.Tour,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        items(tourPackages.filter { it.id != "one_night_two_days" }) { pack ->
            val isSaved = savedPlaces.any { it.id == pack.id }
            TourPackageCard(
                pack = pack,
                onBookClick = { onBookTourClick(pack) },
                isSaved = isSaved,
                onSaveToggle = {
                    val placeRep = Place(
                        id = pack.id,
                        name = pack.name,
                        type = if (currentLanguage == AppLanguage.HINDI) "यात्रा पैकेज" else "Tour Package",
                        description = pack.description,
                        highlights = "",
                        bestTime = "",
                        location = "",
                        imageUrl = "",
                        imageResId = 0
                    )
                    viewModel.toggleSavedPlace(placeRep)
                },
                lang = currentLanguage
            )
        }
    }

    // Detail Dialog Overlay
    if (selectedPlaceForDetails != null) {
        val place = selectedPlaceForDetails!!
        val isSaved = savedPlaces.any { it.id == place.id }
        
        // Find if there's an associated package to suggest booking!
        val associatedPackage = when {
            place.id.contains("ghat") -> tourPackages.firstOrNull { it.id == "morning_boat" || it.id == "evening_aarti" }
            place.id.contains("vishwanath") || place.id.contains("sankat") -> tourPackages.firstOrNull { it.id == "temple_walk" }
            place.id.contains("sarnath") -> tourPackages.firstOrNull { it.id == "sarnath_day" }
            place.id.contains("food") -> tourPackages.firstOrNull { it.id == "food_walk" }
            else -> tourPackages.firstOrNull()
        } ?: tourPackages.first()

        PlaceDetailDialog(
            place = place,
            onDismiss = { selectedPlaceForDetails = null },
            isSaved = isSaved,
            onSaveToggle = { viewModel.toggleSavedPlace(place) },
            onBookRelatedTour = {
                selectedPlaceForDetails = null
                onBookTourClick(associatedPackage)
            },
            lang = currentLanguage
        )
    }
}

// --- PACKAGES SCREEN ---
@Composable
fun PackagesScreen(
    viewModel: TourViewModel,
    onBookTourClick: (TourPackage) -> Unit
) {
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val tourPackages by viewModel.tourPackages.collectAsStateWithLifecycle()
    val savedPlaces by viewModel.allSavedPlaces.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("packages_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "विशेष यात्रा पैकेज ⛵" else "Special Tour Packages ⛵",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) 
                            "काशी के सर्वश्रेष्ठ दर्शनीय स्थलों की सैर और प्रामाणिक अनुभव" 
                        else 
                            "Explore Kashi with curated authentic tours & experienced local guides.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }

        items(tourPackages) { pack ->
            val isSaved = savedPlaces.any { it.id == pack.id }
            TourPackageCard(
                pack = pack,
                onBookClick = { onBookTourClick(pack) },
                isSaved = isSaved,
                onSaveToggle = {
                    val placeRep = Place(
                        id = pack.id,
                        name = pack.name,
                        type = if (currentLanguage == AppLanguage.HINDI) "यात्रा पैकेज" else "Tour Package",
                        description = pack.description,
                        highlights = "",
                        bestTime = "",
                        location = "",
                        imageUrl = "",
                        imageResId = 0
                    )
                    viewModel.toggleSavedPlace(placeRep)
                },
                lang = currentLanguage
            )
        }
    }
}

fun openMapWithAddress(context: android.content.Context, address: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(address)}")).apply {
            setPackage("com.google.android.apps.maps")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}"))
            context.startActivity(webIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Could not open map", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun PlaceHomeCard(
    place: Place,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("place_home_card_${place.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Large Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = if (place.imageResId != 0) place.imageResId else place.imageUrl,
                    contentDescription = place.name,
                    placeholder = painterResource(id = R.drawable.img_kashi_ghat),
                    error = painterResource(id = R.drawable.img_kashi_ghat),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Small Category Badge overlay
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = place.type,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Text Content immediately below
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                val context = LocalContext.current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            openMapWithAddress(context, place.location)
                        }
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = place.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceDetailDialog(
    place: Place,
    onDismiss: () -> Unit,
    onSaveToggle: () -> Unit,
    isSaved: Boolean,
    onBookRelatedTour: () -> Unit,
    lang: AppLanguage
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp)
                .testTag("place_detail_dialog_${place.id}"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    AsyncImage(
                        model = if (place.imageResId != 0) place.imageResId else place.imageUrl,
                        contentDescription = place.name,
                        placeholder = painterResource(id = R.drawable.img_kashi_ghat),
                        error = painterResource(id = R.drawable.img_kashi_ghat),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Save/Bookmark toggle on top left
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (lang == AppLanguage.HINDI) "सहेजें" else "Save place",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Category Badge and Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = place.type,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Location/Address Section
                    val context = LocalContext.current
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                openMapWithAddress(context, place.location)
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = if (lang == AppLanguage.HINDI) "पूरा पता" else "Full Address",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (lang == AppLanguage.HINDI) "📍 पूरा पता (मानचित्र पर देखें)" else "📍 Full Address (View on Map)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = place.location,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Timings Section
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Timings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (lang == AppLanguage.HINDI) "समय और दर्शन का सर्वोत्तम समय" else "Timings & Best Time to Visit",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = place.bestTime,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // About Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (lang == AppLanguage.HINDI) "इस स्थान के बारे में" else "About this Place",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = place.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Highlights Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (lang == AppLanguage.HINDI) "मुख्य आकर्षण / विशेष अनुभव" else "Highlights / Special Experiences",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "✨ " + place.highlights.replace(", ", "\n✨ "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Book Related Tour Button
                    Button(
                        onClick = onBookRelatedTour,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.HINDI) "कॉल करें" else "Call Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TourPackageDescription(description: String, lang: AppLanguage) {
    val lines = description.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        lines.forEach { line ->
            when {
                // Header sections
                line.startsWith("📅") || line.contains("Day 1:") || line.contains("Day 2:") || line.contains("दिन 1:") || line.contains("दिन 2:") -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = line,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                line.startsWith("🍲") || line.contains("Food & Delicacies") || line.contains("खान-पान शामिल") || line.contains("Inclusion of Meals") -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = line,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                line.startsWith("💳") || line.contains("Terms & Conditions") || line.contains("पैकेज की शर्तें") -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = line,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
                line.startsWith("✅") || line.contains("What's Included") || line.contains("Inclusions") || line.contains("शामिल है") -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFE8F5E9), // Light green-ish
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
                line.startsWith("❌") || line.contains("What's Not Included") || line.contains("Exclusions") || line.contains("शामिल नहीं") -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFFEEBEE), // Light red-ish
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }
                
                // Regular bullet points or time schedule items
                else -> {
                    val cleanLine = when {
                        line.startsWith("•") -> line.substring(1).trim()
                        line.startsWith("-") -> line.substring(1).trim()
                        else -> line
                    }
                    
                    // Detect if there's a colon or a dash to highlight
                    val separators = listOf(':', '–', '-')
                    val separatorIndex = cleanLine.indexOfFirst { it in separators }
                    if (separatorIndex != -1) {
                        val highlightPart = cleanLine.substring(0, separatorIndex).trim()
                        val detailsPart = cleanLine.substring(separatorIndex + 1).trim()
                        val separator = cleanLine[separatorIndex]
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Icon based on line content
                            val bulletIcon = when {
                                cleanLine.contains("AM") || cleanLine.contains("PM") || cleanLine.contains("बजे") -> Icons.Default.AccessTime
                                cleanLine.contains("Ghat") || cleanLine.contains("घाट") -> Icons.Default.DirectionsBoat
                                cleanLine.contains("Temple") || cleanLine.contains("मंदिर") -> Icons.Default.TempleHindu
                                cleanLine.contains("Breakfast") || cleanLine.contains("नाश्ता") || cleanLine.contains("Lunch") || cleanLine.contains("भोजन") || cleanLine.contains("Dinner") || cleanLine.contains("भोजन") -> Icons.Default.RestaurantMenu
                                else -> Icons.Default.ChevronRight
                            }
                            
                            Icon(
                                imageVector = bulletIcon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .padding(start = 0.dp, top = 2.dp, end = 6.dp, bottom = 0.dp)
                                    .size(16.dp)
                            )
                            
                            val annotatedText = buildAnnotatedString {
                                // Bold part
                                withStyle(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    append(highlightPart)
                                    append(" $separator ")
                                }
                                // Regular details
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                ) {
                                    append(detailsPart)
                                }
                            }
                            Text(
                                text = annotatedText,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 18.sp
                            )
                        }
                    } else {
                        // Regular text line
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 2.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .padding(start = 0.dp, top = 4.dp, end = 6.dp, bottom = 0.dp)
                                    .size(12.dp)
                            )
                            Text(
                                text = cleanLine,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TourPackageCard(
    pack: TourPackage,
    onBookClick: () -> Unit,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    lang: AppLanguage = AppLanguage.ENGLISH
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tour_card_${pack.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (pack.iconName) {
                        "wb_sunny" -> Icons.Default.WbSunny
                        "temple_hindu" -> Icons.Default.TempleHindu
                        "flare" -> Icons.Default.Flare
                        "explore" -> Icons.Default.Explore
                        "restaurant_menu" -> Icons.Default.RestaurantMenu
                        else -> Icons.Default.DirectionsBoat
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = pack.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "⏳ ${pack.duration}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = androidx.compose.ui.graphics.Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFEF5350))
                    ) {
                        Text(
                            text = if (lang == AppLanguage.HINDI) "🔥 30% छूट" else "🔥 30% OFF",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier.testTag("bookmark_package_${pack.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) {
                                if (lang == AppLanguage.HINDI) "सहेजें नहीं" else "Unsave package"
                            } else {
                                if (lang == AppLanguage.HINDI) "सहेजें" else "Save package"
                            },
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            TourPackageDescription(pack.description, lang)

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Key Highlights:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Bullet points for highlights
            pack.highlights.forEach { highlight ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = highlight,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("book_button_${pack.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone, 
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.HINDI) "कॉल करें" else "Call Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = {
                        try {
                            val msg = if (lang == AppLanguage.HINDI) {
                                "नमस्ते, मैं आपके यात्रा पैकेज '${pack.name}' के बारे में जानकारी प्राप्त करना चाहता हूँ।"
                            } else {
                                "Hello, I would like to inquire about the tour package: ${pack.name}"
                            }
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/918423340923?text=${Uri.encode(msg)}")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("whatsapp_button_${pack.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat, 
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (lang == AppLanguage.HINDI) "व्हाट्सएप" else "WhatsApp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// --- ATTRACTIONS SCREEN ---
@Composable
fun AttractionsScreen(viewModel: TourViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val attractionsPlacesList by viewModel.attractionsPlacesList.collectAsStateWithLifecycle()
    val categories = listOf("All", "Ghat", "Temple", "Food", "Heritage")
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    var selectedPlaceForDetailsDialog by remember { mutableStateOf<Place?>(null) }

    val filteredPlaces = remember(selectedCategory, attractionsPlacesList) {
        val baseList = if (selectedCategory == "All") {
            attractionsPlacesList
        } else if (selectedCategory == "Aarti") {
            attractionsPlacesList.filter { 
                it.type.equals("Ghat", ignoreCase = true) || 
                it.type == Locales.getString("filter_ghat", AppLanguage.HINDI)
            }
        } else {
            attractionsPlacesList.filter { 
                it.type.equals(selectedCategory, ignoreCase = true) || 
                it.type == when (selectedCategory) {
                    "Ghat" -> Locales.getString("filter_ghat", AppLanguage.HINDI)
                    "Temple" -> Locales.getString("filter_temple", AppLanguage.HINDI)
                    "Heritage" -> Locales.getString("filter_heritage", AppLanguage.HINDI)
                    "Food" -> Locales.getString("filter_food", AppLanguage.HINDI)
                    else -> selectedCategory
                }
            }
        }

        if (selectedCategory == "Food") {
            val freeBhandaras = setOf("annapurna_bhandara", "annapurna_free_mess", "iskcon_bhandara")
            baseList.sortedBy { if (it.id in freeBhandaras) 0 else 1 }
        } else {
            baseList
        }
    }

    // Observing saved places from database
    val savedPlaces by viewModel.allSavedPlaces.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("attractions_screen_root")
    ) {
        // Visual Category Tab Bar (Aarti Time, Temples, Ghats, Heritage, Food)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val darshanTabs = listOf(
                HomeTabItem(
                    nameEn = "Aarti Time",
                    nameHi = "आरती समय",
                    category = "Aarti",
                    icon = Icons.Default.AccessTime,
                    color = Color(0xFFFF5722)
                ),
                HomeTabItem(
                    nameEn = "Temples",
                    nameHi = "मंदिर",
                    category = "Temple",
                    icon = Icons.Default.TempleHindu,
                    color = Color(0xFFFF9800)
                ),
                HomeTabItem(
                    nameEn = "Ghats",
                    nameHi = "घाट",
                    category = "Ghat",
                    icon = Icons.Default.DirectionsBoat,
                    color = Color(0xFF03A9F4)
                ),
                HomeTabItem(
                    nameEn = "Heritage",
                    nameHi = "धरोहर",
                    category = "Heritage",
                    icon = Icons.Default.AccountBalance,
                    color = Color(0xFF9C27B0)
                ),
                HomeTabItem(
                    nameEn = "Food",
                    nameHi = "भोजन",
                    category = "Food",
                    icon = Icons.Default.RestaurantMenu,
                    color = Color(0xFF4CAF50)
                )
            )

            darshanTabs.forEach { tab ->
                val label = if (currentLanguage == AppLanguage.HINDI) tab.nameHi else tab.nameEn
                val isSelected = selectedCategory == tab.category

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (isSelected) {
                                viewModel.setSelectedCategory("All")
                            } else {
                                viewModel.setSelectedCategory(tab.category)
                            }
                        }
                        .padding(vertical = 8.dp)
                        .testTag("filter_chip_${tab.category}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) tab.color else tab.color.copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = label,
                            tint = if (isSelected) Color.White else tab.color,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) tab.color else MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        maxLines = 1,
                        fontSize = 11.sp,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }

        // List of places
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filteredPlaces) { place ->
                val isSaved = savedPlaces.any { it.id == place.id }
                AttractionCard(
                    place = place,
                    isSaved = isSaved,
                    onSaveToggle = { viewModel.toggleSavedPlace(place) },
                    lang = currentLanguage,
                    onClick = { selectedPlaceForDetailsDialog = place }
                )
            }
        }
    }

    // Rich detailed Darshan popup with Summer, Winter, and Aarti timings
    if (selectedPlaceForDetailsDialog != null) {
        val place = selectedPlaceForDetailsDialog!!
        val isSaved = savedPlaces.any { it.id == place.id }
        DarshanDetailDialog(
            place = place,
            onDismiss = { selectedPlaceForDetailsDialog = null },
            isSaved = isSaved,
            onSaveToggle = { viewModel.toggleSavedPlace(place) },
            lang = currentLanguage
        )
    }
}

@Composable
fun AttractionCard(
    place: Place,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    lang: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("place_card_${place.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = place.type,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Blinking Free/Paid Badge for Food items
                        val isFood = place.type.contains("Food", ignoreCase = true) || place.type.contains("भोजन")
                        if (isFood) {
                            val freeBhandaras = setOf("annapurna_bhandara", "annapurna_free_mess", "iskcon_bhandara")
                            val isFree = place.id in freeBhandaras
                            
                            val infiniteTransition = rememberInfiniteTransition(label = "blink")
                            val blinkAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "blink_alpha"
                            )
                            
                            val badgeColor = if (isFree) Color(0xFF2E7D32) else Color(0xFFC62828)
                            val badgeText = if (isFree) {
                                if (lang == AppLanguage.HINDI) "● निःशुल्क भंडारा" else "● FREE BHANDARA"
                            } else {
                                if (lang == AppLanguage.HINDI) "● सशुल्क भोजन" else "● PAID FOOD"
                            }

                            Box(
                                modifier = Modifier
                                    .alpha(blinkAlpha)
                                    .background(
                                        badgeColor.copy(alpha = 0.12f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = badgeColor
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Favorite Bookmark Button
                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier.testTag("bookmark_${place.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isSaved) {
                            if (lang == AppLanguage.HINDI) "सहेजें नहीं" else "Unsave place"
                        } else {
                            if (lang == AppLanguage.HINDI) "सहेजें" else "Save place"
                        },
                        tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = place.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(8.dp))

            // Details Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == AppLanguage.HINDI) "✨ सर्वोत्तम समय" else "✨ Best Time",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = place.bestTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val context = LocalContext.current
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            openMapWithAddress(context, place.location)
                        }
                        .padding(4.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.HINDI) "📍 स्थान (नक्शा)" else "📍 Location (Map)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = place.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// --- AI GUIDE SCREEN ---
@Composable
fun AiGuideScreen(viewModel: TourViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    var textState by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Sample Quick Queries (Localized)
    val quickQueries = if (currentLanguage == AppLanguage.HINDI) {
        listOf(
            "गंगा आरती का समय क्या है?",
            "2-दिवसीय यात्रा कार्यक्रम?",
            "सर्वोत्तम बनारसी साड़ी की दुकानें?",
            "वाराणसी के स्थानीय व्यंजन क्या हैं?"
        )
    } else {
        listOf(
            "Ganga Aarti timings?",
            "2-day trip itinerary?",
            "Best Banarasi Saree shops?",
            "Varanasi local street food?"
        )
    }

    // Scroll to the bottom whenever a new message arrives
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_guide_root")
    ) {
        // Chat List
        Box(modifier = Modifier.weight(1f)) {
            if (chatMessages.isEmpty()) {
                // Empty state centered helper
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages) { message ->
                        ChatBubble(message = message)
                    }
                    if (isChatLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) "काशी एआई लिख रहा है..." else "Kashi AI is writing...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Suggestions Chips & Prompt Row
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Suggestion horizontal list
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickQueries) { query ->
                    SuggestionChip(
                        onClick = {
                            viewModel.sendChatMessage(query)
                        },
                        label = {
                            Text(
                                text = query,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            enabled = true
                        )
                    )
                }
            }

            // Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clear Chat button
                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = Locales.getString("clear_chat", currentLanguage),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // TextField
                OutlinedTextField(
                    value = textState,
                    onValueChange = { textState = it },
                    placeholder = { Text(text = Locales.getString("chat_hint", currentLanguage)) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(max = 100.dp)
                        .testTag("chat_text_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (textState.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            CircleShape
                        )
                        .clickable(enabled = textState.isNotBlank()) {
                            viewModel.sendChatMessage(textState)
                            textState = ""
                            keyboardController?.hide()
                        }
                        .testTag("chat_send_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (textState.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        val bubbleShape = if (message.isUser) {
            RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
        } else {
            RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
        }

        val containerColor = if (message.isUser) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        }

        val textColor = if (message.isUser) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        }

        Card(
            shape = bubbleShape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// --- BOOKINGS & SAVED SCREEN ---
@Composable
fun BookingsSavedScreen(viewModel: TourViewModel) {
    val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val savedPlaces by viewModel.allSavedPlaces.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val attractionsPlacesList by viewModel.attractionsPlacesList.collectAsStateWithLifecycle()
    val tourPackages by viewModel.tourPackages.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var selectedScreenTab by remember { mutableStateOf(0) }
    var selectedSavedPlaceForDetails by remember { mutableStateOf<Place?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bookings_screen_root")
    ) {
        // Tab indicator
        TabRow(
            selectedTabIndex = selectedScreenTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedScreenTab == 0,
                onClick = { selectedScreenTab = 0 },
                text = { 
                    val tabText = if (currentLanguage == AppLanguage.HINDI) "मेरी बुकिंग्स (${bookings.size})" else "My Booked Tours (${bookings.size})"
                    Text(tabText, fontWeight = FontWeight.Bold) 
                },
                modifier = Modifier.testTag("tab_bookings")
            )
            Tab(
                selected = selectedScreenTab == 1,
                onClick = { selectedScreenTab = 1 },
                text = { 
                    val tabText = if (currentLanguage == AppLanguage.HINDI) "पसंदीदा स्थान (${savedPlaces.size})" else "Saved Places (${savedPlaces.size})"
                    Text(tabText, fontWeight = FontWeight.Bold) 
                },
                modifier = Modifier.testTag("tab_saved")
            )
        }

        if (selectedScreenTab == 0) {
            // Bookings List
            if (bookings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "अभी तक कोई बुकिंग नहीं मिली!" else "No tour bookings yet!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "मुख्य पृष्ठ टैब देखें और किसी भी आध्यात्मिक, मंदिर या स्ट्रीट फूड पैकेज को बुक करें।" else "Explore the 'Home' tab and book any spiritual, temple, or street food package.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { booking ->
                        BookingItemRow(
                            booking = booking, 
                            onCancel = { viewModel.cancelBooking(booking.id) },
                            lang = currentLanguage
                        )
                    }
                }
            }
        } else {
            // Saved Places
            if (savedPlaces.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "आपकी पसंदीदा सूची खाली है!" else "Your saved places is empty!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सहेजने के लिए अपने पसंदीदा स्थानों पर दिल (Heart) या बुकमार्क दबाएं!" else "Bookmark or heart your favorite places to save them here!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedPlaces) { saved ->
                        // Match with static place structure to load complete details
                        val completePlace = attractionsPlacesList.firstOrNull { it.id == saved.id }
                        val completePackage = tourPackages.firstOrNull { it.id == saved.id }
                        if (completePlace != null) {
                            AttractionCard(
                                place = completePlace,
                                isSaved = true,
                                onSaveToggle = { viewModel.toggleSavedPlace(completePlace) },
                                lang = currentLanguage,
                                onClick = { selectedSavedPlaceForDetails = completePlace }
                            )
                        } else if (completePackage != null) {
                            TourPackageCard(
                                pack = completePackage,
                                onBookClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:8423340923"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Could not launch dialer", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                isSaved = true,
                                onSaveToggle = {
                                    val placeRep = Place(
                                        id = completePackage.id,
                                        name = completePackage.name,
                                        type = if (currentLanguage == AppLanguage.HINDI) "यात्रा पैकेज" else "Tour Package",
                                        description = completePackage.description,
                                        highlights = "",
                                        bestTime = "",
                                        location = "",
                                        imageUrl = "",
                                        imageResId = 0
                                    )
                                    viewModel.toggleSavedPlace(placeRep)
                                },
                                lang = currentLanguage
                            )
                        } else {
                            // Fallback minimal card if not matched
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = saved.name, fontWeight = FontWeight.Bold)
                                        Text(text = saved.type, style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { viewModel.toggleSavedPlace(Place(saved.id, saved.name, saved.type, saved.description, "", "", "", "")) }) {
                                        Icon(imageVector = Icons.Filled.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Showing the rich timings details dialog for the saved places as well
    if (selectedSavedPlaceForDetails != null) {
        val place = selectedSavedPlaceForDetails!!
        val isSaved = savedPlaces.any { it.id == place.id }
        DarshanDetailDialog(
            place = place,
            onDismiss = { selectedSavedPlaceForDetails = null },
            isSaved = isSaved,
            onSaveToggle = { viewModel.toggleSavedPlace(place) },
            lang = currentLanguage
        )
    }
}

@Composable
fun BookingItemRow(booking: TourBooking, onCancel: () -> Unit, lang: AppLanguage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_row_${booking.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = booking.tourName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (lang == AppLanguage.HINDI) "📅 तिथि: ${booking.travelDate}" else "📅 Date: ${booking.travelDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFF4CAF50).copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.HINDI) "पुष्टि की गई" else "Confirmed",
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (lang == AppLanguage.HINDI) {
                            "यात्री: ${booking.userName} (${booking.personsCount} व्यक्ति)"
                        } else {
                            "Traveler: ${booking.userName} (${booking.personsCount} Pers.)"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (lang == AppLanguage.HINDI) "फ़ोन: ${booking.userPhone}" else "Phone: ${booking.userPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (lang == AppLanguage.HINDI) {
                            "भुगतान: ₹${booking.tourPrice * booking.personsCount}"
                        } else {
                            "Paid: ₹${booking.tourPrice * booking.personsCount}"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Cancel Button (Touch targets >= 48dp)
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("cancel_booking_${booking.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel booking",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (lang == AppLanguage.HINDI) "रद्द करें" else "Cancel",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- BOOKING DIALOG FORM ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    tourPackage: TourPackage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, date: String, persons: Int) -> Unit,
    lang: AppLanguage
) {
    var nameState by remember { mutableStateOf("") }
    var phoneState by remember { mutableStateOf("") }
    var dateState by remember { mutableStateOf("") }
    var personsState by remember { mutableStateOf(1) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("booking_form_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Text(
                    text = if (lang == AppLanguage.HINDI) "अपनी यात्रा बुक करें" else "Book Your Yatra",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = tourPackage.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Name TextField
                OutlinedTextField(
                    value = nameState,
                    onValueChange = { nameState = it },
                    label = { Text(if (lang == AppLanguage.HINDI) "आपका पूरा नाम" else "Your Full Name") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_input_name"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone TextField
                OutlinedTextField(
                    value = phoneState,
                    onValueChange = { phoneState = it },
                    label = { Text(if (lang == AppLanguage.HINDI) "संपर्क फ़ोन" else "Contact Phone") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_input_phone"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Date TextField
                OutlinedTextField(
                    value = dateState,
                    onValueChange = { dateState = it },
                    label = { Text(if (lang == AppLanguage.HINDI) "यात्रा की तिथि (जैसे, 2026-07-15)" else "Travel Date (e.g., 2026-07-15)") },
                    leadingIcon = { Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_input_date"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Persons Counter (Touch targets are >= 48dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.HINDI) "व्यक्तियों की संख्या" else "Number of Persons",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Decrease Button (Touch targets >= 48dp)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable(enabled = personsState > 1) { personsState-- }
                                .testTag("btn_decrement"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease count",
                                tint = if (personsState > 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }

                        Text(
                            text = personsState.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .testTag("person_count_text")
                        )

                        // Increase Button (Touch targets >= 48dp)
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { personsState++ }
                                .testTag("btn_increment"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase count",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total Price Calculation Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (lang == AppLanguage.HINDI) "कुल मूल्य:" else "Total Price:", fontWeight = FontWeight.Bold)
                        Text(
                            text = "₹${tourPackage.price * personsState}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.height(48.dp)) {
                        Text(if (lang == AppLanguage.HINDI) "रद्द करें" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nameState.isBlank() || phoneState.isBlank() || dateState.isBlank()) {
                                // Simple local validation fallback or Toast handled in code safely
                            } else {
                                onConfirm(nameState, phoneState, dateState, personsState)
                            }
                        },
                        enabled = nameState.isNotBlank() && phoneState.isNotBlank() && dateState.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("booking_dialog_confirm")
                    ) {
                        Text(if (lang == AppLanguage.HINDI) "बुकिंग की पुष्टि करें" else "Confirm booking", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CopyrightDialog(onDismiss: () -> Unit, lang: AppLanguage) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = if (lang == AppLanguage.HINDI) "यात्रा काशी धाम" else "Yatra Kashi Dham",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (lang == AppLanguage.HINDI) "© २०२६ वाराणसी टूर एंड ट्रेवेल्स। सर्वाधिकार सुरक्षित।" else "© 2026 Varanasi Tour & Travels. All Rights Reserved.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                
                Text(
                    text = if (lang == AppLanguage.HINDI) {
                        "यह एप्लिकेशन वाराणसी (काशी) आने वाले सभी तीर्थयात्रियों, पर्यटकों और भक्तों की सुविधा के लिए विकसित किया गया है।"
                    } else {
                        "This application is developed to assist pilgrims, tourists, and devotees visiting Varanasi (Kashi) with seamless information, curated tour bookings, and local assistance."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (lang == AppLanguage.HINDI) "संस्करण: १.०.० (स्थानीय डेटाबेस)" else "Version: 1.0.0 (Local Database & AI)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (lang == AppLanguage.HINDI) "ठीक है" else "Close")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

data class WorshipItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val price: Int,
    val descEn: String,
    val descHi: String,
    val isService: Boolean,
    val icon: ImageVector,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorshipScreen(viewModel: TourViewModel) {
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryTab by remember { mutableStateOf(0) } // 0: All, 1: Prasad & Items, 2: Puja Rituals
    var selectedWorshipItem by remember { mutableStateOf<WorshipItem?>(null) }
    var showWorshipBookingDialog by remember { mutableStateOf(false) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                searchQuery = results[0]
            }
        }
    }

    val worshipItems = remember {
        listOf(
            WorshipItem(
                id = "ganga_jal",
                nameEn = "Holy Ganga Jal (Gangotri Origin)",
                nameHi = "पवित्र गंगा जल (गंगोत्री उद्गम)",
                price = 51,
                descEn = "Pure and sacred Gangajal collected directly from the holy Ganges river in Kashi. Sanctified and filtered.",
                descHi = "काशी में पतित पावनी माँ गंगा से सीधे संगृहीत पवित्र गंगाजल। वैज्ञानिक रूप से छनित और पूर्णतः शुद्ध।",
                isService = false,
                icon = Icons.Default.WaterDrop,
                imageRes = R.drawable.img_worship_gangajal
            ),
            WorshipItem(
                id = "vishwanath_prasad",
                nameEn = "Kashi Vishwanath Temple Mahaprasad",
                nameHi = "श्री काशी विश्वनाथ मंदिर महाप्रसाद",
                price = 251,
                descEn = "Original dry fruits and peda prasad directly from Sri Kashi Vishwanath Temple, along with protective thread and photo.",
                descHi = "श्री काशी विश्वनाथ मंदिर का मूल सूखा प्रसाद, पेड़ा, रक्षा सूत्र, अभिमंत्रित बेलपत्र और दिव्य चित्र।",
                isService = false,
                icon = Icons.Default.Stars,
                imageRes = R.drawable.img_worship_vishwanath_prasad
            ),
            WorshipItem(
                id = "sankat_mochan_prasad",
                nameEn = "Sankat Mochan Besan Laddu Prasad",
                nameHi = "संकटमोचन हनुमान बेसन लड्डू प्रसाद",
                price = 151,
                descEn = "Traditional pure ghee besan laddus offered to Sri Sankat Mochan Hanuman ji, renowned for blessings and positive energy.",
                descHi = "प्रसिद्ध श्री संकट मोचन हनुमान जी को अर्पित शुद्ध देशी घी के स्वादिष्ट बेसन के लड्डू (प्रसाद)।",
                isService = false,
                icon = Icons.Default.Eco,
                imageRes = R.drawable.img_worship_sankat_mochan_prasad
            ),
            WorshipItem(
                id = "rudraksha",
                nameEn = "Sacred Panchmukhi Rudraksha Bead",
                nameHi = "दिव्य पंचमुखी रुद्राक्ष मनका",
                price = 108,
                descEn = "An authentic, energized Panchmukhi Rudraksha bead from Varanasi, blessed for peace of mind, focus, and health.",
                descHi = "काशी में अभिमंत्रित एवं सिद्ध किया हुआ पवित्र पंचमुखी रुद्राक्ष मनका जो मन की शांति और एकाग्रता बढ़ाता है।",
                isService = false,
                icon = Icons.Default.WorkspacePremium,
                imageRes = R.drawable.img_worship_rudraksha
            ),
            WorshipItem(
                id = "kaal_bhairav_bhasma",
                nameEn = "Kaal Bhairav Raksha Bhasma & Thread",
                nameHi = "काल भैरव रक्षा भस्म व काला धागा",
                price = 51,
                descEn = "Sacred protection ash (vibhuti) and custom black wrist thread blessed in Kashi's Kotwal Kaal Bhairav Temple.",
                descHi = "काशी के कोतवाल बाबा काल भैरव मंदिर से अभिमंत्रित पवित्र रक्षा भस्म (विभूति) और सिद्ध काला सुरक्षा धागा।",
                isService = false,
                icon = Icons.Default.Shield,
                imageRes = R.drawable.img_worship_kaal_bhairav_bhasma
            ),
            WorshipItem(
                id = "ganga_sand",
                nameEn = "Sacred Ganges Clay/Sand",
                nameHi = "गंगा जी की पवित्र मिट्टी व रेत",
                price = 51,
                descEn = "Pure spiritual sand collected from the banks of the sacred Ganges in Kashi, used for home worship and altars.",
                descHi = "काशी की पावन उत्तरवाहिनी गंगा नदी के तटों की पवित्र रेतीली मिट्टी, जो देव-पूजन व घर के मंदिर की शुद्धि हेतु उत्तम है।",
                isService = false,
                icon = Icons.Default.Terrain,
                imageRes = R.drawable.img_worship_ganga_sand
            ),
            WorshipItem(
                id = "cow_upla",
                nameEn = "Pure Desi Cow Dung Cakes (Pack of 11)",
                nameHi = "शुद्ध देसी गाय का उपला (गोबर कंडा)",
                price = 91,
                descEn = "100% natural and sun-dried cow dung cakes, made from pure desi cow breed for traditional home havans and pujas.",
                descHi = "पारंपरिक वैदिक यज्ञ, हवन और घर के वातावरण को पवित्र बनाने के लिए तैयार किए गए प्राकृतिक गोबर के उपले (कंडे)।",
                isService = false,
                icon = Icons.Default.Circle,
                imageRes = R.drawable.img_worship_cow_upla
            ),
            WorshipItem(
                id = "mango_wood",
                nameEn = "Dry Mango Wood (Aam ki Lakdi - 1 Kg)",
                nameHi = "हवन हेतु आम की सूखी लकड़ी (१ किग्रा)",
                price = 121,
                descEn = "Premium quality dried mango wood sticks, carefully harvested and sized for smooth, low-smoke, and pious home havan rituals.",
                descHi = "शुद्ध और सूखी आम की लकड़ियाँ (समिधा), जो हवन-यज्ञ को बिना धुएं के श्रद्धापूर्वक संपन्न करने के लिए उपयुक्त हैं।",
                isService = false,
                icon = Icons.Default.Forest,
                imageRes = R.drawable.img_worship_mango_wood
            ),
            WorshipItem(
                id = "rudrabhishek",
                nameEn = "Vedic Rudrabhishek Puja Booking",
                nameHi = "वैदिक रुद्राभिषेक पूजन बुकिंग",
                price = 1100,
                descEn = "Complete Vedic Rudrabhishek performed at temple or your room by expert Kashi Vedic Brahmins. Includes materials list and pure chanting.",
                descHi = "काशी के विद्वान वैदिक ब्राह्मणों द्वारा विधि-विधान से संपन्न कराया जाने वाला भव्य रुद्राभिषेक पूजन। इसमें पूजन सामग्री मार्गदर्शन शामिल है।",
                isService = true,
                icon = Icons.Default.TempleHindu,
                imageRes = R.drawable.img_worship_rudrabhishek
            ),
            WorshipItem(
                id = "maha_rudrabhishek",
                nameEn = "Maha Rudrabhishek Puja (11 Vedic Priests)",
                nameHi = "महा रुद्राभिषेक पूजन (एकादश ब्राह्मण)",
                price = 3100,
                descEn = "Grand scale elaborate Rudrabhishek performed by multiple high-ranking Vedic pandits of Kashi. Brings supreme peace and prosperity.",
                descHi = "काशी के ११ विद्वान ब्राह्मणों द्वारा सामूहिक रूप से अत्यंत फलदायी एवं कल्याणकारी महा रुद्राभिषेक पूजन और महाकाल महामृत्युंजय पाठ।",
                isService = true,
                icon = Icons.Default.VolunteerActivism,
                imageRes = R.drawable.img_worship_maha_rudrabhishek
            ),
            WorshipItem(
                id = "savan_special_rudrabhishek",
                nameEn = "Savan Special Grand Maha Rudrabhishek",
                nameHi = "सावन विशेष भव्य महा रुद्राभिषेक पूजन",
                price = 5100,
                descEn = "A high-purity, highly auspicious grand Savan Special Rudrabhishek with special Bilvapatra archana, performed by 11 Vedic priests of Kashi on any day during the holy month of Shravan (Savan). Blesses your entire family with health, wealth, and spiritual protection.",
                descHi = "पवित्र सावन मास में काशी के ११ उच्च कोटि के विद्वान वैदिक ब्राह्मणों द्वारा आपके नाम व गोत्र से संपन्न किया जाने वाला भव्य 'सावन विशेष' महा रुद्राभिषेक पूजन। इसमें विशेष ११०० बेलपत्र अर्चन, महामृत्युंजय जाप और रुद्राष्टाध्यायी पाठ शामिल है।",
                isService = true,
                icon = Icons.Default.TempleHindu,
                imageRes = R.drawable.img_worship_maha_rudrabhishek
            )
        )
    }

    // Filter list
    val filteredItems = remember(searchQuery, selectedCategoryTab, currentLanguage) {
        worshipItems.filter { item ->
            val matchesSearch = if (currentLanguage == AppLanguage.HINDI) {
                item.nameHi.contains(searchQuery, ignoreCase = true) || item.descHi.contains(searchQuery, ignoreCase = true)
            } else {
                item.nameEn.contains(searchQuery, ignoreCase = true) || item.descEn.contains(searchQuery, ignoreCase = true)
            }
            val matchesTab = when (selectedCategoryTab) {
                1 -> !item.isService // Prasad & Items
                2 -> item.isService // Puja Rituals
                else -> true // All
            }
            matchesSearch && matchesTab
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
        ) {
            Column {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "श्री काशी पूजा सेवा व प्रसाद" else "Sri Kashi Pooja & Prasad Services",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) 
                        "गंगा जल, सिद्ध प्रसाद व प्रामाणिक वैदिक पूजन सेवाएँ।" 
                    else 
                        "Holy Gangajal, Temple Prasad & authentic Vedic rituals.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        // Search Bar (Lower Height, Positioned Higher, Speech-enabled)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
                .height(48.dp)
                .testTag("worship_search_input"),
            placeholder = {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "गंगा जल, प्रसाद, पूजन खोजें..." else "Search Gangajal, prasad, puja...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (currentLanguage == AppLanguage.HINDI) "hi-IN" else "en-US")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, if (currentLanguage == AppLanguage.HINDI) "बोलकर खोजें..." else "Speak to search...")
                            }
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Voice Search not supported", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
        )

        // Filter Tabs (Chips)
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryTab,
            edgePadding = 16.dp,
            containerColor = Color.Transparent,
            divider = {},
            indicator = {}
        ) {
            val categories = if (currentLanguage == AppLanguage.HINDI) {
                listOf("सभी सेवाएं", "पावन प्रसाद व सामग्री", "पूजा संकल्प बुकिंग")
            } else {
                listOf("All Services", "Prasad & Items", "Puja Ritual Booking")
            }

            categories.forEachIndexed { index, title ->
                val isSelected = selectedCategoryTab == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedCategoryTab = index },
                    modifier = Modifier.padding(end = 8.dp),
                    text = {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Items List
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TempleHindu,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "कोई परिणाम नहीं मिला" else "No items found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredItems) { item ->
                    WorshipItemCard(
                        item = item,
                        lang = currentLanguage,
                        onBookClick = {
                            selectedWorshipItem = item
                            showWorshipBookingDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showWorshipBookingDialog && selectedWorshipItem != null) {
        WorshipBookingDialog(
            item = selectedWorshipItem!!,
            lang = currentLanguage,
            onDismiss = {
                showWorshipBookingDialog = false
                selectedWorshipItem = null
            },
            onConfirm = { name, phone, detail, qty ->
                val typeLabelEn = if (selectedWorshipItem!!.isService) "Puja Booking" else "Prasad/Material"
                val typeLabelHi = if (selectedWorshipItem!!.isService) "पूजन संकल्प बुकिंग" else "प्रसाद व सामग्री आर्डर"
                val fullName = if (currentLanguage == AppLanguage.HINDI) {
                    "[$typeLabelHi] ${selectedWorshipItem!!.nameHi}"
                } else {
                    "[$typeLabelEn] ${selectedWorshipItem!!.nameEn}"
                }

                viewModel.bookTour(
                    tourName = fullName,
                    tourPrice = selectedWorshipItem!!.price,
                    userName = name,
                    userPhone = phone,
                    travelDate = detail, // Address or Puja Date
                    personsCount = qty, // Quantity
                    onSuccess = {
                        val successMsg = if (currentLanguage == AppLanguage.HINDI) {
                            "सफलतापूर्वक आर्डर दर्ज हो गया! 🙏 हम जल्द ही आपसे संपर्क करेंगे।"
                        } else {
                            "Order Placed Successfully! 🙏 We will contact you shortly."
                        }
                        Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
                        showWorshipBookingDialog = false
                        selectedWorshipItem = null
                    }
                )
            }
        )
    }
}

@Composable
fun WorshipItemCard(
    item: WorshipItem,
    lang: AppLanguage,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("worship_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Product Image at the Top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                AsyncImage(
                    model = item.imageRes,
                    contentDescription = if (lang == AppLanguage.HINDI) item.nameHi else item.nameEn,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay Category Badge
                val categoryBadge = if (item.isService) {
                    if (lang == AppLanguage.HINDI) "पूजन" else "Ritual"
                } else {
                    if (lang == AppLanguage.HINDI) "प्रसाद" else "Prasad"
                }
                Surface(
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    color = if (item.isService) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = categoryBadge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isService) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Name
                Text(
                    text = if (lang == AppLanguage.HINDI) item.nameHi else item.nameEn,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Description (Compact)
                Text(
                    text = if (lang == AppLanguage.HINDI) item.descHi else item.descEn,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    lineHeight = 12.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Price and Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "₹${item.price}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )

                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isService) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("btn_book_${item.id}"),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (item.isService) Icons.Default.TempleHindu else Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (item.isService) {
                                if (lang == AppLanguage.HINDI) "बुक" else "Book"
                            } else {
                                if (lang == AppLanguage.HINDI) "ऑर्डर" else "Order"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorshipBookingDialog(
    item: WorshipItem,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, detail: String, qty: Int) -> Unit
) {
    var nameState by remember { mutableStateOf("") }
    var phoneState by remember { mutableStateOf("") }
    var detailState by remember { mutableStateOf("") }
    var qtyState by remember { mutableStateOf(1) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("worship_booking_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (item.isService) {
                        if (lang == AppLanguage.HINDI) "पूजन संकल्प बुकिंग" else "Book Puja Ritual"
                    } else {
                        if (lang == AppLanguage.HINDI) "प्रसाद व सामग्री आर्डर" else "Order Prasad & Items"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (lang == AppLanguage.HINDI) item.nameHi else item.nameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameState,
                    onValueChange = { nameState = it },
                    label = {
                        Text(if (lang == AppLanguage.HINDI) "पूरा नाम" else "Full Name")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("worship_booking_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phoneState,
                    onValueChange = { phoneState = it },
                    label = {
                        Text(if (lang == AppLanguage.HINDI) "मोबाइल नंबर" else "Mobile Number")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("worship_booking_phone"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = detailState,
                    onValueChange = { detailState = it },
                    label = {
                        Text(
                            if (item.isService) {
                                if (lang == AppLanguage.HINDI) "पूजन की पसंदीदा तिथि व समय" else "Preferred Puja Date & Time"
                            } else {
                                if (lang == AppLanguage.HINDI) "वितरण का पता (होटल/निवास स्थान)" else "Delivery Address (Hotel/Home)"
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (item.isService) Icons.Default.CalendarMonth else Icons.Default.LocationOn,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("worship_booking_detail"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (item.isService) {
                            if (lang == AppLanguage.HINDI) "यजमानों की संख्या" else "Number of Devotees"
                        } else {
                            if (lang == AppLanguage.HINDI) "मात्रा (नग)" else "Quantity (Qty)"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { if (qtyState > 1) qtyState-- },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = qtyState.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = { qtyState++ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == AppLanguage.HINDI) "कुल मूल्य (भुगतान आगमन पर)" else "Total Value (Pay on Arrival/Delivery)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "₹${item.price * qtyState}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = if (lang == AppLanguage.HINDI) "रद्द करें" else "Cancel",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (nameState.isNotBlank() && phoneState.isNotBlank() && detailState.isNotBlank()) {
                                onConfirm(nameState, phoneState, detailState, qtyState)
                            } else {
                                Toast.makeText(
                                    context,
                                    if (lang == AppLanguage.HINDI) "कृपया सभी विवरण भरें" else "Please fill all details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.HINDI) "ऑर्डर की पुष्टि करें" else "Confirm Order",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
