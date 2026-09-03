package org.audiocular.nitnem.data

import org.audiocular.nitnem.models.AudioTrack

object CuratedAudioCatalog {

    fun getTracksForBani(
        baniId: String
    ): List<AudioTrack> {

        return tracksMap[baniId]
            ?: emptyList()
    }

    private val tracksMap = mapOf(

        /*
         * ─────────────────────────────────────────
         * JAPJI SAHIB
         * ─────────────────────────────────────────
         */

        "japji-sahib" to listOf(

            AudioTrack(
                id = "curated_japji_hari_singh",
                title = "Japji Sahib",
                artist = "Bhai Hari Singh Ji",
                durationMs = 939213L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/japji_sahib_hari_singh"
            ),

            AudioTrack(
                id = "curated_japji_jarnail",
                title = "Japji Sahib",
                artist = "Bhai Jarnail Singh Ji",
                durationMs = 985568L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/japji_jarnail_singh"
            ),

            AudioTrack(
                id = "curated_japji_harshdeep",
                title = "Japji Sahib",
                artist = "Harshdeep Kaur",
                durationMs = 2217460L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/japji_sahib_harshdeep_kaur"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * JAAP SAHIB
         * ─────────────────────────────────────────
         */

        "jaap-sahib" to listOf(

            AudioTrack(
                id = "curated_jaap_darshan",
                title = "Jaap Sahib",
                artist = "Master Darshan Singh Ji",
                durationMs = 1262847L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/jaap_sahib_darshan_singh"
            ),

            AudioTrack(
                id = "curated_jaap_jarnail",
                title = "Jaap Sahib",
                artist = "Bhai Jarnail Singh Ji",
                durationMs = 990401L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/jaap_sahib_jarnail_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * TAV PRASAD SAVAIYE
         * ─────────────────────────────────────────
         */

        "tav-prasad-savaiye" to listOf(

            AudioTrack(
                id = "curated_savaiye_darshan",
                title = "Tav Prasad Savaiye",
                artist = "Master Darshan Singh Ji",
                durationMs = 462315L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/tav_prasad_darshan_singh"
            ),

            AudioTrack(
                id = "curated_savaiye_thakur",
                title = "Tav Prasad Savaiye",
                artist = "Giani Thakur Singh Ji",
                durationMs = 314496L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/tav_prasad_thakur_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * BENTI CHAUPAI SAHIB
         * ─────────────────────────────────────────
         */

        "benti-chaupai-sahib" to listOf(

            AudioTrack(
                id = "curated_chaupai_darshan",
                title = "Chaupai Sahib",
                artist = "Master Darshan Singh Ji",
                durationMs = 322216L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/chaupai_sahib_darshan_singh"
            ),

            AudioTrack(
                id = "curated_chaupai_jarnail",
                title = "Chaupai Sahib",
                artist = "Bhai Jarnail Singh Ji",
                durationMs = 317251L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/chaupai_sahib_jarnail_singh"
            ),

            AudioTrack(
                id = "curated_chaupai_harshdeep",
                title = "Chaupai Sahib",
                artist = "Harshdeep Kaur",
                durationMs = 655400L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/chaupai_sahib_harshdeep_kaur"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * ANAND SAHIB
         * ─────────────────────────────────────────
         */

        "anand-sahib" to listOf(

            AudioTrack(
                id = "curated_anand_darshan",
                title = "Anand Sahib",
                artist = "Master Darshan Singh Ji",
                durationMs = 891948L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/anand_sahib_darshan_singh"
            ),

            AudioTrack(
                id = "curated_anand_thakur",
                title = "Anand Sahib",
                artist = "Giani Thakur Singh Ji",
                durationMs = 984788L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/anand_sahib_thakur_singh"
            ),

            AudioTrack(
                id = "curated_anand_harshdeep",
                title = "Anand Sahib",
                artist = "Harshdeep Kaur",
                durationMs = 242300L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/anand_sahib_harshdeep_kaur"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * REHRAS SAHIB
         * ─────────────────────────────────────────
         */

        "rehras-sahib" to listOf(

            AudioTrack(
                id = "curated_rehras_darshan",
                title = "Rehras Sahib",
                artist = "Master Darshan Singh Ji",
                durationMs = 1359244L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/rehras_sahib_darshan_singh"
            ),

            AudioTrack(
                id = "curated_rehras_jarnail",
                title = "Rehras Sahib",
                artist = "Bhai Jarnail Singh Ji",
                durationMs = 1445166L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/rehras_sahib_jarnail_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * KIRTAN SOHILA
         * ─────────────────────────────────────────
         */

        "kirtan-sohila" to listOf(

            AudioTrack(
                id = "curated_sohila_darshan",
                title = "Kirtan Sohila",
                artist = "Master Darshan Singh Ji",
                durationMs = 226993L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/kirtan_sohila_darshan_singh"
            ),

            AudioTrack(
                id = "curated_sohila_thakur",
                title = "Kirtan Sohila",
                artist = "Giani Thakur Singh Ji",
                durationMs = 275156L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/kirtan_sohila_thakur_singh"
            ),

            AudioTrack(
                id = "curated_sohila_annie",
                title = "Kirtan Sohila",
                artist = "Annie Ahluwalia",
                durationMs = 472500L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/kirtan_sohila_annie_ahluwalia"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * ARDAS
         * ─────────────────────────────────────────
         */

        "ardas" to listOf(

            AudioTrack(
                id = "curated_ardas_darshan",
                title = "Ardas",
                artist = "Master Darshan Singh Ji",
                durationMs = 137689L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/ardas_darshan_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * SUKHMANI SAHIB
         * ─────────────────────────────────────────
         */

        "sukhmani-sahib" to listOf(

            AudioTrack(
                id = "curated_sukhmani_darshan",
                title = "Sukhmani Sahib",
                artist = "Master Darshan Singh Ji",
                durationMs = 1179104L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/sukhmani_sahib_darshan_singh"
            ),

            AudioTrack(
                id = "curated_sukhmani_thakur",
                title = "Sukhmani Sahib",
                artist = "Giani Thakur Singh Ji",
                durationMs = 2652090L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/sukhmani_sahib_thakur_singh"
            ),

            AudioTrack(
                id = "curated_sukhmani_hargun",
                title = "Sukhmani Sahib",
                artist = "Hargun Kaur",
                durationMs = 11534100L,
                license = "CC BY-SA",
                attribution = "Bundled Asset",
                remoteUrl = "raw/sukhmani_sahib_hargun_kaur"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * ASA DI VAAR
         * ─────────────────────────────────────────
         */

        "asa-di-vaar" to listOf(

            AudioTrack(
                id = "curated_asa_darbar_sahib",
                title = "Asa Di Vaar (Darbar Sahib)",
                artist = "Hazoori Ragi Sri Darbar Sahib",
                durationMs = 4500000L,
                license = "Public Domain",
                attribution = "Archive.org",
                remoteUrl = "https://archive" +
                        ".org/download/AsaDiVaar/Asa%20Di%20Vaar%20June%2018%2C%202017.mp3"
            ),

            AudioTrack(
                id = "curated_asa_kamaljit",
                title = "Asa Di Vaar (Kirtan)",
                artist = "Bhai Kamaljit Singh Ji",
                durationMs = 5200000L,
                license = "CC BY-SA",
                attribution = "Archive.org",
                remoteUrl = "https://archive.org/download/asa-di-vaar-bhai-kamaljit-singh-jee-31" +
                        ".05.2015/Asa%20Di%20Vaar%20Bhai%20Kamaljit%20Singh%20Jee%2031.05.2015.mp3"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * SHABAD HAZARE
         * ─────────────────────────────────────────
         */

        "shabad-hazare" to listOf(

            AudioTrack(
                id = "curated_hazare_darshan",
                title = "Shabad Hazare (Patshahi 1–9)",
                artist = "Master Darshan Singh Ji",
                durationMs = 435514L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/shabad_hazare_darshan_singh_1_9"
            ),

            AudioTrack(
                id = "curated_hazare_darshan2",
                title = "Shabad Hazare (Patshahi 10)",
                artist = "Master Darshan Singh Ji",
                durationMs = 355977L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/shabad_hazare_darshan_singh_10"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * SALOK MAHALLA 9
         * ─────────────────────────────────────────
         */

        "salok-mahalla-9" to listOf(

            AudioTrack(
                id = "curated_salok_m9_darshan",
                title = "Salok Mahalla 9",
                artist = "Master Darshan Singh Ji",
                durationMs = 1131186L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/salok_mahalla_9_darshan_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * BARAH MAHA
         * ─────────────────────────────────────────
         */

        "barah-maha" to listOf(

            AudioTrack(
                id = "curated_barah_darshan",
                title = "Barah Maha",
                artist = "Master Darshan Singh Ji",
                durationMs = 516290L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/barah_maha_darshan_singh"
            )
        ),

       /*
         * ─────────────────────────────────────────
         * DAKHNI OONKAR
         * ─────────────────────────────────────────
         */
"dakhni-oankar" to listOf(
    AudioTrack(
        id = "curated_dakhni_pishora",
        title = "Dakhni Oankar",
        artist = "Pishora Singh",
        durationMs = 1868530L,
        license = "Public Domain",
        attribution = "Bundled Asset",
        remoteUrl = "raw/dakhni_oankar_pishora_singh"
    )
),
   /*
         * ─────────────────────────────────────────
         * SIDHA GOSHT
         * ─────────────────────────────────────────
         */
"sidha-gosht" to listOf(
    AudioTrack(
        id = "curated_sidha_pishora",
        title = "Sidha Gosht",
        artist = "Pishora Singh",
        durationMs = 1876920L,
        license = "Public Domain",
        attribution = "Bundled Asset",
        remoteUrl = "raw/sidha_gosht_pishora_singh"
    )
),

        /*
         * ─────────────────────────────────────────
         * BASANT KI VAAR
         * ─────────────────────────────────────────
         */

        "basant-ki-vaar" to listOf(

            AudioTrack(
                id = "curated_basant_satvinder_harvinder",
                title = "Basant Ki Vaar (Kirtan)",
                artist = "Bhai Satvinder Singh Ji & Harvinder Singh Ji",
                durationMs = 280620L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/basant_ki_vaar_satvinder_harvinder_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * RAMKALI KI VAAR
         * ─────────────────────────────────────────
         */

        "ramkali-ki-vaar" to listOf(

            AudioTrack(
                id = "curated_ramkali_darshan",
                title = "Ramkali Ki Vaar",
                artist = "Master Darshan Singh Ji",
                durationMs = 344176L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/ramkali_ki_vaar_darshan_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * AARTI
         * ─────────────────────────────────────────
         */

        "aarti" to listOf(

            AudioTrack(
                id = "curated_aarti_jujhar",
                title = "Aarti (Sri Darbar Sahib)",
                artist = "Bhai Jujhar Singh Ji",
                durationMs = 5626305L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/aarti_jujhar_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * CHANDI DI VAAR
         * ─────────────────────────────────────────
         */

        "chandi-di-vaar" to listOf(

            AudioTrack(
                id = "curated_chandi_darshan",
                title = "Chandi Di Vaar",
                artist = "Master Darshan Singh Ji",
                durationMs = 1140768L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/chandi_di_vaar_darshan_singh"
            )
        ),

        /*
         * ─────────────────────────────────────────
         * RAGMALA
         * ─────────────────────────────────────────
         */

        "ragmala" to listOf(

            AudioTrack(
                id = "curated_ragmala_darshan",
                title = "Ragmala",
                artist = "Master Darshan Singh Ji",
                durationMs = 1131186L,
                license = "Public Domain",
                attribution = "Bundled Asset",
                remoteUrl = "raw/salok_mahalla_9_darshan_singh"
            )
        )
    )
}