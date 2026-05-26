package org.audiocular.nitnem.data

import org.audiocular.nitnem.models.AudioTrack

object CuratedAudioCatalog {
    fun getTracksForBani(baniId: String): List<AudioTrack> {
        return tracksMap[baniId] ?: emptyList()
    }

    private val tracksMap = mapOf(
        "japji-sahib" to listOf(
            AudioTrack(
                id = "curated_japji_jarnail",
                title = "Japji Sahib (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 939000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=5oYw9oG6t3E"
            ),
            AudioTrack(
                id = "curated_japji_riar",
                title = "Japji Sahib (Melodic Recitation)",
                artist = "Bhai Joginder Singh Riar",
                durationMs = 1110000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Joginder Singh Riar",
                remoteUrl = "https://www.youtube.com/watch?v=D-a61Uv5vQ0"
            )
        ),
        "jaap-sahib" to listOf(
            AudioTrack(
                id = "curated_jaap_darshan",
                title = "Jaap Sahib (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 1262890L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/04Jaap.mp3"
            ),
            AudioTrack(
                id = "curated_jaap_jarnail",
                title = "Jaap Sahib (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1262000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=dQ02q1_q79Q"
            )
        ),
        "tav-prasad-savaiye" to listOf(
            AudioTrack(
                id = "curated_savaiye_darshan",
                title = "Tav Prasad Savaiye (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 462370L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/12Swaiye-10.mp3"
            ),
            AudioTrack(
                id = "curated_savaiye_jarnail",
                title = "Tav Prasad Savaiye (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 462000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=8V-w00a9H4Y"
            )
        ),
        "benti-chaupai-sahib" to listOf(
            AudioTrack(
                id = "curated_chaupai_darshan",
                title = "Chaupai Sahib (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 322270L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/11Chopai.mp3"
            ),
            AudioTrack(
                id = "curated_chaupai_jarnail",
                title = "Chaupai Sahib (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 322000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=0k5G6pLz6i0"
            )
        ),
        "anand-sahib" to listOf(
            AudioTrack(
                id = "curated_anand_darshan",
                title = "Anand Sahib (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 892010L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/08AnandSahib.mp3"
            ),
            AudioTrack(
                id = "curated_anand_jarnail",
                title = "Anand Sahib (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 892000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=k2aB-VnI82U"
            )
        ),
        "rehras-sahib" to listOf(
            AudioTrack(
                id = "curated_rehras_darshan",
                title = "Rehras Sahib (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 1359280L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/09Rahras.mp3"
            ),
            AudioTrack(
                id = "curated_rehras_jarnail",
                title = "Rehras Sahib (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1359000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=w7K4Pswp198"
            )
        ),
        "kirtan-sohila" to listOf(
            AudioTrack(
                id = "curated_sohila_darshan",
                title = "Kirtan Sohila (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 227030L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/10KirtanSohila.mp3"
            ),
            AudioTrack(
                id = "curated_sohila_jarnail",
                title = "Kirtan Sohila (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 227000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=y70jS-2xQyY"
            )
        ),
        "ardas" to listOf(
            AudioTrack(
                id = "curated_ardas_darshan",
                title = "Ardas (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 137740L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/30Ardas.mp3"
            ),
            AudioTrack(
                id = "curated_ardas_jarnail",
                title = "Ardas (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 137000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=7uK7g-Q9QzE"
            )
        ),
        "sukhmani-sahib" to listOf(
            AudioTrack(
                id = "curated_sukhmani_jarnail",
                title = "Sukhmani Sahib (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 3900000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=D-a61Uv5vQ0"
            )
        ),
        "asa-di-vaar" to listOf(
            AudioTrack(
                id = "curated_asa_divaar_jarnail",
                title = "Asa Di Vaar (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1800000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=y70jS-2xQyY"
            )
        ),
        "shabad-hazare" to listOf(
            AudioTrack(
                id = "curated_hazare_darshan",
                title = "Shabad Hazare (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 435570L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/03Hazare.mp3"
            ),
            AudioTrack(
                id = "curated_hazare_jarnail",
                title = "Shabad Hazare (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 435000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=0lWv1_V0h1g"
            )
        ),
        "salok-mahalla-9" to listOf(
            AudioTrack(
                id = "curated_salok_m9_jarnail",
                title = "Salok Mahalla 9 (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 900000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=7s2K0j0V6yY"
            )
        ),
        "barah-maha" to listOf(
            AudioTrack(
                id = "curated_barah_darshan",
                title = "Barah Maha (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 516340L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/23BarahMaah.mp3"
            ),
            AudioTrack(
                id = "curated_barah_jarnail",
                title = "Barah Maha (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 516000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=P6k9P0oK_dY"
            )
        ),
        "dakhni-oankar" to listOf(
            AudioTrack(
                id = "curated_oankar_jarnail",
                title = "Dakhni Oankar (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1200000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=b4b6jV-0h1g"
            )
        ),
        "sidha-gosht" to listOf(
            AudioTrack(
                id = "curated_sidha_gosht_jarnail",
                title = "Sidha Gosht (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1500000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=p4b6jV-0h1g"
            )
        ),
        "basant-ki-vaar" to listOf(
            AudioTrack(
                id = "curated_basant_jarnail",
                title = "Basant Ki Vaar (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 300000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=q4b6jV-0h1g"
            )
        ),
        "ramkali-ki-vaar" to listOf(
            AudioTrack(
                id = "curated_ramkali_jarnail",
                title = "Ramkali Ki Vaar (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1500000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=r4b6jV-0h1g"
            )
        ),
        "aarti" to listOf(
            AudioTrack(
                id = "curated_aarti_jarnail",
                title = "Aarti (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 600000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=s4b6jV-0h1g"
            )
        ),
        "chandi-di-vaar" to listOf(
            AudioTrack(
                id = "curated_chandi_darshan",
                title = "Chandi Di Vaar (Traditional)",
                artist = "Master Darshan Singh Ji",
                durationMs = 1140820L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/06ChandiDiVar.mp3"
            ),
            AudioTrack(
                id = "curated_chandi_jarnail",
                title = "Chandi Di Vaar (Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1140000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=t4b6jV-0h1g"
            )
        ),
        "ragmala" to listOf(
            AudioTrack(
                id = "curated_ragmala_darshan",
                title = "Sri Guru Granth Sahib Paath Bhog (Ragmala)",
                artist = "Master Darshan Singh Ji",
                durationMs = 1131240L,
                license = "Public Domain",
                attribution = "Archive.org: Namdhari Nitnem Audio",
                remoteUrl = "https://archive.org/download/namdhari-nitnem/29BhogSalok.mp3"
            ),
            AudioTrack(
                id = "curated_ragmala_jarnail",
                title = "Sri Guru Granth Sahib Paath Bhog (Ragmala - Santhiya)",
                artist = "Bhai Jarnail Singh Ji (Damdami Taksal)",
                durationMs = 1131000L,
                license = "CC BY-SA",
                attribution = "YouTube: Bhai Jarnail Singh Ji",
                remoteUrl = "https://www.youtube.com/watch?v=u4b6jV-0h1g"
            )
        )
    )
}
