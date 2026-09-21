package com.tourism.backend.config;

import com.tourism.backend.accommodation.entity.Accommodation;
import com.tourism.backend.accommodation.entity.AccommodationType;
import com.tourism.backend.attraction.entity.Attraction;
import com.tourism.backend.destination.entity.Destination;
import com.tourism.backend.destination.entity.DestinationType;
import com.tourism.backend.experience.entity.Experience;
import com.tourism.backend.festival.entity.Festival;
import com.tourism.backend.festivaloccurrence.entity.FestivalOccurrence;
import com.tourism.backend.guide.entity.Guide;
import com.tourism.backend.guide.entity.Language;
import com.tourism.backend.restaurant.entity.Cuisine;
import com.tourism.backend.restaurant.entity.PriceRange;
import com.tourism.backend.restaurant.entity.Restaurant;
import com.tourism.backend.state.entity.State;
import com.tourism.backend.tag.entity.Tag;
import com.tourism.backend.transport.entity.Transport;
import com.tourism.backend.transport.entity.TransportType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@Profile("dev")
@Slf4j
public class DataSeeder implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {

        log.info("Starting tourism demo data seeding...");

        /*
         * ============================================================
         * STATES
         * ============================================================
         */

        State meghalaya = findOrCreateState(
                "Meghalaya",
                "Shillong",
                "A hill state in Northeast India known for waterfalls, living root bridges, caves, forests and indigenous Khasi and Garo culture.",
                "https://images.unsplash.com/photo-1500534623283-312aade485b7"
        );

        State assam = findOrCreateState(
                "Assam",
                "Dispur",
                "A northeastern Indian state known for the Brahmaputra, tea gardens, wildlife, cultural heritage and Kaziranga National Park.",
                "https://images.unsplash.com/photo-1530789253388-582c481c54b0"
        );

        /*
         * ============================================================
         * DESTINATIONS
         * ============================================================
         */

        Destination shillong = findOrCreateDestination(
                "Shillong",
                meghalaya,
                "The Scotland of the East",
                "The capital of Meghalaya surrounded by hills, viewpoints, waterfalls, music culture and local Khasi heritage.",
                "East Khasi Hills",
                DestinationType.CITY,
                25.5788,
                91.8933,
                true,
                true,
                1
        );

        Destination cherrapunji = findOrCreateDestination(
                "Cherrapunji",
                meghalaya,
                "Land of Waterfalls and Living Root Bridges",
                "A scenic destination in the Khasi Hills famous for waterfalls, caves, trekking and living root bridges.",
                "East Khasi Hills",
                DestinationType.HILL_STATION,
                25.2702,
                91.7323,
                true,
                true,
                2
        );

        Destination mawlynnong = findOrCreateDestination(
                "Mawlynnong",
                meghalaya,
                "Asia's Clean Village",
                "A picturesque village known for its clean surroundings, bamboo structures, viewpoints and nearby natural attractions.",
                "East Khasi Hills",
                DestinationType.VILLAGE,
                25.2015,
                91.8767,
                false,
                true,
                3
        );

        Destination dawki = findOrCreateDestination(
                "Dawki",
                meghalaya,
                "Crystal Clear Waters of Meghalaya",
                "A border town famous for the Umngot River, boating, suspension bridges and surrounding hills.",
                "West Jaintia Hills",
                DestinationType.TOWN,
                25.1860,
                92.0247,
                true,
                true,
                4
        );

        Destination guwahati = findOrCreateDestination(
                "Guwahati",
                assam,
                "Gateway to Northeast India",
                "The largest city in Assam and an important gateway to destinations across Northeast India.",
                "Kamrup Metropolitan",
                DestinationType.CITY,
                26.1445,
                91.7362,
                false,
                true,
                5
        );

        Destination kaziranga = findOrCreateDestination(
                "Kaziranga",
                assam,
                "Wildlife and the One-Horned Rhinoceros",
                "A wildlife destination centred around Kaziranga National Park, known for rhinoceroses, elephants, birds and grassland ecosystems.",
                "Golaghat",
                DestinationType.NATIONAL_PARK,
                26.5775,
                93.1711,
                true,
                true,
                6
        );

        /*
         * ============================================================
         * EXPERIENCES
         * ============================================================
         */

        Experience trekking = findOrCreateExperience(
                "Trekking",
                "Walking and trekking through hills, forests and natural trails.",
                "🥾"
        );

        Experience nature = findOrCreateExperience(
                "Nature",
                "Exploring forests, waterfalls, rivers and natural landscapes.",
                "🌿"
        );

        Experience adventure = findOrCreateExperience(
                "Adventure",
                "Outdoor activities involving exploration and physical activity.",
                "🧗"
        );

        Experience wildlife = findOrCreateExperience(
                "Wildlife",
                "Wildlife viewing and exploration of natural habitats.",
                "🦏"
        );

        Experience waterSports = findOrCreateExperience(
                "Water Sports",
                "Boating, river activities and other water-based experiences.",
                "🚣"
        );

        Experience cultural = findOrCreateExperience(
                "Cultural Tourism",
                "Exploring local traditions, communities, festivals and heritage.",
                "🏛️"
        );

        Experience photography = findOrCreateExperience(
                "Photography",
                "Scenic locations suitable for landscape and travel photography.",
                "📷"
        );

        Experience caving = findOrCreateExperience(
                "Caving",
                "Exploring natural caves and underground formations.",
                "🪨"
        );

        Experience scenic = findOrCreateExperience(
                "Scenic",
                "Viewpoints and landscapes suited for relaxing and sightseeing.",
                "🏔️"
        );

        Experience heritage = findOrCreateExperience(
                "Heritage",
                "Exploring historical, architectural and cultural heritage.",
                "🏛️"
        );

        /*
         * ============================================================
         * TAGS
         * ============================================================
         */

        Tag waterfall = findOrCreateTag(
                "Waterfall",
                "Waterfall and cascading water attractions."
        );

        Tag trekkingTag = findOrCreateTag(
                "Trekking",
                "Locations suitable for trekking."
        );

        Tag viewpoint = findOrCreateTag(
                "Viewpoint",
                "Locations offering scenic views."
        );

        Tag natureTag = findOrCreateTag(
                "Nature",
                "Natural landscapes and outdoor attractions."
        );

        Tag adventureTag = findOrCreateTag(
                "Adventure",
                "Adventure-oriented attractions and activities."
        );

        Tag wildlifeTag = findOrCreateTag(
                "Wildlife",
                "Wildlife viewing and nature experiences."
        );

        Tag caveTag = findOrCreateTag(
                "Cave",
                "Natural caves and underground formations."
        );

        Tag river = findOrCreateTag(
                "River",
                "River-based destinations and attractions."
        );

        Tag cultureTag = findOrCreateTag(
                "Culture",
                "Local culture and heritage."
        );

        Tag familyFriendly = findOrCreateTag(
                "Family Friendly",
                "Attractions suitable for family visitors."
        );

        /*
         * ============================================================
         * ATTRACTIONS
         * ============================================================
         */

        Attraction elephantFalls = findOrCreateAttraction(
                "Elephant Falls",
                "A popular multi-tiered waterfall near Shillong surrounded by lush greenery.",
                shillong,
                25.5372,
                91.8756,
                "June to October",
                BigDecimal.valueOf(30),
                true,
                1
        );

        addExperiences(elephantFalls, nature, photography, scenic);
        addTags(elephantFalls, waterfall, natureTag, familyFriendly);

        Attraction shillongPeak = findOrCreateAttraction(
                "Shillong Peak",
                "A high viewpoint offering panoramic views of Shillong and the surrounding hills.",
                shillong,
                25.5375,
                91.8820,
                "October to April",
                BigDecimal.ZERO,
                true,
                2
        );

        addExperiences(shillongPeak, scenic, photography, nature);
        addTags(shillongPeak, viewpoint, natureTag);

        Attraction wardLake = findOrCreateAttraction(
                "Ward's Lake",
                "A landscaped lake in the heart of Shillong surrounded by gardens and walking paths.",
                shillong,
                25.5744,
                91.8840,
                "October to May",
                BigDecimal.valueOf(20),
                false,
                3
        );

        addExperiences(wardLake, nature, photography, scenic);
        addTags(wardLake, natureTag, familyFriendly);

        Attraction laitlum = findOrCreateAttraction(
                "Laitlum Canyon",
                "A dramatic hilltop viewpoint overlooking deep valleys and surrounding landscapes.",
                shillong,
                25.4930,
                91.9250,
                "October to April",
                BigDecimal.ZERO,
                true,
                4
        );

        addExperiences(laitlum, trekking, scenic, photography, nature);
        addTags(laitlum, viewpoint, trekkingTag, natureTag, adventureTag);

        Attraction doubleDecker = findOrCreateAttraction(
                "Double Decker Living Root Bridge",
                "A famous living root bridge reached through a forest trek near Nongriat.",
                cherrapunji,
                25.2418,
                91.6642,
                "October to April",
                BigDecimal.ZERO,
                true,
                1
        );

        addExperiences(
                doubleDecker,
                trekking,
                nature,
                adventure,
                photography,
                cultural
        );

        addTags(
                doubleDecker,
                trekkingTag,
                natureTag,
                adventureTag,
                cultureTag
        );

        Attraction nohkalikai = findOrCreateAttraction(
                "Nohkalikai Falls",
                "One of the most famous waterfalls in Meghalaya, dropping into a deep blue-green pool.",
                cherrapunji,
                25.2802,
                91.6792,
                "June to October",
                BigDecimal.valueOf(50),
                true,
                2
        );

        addExperiences(nohkalikai, nature, photography, scenic);
        addTags(nohkalikai, waterfall, viewpoint, natureTag);

        Attraction sevenSisters = findOrCreateAttraction(
                "Seven Sisters Falls",
                "A spectacular waterfall formed by several streams descending from the cliffs.",
                cherrapunji,
                25.2600,
                91.7220,
                "June to October",
                BigDecimal.valueOf(30),
                true,
                3
        );

        addExperiences(sevenSisters, nature, photography, scenic);
        addTags(sevenSisters, waterfall, viewpoint, natureTag);

        Attraction mawsmai = findOrCreateAttraction(
                "Mawsmai Cave",
                "A natural limestone cave that provides an accessible caving experience.",
                cherrapunji,
                25.2622,
                91.7234,
                "October to May",
                BigDecimal.valueOf(50),
                false,
                4
        );

        addExperiences(mawsmai, caving, adventure, nature);
        addTags(mawsmai, caveTag, adventureTag, natureTag);

        Attraction arwah = findOrCreateAttraction(
                "Arwah Cave",
                "A less commercialised cave experience featuring limestone formations and fossils.",
                cherrapunji,
                25.2760,
                91.7010,
                "October to April",
                BigDecimal.valueOf(30),
                false,
                5
        );

        addExperiences(arwah, caving, trekking, adventure, nature);
        addTags(arwah, caveTag, trekkingTag, adventureTag);

        Attraction ecoPark = findOrCreateAttraction(
                "Eco Park",
                "A scenic park offering views across the surrounding valleys and waterfalls.",
                cherrapunji,
                25.2630,
                91.7270,
                "June to April",
                BigDecimal.valueOf(20),
                false,
                6
        );

        addExperiences(ecoPark, scenic, photography, nature);
        addTags(ecoPark, viewpoint, natureTag, familyFriendly);

        Attraction livingRootBridge = findOrCreateAttraction(
                "Riwai Living Root Bridge",
                "A traditional living root bridge created using the roots of rubber fig trees.",
                mawlynnong,
                25.2050,
                91.8760,
                "October to April",
                BigDecimal.valueOf(20),
                true,
                1
        );

        addExperiences(
                livingRootBridge,
                trekking,
                nature,
                cultural,
                photography
        );

        addTags(
                livingRootBridge,
                trekkingTag,
                natureTag,
                cultureTag
        );

        Attraction balancingRock = findOrCreateAttraction(
                "Balancing Rock",
                "A naturally balanced rock formation that has become a local attraction.",
                mawlynnong,
                25.1990,
                91.8750,
                "October to April",
                BigDecimal.valueOf(10),
                false,
                2
        );

        addExperiences(balancingRock, nature, photography, scenic);
        addTags(balancingRock, natureTag, familyFriendly);

        Attraction umngotRiver = findOrCreateAttraction(
                "Umngot River",
                "A clear river famous for boating and scenic views near Dawki.",
                dawki,
                25.1820,
                92.0240,
                "October to April",
                BigDecimal.valueOf(200),
                true,
                1
        );

        addExperiences(
                umngotRiver,
                waterSports,
                nature,
                photography,
                adventure
        );

        addTags(
                umngotRiver,
                river,
                natureTag,
                adventureTag
        );

        Attraction dawkiBridge = findOrCreateAttraction(
                "Dawki Suspension Bridge",
                "A historic suspension bridge overlooking the Umngot River.",
                dawki,
                25.1870,
                92.0240,
                "October to April",
                BigDecimal.ZERO,
                false,
                2
        );

        addExperiences(dawkiBridge, photography, scenic, heritage);
        addTags(dawkiBridge, viewpoint, river, cultureTag);

        Attraction kamakhya = findOrCreateAttraction(
                "Kamakhya Temple",
                "A major Hindu temple complex located on Nilachal Hill in Guwahati.",
                guwahati,
                26.1664,
                91.7056,
                "October to March",
                BigDecimal.ZERO,
                true,
                1
        );

        addExperiences(kamakhya, cultural, heritage, photography);
        addTags(kamakhya, cultureTag, familyFriendly);

        Attraction brahmaputra = findOrCreateAttraction(
                "Brahmaputra River Cruise",
                "A river cruise experience offering views of the Brahmaputra and Guwahati skyline.",
                guwahati,
                26.1820,
                91.7500,
                "October to April",
                BigDecimal.valueOf(800),
                true,
                2
        );

        addExperiences(brahmaputra, waterSports, scenic, photography);
        addTags(brahmaputra, river, viewpoint);

        Attraction ugratara = findOrCreateAttraction(
                "Ugratara Temple",
                "A historic temple and cultural landmark in central Guwahati.",
                guwahati,
                26.1844,
                91.7494,
                "October to March",
                BigDecimal.ZERO,
                false,
                3
        );

        addExperiences(ugratara, cultural, heritage);
        addTags(ugratara, cultureTag);

        Attraction kazirangaSafari = findOrCreateAttraction(
                "Kaziranga National Park Safari",
                "A wildlife safari experience through grasslands and forest habitats.",
                kaziranga,
                26.5775,
                93.1711,
                "November to April",
                BigDecimal.valueOf(1200),
                true,
                1
        );

        addExperiences(
                kazirangaSafari,
                wildlife,
                photography,
                nature,
                adventure
        );

        addTags(
                kazirangaSafari,
                wildlifeTag,
                natureTag,
                adventureTag
        );

        Attraction orchidPark = findOrCreateAttraction(
                "Kaziranga Orchid Park",
                "A park featuring orchids, local plants, cultural displays and scenic surroundings.",
                kaziranga,
                26.6500,
                93.2500,
                "October to April",
                BigDecimal.valueOf(500),
                false,
                2
        );

        addExperiences(orchidPark, nature, cultural, photography);
        addTags(orchidPark, natureTag, cultureTag, familyFriendly);

        /*
         * ============================================================
         * ACCOMMODATIONS
         * ============================================================
         */

        findOrCreateAccommodation(
                "Pine Hill Resort",
                AccommodationType.RESORT,
                BigDecimal.valueOf(3500),
                4.4,
                shillong,
                "Laitumkhrah, Shillong"
        );

        findOrCreateAccommodation(
                "Shillong View Homestay",
                AccommodationType.HOMESTAY,
                BigDecimal.valueOf(1800),
                4.2,
                shillong,
                "Upper Shillong"
        );

        findOrCreateAccommodation(
                "Cloud View Resort",
                AccommodationType.RESORT,
                BigDecimal.valueOf(4200),
                4.5,
                cherrapunji,
                "Sohra, Meghalaya"
        );

        findOrCreateAccommodation(
                "Sohra Hills Homestay",
                AccommodationType.HOMESTAY,
                BigDecimal.valueOf(2200),
                4.3,
                cherrapunji,
                "Sohra, Meghalaya"
        );

        findOrCreateAccommodation(
                "Nongriat Eco Lodge",
                AccommodationType.LODGE,
                BigDecimal.valueOf(1600),
                4.1,
                cherrapunji,
                "Nongriat"
        );

        findOrCreateAccommodation(
                "Mawlynnong Village Homestay",
                AccommodationType.HOMESTAY,
                BigDecimal.valueOf(2000),
                4.4,
                mawlynnong,
                "Mawlynnong"
        );

        findOrCreateAccommodation(
                "Dawki Riverside Camp",
                AccommodationType.CAMPING,
                BigDecimal.valueOf(1800),
                4.2,
                dawki,
                "Dawki Riverside"
        );

        findOrCreateAccommodation(
                "Guwahati City Hotel",
                AccommodationType.HOTEL,
                BigDecimal.valueOf(2800),
                4.1,
                guwahati,
                "Paltan Bazaar, Guwahati"
        );

        findOrCreateAccommodation(
                "Brahmaputra View Hotel",
                AccommodationType.HOTEL,
                BigDecimal.valueOf(3800),
                4.3,
                guwahati,
                "Fancy Bazaar, Guwahati"
        );

        findOrCreateAccommodation(
                "Kaziranga Wildlife Lodge",
                AccommodationType.LODGE,
                BigDecimal.valueOf(4500),
                4.5,
                kaziranga,
                "Kohora, Kaziranga"
        );

        findOrCreateAccommodation(
                "Kaziranga Nature Resort",
                AccommodationType.RESORT,
                BigDecimal.valueOf(5500),
                4.6,
                kaziranga,
                "Kohora, Kaziranga"
        );

        /*
         * ============================================================
         * RESTAURANTS
         * ============================================================
         */

        findOrCreateRestaurant(
                "Jiva Veg Restaurant",
                Cuisine.NORTH_INDIAN,
                true,
                4.3,
                PriceRange.MID_RANGE,
                cherrapunji
        );

        findOrCreateRestaurant(
                "Highland Grill",
                Cuisine.MULTI_CUISINE,
                false,
                4.4,
                PriceRange.MID_RANGE,
                cherrapunji
        );

        findOrCreateRestaurant(
                "Sohra Khasi Kitchen",
                Cuisine.MEGHALAYAN,
                false,
                4.5,
                PriceRange.BUDGET,
                cherrapunji
        );

        findOrCreateRestaurant(
                "Shillong Local Kitchen",
                Cuisine.MEGHALAYAN,
                false,
                4.4,
                PriceRange.MID_RANGE,
                shillong
        );

        findOrCreateRestaurant(
                "Pine Cafe Shillong",
                Cuisine.CAFE,
                true,
                4.2,
                PriceRange.BUDGET,
                shillong
        );

        findOrCreateRestaurant(
                "Mawlynnong Village Kitchen",
                Cuisine.MEGHALAYAN,
                false,
                4.3,
                PriceRange.BUDGET,
                mawlynnong
        );

        findOrCreateRestaurant(
                "Dawki Riverside Cafe",
                Cuisine.MULTI_CUISINE,
                true,
                4.1,
                PriceRange.BUDGET,
                dawki
        );

        findOrCreateRestaurant(
                "Assamese Rasoi",
                Cuisine.ASSAMESE,
                false,
                4.5,
                PriceRange.MID_RANGE,
                guwahati
        );

        findOrCreateRestaurant(
                "Brahmaputra Cafe",
                Cuisine.CAFE,
                true,
                4.2,
                PriceRange.BUDGET,
                guwahati
        );

        findOrCreateRestaurant(
                "Kaziranga Forest Kitchen",
                Cuisine.ASSAMESE,
                false,
                4.4,
                PriceRange.MID_RANGE,
                kaziranga
        );

        /*
         * ============================================================
         * GUIDES
         * ============================================================
         */

        findOrCreateGuide(
                "Riban Lyngdoh",
                Set.of(Language.ENGLISH, Language.HINDI, Language.KHASI),
                8,
                1800,
                4.7,
                true,
                true,
                "DEMO-MEG-001",
                "9000000001",
                cherrapunji
        );

        findOrCreateGuide(
                "Rituparna Bora",
                Set.of(Language.ENGLISH, Language.HINDI, Language.ASSAMESE),
                6,
                1500,
                4.5,
                true,
                false,
                "DEMO-ASM-002",
                "9000000006",
                guwahati
        );

        findOrCreateGuide(
                "Meban Khonglah",
                Set.of(Language.ENGLISH, Language.HINDI, Language.KHASI),
                6,
                1600,
                4.5,
                true,
                false,
                "DEMO-MEG-002",
                "9000000002",
                cherrapunji
        );

        findOrCreateGuide(
                "Daphne Marak",
                Set.of(Language.ENGLISH, Language.HINDI, Language.GARO),
                7,
                1700,
                4.6,
                true,
                false,
                "DEMO-MEG-003",
                "9000000003",
                mawlynnong
        );

        findOrCreateGuide(
                "Bantei Sangma",
                Set.of(Language.ENGLISH, Language.HINDI, Language.GARO),
                5,
                1500,
                4.4,
                true,
                true,
                "DEMO-MEG-004",
                "9000000004",
                shillong
        );

        findOrCreateGuide(
                "Arun Das",
                Set.of(Language.ENGLISH, Language.HINDI, Language.ASSAMESE),
                10,
                2200,
                4.8,
                true,
                true,
                "DEMO-ASM-001",
                "9000000005",
                kaziranga
        );

        /*
         * ============================================================
         * TRANSPORT
         * ============================================================
         */

        findOrCreateTransport(
                TransportType.CAB,
                "Shillong Taxi Union",
                "Shillong",
                "Cherrapunji",
                "2 hours 30 minutes",
                BigDecimal.valueOf(1800),
                shillong
        );

        findOrCreateTransport(
                TransportType.CAB,
                "Sohra Local Taxi Service",
                "Cherrapunji",
                "Nongriat",
                "1 hour",
                BigDecimal.valueOf(900),
                cherrapunji
        );

        findOrCreateTransport(
                TransportType.CAB,
                "Meghalaya Hill Cabs",
                "Shillong",
                "Mawlynnong",
                "3 hours",
                BigDecimal.valueOf(2500),
                shillong
        );

        findOrCreateTransport(
                TransportType.CAB,
                "Dawki Tourist Cabs",
                "Shillong",
                "Dawki",
                "3 hours 30 minutes",
                BigDecimal.valueOf(2800),
                dawki
        );

        findOrCreateTransport(
                TransportType.BUS,
                "Meghalaya Tourist Bus",
                "Shillong",
                "Cherrapunji",
                "3 hours",
                BigDecimal.valueOf(300),
                cherrapunji
        );

        findOrCreateTransport(
                TransportType.CAB,
                "Guwahati City Cabs",
                "Guwahati",
                "Kamakhya",
                "30 minutes",
                BigDecimal.valueOf(500),
                guwahati
        );

        findOrCreateTransport(
                TransportType.CAB,
                "Kaziranga Tourist Taxi",
                "Guwahati",
                "Kaziranga",
                "4 hours 30 minutes",
                BigDecimal.valueOf(3500),
                kaziranga
        );

        findOrCreateTransport(
                TransportType.BUS,
                "Assam Tourist Bus",
                "Guwahati",
                "Kaziranga",
                "5 hours",
                BigDecimal.valueOf(600),
                kaziranga
        );

        /*
         * ============================================================
         * FESTIVALS
         * ============================================================
         */

        Festival wangala = findOrCreateFestival(
                "Wangala Festival",
                "A harvest festival celebrated by the Garo community featuring music, dance and traditional celebrations.",
                "Cultural"
        );

        Festival nongkrem = findOrCreateFestival(
                "Nongkrem Dance Festival",
                "A traditional Khasi festival featuring religious ceremonies, dance and cultural performances.",
                "Cultural"
        );

        Festival shadSuk = findOrCreateFestival(
                "Shad Suk Mynsiem",
                "A major Khasi cultural festival celebrating traditional dance, music and community identity.",
                "Cultural"
        );

        Festival bihu = findOrCreateFestival(
                "Rongali Bihu",
                "A major Assamese festival associated with spring, agriculture, music and cultural celebrations.",
                "Cultural"
        );

        Festival ambubachi = findOrCreateFestival(
                "Ambubachi Mela",
                "A major annual religious gathering associated with Kamakhya Temple in Guwahati.",
                "Religious"
        );

        /*
         * ============================================================
         * FESTIVAL OCCURRENCES
         * ============================================================
         */

        findOrCreateFestivalOccurrence(
                wangala,
                meghalaya,
                2026,
                LocalDate.of(2026, 11, 13),
                LocalDate.of(2026, 11, 15)
        );

        findOrCreateFestivalOccurrence(
                nongkrem,
                meghalaya,
                2026,
                LocalDate.of(2026, 11, 18),
                LocalDate.of(2026, 11, 22)
        );

        findOrCreateFestivalOccurrence(
                shadSuk,
                meghalaya,
                2026,
                LocalDate.of(2026, 4, 15),
                LocalDate.of(2026, 4, 17)
        );

        findOrCreateFestivalOccurrence(
                bihu,
                assam,
                2026,
                LocalDate.of(2026, 4, 14),
                LocalDate.of(2026, 4, 20)
        );

        findOrCreateFestivalOccurrence(
                ambubachi,
                assam,
                2026,
                LocalDate.of(2026, 6, 22),
                LocalDate.of(2026, 6, 26)
        );

        entityManager.flush();

        log.info("====================================================");
        log.info("Tourism demo data seeding completed.");
        log.info("Existing records were reused where possible.");
        log.info("Missing demo records were created where necessary.");
        log.info("====================================================");
    }

    private State findOrCreateState(
            String name,
            String capital,
            String description,
            String thumbnailUrl) {

        State state = entityManager.createQuery(
                        "SELECT s FROM State s WHERE s.name = :name",
                        State.class
                )
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (state != null) {
            return state;
        }

        state = new State();
        state.setName(name);
        state.setCapital(capital);
        state.setDescription(description);
        state.setThumbnailUrl(thumbnailUrl);

        entityManager.persist(state);

        return state;
    }

    private Destination findOrCreateDestination(
            String name,
            State state,
            String tagline,
            String description,
            String district,
            DestinationType type,
            double latitude,
            double longitude,
            boolean featured,
            boolean popular,
            int displayOrder) {

        Destination destination = entityManager.createQuery(
                        "SELECT d FROM Destination d " +
                                "WHERE d.name = :name AND d.state.id = :stateId",
                        Destination.class
                )
                .setParameter("name", name)
                .setParameter("stateId", state.getId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (destination != null) {
            return destination;
        }

        destination = new Destination();

        destination.setName(name);
        destination.setTagline(tagline);
        destination.setDescription(description);
        destination.setDistrict(district);
        destination.setState(state);
        destination.setType(type);
        destination.setLatitude(latitude);
        destination.setLongitude(longitude);
        destination.setTimezone("Asia/Kolkata");
        destination.setNearestAirport(
                state.getName().equals("Assam")
                        ? "Lokpriya Gopinath Bordoloi International Airport"
                        : "Shillong Airport"
        );
        destination.setNearestRailwayStation(
                "Guwahati Railway Station"
        );
        destination.setFeatured(featured);
        destination.setPopular(popular);
        destination.setDisplayOrder(displayOrder);

        entityManager.persist(destination);

        return destination;
    }

    private Experience findOrCreateExperience(
            String name,
            String description,
            String icon) {

        Experience experience = entityManager.createQuery(
                        "SELECT e FROM Experience e WHERE e.name = :name",
                        Experience.class
                )
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (experience != null) {
            return experience;
        }

        experience = new Experience();
        experience.setName(name);
        experience.setDescription(description);
        experience.setIcon(icon);

        entityManager.persist(experience);

        return experience;
    }

    private Tag findOrCreateTag(
            String name,
            String description) {

        Tag tag = entityManager.createQuery(
                        "SELECT t FROM Tag t WHERE t.name = :name",
                        Tag.class
                )
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (tag != null) {
            return tag;
        }

        tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);

        entityManager.persist(tag);

        return tag;
    }

    private Attraction findOrCreateAttraction(
            String name,
            String description,
            Destination destination,
            double latitude,
            double longitude,
            String bestSeason,
            BigDecimal entryFee,
            boolean featured,
            int displayOrder) {

        Attraction attraction = entityManager.createQuery(
                        "SELECT a FROM Attraction a " +
                                "WHERE a.name = :name " +
                                "AND a.destination.id = :destinationId",
                        Attraction.class
                )
                .setParameter("name", name)
                .setParameter("destinationId", destination.getId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (attraction != null) {
            return attraction;
        }

        attraction = new Attraction();

        attraction.setName(name);
        attraction.setDescription(description);
        attraction.setLatitude(latitude);
        attraction.setLongitude(longitude);
        attraction.setBestSeason(bestSeason);
        attraction.setEntryFee(entryFee);
        attraction.setFeatured(featured);
        attraction.setDisplayOrder(displayOrder);
        attraction.setDestination(destination);

        entityManager.persist(attraction);

        return attraction;
    }

    private void addExperiences(
            Attraction attraction,
            Experience... experiences) {

        attraction.getExperiences().addAll(
                Set.of(experiences)
        );
    }

    private void addTags(
            Attraction attraction,
            Tag... tags) {

        attraction.getTags().addAll(
                Set.of(tags)
        );
    }

    private void findOrCreateAccommodation(
            String name,
            AccommodationType type,
            BigDecimal pricePerNight,
            double rating,
            Destination destination,
            String address) {

        Accommodation accommodation = entityManager.createQuery(
                        "SELECT a FROM Accommodation a " +
                                "WHERE a.name = :name " +
                                "AND a.destination.id = :destinationId",
                        Accommodation.class
                )
                .setParameter("name", name)
                .setParameter("destinationId", destination.getId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (accommodation != null) {
            return;
        }

        accommodation = new Accommodation();

        accommodation.setName(name);
        accommodation.setDescription(
                "Demo accommodation for tourism application testing."
        );
        accommodation.setType(type);
        accommodation.setPricePerNight(pricePerNight);
        accommodation.setRating(rating);
        accommodation.setContactNumber("9000000000");
        accommodation.setEmail("demo@example.com");
        accommodation.setAddress(address);
        accommodation.setLatitude(destination.getLatitude());
        accommodation.setLongitude(destination.getLongitude());
        accommodation.setAvailable(true);
        accommodation.setDestination(destination);

        entityManager.persist(accommodation);
    }

    private void findOrCreateRestaurant(
            String name,
            Cuisine cuisine,
            boolean vegetarian,
            double rating,
            PriceRange priceRange,
            Destination destination) {

        Restaurant restaurant = entityManager.createQuery(
                        "SELECT r FROM Restaurant r " +
                                "WHERE r.name = :name " +
                                "AND r.destination.id = :destinationId",
                        Restaurant.class
                )
                .setParameter("name", name)
                .setParameter("destinationId", destination.getId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (restaurant != null) {
            return;
        }

        restaurant = new Restaurant();

        restaurant.setName(name);
        restaurant.setDescription(
                "Demo restaurant for tourism application testing."
        );
        restaurant.setCuisine(cuisine);
        restaurant.setVegetarian(vegetarian);
        restaurant.setRating(rating);
        restaurant.setPriceRange(priceRange);
        restaurant.setOpeningHours("08:00-22:00");
        restaurant.setPhone("9000000000");
        restaurant.setDestination(destination);

        entityManager.persist(restaurant);
    }

    private void findOrCreateGuide(
            String name,
            Set<Language> languages,
            int yearsOfExperience,
            double pricePerDay,
            double rating,
            boolean available,
            boolean providesTransport,
            String licenseNumber,
            String phone,
            Destination destination) {

        Guide guide = entityManager.createQuery(
                        "SELECT g FROM Guide g " +
                                "WHERE g.licenseNumber = :licenseNumber",
                        Guide.class
                )
                .setParameter("licenseNumber", licenseNumber)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (guide != null) {
            return;
        }

        guide = new Guide();

        guide.setName(name);
        guide.setBio(
                "Demo local guide for tourism application testing."
        );
        guide.setPhone(phone);
        guide.setEmail(
                name.toLowerCase()
                        .replace(" ", ".")
                        + "@demo-tourism.local"
        );
        guide.setLanguages(new HashSet<>(languages));
        guide.setYearsOfExperience(yearsOfExperience);
        guide.setPricePerDay(pricePerDay);
        guide.setRating(rating);
        guide.setAvailable(available);
        guide.setLicenseNumber(licenseNumber);
        guide.setProvidesTransport(providesTransport);
        guide.setDestination(destination);

        entityManager.persist(guide);
    }

    private void findOrCreateTransport(
            TransportType type,
            String providerName,
            String pickupLocation,
            String dropLocation,
            String estimatedDuration,
            BigDecimal estimatedFare,
            Destination destination) {

        Transport transport = entityManager.createQuery(
                        "SELECT t FROM Transport t " +
                                "WHERE t.providerName = :providerName " +
                                "AND t.pickupLocation = :pickupLocation " +
                                "AND t.dropLocation = :dropLocation " +
                                "AND t.destination.id = :destinationId",
                        Transport.class
                )
                .setParameter("providerName", providerName)
                .setParameter("pickupLocation", pickupLocation)
                .setParameter("dropLocation", dropLocation)
                .setParameter("destinationId", destination.getId())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (transport != null) {
            return;
        }

        transport = new Transport();

        transport.setType(type);
        transport.setProviderName(providerName);
        transport.setPickupLocation(pickupLocation);
        transport.setDropLocation(dropLocation);
        transport.setEstimatedDuration(estimatedDuration);
        transport.setEstimatedFare(estimatedFare);
        transport.setContactNumber("9000000000");
        transport.setAvailable(true);
        transport.setDestination(destination);

        entityManager.persist(transport);
    }

    private Festival findOrCreateFestival(
            String name,
            String description,
            String category) {

        Festival festival = entityManager.createQuery(
                        "SELECT f FROM Festival f WHERE f.name = :name",
                        Festival.class
                )
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (festival != null) {
            return festival;
        }

        festival = new Festival();

        festival.setName(name);
        festival.setDescription(description);
        festival.setCategory(category);

        entityManager.persist(festival);

        return festival;
    }

    private void findOrCreateFestivalOccurrence(
            Festival festival,
            State state,
            int year,
            LocalDate startDate,
            LocalDate endDate) {

        FestivalOccurrence occurrence =
                entityManager.createQuery(
                                "SELECT fo FROM FestivalOccurrence fo " +
                                        "WHERE fo.festival.id = :festivalId " +
                                        "AND fo.state.id = :stateId " +
                                        "AND fo.year = :year",
                                FestivalOccurrence.class
                        )
                        .setParameter("festivalId", festival.getId())
                        .setParameter("stateId", state.getId())
                        .setParameter("year", year)
                        .getResultStream()
                        .findFirst()
                        .orElse(null);

        if (occurrence != null) {
            return;
        }

        occurrence = new FestivalOccurrence();

        occurrence.setFestival(festival);
        occurrence.setState(state);
        occurrence.setYear(year);
        occurrence.setStartDate(startDate);
        occurrence.setEndDate(endDate);
        occurrence.setConfirmed(true);
        occurrence.setNotes(
                "Demo occurrence for application testing."
        );

        entityManager.persist(occurrence);
    }
}