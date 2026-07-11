package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.model.SavedPlace
import com.example.data.model.TourBooking
import com.example.data.repository.TourRepository
import com.example.data.api.GeminiClient
import com.example.data.api.Content
import com.example.data.api.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class AppLanguage {
    ENGLISH, HINDI
}

object Locales {
    fun getString(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.HINDI -> hindiStrings[key] ?: englishStrings[key] ?: key
            AppLanguage.ENGLISH -> englishStrings[key] ?: key
        }
    }

    private val englishStrings = mapOf(
        "app_title" to "YATRA KASHI DHAM",
        "tab_home" to "Home",
        "tab_attractions" to "Darshan",
        "tab_packages" to "Packages",
        "tab_bookings" to "Bookings",
        "tab_worship" to "Worship",
        "tab_ai_guide" to "AI Guide",
        "welcome" to "Welcome to Varanasi",
        "welcome_desc" to "Experience the spiritual capital of India. Explore ancient ghats, historic temples, and exquisite street food on the banks of the sacred Ganges.",
        "welcome_desc_kashi" to "Experience the divine journey of Sri Kashi Vishwanath Temple, the sacred Jyotirlinga of Lord Shiva in Varanasi (Uttar Pradesh). This spiritual voyage includes a view of the grand Kashi Vishwanath Corridor, mesmerizing Ganga Aarti, sacred Ganges boat cruise, and darshan of the famous temples and religious spots of Kashi.\n Come, experience the divinity of Kashi and eternal culture with us.",
        "search_hint" to "Search places, ghats, temples...",
        "filter_all" to "All",
        "filter_ghat" to "Ghats",
        "filter_temple" to "Temples",
        "filter_heritage" to "Heritage",
        "filter_food" to "Food",
        "saved_places" to "Saved Places",
        "saved_places_empty" to "No saved places yet. Heart your favorite places to save them here!",
        "special_packages" to "Special Tour Packages",
        "special_packages_desc" to "Handpicked authentic experiences with professional local guides.",
        "book_now" to "Call Now",
        "booking_history" to "My Booked Tours",
        "no_bookings" to "No bookings found yet. Plan and book a tour from our premium packages!",
        "book_tour_title" to "Book Your Experience",
        "fill_details" to "Fill in your details to reserve your package. Payment is processed on arrival.",
        "full_name" to "Full Name",
        "phone_number" to "Phone Number",
        "select_date" to "Select Travel Date",
        "number_of_persons" to "Number of Persons",
        "cancel" to "Cancel",
        "confirm_booking" to "Confirm Booking",
        "booking_success" to "Tour Booked Successfully! 🎉 We will contact you soon.",
        "booking_cancelled" to "Booking Cancelled",
        "persons" to "persons",
        "about" to "About Kashi",
        "about_text" to "Varanasi (Kashi) is one of the oldest continuously inhabited cities in the world, serving as the spiritual heart of India.",
        "chat_welcome" to "Namaste! 🙏 I am Kashi AI, your personal Banaras travel guide. Ask me anything about Ghats, Temples, Food, custom itineraries, or the history of this ancient city!",
        "chat_hint" to "Ask Kashi AI about Varanasi...",
        "clear_chat" to "Clear Chat",
        "empty_chat" to "Ask me about boat rides, Ganga Aarti timings, or Kashi Vishwanath VIP darshan...",
        "highlights" to "Highlights",
        "best_time" to "Best Time to Visit",
        "location" to "Location",
        "back" to "Back",
        "contact_us" to "Contact Local Support",
        "support_text" to "Need custom arrangements or have questions? Contact us on WhatsApp or call at +91 8423340923."
    )

    private val hindiStrings = mapOf(
        "app_title" to "यात्रा काशी धाम",
        "tab_home" to "होम",
        "tab_attractions" to "दर्शन",
        "tab_packages" to "पैकेज",
        "tab_bookings" to "बुकिंग्स",
        "tab_worship" to "पूजा-प्रसाद",
        "tab_ai_guide" to "AI गाइड",
        "welcome" to "वाराणसी में आपका स्वागत है",
        "welcome_desc" to "भारत की आध्यात्मिक राजधानी का अनुभव करें। पवित्र गंगा के तट पर प्राचीन घाटों, ऐतिहासिक मंदिरों और स्वादिष्ट स्ट्रीट फूड का अन्वेषण करें।",
        "welcome_desc_kashi" to "वाराणसी (उत्तर प्रदेश) स्थित भगवान शिव के पावन ज्योतिर्लिंग श्री काशी विश्वनाथ मंदिर की दिव्य यात्रा का अनुभव करें। इस आध्यात्मिक यात्रा में भव्य काशी विश्वनाथ कॉरिडोर, मनमोहक गंगा आरती, पवित्र गंगा नौका विहार तथा काशी के प्रसिद्ध मंदिरों और धार्मिक स्थलों के दर्शन शामिल हैं।\n आइए, काशी की दिव्यता और सनातन संस्कृति का अद्भुत अनुभव हमारे साथ करें।",
        "search_hint" to "स्थान, घाट, मंदिर खोजें...",
        "filter_all" to "सभी",
        "filter_ghat" to "घाट",
        "filter_temple" to "मंदिर",
        "filter_heritage" to "धरोहर",
        "filter_food" to "भोजन",
        "saved_places" to "पसंदीदा स्थान",
        "saved_places_empty" to "अभी तक कोई पसंदीदा स्थान सहेजा नहीं गया है। सहेजने के लिए अपने पसंदीदा स्थानों पर दिल (Heart) दबाएं!",
        "special_packages" to "विशेष यात्रा पैकेज",
        "special_packages_desc" to "पेशेवर स्थानीय गाइडों के साथ चुने हुए प्रामाणिक अनुभव।",
        "book_now" to "कॉल करें",
        "booking_history" to "मेरी बुक की गई यात्राएं",
        "no_bookings" to "अभी तक कोई बुकिंग नहीं मिली। हमारे प्रीमियम पैकेजों से अपनी यात्रा की योजना बनाएं और बुक करें!",
        "book_tour_title" to "अपना अनुभव बुक करें",
        "fill_details" to "अपने पैकेज को आरक्षित करने के लिए अपना विवरण भरें। भुगतान आगमन पर किया जाएगा।",
        "full_name" to "पूरा नाम",
        "phone_number" to "फ़ोन नंबर",
        "select_date" to "यात्रा की तिथि चुनें",
        "number_of_persons" to "व्यक्तियों की संख्या",
        "cancel" to "रद्द करें",
        "confirm_booking" to "बुकिंग की पुष्टि करें",
        "booking_success" to "यात्रा सफलतापूर्वक बुक हो गई! 🎉 हम जल्द ही आपसे संपर्क करेंगे।",
        "booking_cancelled" to "बुकिंग रद्द कर दी गई",
        "persons" to "लोग",
        "about" to "काशी के बारे में",
        "about_text" to "वाराणसी (काशी) दुनिया के सबसे पुराने लगातार बसे शहरों में से एक है, जो भारत के आध्यात्मिक हृदय के रूप में कार्य करता है।",
        "chat_welcome" to "नमस्ते! 🙏 मैं काशी एआई हूँ, आपका व्यक्तिगत बनारस यात्रा गाइड। मुझसे घाट, मंदिर, भोजन, कस्टम यात्रा कार्यक्रम या इस प्राचीन शहर के इतिहास के बारे में कुछ भी पूछें!",
        "chat_hint" to "वाराणसी के बारे में काशी एआई से पूछें...",
        "clear_chat" to "चैट साफ करें",
        "empty_chat" to "मुझसे नौका विहार, गंगा आरती के समय या काशी विश्वनाथ वीआईपी दर्शन के बारे में पूछें...",
        "highlights" to "मुख्य विशेषताएं",
        "best_time" to "यात्रा का सर्वोत्तम समय",
        "location" to "स्थान",
        "back" to "वापस",
        "contact_us" to "स्थानीय सहायता से संपर्क करें",
        "support_text" to "कस्टम व्यवस्था की आवश्यकता है या कोई प्रश्न हैं? व्हाट्सएप पर संपर्क करें या +91 8423340923 पर कॉल करें।"
    )
}

data class Place(
    val id: String,
    val name: String,
    val type: String, // "Ghat", "Temple", "Food", "Heritage"
    val description: String,
    val highlights: String,
    val bestTime: String,
    val location: String,
    val imageUrl: String,
    val imageResId: Int = 0
)

data class TourPackage(
    val id: String,
    val name: String,
    val price: Int,
    val duration: String,
    val description: String,
    val highlights: List<String>,
    val iconName: String,
    val originalPrice: Int = 0
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

private data class LocalizedPlace(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val typeEn: String,
    val typeHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val highlightsEn: String,
    val highlightsHi: String,
    val bestTimeEn: String,
    val bestTimeHi: String,
    val locationEn: String,
    val locationHi: String,
    val imageUrl: String,
    val imageResId: Int = 0
) {
    fun toPlace(lang: AppLanguage): Place {
        return Place(
            id = id,
            name = if (lang == AppLanguage.HINDI) nameHi else nameEn,
            type = if (lang == AppLanguage.HINDI) typeHi else typeEn,
            description = if (lang == AppLanguage.HINDI) descriptionHi else descriptionEn,
            highlights = if (lang == AppLanguage.HINDI) highlightsHi else highlightsEn,
            bestTime = if (lang == AppLanguage.HINDI) bestTimeHi else bestTimeEn,
            location = if (lang == AppLanguage.HINDI) locationHi else locationEn,
            imageUrl = imageUrl,
            imageResId = imageResId
        )
    }
}

private data class LocalizedTourPackage(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val price: Int,
    val durationEn: String,
    val durationHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val highlightsEn: List<String>,
    val highlightsHi: List<String>,
    val iconName: String,
    val originalPrice: Int = 0
) {
    fun toTourPackage(lang: AppLanguage): TourPackage {
        return TourPackage(
            id = id,
            name = if (lang == AppLanguage.HINDI) nameHi else nameEn,
            price = price,
            duration = if (lang == AppLanguage.HINDI) durationHi else durationEn,
            description = if (lang == AppLanguage.HINDI) descriptionHi else descriptionEn,
            highlights = if (lang == AppLanguage.HINDI) highlightsHi else highlightsEn,
            iconName = iconName,
            originalPrice = originalPrice
        )
    }
}

class TourViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TourRepository

    val allBookings: StateFlow<List<TourBooking>>
    val allSavedPlaces: StateFlow<List<SavedPlace>>

    private val sharedPrefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _currentLanguage = MutableStateFlow(
        try {
            AppLanguage.valueOf(sharedPrefs.getString("selected_lang", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name)
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleLanguage() {
        val newLang = if (_currentLanguage.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
        _currentLanguage.value = newLang
        sharedPrefs.edit().putString("selected_lang", newLang.name).apply()

        // Sync initial welcome message in chat history if it is untouched
        if (_chatMessages.value.size == 1 && (
            _chatMessages.value[0].text == Locales.getString("chat_welcome", AppLanguage.ENGLISH) ||
            _chatMessages.value[0].text == Locales.getString("chat_welcome", AppLanguage.HINDI)
        )) {
            _chatMessages.value = listOf(
                ChatMessage(Locales.getString("chat_welcome", newLang), isUser = false)
            )
        }
    }

    // Predefined Places of Interest (Localized)
    private val localizedPlacesList = listOf(
        LocalizedPlace(
            id = "dashashwamedh_ghat",
            nameEn = "Dashashwamedh Ghat",
            nameHi = "दशाश्वमेध घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "The most spectacular and busiest ghat on the Ganga, located close to Kashi Vishwanath. It is famous worldwide for its mesmerizing evening Ganga Aarti.",
            descriptionHi = "गंगा नदी का सबसे शानदार और व्यस्त घाट, जो काशी विश्वनाथ के करीब स्थित है। यह अपनी मंत्रमुग्ध कर देने वाली शाम की गंगा आरती के लिए दुनिया भर में प्रसिद्ध है।",
            highlightsEn = "Evening Ganga Aarti, spiritual atmosphere, wooden boat rides",
            highlightsHi = "शाम की गंगा आरती, आध्यात्मिक वातावरण, लकड़ी की नाव की सवारी",
            bestTimeEn = "6:00 PM - 8:00 PM (for Aarti)",
            bestTimeHi = "शाम 6:00 बजे - रात 8:00 बजे (आरती के लिए)",
            locationEn = "Central Varanasi, near Godowlia",
            locationHi = "मध्य वाराणसी, गोदौलिया के पास",
            imageUrl = "https://images.unsplash.com/photo-1561361513-2d000a45f1d2?w=600&auto=format&fit=crop",
            imageResId = R.drawable.img_dashashwamedh_ghat
        ),
        LocalizedPlace(
            id = "kashi_vishwanath",
            nameEn = "Kashi Vishwanath Temple",
            nameHi = "काशी विश्वनाथ मंदिर",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "Dedicated to Lord Shiva, this is one of the twelve sacred Jyotirlingas. Known as the Golden Temple, it stands on the western bank of the holy Ganges.",
            descriptionHi = "भगवान शिव को समर्पित, यह बारह पवित्र ज्योतिर्लिंगों में से एक है। स्वर्ण मंदिर के रूप में प्रसिद्ध, यह पवित्र गंगा के पश्चिमी तट पर स्थित है।",
            highlightsEn = "Golden Spire, Kashi Vishwanath Corridor, morning Shringar Aarti",
            highlightsHi = "स्वर्ण शिखर, काशी विश्वनाथ कॉरिडोर, सुबह की श्रृंगार आरती",
            bestTimeEn = "4:00 AM - 11:00 AM",
            bestTimeHi = "सुबह 4:00 बजे - सुबह 11:00 बजे",
            locationEn = "Vishwanath Gali, Varanasi",
            locationHi = "विश्वनाथ गली, वाराणसी",
            imageUrl = "https://images.unsplash.com/photo-1627664813831-26e957c735d4?w=600&auto=format&fit=crop",
            imageResId = R.drawable.img_kashi_vishwanath
        ),
        LocalizedPlace(
            id = "assi_ghat",
            nameEn = "Assi Ghat",
            nameHi = "अस्सी घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "Situated where the river Assi meets the Ganga, this ghat is famous for hosting 'Subah-e-Banaras' (a spectacular morning ritual of fire, Vedic chants, yoga, and music).",
            descriptionHi = "जहाँ अस्सी नदी गंगा से मिलती है वहाँ स्थित, यह घाट 'सुबह-ए-बनारस' (अग्नि, वैदिक मंत्रोच्चार, योग और संगीत का एक भव्य सुबह का अनुष्ठान) की मेजबानी के लिए प्रसिद्ध है।",
            highlightsEn = "Subah-e-Banaras morning Aarti, open-air concerts, student hub",
            highlightsHi = "सुबह-ए-बनारस सुबह की आरती, खुले मंच पर संगीत कार्यक्रम, छात्रों का केंद्र",
            bestTimeEn = "5:00 AM - 7:00 AM (at Sunrise)",
            bestTimeHi = "सुबह 5:00 बजे - सुबह 7:00 बजे (सूर्योदय के समय)",
            locationEn = "South Varanasi, near BHU",
            locationHi = "दक्षिण वाराणसी, बीएचयू के पास",
            imageUrl = "https://images.unsplash.com/photo-1598977123418-45f0470c682e?w=600&auto=format&fit=crop",
            imageResId = R.drawable.img_assi_ghat
        ),
        LocalizedPlace(
            id = "sarnath",
            nameEn = "Sarnath Buddhist Site",
            nameHi = "सारनाथ बौद्ध स्थल",
            typeEn = "Heritage",
            typeHi = "धरोहर",
            descriptionEn = "Located 10 km from Varanasi, this is the sacred place where Lord Buddha delivered his first sermon after attaining enlightenment under the Bodhi Tree.",
            descriptionHi = "वाराणसी से 10 किमी दूर स्थित, यह वह पवित्र स्थान है जहाँ भगवान बुद्ध ने बोधि वृक्ष के नीचे ज्ञान प्राप्त करने के बाद अपना पहला उपदेश दिया था।",
            highlightsEn = "Dhamek Stupa, Ashoka Pillar, archaeological museum, deer park",
            highlightsHi = "धमेक स्तूप, अशोक स्तंभ, पुरातात्विक संग्रहालय, हिरण पार्क",
            bestTimeEn = "9:00 AM - 5:00 PM",
            bestTimeHi = "सुबह 9:00 बजे - शाम 5:00 बजे",
            locationEn = "Sarnath, Varanasi District",
            locationHi = "सारनाथ, वाराणसी जिला",
            imageUrl = "https://images.unsplash.com/photo-1601931320005-2244a2c7c514?w=600&auto=format&fit=crop",
            imageResId = R.drawable.img_sarnath
        ),
        LocalizedPlace(
            id = "manikarnika_ghat",
            nameEn = "Manikarnika Ghat",
            nameHi = "मणिकर्णिका घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "The principal cremation ghat of Varanasi, signifying liberation (Moksha). According to beliefs, cremation here grants ultimate peace to the soul.",
            descriptionHi = "वाराणसी का मुख्य श्मशान घाट, जो मोक्ष का प्रतीक है। मान्यताओं के अनुसार, यहाँ दाह संस्कार करने से आत्मा को परम शांति प्राप्त होती है।",
            highlightsEn = "Eternal burning pyres, deep philosophical life lessons, sunset views",
            highlightsHi = "शाश्वत जलती चिताएं, जीवन के गहरे दार्शनिक पाठ, सूर्यास्त के दृश्य",
            bestTimeEn = "Best viewed from a boat on the river",
            bestTimeHi = "नदी में नाव से सबसे अच्छा देखा जा सकता है",
            locationEn = "Near Chowk, central riverfront",
            locationHi = "चौक के पास, केंद्रीय नदी तट",
            imageUrl = "https://images.unsplash.com/photo-1585135497273-1a86b09fe70e?w=600&auto=format&fit=crop",
            imageResId = R.drawable.img_manikarnika_ghat
        ),
        LocalizedPlace(
            id = "sankat_mochan",
            nameEn = "Sankat Mochan Temple",
            nameHi = "संकट मोचन मंदिर",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "Established by the famous saint-poet Goswami Tulsidas, this historical temple is dedicated to Lord Hanuman, the destroyer of all troubles.",
            descriptionHi = "प्रसिद्ध संत-कवि गोस्वामी तुलसीदास द्वारा स्थापित, यह ऐतिहासिक मंदिर सभी संकटों को हरने वाले भगवान हनुमान को समर्पित है।",
            highlightsEn = "Peaceful chanting, legendary Besan Laddoo offering, historical heritage",
            highlightsHi = "शांतिपूर्ण भजन-कीर्तन, प्रसिद्ध बेसन के लड्डू का प्रसाद, ऐतिहासिक विरासत",
            bestTimeEn = "5:00 AM or 8:00 PM",
            bestTimeHi = "सुबह 5:00 बजे या रात 8:00 बजे",
            locationEn = "Saket Nagar, Varanasi",
            locationHi = "साकेत नगर, वाराणसी",
            imageUrl = "https://images.unsplash.com/photo-1590050752117-238cb0612b1b?w=600&auto=format&fit=crop",
            imageResId = R.drawable.img_sankat_mochan
        ),
        LocalizedPlace(
            id = "street_food_trail",
            nameEn = "Banarasi Street Food Trail",
            nameHi = "बनारसी स्ट्रीट फूड ट्रेल",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A culinary delight for food lovers! Sample the unique breakfast of Kachori Sabzi, refreshing rich Lassi in clay cups, and the legendary Banarasi Meetha Paan.",
            descriptionHi = "भोजन प्रेमियों के लिए एक स्वादिष्ट आनंद! कचौड़ी सब्जी का अनूठा नाश्ता, मिट्टी के कुल्हड़ में ठंडी मलाईदार लस्सी, और प्रसिद्ध बनारसी मीठे पान का स्वाद लें।",
            highlightsEn = "Chachi ki Kachori, Blue Lassi, Kashi Chat Bhandar, Tamatar Chat",
            highlightsHi = "चाची की कचौड़ी, ब्लू लस्सी, काशी चाट भंडार, टमाटर चाट",
            bestTimeEn = "Morning (Kachori) & Evening (Chaat/Paan)",
            bestTimeHi = "सुबह (कचौड़ी) और शाम (चाट/पान)",
            locationEn = "Godowlia and Chowk alleys",
            locationHi = "गोदौलिया और चौक की गलियां",
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df056fb4ce78?w=600&auto=format&fit=crop",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "namo_ghat",
            nameEn = "Namo Ghat (Khidkiya Ghat)",
            nameHi = "नमो घाट (खिड़किया घाट)",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "Varanasi's first modernized and fully accessible ghat, famous for the majestic metal sculptures of three pairs of folded hands doing Namaste, symbolizing traditional hospitality.",
            descriptionHi = "वाराणसी का पहला आधुनिक और पूरी तरह से सुलभ घाट, जो नमस्ते की मुद्रा में जुड़े हुए हाथ की विशाल धातु की मूर्तियों के लिए प्रसिद्ध है, जो पारंपरिक आतिथ्य का प्रतीक है।",
            highlightsEn = "Folded hands sculptures, heliport, spacious modern walking promenade, water sports",
            highlightsHi = "जुड़े हुए हाथों की मूर्तियाँ, हेलीपैड, विशाल आधुनिक वॉकिंग प्रोमेनेड, वाटर स्पोर्ट्स",
            bestTimeEn = "4:30 PM - 8:00 PM (for sunset & lights)",
            bestTimeHi = "शाम 4:30 बजे - रात 8:00 बजे (सूर्यास्त और रोशनी के लिए)",
            locationEn = "Rajghat area, northern Varanasi",
            locationHi = "राजघाट क्षेत्र, उत्तरी वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_namo_ghat
        ),
        LocalizedPlace(
            id = "ramnagar_fort",
            nameEn = "Ramnagar Fort & Museum",
            nameHi = "रामनगर किला और संग्रहालय",
            typeEn = "Heritage",
            typeHi = "धरोहर",
            descriptionEn = "The beautiful 17th-century sandstone fortress of the Kashi Naresh (King of Kashi), boasting a rich vintage museum of royal vintage cars, armory, and astronomical clocks.",
            descriptionHi = "काशी नरेश (काशी के राजा) का सुंदर 17वीं शताब्दी का बलुआ पत्थर का किला, जिसमें शाही विंटेज कारों, शस्त्रागार और खगोलीय घड़ियों का समृद्ध संग्रहालय है।",
            highlightsEn = "Royal museum, scenic sunset views over Ganges, Ramlila ground, ancient armor",
            highlightsHi = "शाही संग्रहालय, गंगा नदी पर सुंदर सूर्यास्त के दृश्य, रामलीला मैदान, प्राचीन हथियार",
            bestTimeEn = "10:00 AM - 5:00 PM",
            bestTimeHi = "सुबह 10:00 बजे - शाम 5:00 बजे",
            locationEn = "Ramnagar, eastern bank of Ganges",
            locationHi = "रामनगर, गंगा का पूर्वी तट",
            imageUrl = "",
            imageResId = R.drawable.img_ramnagar_fort
        ),
        LocalizedPlace(
            id = "durga_kund",
            nameEn = "Durga Temple (Durga Kund)",
            nameHi = "दुर्गा मंदिर (दुर्गा कुंड)",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "An iconic 18th-century temple styled in the vibrant multi-tiered Nagara architectural pattern, colored in dark red, situated right next to the sacred Durga Kund pool.",
            descriptionHi = "एक ऐतिहासिक 18वीं शताब्दी का मंदिर जो जीवंत बहु-स्तरीय नागर वास्तुकला शैली में निर्मित है, गहरे लाल रंग का है और पवित्र दुर्गा कुंड तालाब के ठीक बगल में स्थित है।",
            highlightsEn = "Stunning Nagara architecture, red temple towers, historic sacred pool reflection",
            highlightsHi = "आश्चर्यजनक नागर वास्तुकla, लाल मंदिर के शिखर, ऐतिहासिक पवित्र तालाब का प्रतिबिंब",
            bestTimeEn = "6:00 AM - 12:00 PM, 4:00 PM - 9:00 PM",
            bestTimeHi = "सुबह 6:00 बजे - दोपहर 12:00 बजे, शाम 4:00 बजे - रात 9:00 बजे",
            locationEn = "Durgakund Road, Jawahar Nagar",
            locationHi = "दुर्गाकुंड रोड, जवाहर नगर",
            imageUrl = "",
            imageResId = R.drawable.img_durga_kund
        )
    )

    private val localizedAttractionsPlacesList = localizedPlacesList + listOf(
        LocalizedPlace(
            id = "tulsi_ghat",
            nameEn = "Tulsi Ghat",
            nameHi = "तुलसी घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "A serene, historic ghat named after the great saint-poet Goswami Tulsidas, who wrote the epic Ramcharitmanas here. It is historically very quiet and spiritually nourishing.",
            descriptionHi = "एक शांत और ऐतिहासिक घाट जिसका नाम महान संत-कवि गोस्वामी तुलसीदास जी के नाम पर रखा गया है, जिन्होंने यहाँ बैठकर महाकाव्य रामचरितमानस की रचना की थी। यह स्थल बेहद शांत और आध्यात्मिक ऊर्जा से भरपूर है।",
            highlightsEn = "Association with Goswami Tulsidas, peaceful ambience, old historical residence",
            highlightsHi = "गोस्वामी तुलसीदास जी की स्मृति, शांत वातावरण, प्राचीन ऐतिहासिक आवास",
            bestTimeEn = "5:00 AM - 9:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 9:00 बजे",
            locationEn = "Shivala, near Assi Ghat, Varanasi",
            locationHi = "शिवाला, अस्सी घाट के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "chet_singh_ghat",
            nameEn = "Chet Singh Ghat",
            nameHi = "चेत सिंह घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "A spectacular fortified ghat that witnessed a historic battle between Maharaja Chet Singh and British troops in 1781. Marked by a magnificent palace-fort structure.",
            descriptionHi = "एक भव्य और सुदृढ़ किला-घाट जो १७८१ में महाराजा चेत सिंह और ब्रिटिश सेना के बीच ऐतिहासिक युद्ध का गवाह बना। यह घाट अपने विशाल और भव्य महलनुमा दुर्ग संरचना के लिए प्रसिद्ध है।",
            highlightsEn = "Grand Chet Singh Fort, historical battle site, impressive stone architecture",
            highlightsHi = "भव्य चेत सिंह किला, ऐतिहासिक युद्ध स्थल, प्रभावशाली पत्थर की नक्काशी",
            bestTimeEn = "6:00 AM - 8:00 PM",
            bestTimeHi = "सुबह 6:00 बजे - रात 8:00 बजे",
            locationEn = "Shivala, Varanasi",
            locationHi = "शिवाला, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "shivala_ghat",
            nameEn = "Shivala Ghat",
            nameHi = "शीवाला घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "Reflecting the rich culture and royal heritage of Varanasi, this ghat features historic mansions, ancient Shiva temples, and a calm, meditative atmosphere.",
            descriptionHi = "वाराणसी की समृद्ध संस्कृति और शाही विरासत को प्रदर्शित करने वाला यह घाट ऐतिहासिक महलों, प्राचीन शिव मंदिरों और बेहद शांत, ध्यानपूर्ण वातावरण के लिए जाना जाता है।",
            highlightsEn = "Historic royal mansions, ancient Shiva shrines, calm riverfront walkway",
            highlightsHi = "ऐतिहासिक शाही महल, प्राचीन शिव मंदिर, शांत गंगा तट मार्ग",
            bestTimeEn = "5:00 AM - 9:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 9:00 बजे",
            locationEn = "Shivala, Varanasi",
            locationHi = "शिवाला, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "harischandra_ghat",
            nameEn = "Harishchandra Ghat",
            nameHi = "हरिश्चंद्र घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "One of the oldest and most sacred cremation ghats of Kashi, named after King Harishchandra who practiced truthfulness here. Symbolizes life, death, and ultimate truth.",
            descriptionHi = "काशी के सबसे प्राचीन और पवित्र श्मशान घाटों में से एक, जिसका नाम सत्यवादी राजा हरिश्चंद्र के नाम पर रखा गया है जिन्होंने यहाँ सत्य की परीक्षा दी थी। यह जीवन, मृत्यु और परम सत्य का प्रतीक है।",
            highlightsEn = "Mythological cremation site, King Harishchandra shrine, deep spiritual insights",
            highlightsHi = "पौराणिक श्मशान भूमि, राजा हरिश्चंद्र की तपोस्थली, जीवन का गहन सत्य",
            bestTimeEn = "Open 24 Hours",
            bestTimeHi = "२४ घंटे खुला",
            locationEn = "Near Shivala, Varanasi",
            locationHi = "शिवाला के निकट, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "kedar_ghat",
            nameEn = "Kedar Ghat",
            nameHi = "केदार घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "A visually distinct ghat painted in beautiful red and white stripes, featuring south Indian architectural styles and the famous Kedareshwar Mahadev Temple.",
            descriptionHi = "सुंदर लाल और सफेद पट्टियों से रंगा हुआ एक अनोखा और आकर्षक घाट, जहाँ दक्षिण भारतीय वास्तुकला शैली के दर्शन होते हैं। यहाँ प्रसिद्ध केदारेश्वर महादेव मंदिर स्थित है।",
            highlightsEn = "Kedareshwar Mahadev Temple, distinct striped steps, popular south Indian pilgrim spot",
            highlightsHi = "केदारेश्वर महादेव मंदिर, आकर्षक धारीदार सीढ़ियाँ, दक्षिण भारतीय तीर्थयात्रियों का मुख्य केंद्र",
            bestTimeEn = "5:00 AM - 9:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 9:00 बजे",
            locationEn = "Bengali Tola, Varanasi",
            locationHi = "बंगाली टोला, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "ahilyabai_ghat",
            nameEn = "Ahilyabai Ghat",
            nameHi = "अहिल्याबाई घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "A beautiful and clean ghat built by Queen Ahilyabai Holkar of Indore in the late 18th century, showcasing stunning stone steps and fine heritage structures.",
            descriptionHi = "१८वीं शताब्दी के अंत में इंदौर की महारानी अहिल्याबाई होल्कर द्वारा निर्मित एक बेहद सुंदर और स्वच्छ घाट, जो भव्य पथरीली सीढ़ियों और ऐतिहासिक धरोहर संरचनाओं को दर्शाता है।",
            highlightsEn = "Ghat built by Queen Ahilyabai, beautiful stone pavillion, peaceful morning walk",
            highlightsHi = "महारानी अहिल्याबाई द्वारा निर्मित घाट, सुंदर पत्थर के मंडप, शांतिपूर्ण सुबह की सैर",
            bestTimeEn = "5:00 AM - 9:30 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 9:30 बजे",
            locationEn = "Near Dashashwamedh Ghat, Varanasi",
            locationHi = "दशाश्वमेध घाट के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "sheetla_ghat",
            nameEn = "Sheetla Ghat",
            nameHi = "शीतला घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "Located right adjacent to Dashashwamedh Ghat, this ghat is named after the ancient temple of Goddess Sheetla, believed to bless devotees with health and protect from diseases.",
            descriptionHi = "दशाश्वमेध घाट के ठीक बगल में स्थित, इस घाट का नाम माँ शीतला के प्राचीन मंदिर पर रखा गया है, जिनके बारे में माना जाता है कि वे भक्तों को उत्तम स्वास्थ्य और रोगों से मुक्ति का आशीर्वाद देती हैं।",
            highlightsEn = "Sheetla Devi Temple, proximity to main Aarti, religious ceremonies",
            highlightsHi = "शीतला देवी मंदिर, मुख्य आरती स्थल से निकटता, धार्मिक अनुष्ठान",
            bestTimeEn = "5:00 AM - 10:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 10:00 बजे",
            locationEn = "Adjacent to Dashashwamedh Ghat, Varanasi",
            locationHi = "दशाश्वमेध घाट के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "lalita_ghat",
            nameEn = "Lalita Ghat",
            nameHi = "ललिता घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "Famous for the spectacular Nepalese-style Pashupatinath Temple built of fine wood, terracotta and stone, featuring exquisite hand carvings by Nepali artisans.",
            descriptionHi = "लकड़ी, टेराकोटा और पत्थरों से बने भव्य नेपाली शैली के पशुपतिनाथ मंदिर के लिए प्रसिद्ध, जिसमें नेपाली कारीगरों द्वारा की गई उत्कृष्ट नक्काशी देखने को मिलती है।",
            highlightsEn = "Wooden Nepalese Temple, Ganga views, beautiful artistic wood carvings",
            highlightsHi = "काष्ठ निर्मित नेपाली मंदिर, सुंदर गंगा दर्शन, उत्कृष्ट कलात्मक लकड़ी की नक्काशी",
            bestTimeEn = "5:00 AM - 9:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 9:00 बजे",
            locationEn = "Lalita Gali, near Manikarnika Ghat, Varanasi",
            locationHi = "ललिता गली, मणिकर्णिका घाट के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "panchganga_ghat",
            nameEn = "Panchganga Ghat",
            nameHi = "पंचगंगा घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "A highly auspicious ghat representing the mystical confluence of five holy rivers (Ganga, Yamuna, Saraswati, Kirana, and Dhutpapa). Home to the ancient Bindu Madhav Temple.",
            descriptionHi = "पाँच पवित्र नदियों (गंगा, यमुना, सरस्वती, किरणा और धूतपापा) के अदृश्य संगम का प्रतिनिधित्व करने वाला एक परम पावन घाट। यहाँ प्राचीन बिंदु माधव मंदिर स्थित है।",
            highlightsEn = "Five rivers confluence point, historic Bindu Madhav Temple, scenic stone steps",
            highlightsHi = "पाँच नदियों का पावन संगम स्थल, ऐतिहासिक बिंदु माधव मंदिर, सुंदर पथरीली सीढ़ियाँ",
            bestTimeEn = "5:00 AM - 8:30 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 8:30 बजे",
            locationEn = "Panchganga Gali, Varanasi",
            locationHi = "पंचगंगा गली, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "raj_ghat",
            nameEn = "Raj Ghat",
            nameHi = "राज घाट",
            typeEn = "Ghat",
            typeHi = "घाट",
            descriptionEn = "Situated near the historic Malviya Bridge, Raj Ghat offers a sprawling, panoramic, and highly photogenic view of the Ganges river as it bends beautifully.",
            descriptionHi = "ऐतिहासिक मालवीय पुल के पास स्थित, राज घाट गंगा नदी का एक अत्यंत विस्तृत, मनोरम और सुंदर दृश्य प्रस्तुत करता है, जहाँ नदी एक सुंदर घुमाव लेती है।",
            highlightsEn = "Panoramic views of Ganges, Malviya Bridge view, peaceful wide riverfront",
            highlightsHi = "गंगा का विहंगम दृश्य, मालवीय पुल का नज़ारा, शांत और विस्तृत घाट परिसर",
            bestTimeEn = "5:00 AM - 9:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 9:00 बजे",
            locationEn = "Near Malviya Bridge, northern Varanasi",
            locationHi = "मालवीय ब्रिज के पास, उत्तरी वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "annapurna_temple",
            nameEn = "Maa Annapurna Temple",
            nameHi = "माता अन्नपूर्णा मंदिर",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "Dedicated to Goddess Annapurna, the deity of food and nourishment. It is believed that Goddess Annapurna herself fed Lord Shiva here, and no one goes hungry in Kashi.",
            descriptionHi = "अन्न और पोषण की देवी माँ अन्नपूर्णा को समर्पित। यह मान्यता है कि स्वयं माँ अन्नपूर्णा ने यहाँ भगवान शिव को भोजन कराया था, और काशी में कोई भी भूखा नहीं रहता।",
            highlightsEn = "Golden idol of Annapurna Devi, Annakut festival, free distribution of Prasad",
            highlightsHi = "अन्नपूर्णा देवी की स्वर्ण प्रतिमा, अन्नकूट उत्सव, निःशुल्क महाप्रसाद वितरण",
            bestTimeEn = "5:00 AM - 10:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 10:00 बजे",
            locationEn = "Near Kashi Vishwanath Temple, Varanasi",
            locationHi = "काशी विश्वनाथ मंदिर के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_annapurna_temple
        ),
        LocalizedPlace(
            id = "kaal_bhairav",
            nameEn = "Kaal Bhairav Temple (Kashi Ke Kotwal)",
            nameHi = "काल भैरव मंदिर (काशी के कोतवाल)",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "Dedicated to Kaal Bhairav, the fierce manifestation of Lord Shiva who is revered as the guardian deity or 'Kotwal' of Varanasi. Visiting this temple is considered essential before leaving Kashi.",
            descriptionHi = "भगवान शिव के उग्र रूप काल भैरव को समर्पित, जिन्हें वाराणसी का रक्षक देवता या 'कोतवाल' माना जाता है। काशी यात्रा पूरी करने के लिए यहाँ दर्शन करना अनिवार्य माना जाता है।",
            highlightsEn = "Silver face of deity, sacred black threads (Kanda), ancient holy spot",
            highlightsHi = "देवता का रजत मुखौटा, सुरक्षा का काला धागा (कंडा), प्राचीन सिद्ध पीठ",
            bestTimeEn = "5:00 AM - 1:30 PM, 4:30 PM - 9:30 PM",
            bestTimeHi = "सुबह 5:00 बजे - दोपहर 1:30 बजे, शाम 4:30 बजे - रात 9:30 बजे",
            locationEn = "K65/40, Vishweshwarganj, Varanasi",
            locationHi = "K65/40, विशेश्वरगंज, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kaal_bhairav
        ),
        LocalizedPlace(
            id = "mallikarjun_mahadev",
            nameEn = "Mallikarjun Mahadev Temple",
            nameHi = "मल्लिकार्जुन महादेव मंदिर",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "A highly revered ancient Shiva temple in Varanasi representing the sacred Mallikarjun Jyotirlinga, blessing devotees with spiritual elevation and inner peace.",
            descriptionHi = "वाराणसी में स्थित एक अत्यंत पूजनीय प्राचीन शिव मंदिर जो पावन मल्लिकार्जुन ज्योतिर्लिंग का प्रतिनिधित्व करता है, जो भक्तों को आध्यात्मिक उन्नति और आंतरिक शांति प्रदान करता है।",
            highlightsEn = "Sacred Shivlingam, peaceful atmosphere for meditation, spiritual vibration",
            highlightsHi = "पवित्र शिवलिंग, ध्यान के लिए शांत वातावरण, दिव्य आध्यात्मिक स्पंदन",
            bestTimeEn = "6:00 AM - 9:00 PM",
            bestTimeHi = "सुबह 6:00 बजे - रात 9:00 बजे",
            locationEn = "Sigra, Varanasi",
            locationHi = "सिगरा, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_vishwanath
        ),
        LocalizedPlace(
            id = "keenaram_ashram",
            nameEn = "Baba Keenaram Ashram (Keeva Baba)",
            nameHi = "कीना राम बाबा आश्रम",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "The world-famous spiritual center and headquarters of Aghori sect, founded by the great saint Baba Keenaram. Known for its humanitarian work, spiritual practices, and the sacred eternal fire (Dhuni).",
            descriptionHi = "महान संत बाबा कीनाराम द्वारा स्थापित अघोर संप्रदाय का विश्व प्रसिद्ध आध्यात्मिक केंद्र और मुख्यालय। यह अपने मानवतावादी कार्यों, आध्यात्मिक साधना और पवित्र अखंड धूनी के लिए जाना जाता है।",
            highlightsEn = "Sacred tomb of Baba Keenaram, holy pond (Krim Kund), eternal flame",
            highlightsHi = "बाबा कीनाराम की समाधि, पावन क्रीं कुंड, अखंड धूनी",
            bestTimeEn = "7:00 AM - 12:00 PM, 4:00 PM - 8:00 PM",
            bestTimeHi = "सुबह 7:00 बजे - दोपहर 12:00 बजे, शाम 4:00 बजे - रात 8:00 बजे",
            locationEn = "Ravindrapuri, Varanasi",
            locationHi = "रवींद्रपुरी, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "tulsi_manas",
            nameEn = "Tulsi Manas Temple",
            nameHi = "तुलसी मानस मंदिर",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "A magnificent modern marble temple built on the spot where legendary saint Goswami Tulsidas wrote the Hindu epic 'Ramcharitmanas'. The temple walls are beautifully engraved with verses from the epic.",
            descriptionHi = "सफेद संगमरमर से निर्मित एक भव्य आधुनिक मंदिर, जो उसी स्थान पर बना है जहाँ महान संत गोस्वामी तुलसीदास ने महाकाव्य 'रामचरितमानस' की रचना की थी। मंदिर की दीवारों पर रामचरितमानस के दोहे और चौपाइयां अंकित हैं।",
            highlightsEn = "Engraved Ramcharitmanas verses on walls, moving puppets depicting Ramayana scenes",
            highlightsHi = "दीवारों पर उकेरी गई रामचरितमानस की चौपाइयां, रामायण के दृश्यों को दर्शाती चलती-फिरती कठपुतलियां",
            bestTimeEn = "5:30 AM - 12:00 PM, 3:30 PM - 9:00 PM",
            bestTimeHi = "सुबह 5:30 बजे - दोपहर 12:00 बजे, दोपहर 3:30 बजे - रात 9:00 बजे",
            locationEn = "Durgakund Road, near Durga Temple, Varanasi",
            locationHi = "दुर्गाकुंड रोड, दुर्गा मंदिर के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_durga_kund
        ),
        LocalizedPlace(
            id = "tridev_mandir",
            nameEn = "Tridev Temple",
            nameHi = "त्रिदेव मंदिर",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "A beautiful, modern temple dedicated to the three revered deities: Lord Hanuman, Lord Salasar Balaji, and Goddess Rani Sati. Built with exquisite white marble and featuring marvelous architecture.",
            descriptionHi = "तीन परम पूजनीय देवताओं: भगवान हनुमान, भगवान सालासर बालाजी और देवी रानी सती को समर्पित एक सुंदर और आधुनिक मंदिर। उत्कृष्ट सफेद संगमरमर से निर्मित और अद्भुत वास्तुकला से सुसज्जित।",
            highlightsEn = "Beautiful white marble carvings, Salasar Balaji idol, calm and neat environment",
            highlightsHi = "सफेद संगमरमर पर सुंदर नक्काशी, सालासर बालाजी की प्रतिमा, शांत और स्वच्छ परिसर",
            bestTimeEn = "6:00 AM - 10:00 PM",
            bestTimeHi = "सुबह 6:00 बजे - रात 10:00 बजे",
            locationEn = "Durgakund Road, near Tulsi Manas Mandir, Varanasi",
            locationHi = "दुर्गाकुंड रोड, तुलसी मानस मंदिर के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_vishwanath
        ),
        LocalizedPlace(
            id = "bhu_vt",
            nameEn = "New Vishwanath Temple (BHU-VT)",
            nameHi = "नया विश्वनाथ मंदिर, BHU",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "Located inside the Banaras Hindu University (BHU) campus, this temple is also known as Birla Temple. It is dedicated to Lord Shiva and features the tallest temple tower (Shikhara) in the world, built of pure marble.",
            descriptionHi = "बनारस हिंदू विश्वविद्यालय (BHU) परिसर के अंदर स्थित, इस मंदिर को बिड़ला मंदिर भी कहा जाता है। यह भगवान शिव को समर्पित है और इसमें शुद्ध संगमरमर से बना दुनिया का सबसे ऊँचा मंदिर शिखर है।",
            highlightsEn = "Tallest temple tower (77m), beautiful marble architecture, spacious green gardens, Gita verses on walls",
            highlightsHi = "दुनिया का सबसे ऊँचा मंदिर शिखर (77 मीटर), भव्य संगमरमर वास्तुकला, विशाल हरे-भरे बगीचे, दीवारों पर गीता के श्लोक",
            bestTimeEn = "4:00 AM - 12:00 PM, 1:00 PM - 9:00 PM",
            bestTimeHi = "सुबह 4:00 बजे - दोपहर 12:00 बजे, दोपहर 1:00 बजे - रात 9:00 बजे",
            locationEn = "BHU Campus, Varanasi",
            locationHi = "BHU कैंपस, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_bhu_vt
        ),
        LocalizedPlace(
            id = "shitala_mata",
            nameEn = "Shitala Mata Temple",
            nameHi = "शीतला माता मंदिर",
            typeEn = "Temple",
            typeHi = "मंदिर",
            descriptionEn = "An ancient temple dedicated to Goddess Sheetla, situated on the sacred Sheetla Ghat. Devotees visit to seek protection from health issues and diseases.",
            descriptionHi = "पवित्र शीतला घाट पर स्थित माँ शीतला को समर्पित एक अत्यंत प्राचीन और सिद्ध मंदिर। भक्तजन यहाँ रोगों से मुक्ति और उत्तम स्वास्थ्य की कामना के लिए आते हैं।",
            highlightsEn = "Historic shrine of Sheetla Devi, spectacular view of Ganga from the temple, deep spiritual relevance",
            highlightsHi = "शीतला देवी का ऐतिहासिक गर्भगृह, मंदिर से गंगा जी का सुंदर दृश्य, गहरी आध्यात्मिक मान्यता",
            bestTimeEn = "5:00 AM - 10:00 PM",
            bestTimeHi = "सुबह 5:00 बजे - रात 10:00 बजे",
            locationEn = "Sheetla Ghat, Varanasi",
            locationHi = "शीतला घाट, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_kashi_ghat
        ),
        LocalizedPlace(
            id = "annapurna_bhandara",
            nameEn = "Shree Annapurna Mandir (Free Bhandara)",
            nameHi = "श्री अन्नपूर्णा मंदिर (निःशुल्क भंडारा)",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "Maa Annapurna's world-famous sacred food hall. Feeding thousands of pilgrims and devotees every single day for free as part of ancient Kashi's tradition where no one sleeps hungry.",
            descriptionHi = "माँ अन्नपूर्णा का विश्व प्रसिद्ध पावन अन्नक्षेत्र। यहाँ प्राचीन काशी की परंपरा के अनुसार प्रतिदिन हजारों श्रद्धालुओं व यात्रियों को निःशुल्क एवं श्रद्धापूर्वक महाप्रसाद (खिचड़ी, दाल-चावल, प्रसाद) खिलाया जाता है।",
            highlightsEn = "Delicious Khichdi, Dal, Rice, Traditional Prasad, Pure Desi Ghee",
            highlightsHi = "स्वादिष्ट खिचड़ी, दाल, चावल, पारंपरिक महाप्रसाद, शुद्ध और सात्विक भोजन",
            bestTimeEn = "11:30 AM - 2:00 PM (Daily)",
            bestTimeHi = "दोपहर 11:30 AM से दोपहर 2:00 PM तक (प्रतिदिन)",
            locationEn = "Near Kashi Vishwanath Temple, Godowlia, Varanasi",
            locationHi = "काशी विश्वनाथ मंदिर के पास, गोदौलिया, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_annapurna_temple
        ),
        LocalizedPlace(
            id = "annapurna_free_mess",
            nameEn = "Annapurna Free Bhojanam Mess",
            nameHi = "अन्नपूर्णा फ्री भोजनम मेस",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A dedicated local community kitchen offering complete, warm, and highly hygienic vegetarian meals to travelers, pilgrims, and those in need without any charges.",
            descriptionHi = "एक समर्पित स्थानीय रसोईघर जो जरूरतमंदों, यात्रियों और श्रद्धालुओं को बिना किसी शुल्क के पूर्ण, गर्म और अत्यधिक स्वच्छ शाकाहारी भोजन प्रदान करता है।",
            highlightsEn = "Full Vegetarian Meal, Hygienic Kitchen, Community Service",
            highlightsHi = "पूर्ण शाकाहारी भोजन, स्वच्छ रसोईघर, निःशुल्क सेवा और सहयोग",
            bestTimeEn = "12:00 PM - 2:00 PM",
            bestTimeHi = "दोपहर 12:00 बजे से दोपहर 2:00 बजे तक",
            locationEn = "Naria, Near BHU, Varanasi",
            locationHi = "नरिया, बीएचयू के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "iskcon_bhandara",
            nameEn = "ISKCON Varanasi (Krishna Prasad)",
            nameHi = "इस्कॉन वाराणसी (कृष्ण प्रसाद)",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "The famous Krishna Prasad (sanctified food) distributed lovingly to devotees after daily morning and evening aartis. Sunday features a grand special feast distribution.",
            descriptionHi = "दैनिक सुबह और शाम की आरती के बाद श्रद्धालुओं को प्रेमपूर्वक वितरित किया जाने वाला प्रसिद्ध कृष्ण महाप्रसाद (खिचड़ी, हलवा, सब्जी)। रविवार को यहाँ विशेष भंडारा व महाप्रसाद वितरण होता है।",
            highlightsEn = "Khichdi, Halwa, Special Sunday Feast, Spiritual Chanting",
            highlightsHi = "खिचड़ी, मलाईदार हलवा, विशेष रविवार भंडारा, कीर्तन और सत्संग",
            bestTimeEn = "After Aarti (Afternoon & Evening, Special Sunday Feast)",
            bestTimeHi = "आरती के बाद (दोपहर व शाम, विशेष रविवार महाप्रसाद)",
            locationEn = "B 38/182-A, Birdopur, Mahmoorganj, Varanasi",
            locationHi = "B 38/182-A, बिर्दोपुर, महमूरगंज, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "ram_bhandar",
            nameEn = "The Ram Bhandar",
            nameHi = "द राम भंडार",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "The ultimate traditional breakfast spot of Varanasi! Famous across India for serving hot, crispy Kachori-Sabzi with spicy potato gravy and mouth-watering sweet Jalebis.",
            descriptionHi = "वाराणसी के पारंपरिक नाश्ते की सबसे प्रसिद्ध और पुरानी दुकान! अपनी गरमा-गरम कचौड़ी-सब्जी, मसालेदार आलू रसा और रसीली जलेबियों के लिए पूरे भारत में विख्यात है।",
            highlightsEn = "Crispy Kachori-Sabzi, Hot Jalebi, Premium Khasta, Traditional Taste",
            highlightsHi = "कुरकुरी कचौड़ी-सब्जी, गरमा-गरम जलेबी, लाजवाब खस्ता, पारंपरिक स्वाद",
            bestTimeEn = "7:30 AM - 11:00 AM, 2:30 PM - 6:00 PM",
            bestTimeHi = "सुबह 7:30 - 11:00, दोपहर 2:30 - शाम 6:00",
            locationEn = "C.K. 37/35, Chowk, Varanasi",
            locationHi = "C.K. 37/35, चौक, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "kashi_chaat_bhandar",
            nameEn = "Kashi Chaat Bhandar (Godowlia)",
            nameHi = "काशी चाट भंडार (गोदौलिया)",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "One of Kashi's most legendary food joints, world-renowned for its signature Tamatar Chaat (savory tomato-based street food in clay cups) and crispy Aloo Tikki.",
            descriptionHi = "काशी का सबसे प्रसिद्ध चाट भंडार, जो अपनी लाजवाब 'टमाटर चाट' (मिट्टी के कुल्हड़ में परोसी जाने वाली चटपटी चाट), कुरकुरी टिक्की और तीखे गोलगप्पों के लिए देश-विदेश में लोकप्रिय है।",
            highlightsEn = "Signature Tamatar Chaat, Aloo Tikki, Golgappe, Kulfi Falooda",
            highlightsHi = "मशहूर टमाटर चाट, घी वाली आलू टिक्की, तीखे गोलगप्पे, कुल्फी फालूदा",
            bestTimeEn = "2:00 PM - 10:30 PM (Evening is best)",
            bestTimeHi = "दोपहर 2:00 बजे से रात 10:30 बजे तक (शाम का समय सर्वोत्तम)",
            locationEn = "D. 15/2, Godowlia Crossing, Varanasi",
            locationHi = "D. 15/2, गोदौलिया चौराहा, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "deena_chaat_bhandar",
            nameEn = "Deena Chaat Bhandar",
            nameHi = "दीना चाट भंडार",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A historic institution celebrating Banaras's street-food culture. Unmissable for its crunchy Palak Patta Chaat, richly laden Dahi Chaat, and exquisite Tokri (Basket) Chaat.",
            descriptionHi = "बनारस की स्ट्रीट-फूड संस्कृति को संजोए रखने वाली एक ऐतिहासिक दुकान। यहाँ की कुरकुरी पालक पत्ता चाट, मलाईदार दही चाट और विशेष टोकरी चाट बेहद लाजवाब होती है।",
            highlightsEn = "Palak Patta Chaat, Tokri Chaat, Chura Matar, Dahi Bhalla",
            highlightsHi = "पालक पत्ता चाट, भरी हुई टोकरी चाट, चूड़ा मटर, मलाईदार दही भल्ला",
            bestTimeEn = "1:00 PM - 10:00 PM",
            bestTimeHi = "दोपहर 1:00 बजे से रात 10:00 बजे तक",
            locationEn = "Luxa Road, near Dashashwamedh, Varanasi",
            locationHi = "लक्सा रोड, दशाश्वमेध के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "pehalwan_lassi",
            nameEn = "Pehalwan Lassi",
            nameHi = "पहलवान लस्सी",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "Varanasi's legendary dessert shop, serving incredibly thick Malai Lassi and rich Rabdi Lassi topped with dense layers of fresh cream in traditional earthen glasses.",
            descriptionHi = "वाराणसी की प्रसिद्ध मिठाई व लस्सी की दुकान, जहाँ पारंपरिक कुल्हड़ में अत्यधिक गाढ़ी मलाई लस्सी और लाजवाब रबड़ी लस्सी परोसी जाती है जो बेहद तृप्त करने वाली होती है।",
            highlightsEn = "Thick Malai Lassi, Sweet Rabdi, Earthen Earthen Glass, Traditional Recipe",
            highlightsHi = "गाढ़ी मलाई लस्सी, मलाईदार रबड़ी, कुल्हड़ का सोंधा स्वाद, पारंपरिक विधि",
            bestTimeEn = "9:00 AM - 11:00 PM",
            bestTimeHi = "सुबह 9:00 बजे से रात 11:00 बजे तक",
            locationEn = "Lanka Crossing, Near BHU, Varanasi",
            locationHi = "लंका चौराहा, बीएचयू के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "neelu_kachori_bhandar",
            nameEn = "Neelu Kachori Bhandar",
            nameHi = "नीलू कचौड़ी भंडार",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A hidden gem among the old alleys of Kashi, beloved by locals for its spicy Aloo Rasa and hot, freshly fried Kachoris made over wood fire.",
            descriptionHi = "काशी की पुरानी गलियों में छिपा हुआ एक प्रसिद्ध स्वाद केंद्र, जो अपने तीखे और चटपटे आलू रसा और लकड़ी की आँच पर छनी गरमा-गरम खस्ता कचौड़ियों के लिए विख्यात है।",
            highlightsEn = "Wood-fired Kachori, Spicy Aloo Rasa, Sweet Jalebi",
            highlightsHi = "लकड़ी की आँच वाली कचौड़ी, तीखा आलू रसा, गरमा-गरम जलेबी",
            bestTimeEn = "7:00 AM - 2:00 PM",
            bestTimeHi = "सुबह 7:00 बजे से दोपहर 2:00 बजे तक",
            locationEn = "Kachori Gali, Chowk, Varanasi",
            locationHi = "कचौड़ी गली, चौक, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "baati_chokha_restaurant",
            nameEn = "Baati Chokha Restaurant",
            nameHi = "बाटी चोखा रेस्टोरेंट",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A rustic, village-themed dhaba-style restaurant offering authentic Purvanchali cuisine. Known for wood-fired wheat Baatis drenched in pure ghee and spicy mashed Chokha.",
            descriptionHi = "एक ग्रामीण थीम पर आधारित पारंपरिक भोजनालय जो प्रामाणिक पूर्वांचली व्यंजन परोसता है। यहाँ कंडे की आँच पर पकी और शुद्ध घी में डूबी बाटी तथा चटपटा चोखा मिट्टी के बर्तनों में मिलता है।",
            highlightsEn = "Wood-fired Baati, Spicy Chokha, Sweet Churma, Clay Crockery",
            highlightsHi = "कंडे पर पकी बाटी, स्वादिष्ट चोखा, मीठा चूरमा, मिट्टी के बर्तनों में परोसना",
            bestTimeEn = "12:00 PM - 10:30 PM",
            bestTimeHi = "दोपहर 12:00 बजे से रात 10:30 बजे तक",
            locationEn = "Teliabagh, near Cantonment, Varanasi",
            locationHi = "तेलियाबाग, कैंटोनमेंट के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "blue_lassi_shop",
            nameEn = "Blue Lassi Shop (Vishwanath Gali)",
            nameHi = "ब्लू लस्सी शॉप (विश्वनाथ गली)",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A tiny, globally-known shop tucked in Kashi's narrow alleyways. Famous for serving over 80 varieties of premium artisanal fruit lassis, decorated with real saffron, pistachios, and fresh fruits.",
            descriptionHi = "काशी की संकरी गलियों में स्थित एक विश्व प्रसिद्ध छोटी सी दुकान, जो केसर, अनार, पिस्ता, चॉकलेट और आम सहित 80 से अधिक प्रकार की लजीज फ्लेवर वाली लस्सी मिट्टी के कुल्हड़ में परोसने के लिए जानी जाती है।",
            highlightsEn = "80+ Flavored Lassis, Mango & Saffron Lassi, Historic Wall of Sticky Notes",
            highlightsHi = "80+ फ्लेवर वाली लस्सी, आम-केसर-अनार लस्सी, यादों की दीवार (स्टीकी नोट्स)",
            bestTimeEn = "9:00 AM - 10:30 PM",
            bestTimeHi = "सुबह 9:00 बजे से रात 10:30 बजे तक",
            locationEn = "C.K. 21/35, near Manikarnika Ghat, Varanasi",
            locationHi = "C.K. 21/35, मणिकर्णिका घाट के पास, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "sri_krishna_bhandar",
            nameEn = "Sri Krishna Bhandar",
            nameHi = "श्री कृष्ण भंडार",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A traditional sweet meat shop beloved for its early-morning puri-bhaji, aromatic soft Malpua, sweet Rabdi, and crispy saffron-infused Jalebis.",
            descriptionHi = "एक पारंपरिक एवं सुप्रसिद्ध मिष्ठान भंडार जो अपने सुबह के नाश्ते (पूरी-भाजी), रसीले मालपुआ, मलाईदार रबड़ी और केसरिया जलेबी के लिए बेहद लोकप्रिय है।",
            highlightsEn = "Spicy Puri Bhaji, Soft Malpua, Thick Rabdi, Saffron Jalebi",
            highlightsHi = "तीखी पूरी भाजी, रसीला मालपुआ, गाढ़ी रबड़ी, केसरिया जलेबी",
            bestTimeEn = "7:00 AM - 10:00 PM",
            bestTimeHi = "सुबह 7:00 बजे से रात 10:00 बजे तक",
            locationEn = "Dashashwamedh Ghat Road, Varanasi",
            locationHi = "दशाश्वमेध घाट रोड, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "bana_lassi",
            nameEn = "Bana Lassi / Shiv Prasad Lassi",
            nameHi = "बना लस्सी / शिव प्रसाद लस्सी",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "A wonderful, hygienic lassi corner offering creamy sweet curd whip, loaded with fresh nuts, thick layers of malai, and seasonal fruit toppings in eco-friendly clay cups.",
            descriptionHi = "एक बेहतरीन और स्वच्छ लस्सी कॉर्नर, जहाँ गाढ़ी और मलाईदार मीठी दही, सूखे मेवे और ताजे मौसमी फलों की टॉपिंग के साथ पारंपरिक कुल्हड़ में परोसी जाती है।",
            highlightsEn = "Kulhad Lassi, Fresh Malai Cream, Seasonal Fruit Toppings",
            highlightsHi = "सोंधी कुल्हड़ लस्सी, ताजी गाढ़ी मलाई, मौसमी फलों का स्वाद",
            bestTimeEn = "10:00 AM - 10:00 PM",
            bestTimeHi = "सुबह 10:00 बजे से रात 10:00 बजे तक",
            locationEn = "Shivala Ghat area, Varanasi",
            locationHi = "शिवाला घाट क्षेत्र, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        ),
        LocalizedPlace(
            id = "markandey_malaiyo",
            nameEn = "Markandey Malaiyo (Winter Special)",
            nameHi = "मार्कंडेय मलइयो (सर्दियों का विशेष प्रसाद)",
            typeEn = "Food",
            typeHi = "भोजन",
            descriptionEn = "Varanasi's legendary, unique winter-only dessert! An ethereal, light-as-air milk foam flavored with saffron, cardamom, and fine pistachios, prepared under the dew drops of cold winter nights.",
            descriptionHi = "वाराणसी की विश्व प्रसिद्ध और अनोखी सर्दियों की प्रसिद्ध मिठाई! केसर, इलायची और पिस्ते के स्वाद से भरपूर, हवा जैसी हल्की और ओस की बूंदों में तैयार होने वाली दिव्य मलाई का झाग।",
            highlightsEn = "Authentic Malaiyo (Foam Dessert), Winter-Only Specialty, Saffron and Cardamom",
            highlightsHi = "प्रामाणिक मलइयो, सर्दियों की अनूठी मिठाई (दिसंबर-फरवरी), केसर और पिस्ता जुगलबंदी",
            bestTimeEn = "7:00 AM - 11:00 AM (Only December to February)",
            bestTimeHi = "सुबह 7:00 से 11:00 बजे तक (केवल दिसंबर से फरवरी में उपलब्ध)",
            locationEn = "Thatheri Bazar, Chowk, Varanasi",
            locationHi = "ठठेरी बाजार, चौक, वाराणसी",
            imageUrl = "",
            imageResId = R.drawable.img_street_food_trail
        )
    )

    // Reactive StateFlow returning correctly localized Places
    val placesList: StateFlow<List<Place>> = _currentLanguage
        .map { lang -> localizedPlacesList.map { it.toPlace(lang) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = localizedPlacesList.map { it.toPlace(_currentLanguage.value) }
        )

    val attractionsPlacesList: StateFlow<List<Place>> = _currentLanguage
        .map { lang -> localizedAttractionsPlacesList.map { it.toPlace(lang) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = localizedAttractionsPlacesList.map { it.toPlace(_currentLanguage.value) }
        )

    // Predefined Tour Packages (Localized)
    private val localizedTourPackagesList = listOf(
        LocalizedTourPackage(
            id = "morning_boat",
            nameEn = "Varanasi One-Day Temple Tour (Full Package)",
            nameHi = "वाराणसी वन-डे टेम्पल टूर (फुल पैकेज)",
            price = 1050,
            durationEn = "10 Hours (8:00 AM - 6:00 PM)",
            durationHi = "10 घंटे (सुबह 08:00 - शाम 06:00)",
            descriptionEn = "If you want to visit all 12 major temples of Varanasi and taste its famous delicacies in a single day, this 'all-in-one' package is just for you.\n\n🗺️ 12 Major Sightseeing Places:\n1. Sri Kashi Vishwanath Temple\n2. Maa Annapurna Temple\n3. Kaal Bhairav Temple\n4. Mallikarjun Mahadev\n5. Keena Ram Baba Ashram\n6. Durga Kund Temple\n7. Tulsi Manas Mandir\n8. Tridev Mandir\n9. Sankat Mochan Hanuman Temple\n10. New Vishwanath Temple (BHU - VT)\n11. Shitala Mata Mandir\n12. Dashashwamedh Ghat (Grand Ganga Aarti)\n\n🍲 Food & Delicacies Included:\n• Breakfast: Hot Poori-Sabzi, crispy Jalebi & thick Banarasi Lassi\n• Lunch: Traditional Purvanchal 'Baati-Chokha'\n• Special Treat: Legendary Banarasi Paan\n\n💳 Terms & Conditions:\n✅ Inclusions: Complete E-Rickshaw/Car fare, breakfast, lunch, and VIP Darshan tickets.\n❌ Exclusions: Tour Guide services (Optional on extra charges).",
            descriptionHi = "यदि आप एक ही दिन में वाराणसी के सभी प्रमुख मंदिरों के दर्शन और यहाँ के प्रसिद्ध व्यंजनों का स्वाद चखना चाहते हैं, तो यह 'ऑल-इन-वन' पैकेज आपके लिए ही है।\n\n🗺️ दर्शन करने योग्य 12 प्रमुख स्थान:\n1. श्री काशी विश्वनाथ मंदिर\n2. माता अन्नपूर्णा मंदिर\n3. काल भैरव मंदिर\n4. मल्लिकार्जुन महादेव\n5. कीना राम बाबा आश्रम\n6. दुर्गा कुंड / दुर्गा माता मंदिर\n7. तुलसी मानस मंदिर\n8. त्रिदेव मंदिर\n9. संकट मोचन हनुमान मंदिर\n10. नया विश्वनाथ मंदिर, BHU (VT)\n11. शीतला माता मंदिर\n12. दशाश्वमेध घाट - भव्य गंगा आरती\n\n🍲 खान-पान शामिल:\n• सुबह का नाश्ता: गरमा-गरम पूड़ी-सब्जी, कुरकुरी जलेबी और गाढ़ी बनारसी लस्सी\n• दोपहर का भोजन: पूर्वांचल का पारंपरिक और स्वादिष्ट 'बाटी-चोखा'\n• स्पेशल ट्रीट: प्रसिद्ध बनारसी पान\n\n💳 पैकेज की शर्तें:\n✅ शामिल (No Extra Cost): पूरे टूर के लिए ई-रिक्शा या कार किराया, नाश्ता, दोपहर का खाना और VIP दर्शन टिकट।\n❌ शामिल नहीं (Extra Cost): इतिहास और जानकारी के लिए गाइड की सुविधा (अलग से शुल्क)।",
            highlightsEn = listOf("12 Sacred Temples & Ganga Aarti", "Breakfast, Lunch & Banarasi Paan", "Car/Rickshaw & VIP Darshan Inclusions", "Assorted Sightseeing with Guide Option"),
            highlightsHi = listOf("12 पावन प्रसिद्ध मंदिर एवं आरती दर्शन", "सुबह का नाश्ता, दोपहर का खाना और बनारसी पान", "परिवहन (ई-रिक्शा/कार) और VIP दर्शन", "इतिहास व गाइड का विकल्प"),
            iconName = "temple_hindu",
            originalPrice = 1500
        ),
        LocalizedTourPackage(
            id = "ghat_boat_tour",
            nameEn = "Varanasi One-Day Ghat Tour (Full Package) ⛵",
            nameHi = "वाराणसी वन-डे घाट टूर (फुल पैकेज) ⛵",
            price = 1190,
            durationEn = "8 Hours (10:00 AM - 6:00 PM)",
            durationHi = "8 घंटे (सुबह 10:00 - शाम 06:00)",
            descriptionEn = "Experience the true soul of Banaras that resides in its sacred ghats! In this exclusive, premium tour package, we will take you on a journey to the most famous and historical ghats of Varanasi in a single day, all via a comfortable, relaxing Boat ride.\n\n🕒 Tour Timings:\n10:00 AM - 6:00 PM\n\n🗺️ 14 Major Sightseeing Ghats:\n1. Assi Ghat (Peaceful morning & cultural importance)\n2. Tulsi Ghat (Historical & quiet, connected to Goswami Tulsidas)\n3. Chet Singh Ghat (Grand Chet Singh Fort & heritage site)\n4. Shivala Ghat (Old palaces & ancient temples)\n5. Harishchandra Ghat (Historic & mythological cremation site)\n6. Kedar Ghat (South-Indian architecture & Kedareshwar Mahadev)\n7. Ahilyabai Ghat (Beautiful ghat built by Queen Ahilyabai Holkar)\n8. Dashashwamedh Ghat (Main center of World-Famous Ganga Aarti)\n9. Sheetla Ghat (Maa Sheetla temple & religious significance)\n10. Lalita Ghat (Unique Nepalese-style Pashupatinath Temple)\n11. Manikarnika Ghat (Most ancient Mahashmashan, gateway to salvation)\n12. Panchganga Ghat (Confluence of 5 holy rivers & Bindu Madhav Temple)\n13. Raj Ghat (Wide & beautiful view of Ganga near Malviya Bridge)\n14. Namo Ghat (New modern ghat with giant 'Namaste' sculptures)\n\n🍲 Food & Delicacies Included:\n• Breakfast: Hot Poori-Sabzi, crispy Banarasi Jalebi & thick Lassi\n• Lunch: Traditional Purvanchal 'Baati-Chokha'\n• Special Treat: Famous Banarasi Paan\n\n💳 Terms & Conditions:\n✅ Inclusions (No Extra Cost): All-inclusive boat fare, evening Ganga Aarti on boat, breakfast, lunch, and Banarasi Paan.\n❌ Exclusions: Tour Guide services (Optional on extra charges).",
            descriptionHi = "बनारस की असली आत्मा यहाँ के घाटों में बसती है। इस विशेष पैकेज में हम आपको एक ही दिन में वाराणसी के सबसे प्रसिद्ध और ऐतिहासिक घाटों की यात्रा करवाएंगे, वह भी पूरी तरह आरामदायक नाव (Boat) के जरिए।\n\n🕒 समय (Tour Timings):\nसुबह 10:00 बजे से शाम 06:00 बजे तक\n\n🗺️ दर्शन करने योग्य 14 प्रमुख घाट (Ghats Sightseeing List):\n1. अस्सी घाट (सुबह की शांति, \"सुबह-ए-बनारस\" और सांस्कृतिक महत्ता)\n2. तुलसी घाट (गोस्वामी तुलसीदास जी से जुड़ा ऐतिहासिक और शांत स्थल)\n3. चेत सिंह घाट (भव्य चेत सिंह किला और इसका ऐतिहासिक महत्व)\n4. शिवाला घाट (पुराने महल, प्राचीन मंदिर और शांत वातावरण)\n5. हरिश्चंद्र घाट (काशी का ऐतिहासिक एवं पौराणिक अंतिम संस्कार स्थल)\n6. केदार घाट (दक्षिण भारतीय शैली की वास्तुकला और केदारेश्वर महादेव मंदिर)\n7. अहिल्याबाई घाट (महारानी अहिल्याबाई होल्कर द्वारा निर्मित सुंदर घाट)\n8. दशाश्वमेध घाट (विश्वप्रसिद्ध गंगा आरती और पूजा-अर्चन का मुख्य केंद्र)\n9. शीतला घाट (माँ शीतला का मंदिर और धार्मिक मान्यताएँ)\n10. ललिता घाट (नेपाली शैली का अनूठा पशुपतिनाथ मंदिर और सुंदर नक्काशी)\n11. मणिकर्णिका घाट (काशी का सबसे प्राचीन महाश्मशान, जिसे मोक्ष का द्वार माना जाता है)\n12. पंचगंगा घाट (पाँच पवित्र नदियों का संगम स्थल और प्राचीन बिंदु माधव मंदिर)\n13. राज घाट (मालवीय पुल के पास गंगा का विस्तृत और बेहद सुंदर दृश्य)\n14. नमो घाट (काशी का नया आधुनिक घाट, जहाँ 'नमस्ते' के विशाल स्कल्पचर्स हैं)\n\n🍲 खान-पान शामिल:\n• सुबह का नाश्ता: गरमा-गरम पूड़ी-सब्जी, कुरकुरी बनारसी जलेबी और गाढ़ी लस्सी\n• दोपहर का भोजन: पूर्वांचल का पारंपरिक और प्रसिद्ध 'बाटी-चोखा'\n• स्पेशल ट्रीट: बनारस की पहचान—मशहूर बनारसी पान\n\n💳 पैकेज की शर्तें:\n✅ शामिल (No Extra Cost): नाव की सैर (Boat Tour) से सभी घाट दर्शन, दशाश्वमेध घाट की भव्य गंगा आरती का नाव पर बैठकर दर्शन, सुबह का नाश्ता, दोपहर का खाना और स्पेशल बनारसी पान।\n❌ शामिल नहीं: इतिहास और जानकारी के लिए गाइड की सुविधा (अलग से शुल्क)।",
            highlightsEn = listOf("14 Historic Ghats Sightseeing", "Relaxing Boat ride & Evening Ganga Aarti", "Breakfast, Bati-Chokha Lunch & Banarasi Paan", "Professional Guide option (Extra charge)"),
            highlightsHi = listOf("१४ ऐतिहासिक घाटों के दर्शन", "आरामदायक नौका विहार व भव्य गंगा आरती दर्शन", "सुबह का नाश्ता, बाटी-चोखा दोपहर भोजन व पान", "गाइड का विकल्प (अलग शुल्क)"),
            iconName = "directions_boat",
            originalPrice = 1700
        ),
        LocalizedTourPackage(
            id = "one_night_two_days",
            nameEn = "Kashi 1 Night / 2 Days Tour 🌅",
            nameHi = "काशी 1 रात / 2 दिन टूर 🌅",
            price = 2499,
            durationEn = "1 Night / 2 Days",
            durationHi = "1 रात / 2 दिन",
            descriptionEn = "A divine and memorable experience of one night and two days in the city of Lord Shiva, Kashi.\n\nThis package is best for those who want to spend a night in Banaras and experience both the evening and morning beauty of the city up close.\n\n📅 Day 1: Evening Grandeur & Ghats Journey\n• 03:00 PM – Hotel Check-in: Time to check in, relax, and freshen up.\n• 04:30 PM – Boat Ride: Explore the famous ghats (Assi Ghat, Manikarnika Ghat, Harishchandra Ghat, Chet Singh Ghat) of Banaras by boat.\n• 06:30 PM – Grand Ganga Aarti: Witness the world-famous Ganga Aarti at Dashashwamedh Ghat from a boat in a VIP style.\n• 08:00 PM – Banarasi Chat Street Food: Enjoy famous Tamatar Chat, Kachori, and Kulhad Tea.\n• 09:30 PM – Dinner: Delicious dinner at the hotel or a traditional Banarasi restaurant and overnight stay.\n\n📅 Day 2: Subah-e-Banaras & Temple Darshan\n• 05:30 AM – Subah-e-Banaras: Beautiful sunrise view at Assi Ghat, morning special aarti, Vedic chanting, and classical music.\n• 08:00 AM – Banarasi Breakfast: Hot Poori-Sabzi, crispy Jalebi, and thick Banarasi Lassi.\n• 09:30 AM – Main Temple Darshan: Sri Kashi Vishwanath Temple (VIP Darshan), Maa Annapurna Temple, Kaal Bhairav Temple (Kotwal of Kashi).\n• 01:00 PM – Lunch: Pure and traditional 'Baati-Chokha'.\n• 02:30 PM – Local Sightseeing: Sankat Mochan Temple, Durga Kund, and New Vishwanath Temple (BHU).\n• 04:30 PM – Shopping & Departure: Shopping for Banarasi sarees and handicrafts, smooth tour conclusion with special Banarasi Paan and drop-off.\n\n🍲 Food & Meals Included:\n• Day 1: Famous evening street food (Chat/Tea) + delicious dinner.\n• Day 2: Traditional morning breakfast (Poori-Sabzi, Jalebi, Lassi) + famous Baati-Chokha lunch + special Banarasi Paan.\n\n💳 Package Terms:\n✅ What's Included? (No Extra Cost)\n• Hotel Stay: 1-night AC room stay (Twin Sharing).\n• Local Transport: Private AC car or E-Rickshaw for both days.\n• Boat: Full boat fare for evening Ganga Aarti and ghat sightseeing.\n• Meals: All meals as scheduled in the package.\n• VIP Pass: Ticket for hassle-free VIP Darshan at Kashi Vishwanath Temple.\n\n❌ What's Not Included? (Extra Charges)\n• Tour Guide: If you need a guide to explore history and stories, it will be charged extra.\n• Personal Expenses: Shopping, camera fees, or any order outside the package.",
            descriptionHi = "शिव की नगरी काशी में एक रात और दो दिनों का एक दिव्य और यादगार अनुभव\n\nयह पैकेज उन लोगों के लिए सबसे बेस्ट है जो बनारस में एक रात रुककर यहाँ की शाम और सुबह दोनों की खूबसूरती को करीब से देखना चाहते हैं।\n\n📅 दिन 1: शाम की भव्यता और घाटों का सफर (Day 1 - Evening & Night)\n03:00 PM – होटल चेक-इन: होटल पहुँचकर थोड़ा आराम और फ्रेश होने का समय।\n04:30 PM – नाव की सैर (Boat Ride): नाव के जरिए बनारस के प्रसिद्ध घाटों (अस्सी घाट, मणिकर्णिका घाट, हरिश्चंद्र घाट, चेत सिंह घाट) का भ्रमण।\n06:30 PM – भव्य गंगा आरती: विश्वप्रसिद्ध दशाश्वमेध घाट की गंगा आरती को नाव पर बैठकर वीआईपी तरीके से देखने का अद्भुत अनुभव।\n08:00 PM – बनारसी चाट स्ट्रीट फूड: काशी की प्रसिद्ध टमाटर चाट, कचौड़ी और कुल्हड़ वाली चाय का आनंद।\n09:30 PM – रात्रि भोजन (Dinner): होटल या बनारस के पारंपरिक रेस्टोरेंट में स्वादिष्ट डिनर और रात्रि विश्राम।\n\n📅 दिन 2: सुबह-ए-बनारस और मंदिर दर्शन (Day 2 - Morning & Afternoon)\n05:30 AM – सुबह-ए-बनारस: अस्सी घाट पर सूर्योदय का सुंदर नजारा, सुबह की विशेष आरती, वैदिक मंत्रोच्चार और शास्त्रीय संगीत का आनंद।\n08:00 AM – बनारसी नाश्ता: गरमा-गरम पूड़ी-सब्जी, कुरकुरी जलेबी और गाढ़ी बनारसी लस्सी।\n09:30 AM – मुख्य मंदिर दर्शन: श्री काशी विश्वनाथ मंदिर (वीआईपी दर्शन), माता अन्नपूर्णा मंदिर, काल भैरव मंदिर (काशी के कोतवाल)\n01:00 PM – दोपहर का भोजन (Lunch): शुद्ध और पारंपरिक 'बाटी-चोखा'।\n02:30 PM – लोकल साइटसीइंग: संकट मोचन मंदिर, दुर्गा कुंड, और न्यू विश्वनाथ मंदिर (BHU)।\n04:30 PM – शॉपिंग और विदाई: बनारसी साड़ियों और हस्तशिल्प की खरीदारी, स्पेशल बनारसी पान के साथ टूर की सुखद समाप्ति और ड्रॉप।\n\n🍲 खान-पान (Inclusion of Meals):\n• दिन 1: शाम का प्रसिद्ध स्ट्रीट फूड (चाट/चाय) + रात का शानदार डिनर।\n• दिन 2: सुबह का पारंपरिक नाश्ता (पूड़ी-सब्जी, जलेबी, लस्सी) + दोपहर का प्रसिद्ध बाटी-चोखा + स्पेशल बनारसी पान।\n\n💳 पैकेज की शर्तें (Inclusions & Exclusions):\n✅ पैकेज में क्या-क्या शामिल है? (No Extra Cost)\n• होटल स्टे: एसी रूम में 1 रात रुकने की व्यवस्था (Twin Sharing)।\n• लोकल ट्रांसपोर्ट: दोनों दिन घूमने के लिए प्राइवेट एसी कार या ई-रिक्शा की सुविधा।\n• नाव (Boat): शाम की गंगा आरती और घाट घूमने के लिए बोट का पूरा खर्च।\n• भोजन: पैकेज के अनुसार तय किया गया सभी समय का खाना।\n• वीआईपी पास: काशी विश्वनाथ मंदिर के सुगम दर्शन का टिकट।\n\n❌ क्या शामिल नहीं है? (Extra Charges)\n• गाइड की सुविधा (Tour Guide): यदि आपको इतिहास और कहानियों को जानने के लिए गाइड चाहिए, तो उसका अलग से शुल्क लगेगा।\n• निजी खर्च: शॉपिंग, कैमरा फीस या पैकेज से अलग कुछ भी ऑर्डर करने पर उसका खर्च आपका होगा।",
            highlightsEn = listOf("1 Night Hotel & AC Transport", "Evening Boat Ride & VIP Ganga Aarti", "VIP Kashi Vishwanath Darshan", "All Scheduled Meals Included"),
            highlightsHi = listOf("१ रात होटल और एसी गाड़ी की सुविधा", "शाम की नौका विहार और वीआईपी गंगा आरती", "सुगम काशी विश्वनाथ मंदिर वीआईपी दर्शन", "सभी समय का स्वादिष्ट भोजन शामिल"),
            iconName = "wb_sunny",
            originalPrice = 3500
        ),
        LocalizedTourPackage(
            id = "temple_walk",
            nameEn = "Divine Temples Heritage Tour",
            nameHi = "दिव्य मंदिर धरोहर यात्रा",
            price = 1200,
            durationEn = "5 Hours (8:00 AM - 1:00 PM)",
            durationHi = "5 घंटे (सुबह 8:00 - दोपहर 1:00)",
            descriptionEn = "A sacred guided walking tour through the narrow alleys of Varanasi, visiting the most auspicious and historical temples of Kashi.",
            descriptionHi = "वाराणसी की तंग गलियों से गुजरने वाली एक पवित्र निर्देशित पैदल यात्रा, जिसमें काशी के सबसे शुभ और ऐतिहासिक मंदिरों के दर्शन शामिल हैं।",
            highlightsEn = listOf("Kashi Vishwanath Corridor", "Sankat Mochan Hanuman", "Durga Temple", "Kaal Bhairav Temple"),
            highlightsHi = listOf("काशी विश्वनाथ कॉरिडोर", "संकट मोचन हनुमान", "दुर्गा मंदिर", "काल भैरव मंदिर"),
            iconName = "temple_hindu"
        ),
        LocalizedTourPackage(
            id = "evening_aarti",
            nameEn = "Ganga Aarti Evening Cruise",
            nameHi = "गंगा आरती शाम का नौका विहार",
            price = 450,
            durationEn = "2.5 Hours (5:30 PM - 8:00 PM)",
            durationHi = "2.5 घंटे (शाम 5:30 - रात 8:00)",
            descriptionEn = "A shared grand boat cruise from Assi Ghat to Dashashwamedh Ghat to witness the stunning multi-priest evening prayer ceremony from the river.",
            descriptionHi = "अस्सी घाट से दशाश्वमेध घाट तक एक भव्य नौका विहार, जिससे नदी से होने वाली भव्य शाम की महाआरती का सीधा अनुभव लिया जा सके।",
            highlightsEn = listOf("Ganga Aarti from Boat", "Ganga Clay Lamp Floating", "Historic Ghat Explanations", "Sunset photography"),
            highlightsHi = listOf("नाव से गंगा आरती", "गंगा में दीप दान", "ऐतिहासिक घाटों का परिचय", "सूर्यास्त फोटोग्राफी"),
            iconName = "flare"
        ),
        LocalizedTourPackage(
            id = "sarnath_day",
            nameEn = "Sarnath Historical Excursion",
            nameHi = "सारनाथ ऐतिहासिक भ्रमण",
            price = 800,
            durationEn = "4 Hours (10:00 AM - 2:00 PM)",
            durationHi = "4 घंटे (सुबह 10:00 - दोपहर 2:00)",
            descriptionEn = "Explore the peaceful Buddhist heritage town. Visit major stupas, temple ruins, Japanese temple, and the world-renowned Sarnath museum.",
            descriptionHi = "शांतिपूर्ण बौद्ध विरासत शहर का अन्वेषण करें। प्रमुख स्तूपों, मंदिर के खंडहरों, जापानी मंदिर और विश्व प्रसिद्ध सारनाथ संग्रहालय का भ्रमण करें।",
            highlightsEn = listOf("Dhamek Stupa", "Mulagandhakuti Vihara", "Ashoka Pillar Ruins", "Archaeological Museum Entry"),
            highlightsHi = listOf("धमेक स्तूप", "मूलगंधकुटी विहार", "अशोक स्तंभ खंडहर", "पुरातात्विक संग्रहालय प्रवेश"),
            iconName = "explore"
        ),
        LocalizedTourPackage(
            id = "food_walk",
            nameEn = "Banaras Culinary Delights Walk",
            nameHi = "बनारस स्वादिष्ट व्यंजन यात्रा",
            price = 600,
            durationEn = "3 Hours (4:00 PM - 7:00 PM)",
            durationHi = "3 घंटे (शाम 4:00 - शाम 7:00)",
            descriptionEn = "A delicious street food walk through the famous alleys. Taste the local Tamatar Chaat, Kulhad Lassi, Malaiyo (seasonal), and Banarasi Paan.",
            descriptionHi = "प्रसिद्ध गलियों में एक स्वादिष्ट स्ट्रीट फूड वॉक। स्थानीय टमाटर चाट, कुल्हड़ लस्सी, मलइयो (मौसमी), और बनारसी पान का स्वाद चखें।",
            highlightsEn = listOf("Kashi Chat Bhandar", "Legendary Kulhad Lassi", "Banarasi Sweet Sampling", "Mouth-watering Paan experience"),
            highlightsHi = listOf("काशी चाट भंडार", "प्रसिद्ध कुल्हड़ लस्सी", "बनारसी मिठाइयों का स्वाद", "मुंह में पानी लाने वाला पान अनुभव"),
            iconName = "restaurant_menu"
        )
    )

    // Reactive StateFlow returning correctly localized Tour Packages
    val tourPackages: StateFlow<List<TourPackage>> = _currentLanguage
        .map { lang -> localizedTourPackagesList.map { it.toTourPackage(lang) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = localizedTourPackagesList.map { it.toTourPackage(_currentLanguage.value) }
        )

    // Kashi AI Chat States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Namaste! 🙏 I am Kashi AI, your personal Banaras travel guide. Ask me anything about Ghats, Temples, Food, custom itineraries, or the history of this ancient city!", isUser = false)
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TourRepository(database.bookingDao())

        allBookings = repository.allBookings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSavedPlaces = repository.allSavedPlaces.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Book Tour
    fun bookTour(
        tourName: String,
        tourPrice: Int,
        userName: String,
        userPhone: String,
        travelDate: String,
        personsCount: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.insertBooking(
                TourBooking(
                    tourName = tourName,
                    tourPrice = tourPrice,
                    userName = userName,
                    userPhone = userPhone,
                    travelDate = travelDate,
                    personsCount = personsCount
                )
            )
            onSuccess()
        }
    }

    // Cancel Booking
    fun cancelBooking(bookingId: Int) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
        }
    }

    // Bookmark Saved Place
    fun toggleSavedPlace(place: Place) {
        viewModelScope.launch {
            val isSaved = repository.isPlaceSaved(place.id)
            if (isSaved) {
                repository.unsavePlace(place.id)
            } else {
                repository.savePlace(
                    SavedPlace(
                        id = place.id,
                        name = place.name,
                        type = place.type,
                        description = place.description
                    )
                )
            }
        }
    }

    // Check if place is saved
    fun isPlaceSaved(placeId: String): Boolean {
        return allSavedPlaces.value.any { it.id == placeId }
    }

    // Send Message to Kashi AI
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        // Add user message to UI
        val currentMessages = _chatMessages.value.toMutableList()
        currentMessages.add(ChatMessage(text, isUser = true))
        _chatMessages.value = currentMessages

        _isChatLoading.value = true

        viewModelScope.launch {
            // Build conversational history for Gemini REST API
            // Map the last 8 messages to Content/Part structures to stay fast
            val historyLength = maxOf(0, currentMessages.size - 9)
            val chatHistory = currentMessages.subList(historyLength, currentMessages.size - 1).map { msg ->
                Content(parts = listOf(Part(text = msg.text)))
            }

            val reply = GeminiClient.getChatResponse(text, chatHistory)

            // Add AI response to UI
            val updatedMessages = _chatMessages.value.toMutableList()
            updatedMessages.add(ChatMessage(reply, isUser = false))
            _chatMessages.value = updatedMessages

            _isChatLoading.value = false
        }
    }

    // Clear Chat
    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage("Namaste! 🙏 I am Kashi AI, your personal Banaras travel guide. Ask me anything about Ghats, Temples, Food, custom itineraries, or the history of this ancient city!", isUser = false)
        )
    }
}

class TourViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
