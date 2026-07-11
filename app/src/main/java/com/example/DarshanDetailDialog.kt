package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.Place

// Model for rich timing & attraction information
data class DarshanTimingInfo(
    val summerTimings: String,
    val winterTimings: String,
    val aartiTimings: String,
    val attractionsKey: String,
    val bestTime: String,
    val entryFee: String,
    val famousFor: String? = null,
    val nearbyLocations: String? = null,
    val location: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null
)

// Helper function to resolve highly accurate details for Varanasi's 28 places
fun getDarshanTimingInfo(placeId: String, lang: AppLanguage): DarshanTimingInfo {
    return when (placeId) {
        "kashi_vishwanath" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 3:00 बजे - रात 11:00 बजे",
                    winterTimings = "सुबह 4:00 बजे - रात 10:00 बजे",
                    aartiTimings = "मंगला आरती: सुबह 3:00 बजे\nभोग आरती: दोपहर 11:30 बजे\nसप्तर्षि आरती: शाम 7:00 बजे\nशृंगार आरती: रात 9:00 बजे\nशयन आरती: रात 10:30 बजे",
                    attractionsKey = "स्वर्ण कलश शिखर, भव्य काशी कॉरिडोर, आदि शिवलिंग, ज्ञानवापी कूप",
                    bestTime = "सुबह 4:00 बजे - सुबह 8:00 बजे (सुगम दर्शन)",
                    entryFee = "निशुल्क (विशेष दर्शन हेतु सुगम दर्शन टिकट: ₹300)"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "3:00 AM - 11:00 PM",
                    winterTimings = "4:00 AM - 10:00 PM",
                    aartiTimings = "Mangala Aarti: 3:00 AM\nBhog Aarti: 11:30 AM\nSaptarishi Aarti: 7:00 PM\nShringar Aarti: 9:00 PM\nShayan Aarti: 10:30 PM",
                    attractionsKey = "Golden Shikhara Dome, Divine Kashi Corridor, Ancient Adi Shivlingam, Gyanvapi Well",
                    bestTime = "4:00 AM - 8:00 AM (for lesser crowd)",
                    entryFee = "Free (Sugam Darshan VIP Pass: ₹300)"
                )
            }
        }
        "kaal_bhairav" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 5:00 बजे - दोपहर 1:30 बजे, शाम 4:30 बजे - रात 10:00 बजे",
                    winterTimings = "सुबह 5:30 बजे - दोपहर 1:00 बजे, शाम 4:30 बजे - रात 9:30 बजे",
                    aartiTimings = "मंगला आरती: सुबह 5:00 बजे\nसंध्या आरती: रात 8:30 बजे",
                    attractionsKey = "काशी के रक्षक 'कोतवाल' का रजत मुखौटा, सुरक्षा का पवित्र काला धागा (कंडा), सरसों तेल का दीपक",
                    bestTime = "रविवार और मंगलवार को विशेष पूजा",
                    entryFee = "निशुल्क"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "5:00 AM - 1:30 PM, 4:30 PM - 10:00 PM",
                    winterTimings = "5:30 AM - 1:00 PM, 4:30 PM - 9:30 PM",
                    aartiTimings = "Mangala Aarti: 5:00 AM\nEvening Aarti: 8:30 PM",
                    attractionsKey = "Silver Deity Mask of the 'Guardian Kotwal', Sacred Black Thread (Kanda), Mustard Oil Lamp Ritual",
                    bestTime = "Sundays & Tuesdays (Special Auspicious Days)",
                    entryFee = "Free"
                )
            }
        }
        "annapurna_temple" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 4:00 बजे - रात 10:00 बजे",
                    winterTimings = "सुबह 4:30 बजे - रात 9:30 बजे",
                    aartiTimings = "सुबह की आरती: सुबह 4:30 बजे\nमहाभोग आरती: दोपहर 12:00 बजे\nसंध्या आरती: रात 8:00 बजे\n(मुफ़्त महाप्रसाद लंच: सुबह 9:00 बजे से दोपहर 3:00 बजे तक)",
                    attractionsKey = "अन्नपूर्णा माता की भव्य स्वर्ण मूर्ति, अन्नकूट त्योहार (दीपावली पर), अन्न भण्डार प्रसाद",
                    bestTime = "दोपहर 11:30 बजे (आरती और महाप्रसाद का सर्वोत्तम समय)",
                    entryFee = "निशुल्क"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "4:00 AM - 10:00 PM",
                    winterTimings = "4:30 AM - 9:30 PM",
                    aartiTimings = "Morning Aarti: 4:30 AM\nMahabhog Aarti: 12:00 PM\nEvening Aarti: 8:00 PM\n(Free Prasad Meals served daily: 9:00 AM - 3:00 PM)",
                    attractionsKey = "Golden Idol of Mother Annapurna, Annakut Festival (on Diwali), Free Sacred Kitchen Meals",
                    bestTime = "11:30 AM (best for Aarti & free lunch prasadam)",
                    entryFee = "Free"
                )
            }
        }
        "sankat_mochan" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 5:00 बजे - दोपहर 12:00 बजे, दोपहर 3:00 बजे - रात 11:00 बजे",
                    winterTimings = "सुबह 5:30 बजे - दोपहर 11:30 बजे, दोपहर 3:30 बजे - रात 10:30 बजे",
                    aartiTimings = "मंगला आरती: सुबह 5:00 बजे\nशयन आरती: रात 10:00 बजे\n(मंगलवार और शनिवार को पूरे दिन अखंड सुंदरकांड पाठ)",
                    attractionsKey = "प्रसिद्ध शुद्ध घी के बेसन लड्डू, वानर सेना (बंदरों की अटखेलियां), तुलसीदास जी का हस्तलिखित ग्रंथ",
                    bestTime = "मंगलवार और शनिवार (विशेष हनुमान पूजन)",
                    entryFee = "निशुल्क"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "5:00 AM - 12:00 PM, 3:00 PM - 11:00 PM",
                    winterTimings = "5:30 AM - 11:30 AM, 3:30 PM - 10:30 PM",
                    aartiTimings = "Mangala Aarti: 5:00 AM\nShayan Aarti: 10:00 PM\n(Non-stop Sundarkand recitation on Tuesdays & Saturdays)",
                    attractionsKey = "Legendary Pure Ghee Besan Laddoo Prasad, Friendly Monkey Troops, Tulsidas Ji's Historical Memorial",
                    bestTime = "Tuesdays & Saturdays (Special Prayers & Festivity)",
                    entryFee = "Free"
                )
            }
        }
        "bhu_vt" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 4:00 बजे - दोपहर 12:00 बजे, दोपहर 1:00 बजे - रात 9:00 PM",
                    winterTimings = "सुबह 4:30 बजे - दोपहर 12:00 बजे, दोपहर 1:00 बजे - रात 8:30 PM",
                    aartiTimings = "मंगला आरती: सुबह 4:15 बजे\nभोग आरती: दोपहर 11:30 बजे\nसंध्या आरती: शाम 7:30 बजे",
                    attractionsKey = "दुनिया का सबसे ऊँचा संगमरमर शिखर (77 मीटर), दीवारों पर नक्काशीदार श्रीमद्भगवद्गीता के श्लोक, बीएचयू परिसर का शांत वातावरण",
                    bestTime = "शाम 6:30 बजे - रात 8:00 बजे (आरती एवं शीतल बयार)",
                    entryFee = "निशुल्क"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "4:00 AM - 12:00 PM, 1:00 PM - 9:00 PM",
                    winterTimings = "4:30 AM - 12:00 PM, 1:00 PM - 8:30 PM",
                    aartiTimings = "Mangala Aarti: 4:15 AM\nBhog Aarti: 11:30 AM\nEvening Aarti: 7:30 PM",
                    attractionsKey = "Tallest Temple Tower in the World (77m Shikhara), Srimad Bhagavad Gita Verses on Walls, Peaceful Lush BHU University Green Campus",
                    bestTime = "6:30 PM - 8:00 PM (for Aarti & evening breeze)",
                    entryFee = "Free"
                )
            }
        }
        "durga_kund" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 5:00 बजे - दोपहर 1:00 बजे, शाम 3:00 बजे - रात 11:00 बजे",
                    winterTimings = "सुबह 5:30 बजे - दोपहर 12:30 बजे, शाम 3:30 बजे - रात 10:30 बजे",
                    aartiTimings = "मंगला आरती: सुबह 5:30 बजे\nसंध्या आरती: रात 8:00 बजे",
                    attractionsKey = "लाल बलुआ पत्थर की नागर वास्तुकला, प्राचीन पवित्र दुर्गा कुंड तालाब, नवरात्रि का भव्य उत्सव",
                    bestTime = "नवरात्रि उत्सव के दौरान (भव्य श्रृंगार)",
                    entryFee = "निशुल्क"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "5:00 AM - 1:00 PM, 3:00 PM - 11:00 PM",
                    winterTimings = "5:30 AM - 12:30 PM, 3:30 PM - 10:30 PM",
                    aartiTimings = "Mangala Aarti: 5:30 AM\nEvening Aarti: 8:00 PM",
                    attractionsKey = "Red Sandstone Nagara Style Architecture, Sacred Durga Kund Pool, Magnificent Navratri Festival Decorations",
                    bestTime = "During Navratri (Nine Sacred Nights)",
                    entryFee = "Free"
                )
            }
        }
        "dashashwamedh_ghat" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "24 घंटे खुला",
                    winterTimings = "24 घंटे खुला",
                    aartiTimings = "विश्व प्रसिद्ध भव्य गंगा आरती:\n• गर्मियों में: शाम 6:45 बजे से रात 7:30 बजे\n• सर्दियों में: शाम 6:00 बजे से शाम 6:45 बजे",
                    attractionsKey = "दशाश्वमेध यज्ञ वेदी, भव्य आरती अर्चक मंच, नदी में सैकड़ों बजरे (नावें), दीपदान की अलौकिक छवि",
                    bestTime = "शाम 5:30 बजे (आरती हेतु नाव की सीट बुक करने के लिए)",
                    entryFee = "निशुल्क (नाव की सवारी एवं बजरा सीट का किराया अलग से)"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "Open 24 Hours",
                    winterTimings = "Open 24 Hours",
                    aartiTimings = "World-Famous Evening Ganga Aarti:\n• Summer: 6:45 PM - 7:30 PM\n• Winter: 6:00 PM - 6:45 PM",
                    attractionsKey = "Dashashwamedh Sacrifice Site, Dynamic Multi-tier Puja Platforms, Hundreds of Illuminated Boats, Floating Diya (Lamp) Ritual",
                    bestTime = "5:30 PM (arrive early to secure a boat seat for the Aarti)",
                    entryFee = "Free (Boat rides & VIP chairs at the ghat are chargeable)"
                )
            }
        }
        "assi_ghat" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "24 घंटे खुला",
                    winterTimings = "24 घंटे खुला",
                    aartiTimings = "सुबह-ए-बनारस (सूर्योदय अनुष्ठान):\n• गर्मियों में: सुबह 5:00 बजे\n• सर्दियों में: सुबह 5:45 बजे\nसंध्या गंगा आरती: शाम 6:30 बजे (गर्मियों में) / शाम 5:45 बजे (सर्दियों में)",
                    attractionsKey = "यज्ञशाला हवन, वैदिक बटुकों द्वारा मंगलाचरण, शास्त्रीय गायन-वादन संगीत सभा, सुबह का योग शिविर",
                    bestTime = "सुबह 4:45 बजे (सुबह-ए-बनारस का संपूर्ण अनुभव लेने हेतु)",
                    entryFee = "निशुल्क"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "Open 24 Hours",
                    winterTimings = "Open 24 Hours",
                    aartiTimings = "Subah-e-Banaras Sunrise Ritual:\n• Summer: 5:00 AM\n• Winter: 5:45 AM\nEvening Ganga Aarti: 6:30 PM (Summer) / 5:45 PM (Winter)",
                    attractionsKey = "Yagya Shala Fire Ritual, Vedic Chanting by Gurukul Students, Live Indian Classical Music Performance, Sunrise Yoga Session",
                    bestTime = "4:45 AM (must attend for the soulful Subah-e-Banaras experience)",
                    entryFee = "Free"
                )
            }
        }
        "namo_ghat" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "24 घंटे खुला",
                    winterTimings = "24 घंटे खुला",
                    aartiTimings = "दैनिक संध्या महाआरती: शाम 6:30 बजे (गर्मियों में) / शाम 5:45 बजे (सर्दियों में)",
                    attractionsKey = "नमस्ते मुद्रा में जुड़े हुए हाथों की विशाल धातु मूर्तियाँ, आधुनिक हेलीपैड परिसर, भव्य सीएनजी क्रूज टर्मिनल, वाटर स्पोर्ट्स और बच्चों का पार्क",
                    bestTime = "शाम 4:30 बजे से रात 9:00 बजे तक (रंग-बिरंगी लाइटें और ठंडी हवा)",
                    entryFee = "निशुल्क (पार्किंग और मनोरंजक गतिविधियों का मामूली शुल्क)"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "Open 24 Hours",
                    winterTimings = "Open 24 Hours",
                    aartiTimings = "Daily Evening Ganga Aarti: 6:30 PM (Summer) / 5:45 PM (Winter)",
                    attractionsKey = "Giant Folded Hands 'Namaste' Sculptures, Modern Heliport Compound, Premium Alaknanda Cruise Terminal, Speedboats & Children's Park",
                    bestTime = "4:30 PM - 9:00 PM (for sunset views and beautiful decorative lighting)",
                    entryFee = "Free (standard parking and activity fees apply)"
                )
            }
        }
        "manikarnika_ghat" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "24 घंटे खुला (निरंतर चिता दहन)",
                    winterTimings = "24 घंटे खुला (निरंतर चिता दहन)",
                    aartiTimings = "श्मशान महाकाली पूजन: शाम 7:00 बजे\n(विशेष महाश्मशान मसान होली होली: रंगभरी एकादशी पर)",
                    attractionsKey = "चक्रपुष्करणी पवित्र कुंड, मणिकर्णेश्वर महादेव गुप्त शिव मंदिर, भगवान शिव के चरण पादुका शिला",
                    bestTime = "नदी में नाव की सवारी से सूर्यास्त के समय अवलोकन (संवेदनशील स्थान)",
                    entryFee = "निशुल्क (चिता दान या फोटोग्राफी पूरी तरह से वर्जित है)"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "Open 24 Hours (Continuous cremation)",
                    winterTimings = "Open 24 Hours (Continuous cremation)",
                    aartiTimings = "Masan Mahakali Worship: 7:00 PM Daily\n(Special Masan Holi of ashes celebrated on Rangbhari Ekadashi)",
                    attractionsKey = "Chakra Pushkarini Sacred Well, Manikarneshwar Mahadev Temple, Sacred Footprints (Charan Paduka) of Lord Vishnu",
                    bestTime = "Best viewed quietly from a river boat during sunset (maintain silence & respect)",
                    entryFee = "Free (Photography is strictly prohibited at the cremation area)"
                )
            }
        }
        "sarnath" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 8:00 बजे - शाम 6:00 बजे",
                    winterTimings = "सुबह 8:30 बजे - शाम 5:00 बजे",
                    aartiTimings = "सारनाथ लाइट एंड साउंड शो (बुद्ध गाथा):\n• गर्मियों में: शाम 7:30 बजे\n• सर्दियों में: शाम 6:30 बजे",
                    attractionsKey = "धमेक स्तूप (सम्राट अशोक द्वारा निर्मित), मूलगंध कुटी विहार बुद्ध मंदिर, राष्ट्रीय प्रतीक सिंह चतुर्मुख स्तंभ, हिरण पार्क",
                    bestTime = "सुबह 9:00 बजे - दोपहर 12:00 बजे (जब मौसम अनुकूल हो)",
                    entryFee = "भारतीय पर्यटकों के लिए ₹25, विदेशी पर्यटकों के लिए ₹300 (म्यूजियम टिकट अलग)"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "8:00 AM - 6:00 PM",
                    winterTimings = "8:30 AM - 5:00 PM",
                    aartiTimings = "Sarnath Laser Light & Sound Show (Life of Buddha):\n• Summer: 7:30 PM\n• Winter: 6:30 PM",
                    attractionsKey = "Dhamek Stupa (built by Emperor Ashoka), Mulagandha Kuti Vihar Buddhist Temple, Ashoka Pillar Lion Capital (National Emblem), Deer Park",
                    bestTime = "9:00 AM - 12:00 PM (for comfortable walking and exploring)",
                    entryFee = "₹25 for Indian nationals, ₹300 for foreigners (Archaeological Museum has a separate ticket)"
                )
            }
        }
        "ramnagar_fort" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 9:00 बजे - शाम 5:30 बजे",
                    winterTimings = "सुबह 9:30 बजे - शाम 5:00 बजे",
                    aartiTimings = "N/A (कोई दैनिक आरती नहीं)\n(भव्य रामलीला उत्सव: दशहरा के दौरान)",
                    attractionsKey = "शाही विंटेज कार गैराज, प्राचीन अस्त्र-शस्त्र संग्रहालय, अद्वितीय विशाल खगोलीय घड़ी, गंगा के किनारे की मजबूत बलुआ पत्थर की दीवारें",
                    bestTime = "दोपहर 3:00 बजे से शाम 5:00 बजे (सूर्यास्त के दृश्य)",
                    entryFee = "वयस्कों के लिए ₹75, बच्चों के लिए ₹40"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "9:00 AM - 5:30 PM",
                    winterTimings = "9:30 AM - 5:00 PM",
                    aartiTimings = "N/A (No daily religious aarti)\n(Historic Ramlila theater organized during Dussehra month)",
                    attractionsKey = "Royal Vintage Car Garage, Ancient Armory & Weapons Museum, Majestic Astronomical Clock, Massive Red Sandstone Fort Ramparts on Ganga Bank",
                    bestTime = "3:00 PM - 5:00 PM (great afternoon lighting & cool river breeze)",
                    entryFee = "₹75 for adults, ₹40 for children"
                )
            }
        }
        "street_food_trail" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 7:00 बजे - रात 11:30 बजे",
                    winterTimings = "सुबह 7:30 बजे - रात 11:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "चाची की प्रसिद्ध कचौड़ी-सब्जी, काशी चाट भंडार की टमाटर चाट, रामनगर की मलाईदार लस्सी, प्रसिद्ध बनारसी मीठा पान",
                    bestTime = "सुबह 8:00 बजे (नाश्ते के लिए), शाम 6:00 बजे (चाट के लिए)",
                    entryFee = "निशुल्क (दुकानों पर सीधे भुगतान)",
                    famousFor = "कचौड़ी-सब्जी, टमाटर चाट, गाढ़ी लस्सी और प्रसिद्ध बनारसी पान",
                    location = "गोदौलिया और चौक क्षेत्र, वाराणसी",
                    nearbyLocations = "दशाश्वमेध घाट, काशी विश्वनाथ मंदिर",
                    phone = "+919451390490",
                    whatsapp = "919451390490"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "7:00 AM - 11:30 PM",
                    winterTimings = "7:30 AM - 11:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Famous Kachori-Sabzi, Legendary Tamatar Chaat, Thick Creamy Lassi, Iconic Banarasi Meetha Paan",
                    bestTime = "8:00 AM (Breakfast) & 6:00 PM (Evening Chaat)",
                    entryFee = "Free (Pay directly to vendors)",
                    famousFor = "Kachori-Sabzi, Tamatar Chaat, Thick Lassi, and Banarasi Paan",
                    location = "Godowlia and Chowk Areas, Varanasi",
                    nearbyLocations = "Dashashwamedh Ghat, Kashi Vishwanath Temple",
                    phone = "+919451390490",
                    whatsapp = "919451390490"
                )
            }
        }
        "annapurna_bhandara" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "दोपहर 11:30 बजे - दोपहर 2:00 बजे",
                    winterTimings = "दोपहर 11:30 बजे - दोपहर 2:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "मां अन्नपूर्णा मंदिर अन्नक्षेत्र",
                    bestTime = "दोपहर 11:30 बजे - दोपहर 1:30 बजे",
                    entryFee = "निशुल्क (महाप्रसाद)",
                    famousFor = "खिचड़ी, स्वादिष्ट दाल-चावल, सब्जी, सात्विक महाप्रसाद",
                    location = "काशी विश्वनाथ मंदिर के पास, गोदौलिया, वाराणसी",
                    nearbyLocations = "काशी विश्वनाथ मंदिर, दशाश्वमेध घाट, मणिकर्णिका घाट",
                    phone = "+919451390490",
                    whatsapp = "919451390490"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "11:30 AM - 2:00 PM",
                    winterTimings = "11:30 AM - 2:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Maa Annapurna Mandir Prasad Hall",
                    bestTime = "11:30 AM - 1:30 PM",
                    entryFee = "Free (Mahaprasad)",
                    famousFor = "Khichdi, Delicious Dal-Rice, Mixed Sabzi, Saathvik Prasad",
                    location = "Near Kashi Vishwanath Temple, Godowlia, Varanasi",
                    nearbyLocations = "Kashi Vishwanath Temple, Dashashwamedh Ghat, Manikarnika Ghat",
                    phone = "+919451390490",
                    whatsapp = "919451390490"
                )
            }
        }
        "annapurna_free_mess" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "दोपहर 12:00 बजे - दोपहर 2:00 बजे",
                    winterTimings = "दोपहर 12:00 बजे - दोपहर 2:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "अन्नपूर्णा फ्री भोजनम मेस रसोई",
                    bestTime = "दोपहर 12:00 बजे - दोपहर 1:30 बजे",
                    entryFee = "निशुल्क भोजन सेवा",
                    famousFor = "पूर्ण सात्विक और स्वच्छ शाकाहारी दोपहर का भोजन",
                    location = "नरिया, बीएचयू के पास, वाराणसी",
                    nearbyLocations = "संकट मोचन हनुमान मंदिर, नया विश्वनाथ मंदिर (BHU)",
                    phone = "+919451390491",
                    whatsapp = "919451390491"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "12:00 PM - 2:00 PM",
                    winterTimings = "12:00 PM - 2:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Annapurna Free Bhojanam Mess Kitchen",
                    bestTime = "12:00 PM - 1:30 PM",
                    entryFee = "Free Dining Service",
                    famousFor = "Full Saathvik and Clean Vegetarian Lunch Meal",
                    location = "Naria, Near BHU, Varanasi",
                    nearbyLocations = "Sankat Mochan Hanuman Temple, New Vishwanath Temple (BHU)",
                    phone = "+919451390491",
                    whatsapp = "919451390491"
                )
            }
        }
        "iskcon_bhandara" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "दैनिक आरती के बाद (दोपहर और शाम)",
                    winterTimings = "दैनिक आरती के बाद (दोपहर और शाम)",
                    aartiTimings = "मध्याह्न आरती: दोपहर 12:30 बजे, संध्या आरती: शाम 7:00 बजे",
                    attractionsKey = "इस्कॉन मंदिर सत्संग और कीर्तन हॉल",
                    bestTime = "रविवार दोपहर 12:30 बजे (रविवार महाप्रसाद भोज)",
                    entryFee = "निशुल्क (कृष्ण महाप्रसाद)",
                    famousFor = "स्वादिष्ट खिचड़ी प्रसाद, मलाईदार हलवा, रविवार भव्य महाप्रसाद",
                    location = "B 38/182-A, बिर्दोपुर, महमूरगंज, वाराणसी",
                    nearbyLocations = "दुर्गा कुंड मंदिर, तुलसी मानस मंदिर, त्रिदेव मंदिर",
                    phone = "+919451390492",
                    whatsapp = "919451390492"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "After Daily Aarti (Afternoon & Evening)",
                    winterTimings = "After Daily Aarti (Afternoon & Evening)",
                    aartiTimings = "Noon Aarti: 12:30 PM, Evening Aarti: 7:00 PM",
                    attractionsKey = "ISKCON Temple Satsang & Kirtan Hall",
                    bestTime = "Sundays at 12:30 PM (Grand Sunday Feast)",
                    entryFee = "Free (Krishna Mahaprasad)",
                    famousFor = "Delicious Khichdi Prasad, Rich Halwa, Sunday Grand Feast",
                    location = "B 38/182-A, Birdopur, Mahmoorganj, Varanasi",
                    nearbyLocations = "Durga Kund Temple, Tulsi Manas Mandir, Tridev Mandir",
                    phone = "+919451390492",
                    whatsapp = "919451390492"
                )
            }
        }
        "ram_bhandar" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "मई से अगस्त: सुबह 7:30 बजे - सुबह 11:00 बजे, दोपहर 2:30 बजे - शाम 6:00 बजे",
                    winterTimings = "सितंबर से अप्रैल: सुबह 7:30 बजे - सुबह 11:00 बजे, दोपहर 2:30 बजे - शाम 6:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "द राम भंडार",
                    bestTime = "सुबह 8:00 बजे - सुबह 10:00 बजे (गरमा-गरम कचौड़ी)",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "मसालेदार और खुशबूदार कचौड़ी-सब्जी और रसीली जलेबी",
                    location = "C.K. 37/35, चौक, वाराणसी",
                    nearbyLocations = "काशी विश्वनाथ मंदिर, चौक बाजार, काल भैरव मंदिर",
                    phone = "+919451390493",
                    whatsapp = "919451390493"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "May to Aug: 7:30 AM - 11:00 AM, 2:30 PM - 6:00 PM",
                    winterTimings = "Sep to Apr: 7:30 AM - 11:00 AM, 2:30 PM - 6:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "The Ram Bhandar",
                    bestTime = "8:00 AM - 10:00 AM (for hot Kachori breakfast)",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Spicy and Fragrant Kachori-Sabzi with Rich Potato Gravy and Sweet Jalebis",
                    location = "C.K. 37/35, Chowk, Varanasi",
                    nearbyLocations = "Kashi Vishwanath Temple, Chowk Market, Kaal Bhairav Temple",
                    phone = "+919451390493",
                    whatsapp = "919451390493"
                )
            }
        }
        "kashi_chaat_bhandar" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "दोपहर 2:00 बजे - रात 10:30 बजे",
                    winterTimings = "दोपहर 2:00 बजे - रात 10:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "काशी चाट भंडार गोदौलिया",
                    bestTime = "शाम 5:00 बजे - रात 8:30 बजे",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "मिट्टी के कुल्हड़ में परोसी जाने वाली असली टमाटर चाट और आलू टिक्की",
                    location = "D. 15/2, गोदौलिया चौराहा, वाराणसी",
                    nearbyLocations = "दशाश्वमेध घाट, गोदौलिया मार्केट, काशी विश्वनाथ मंदिर",
                    phone = "+915422412116",
                    whatsapp = "915422412116"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "2:00 PM - 10:30 PM",
                    winterTimings = "2:00 PM - 10:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Kashi Chaat Bhandar Godowlia",
                    bestTime = "5:00 PM - 8:30 PM (Evening Chat rush)",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Authentic Hot Tamatar Chaat in Clay Cups and Desi Ghee Aloo Tikki",
                    location = "D. 15/2, Godowlia Crossing, Varanasi",
                    nearbyLocations = "Dashashwamedh Ghat, Godowlia Market, Kashi Vishwanath Temple",
                    phone = "+915422412116",
                    whatsapp = "915422412116"
                )
            }
        }
        "deena_chaat_bhandar" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "दोपहर 1:00 बजे - रात 10:00 बजे",
                    winterTimings = "दोपहर 1:00 बजे - रात 9:30 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "दीना चाट भंडार",
                    bestTime = "शाम 4:30 बजे - रात 8:30 बजे",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "कुरकुरी पालक पत्ता चाट, चूड़ा मटर, मलाईदार दही भल्ला और टोकरी चाट",
                    location = "लक्सा रोड, दशाश्वमेध के पास, वाराणसी",
                    nearbyLocations = "दशाश्वमेध घाट, गोदौलिया मार्केट, गिरजाघर चौराहा",
                    phone = "+919415223403",
                    whatsapp = "919415223403"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "1:00 PM - 10:00 PM",
                    winterTimings = "1:00 PM - 9:30 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Deena Chaat Bhandar",
                    bestTime = "4:30 PM - 8:30 PM",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Crunchy Palak Patta Chaat, Chura Matar, Dahi Bhalla, and Basket (Tokri) Chaat",
                    location = "Luxa Road, near Dashashwamedh, Varanasi",
                    nearbyLocations = "Dashashwamedh Ghat, Godowlia Market, Girjghar Crossing",
                    phone = "+919415223403",
                    whatsapp = "919415223403"
                )
            }
        }
        "pehalwan_lassi" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 9:00 बजे - रात 11:00 बजे",
                    winterTimings = "सुबह 9:00 बजे - रात 10:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "पहलवान लस्सी लंका",
                    bestTime = "दोपहर 12:00 बजे - शाम 4:00 बजे (गर्मियों में)",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "पारंपरिक कुल्हड़ में परोसी जाने वाली गाढ़ी मलाई लस्सी और मीठी रबड़ी",
                    location = "लंका चौराहा, बीएचयू के पास, वाराणसी",
                    nearbyLocations = "बीएचयू मुख्य द्वार, संकट मोचन हनुमान मंदिर, रविंद्रपुरी",
                    phone = "+919451390494",
                    whatsapp = "919451390494"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "9:00 AM - 11:00 PM",
                    winterTimings = "9:00 AM - 10:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Pehalwan Lassi Lanka",
                    bestTime = "12:00 PM - 4:00 PM (Summer coolness)",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Incredibly Thick Malai Lassi and Sweet Rabdi Lassi in traditional earthen glasses",
                    location = "Lanka Crossing, Near BHU, Varanasi",
                    nearbyLocations = "BHU Main Gate, Sankat Mochan Temple, Ravindrapuri",
                    phone = "+919451390494",
                    whatsapp = "919451390494"
                )
            }
        }
        "neelu_kachori_bhandar" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 7:00 बजे - दोपहर 2:00 बजे",
                    winterTimings = "सुबह 7:30 बजे - दोपहर 2:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "नीलू कचौड़ी भंडार कचौड़ी गली",
                    bestTime = "सुबह 8:00 बजे - सुबह 10:30 बजे",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "लकड़ी की आँच पर बनी कुरकुरी और तीखी खस्ता कचौड़ी-सब्जी",
                    location = "कचौड़ी गली, चौक, वाराणसी",
                    nearbyLocations = "काशी विश्वनाथ मंदिर, मणिकर्णिका घाट, मणिकर्णिका गली",
                    phone = "+919451390495",
                    whatsapp = "919451390495"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "7:00 AM - 2:00 PM",
                    winterTimings = "7:30 AM - 2:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Neelu Kachori Bhandar Kachori Gali",
                    bestTime = "8:00 AM - 10:30 AM",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Wood-fired Crispy Kachori-Sabzi with Aromatic Spicy Potato Rasa",
                    location = "Kachori Gali, Chowk, Varanasi",
                    nearbyLocations = "Kashi Vishwanath Temple, Manikarnika Ghat, Manikarnika Lane",
                    phone = "+919451390495",
                    whatsapp = "919451390495"
                )
            }
        }
        "baati_chokha_restaurant" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "दोपहर 12:00 बजे - रात 10:30 बजे",
                    winterTimings = "दोपहर 12:00 बजे - रात 10:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "बाटी चोखा रेस्टोरेंट तेलियाबाग",
                    bestTime = "दोपहर 1:30 बजे (दोपहर के भोजन के लिए), रात 8:00 बजे",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "कंडे पर पकी और घी में डूबी बाटी, स्वादिष्ट चोखा और मीठा चूरमा",
                    location = "तेलियाबाग, कैंटोनमेंट के पास, वाराणसी",
                    nearbyLocations = "वाराणसी कैंट रेलवे स्टेशन, सिगरा, नदेसर पैलेस",
                    phone = "+915422205561",
                    whatsapp = "915422205561"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "12:00 PM - 10:30 PM",
                    winterTimings = "12:00 PM - 10:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Baati Chokha Restaurant Teliabagh",
                    bestTime = "1:30 PM (Lunch) & 8:00 PM (Dinner)",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Wood-fired Baatis Drenched in Pure Ghee, Spicy Mashed Chokha and Sweet Churma",
                    location = "Teliabagh, near Cantonment, Varanasi",
                    nearbyLocations = "Varanasi Cantonment Station, Sigra, Nadesar Palace",
                    phone = "+915422205561",
                    whatsapp = "915422205561"
                )
            }
        }
        "blue_lassi_shop" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 9:00 बजे - रात 10:30 बजे",
                    winterTimings = "सुबह 9:00 बजे - रात 10:00 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "ब्लू लस्सी शॉप मणिकर्णिका",
                    bestTime = "दोपहर 12:00 बजे - शाम 5:00 बजे",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "केसर, आम, अनार, पिस्ता और चॉकलेट सहित 80+ फ्लेवर वाली लस्सी",
                    location = "C.K. 21/35, मणिकर्णिका घाट के पास, वाराणसी",
                    nearbyLocations = "मणिकर्णिका घाट, काशी विश्वनाथ मंदिर, चौक",
                    phone = "+919451390496",
                    whatsapp = "919451390496"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "9:00 AM - 10:30 PM",
                    winterTimings = "9:00 AM - 10:00 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Blue Lassi Shop Manikarnika",
                    bestTime = "12:00 PM - 5:00 PM",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "80+ Varieties of Creamy Fruit Lassis Topped with Saffron & Pistachios",
                    location = "C.K. 21/35, near Manikarnika Ghat, Varanasi",
                    nearbyLocations = "Manikarnika Ghat, Kashi Vishwanath Temple, Chowk",
                    phone = "+919451390496",
                    whatsapp = "919451390496"
                )
            }
        }
        "sri_krishna_bhandar" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 7:00 बजे - रात 10:00 बजे",
                    winterTimings = "सुबह 7:30 बजे - रात 9:30 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "श्री कृष्ण भंडार दशाश्वमेध",
                    bestTime = "सुबह 8:00 बजे - सुबह 10:00 बजे (स्वादिष्ट मालपुआ)",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "स्वादिष्ट नाश्ता पूरी-सब्जी, रसीला गरम मालपुआ और केसरिया जलेबी",
                    location = "दशाश्वमेध घाट रोड, वाराणसी",
                    nearbyLocations = "दशाश्वमेध घाट, गोदौलिया चौराहा, मान मंदिर घाट",
                    phone = "+919451390497",
                    whatsapp = "919451390497"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "7:00 AM - 10:00 PM",
                    winterTimings = "7:30 AM - 9:30 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Sri Krishna Bhandar Dashashwamedh",
                    bestTime = "8:00 AM - 10:00 AM",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Spicy Puri Bhaji Breakfast, Soft Aromatic Malpua, and Crispy Saffron Jalebis",
                    location = "Dashashwamedh Ghat Road, Varanasi",
                    nearbyLocations = "Dashashwamedh Ghat, Godowlia Crossing, Man Mandir Ghat",
                    phone = "+919451390497",
                    whatsapp = "919451390497"
                )
            }
        }
        "bana_lassi" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "सुबह 10:00 बजे - रात 10:00 बजे",
                    winterTimings = "सुबह 10:00 बजे - रात 9:30 बजे",
                    aartiTimings = "N/A",
                    attractionsKey = "बना लस्सी शिवाला",
                    bestTime = "दोपहर 12:30 बजे - शाम 5:00 बजे",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "सोंधी कुल्हड़ लस्सी, मौसमी ताजे फल और गाढ़ी मलाई की परत",
                    location = "शिवाला घाट क्षेत्र, वाराणसी",
                    nearbyLocations = "शिवाला घाट, केदार घाट, हरिश्चंद्र घाट",
                    phone = "+919451390498",
                    whatsapp = "919451390498"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "10:00 AM - 10:00 PM",
                    winterTimings = "10:00 AM - 9:30 PM",
                    aartiTimings = "N/A",
                    attractionsKey = "Bana Lassi Shivala",
                    bestTime = "12:30 PM - 5:00 PM",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Eco-friendly Kulhad Lassi loaded with Dry Fruits & Thick seasonal fruit toppings",
                    location = "Shivala Ghat area, Varanasi",
                    nearbyLocations = "Shivala Ghat, Kedar Ghat, Harishchandra Ghat",
                    phone = "+919451390498",
                    whatsapp = "919451390498"
                )
            }
        }
        "markandey_malaiyo" -> {
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = "N/A (केवल सर्दियों में उपलब्ध)",
                    winterTimings = "सुबह 7:00 बजे - सुबह 11:00 बजे (केवल दिसंबर से फरवरी)",
                    aartiTimings = "N/A",
                    attractionsKey = "मार्कंडेय मलइयो ठठेरी बाजार",
                    bestTime = "सुबह 8:00 बजे - सुबह 10:00 बजे (ताजा झागदार मलइयो)",
                    entryFee = "निशुल्क प्रवेश (सशुल्क भोजन)",
                    famousFor = "हवा जैसी अनूठी सर्दियों की प्रसिद्ध मिठाई 'मलइयो', केसर-इलायची और पिस्ता स्वाद",
                    location = "ठठेरी बाजार, चौक, वाराणसी",
                    nearbyLocations = "गोपाल मंदिर, काल भैरव मंदिर, चौक कोतवाली",
                    phone = "+919451390499",
                    whatsapp = "919451390499"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = "N/A (Closed in Summer)",
                    winterTimings = "7:00 AM - 11:00 AM (Only December to February)",
                    aartiTimings = "N/A",
                    attractionsKey = "Markandey Malaiyo Thatheri Bazar",
                    bestTime = "8:00 AM - 10:00 AM",
                    entryFee = "Free Entry (Paid Food)",
                    famousFor = "Ethereal Milk Foam Winter Dessert 'Malaiyo' flavored with Saffron",
                    location = "Thatheri Bazar, Chowk, Varanasi",
                    nearbyLocations = "Gopal Mandir, Kaal Bhairav Temple, Chowk area",
                    phone = "+919451390499",
                    whatsapp = "919451390499"
                )
            }
        }
        else -> {
            // General Fallback for Ghats or Temples
            val isGhat = placeId.contains("ghat")
            if (lang == AppLanguage.HINDI) {
                DarshanTimingInfo(
                    summerTimings = if (isGhat) "24 घंटे खुला" else "सुबह 5:00 बजे - रात 9:00 बजे",
                    winterTimings = if (isGhat) "24 घंटे खुला" else "सुबह 5:30 बजे - रात 8:30 बजे",
                    aartiTimings = "संध्या आरती: शाम 6:30 बजे (गर्मियों में) / शाम 6:00 बजे (सर्दियों में)",
                    attractionsKey = if (isGhat) "सुंदर गंगा तट, नौका विहार, प्राचीन सीढ़ियाँ, शांत सूर्यास्त" else "प्राचीन गर्भगृह, पारंपरिक वास्तुकला, शांतिपूर्ण साधना परिसर",
                    bestTime = "सुबह 5:30 बजे (सूर्योदय) अथवा शाम 6:00 बजे (आरती)",
                    entryFee = "निशुल्क"
                )
            } else {
                DarshanTimingInfo(
                    summerTimings = if (isGhat) "Open 24 Hours" else "5:00 AM - 9:00 PM",
                    winterTimings = if (isGhat) "Open 24 Hours" else "5:30 AM - 8:30 PM",
                    aartiTimings = "Evening Aarti: 6:30 PM (Summer) / 6:00 PM (Winter)",
                    attractionsKey = if (isGhat) "Scenic Ganges view, Boat riding experience, Ancient stone steps, Peaceful sunset breeze" else "Historic inner sanctum, Traditional carvings, Peaceful prayer hall, Holy atmosphere",
                    bestTime = "5:30 AM (Sunrise) or 6:00 PM (Evening Aarti)",
                    entryFee = "Free"
                )
            }
        }
    }
}

@Composable
fun DarshanDetailDialog(
    place: Place,
    onDismiss: () -> Unit,
    onSaveToggle: () -> Unit,
    isSaved: Boolean,
    lang: AppLanguage
) {
    val context = LocalContext.current
    val info = getDarshanTimingInfo(place.id, lang)
    
    // Custom colors based on category
    val (primaryColor, bannerBg) = when {
        place.type.contains("Temple") || place.type.contains("मंदिर") -> {
            Color(0xFFE65100) to Color(0xFFFFF3E0) // Saffron Orange for Temples
        }
        place.type.contains("Ghat") || place.type.contains("घाट") -> {
            Color(0xFF0D47A1) to Color(0xFFE3F2FD) // Ganga Blue for Ghats
        }
        place.type.contains("Heritage") || place.type.contains("धरोहर") -> {
            Color(0xFF5D4037) to Color(0xFFEFEBE9) // Rustic Brown for Heritage
        }
        else -> {
            Color(0xFF2E7D32) to Color(0xFFE8F5E9) // Leaf Green for Food
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Cover Photo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    AsyncImage(
                        model = if (place.imageResId != 0) place.imageResId else place.imageUrl,
                        contentDescription = place.name,
                        placeholder = painterResource(id = R.drawable.img_kashi_ghat),
                        error = painterResource(id = R.drawable.img_kashi_ghat),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Translucent overlay gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                )
                            )
                    )

                    // Close Button (Floating right)
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Bookmark Save Button (Floating left)
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (lang == AppLanguage.HINDI) "सहेजें" else "Save",
                            tint = if (isSaved) primaryColor else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Floating Category and Blinking Status Badges
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(topEnd = 12.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = primaryColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = place.type.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        val isFood = place.type.contains("Food", ignoreCase = true) || place.type.contains("भोजन")
                        if (isFood) {
                            val freeBhandaras = setOf("annapurna_bhandara", "annapurna_free_mess", "iskcon_bhandara")
                            val isFree = place.id in freeBhandaras
                            
                            val infiniteTransition = rememberInfiniteTransition(label = "blink_dialog")
                            val blinkAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "blink_alpha_dialog"
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
                                        badgeColor.copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .border(1.dp, badgeColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
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
                }

                // Details Content Panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Place Name
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summer & Winter Timings Grid (Two side-by-side cards)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Summer Timings Block
                        Surface(
                            color = Color(0xFFFFF8E1), // Light golden-yellow
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = Color(0xFFE65100),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (lang == AppLanguage.HINDI) "गर्मियों का समय" else "Summer Timings",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = info.summerTimings,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF424242),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Winter Timings Block
                        Surface(
                            color = Color(0xFFE1F5FE), // Light sky blue
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AcUnit,
                                        contentDescription = null,
                                        tint = Color(0xFF0277BD),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (lang == AppLanguage.HINDI) "सर्दियों का समय" else "Winter Timings",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0277BD)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = info.winterTimings,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF424242),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Aarti Timings Block
                    Surface(
                        color = Color(0xFFFFF3E0), // Sacred saffron-yellow
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign, // bell or similar, Campaign looks nice, or we can use generic icons
                                    contentDescription = null,
                                    tint = Color(0xFFD84315),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (lang == AppLanguage.HINDI) "🪔 दिव्य आरती का समय" else "🪔 Divine Aarti Timings",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD84315)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = info.aartiTimings,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4E342E),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location / Address Link
                    Surface(
                        color = bannerBg,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(place.location)}"))
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Google Maps not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == AppLanguage.HINDI) "📍 स्थान (गूगल मैप पर देखें)" else "📍 Location (Tap to view on Map)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                                Text(
                                    text = place.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                    textDecoration = TextDecoration.Underline,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = primaryColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // About / Description
                    Text(
                        text = if (lang == AppLanguage.HINDI) "इतिहास और विवरण" else "History & Description",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = place.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Key Attractions / highlights
                    Text(
                        text = if (lang == AppLanguage.HINDI) "✨ मुख्य आकर्षण एवं विशेष अनुभव" else "✨ Key Attractions & Highlights",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• " + info.attractionsKey.replace(", ", "\n• "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Famous For section (if present)
                    info.famousFor?.let { famous ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (lang == AppLanguage.HINDI) "🍽️ यह क्यों प्रसिद्ध है?" else "🍽️ Why is it Famous?",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = famous,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }

                    // Nearby Famous Locations section (if present)
                    info.nearbyLocations?.let { nearby ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (lang == AppLanguage.HINDI) "🚩 आस-पास के प्रसिद्ध स्थल" else "🚩 Nearby Famous Locations",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nearby,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Best time to visit and Entry Fee Row
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.HINDI) "🕒 सर्वोत्तम दर्शन समय" else "🕒 Best Visiting Time",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = info.bestTime,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.HINDI) "🎟️ प्रवेश शुल्क" else "🎟️ Entry Fee",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            Text(
                                text = info.entryFee,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Interactive Book Tour / Local Support button
                    val targetPhone = info.phone ?: "8423340923"
                    val targetWhatsapp = info.whatsapp ?: "8423340923"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$targetPhone"))
                                    context.startActivity(callIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not launch dialer", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == AppLanguage.HINDI) "कॉल करें" else "Call Us",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                try {
                                    val isFood = place.type.contains("Food", ignoreCase = true) || place.type.contains("भोजन")
                                    val msg = if (lang == AppLanguage.HINDI) {
                                        if (isFood) {
                                            "नमस्ते, मैं आपके यहाँ मिलने वाले स्वादिष्ट भोजन/प्रसाद '${place.name}' के बारे में जानकारी प्राप्त करना चाहता हूँ।"
                                        } else {
                                            "नमस्ते, मैं दर्शन स्थल '${place.name}' के बारे में जानकारी प्राप्त करना चाहता हूँ।"
                                        }
                                    } else {
                                        if (isFood) {
                                            "Hello, I would like to inquire about the food/prasad at: ${place.name}"
                                        } else {
                                            "Hello, I would like to inquire about the attraction/temple: ${place.name}"
                                        }
                                    }
                                    val waIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://wa.me/$targetWhatsapp?text=${Uri.encode(msg)}")
                                    }
                                    context.startActivity(waIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == AppLanguage.HINDI) "व्हाट्सएप" else "WhatsApp",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
