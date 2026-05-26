import { execFileSync } from "node:child_process";
import { mkdirSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";

const db = "node_modules/@shabados/database/dist/master.sqlite";
const jsonOut = "composeApp/src/commonMain/composeResources/files/content/banis";
const kotlinOut = "shared/data/src/commonMain/kotlin/org/audiocular/nitnem/data/generated/GeneratedBaniCatalog.kt";

const banis = [
  {
    id: "japji-sahib",
    dbId: "JAPJ",
    title: "Japji Sahib",
    time: "Morning",
    minutes: 16,
    audio: {
      id: "audio-japji",
      title: "Japji Sahib",
      artist: "Hari Singh",
      durationMs: 939000,
      license: "CC BY-SA 3.0 / GFDL",
      attribution: "Audio: JapjiSahib.ogg by Hari Singh, Wikimedia Commons",
      remoteUrl: "https://upload.wikimedia.org/wikipedia/commons/5/5d/JapjiSahib.ogg"
    }
  },
  { id: "jaap-sahib", dbId: "JAAP", title: "Jaap Sahib", time: "Morning", minutes: 30 },
  { id: "tav-prasad-savaiye", dbId: "TPSS", title: "Tav Prasad Savaiye", time: "Morning", minutes: 5 },
  { id: "benti-chaupai-sahib", dbId: "BNCP", title: "Benti Chaupai Sahib", time: "Morning", minutes: 12 },
  { id: "anand-sahib", dbId: "ANND", title: "Anand Sahib", time: "Morning", minutes: 15 },
  { id: "rehras-sahib", dbId: "RHRS", title: "Rehras Sahib", time: "Evening", minutes: 20 },
  { id: "kirtan-sohila", dbId: "SHLA", title: "Kirtan Sohila", time: "Night", minutes: 7 },
  { id: "ardas", dbId: "ARDS", title: "Ardas", time: "Night", minutes: 5 },
  { id: "sukhmani-sahib", dbId: "SKMI", title: "Sukhmani Sahib", time: "Morning", minutes: 60 },
  { id: "asa-di-vaar", dbId: "ASKV", title: "Asa Di Vaar", time: "Morning", minutes: 30 },
  { id: "shabad-hazare", dbId: "SHZR", title: "Shabad Hazare", time: "Morning", minutes: 10 },
  { id: "salok-mahalla-9", dbId: "SLK9", title: "Salok Mahalla 9", time: "Evening", minutes: 15 },
  { id: "barah-maha", dbId: "BRMH", title: "Barah Maha", time: "Morning", minutes: 10 },
  { id: "dakhni-oankar", dbId: "OANK", title: "Dakhni Oankar", time: "Morning", minutes: 20 },
  { id: "sidha-gosht", dbId: "SGST", title: "Sidha Gosht", time: "Morning", minutes: 25 },
  { id: "basant-ki-vaar", dbId: "BSKV", title: "Basant Ki Vaar", time: "Morning", minutes: 5 },
  { id: "ramkali-ki-vaar", dbId: "RKKV", title: "Ramkali Ki Vaar", time: "Morning", minutes: 25 },
  { id: "aarti", dbId: "ARTI", title: "Aarti", time: "Evening", minutes: 10 },
  { id: "chandi-di-vaar", dbId: "CDDV", title: "Chandi Di Vaar", time: "Morning", minutes: 15 },
  { id: "ragmala", dbId: "RGMA", title: "Sri Guru Granth Sahib Paath Bhog (Ragmala)", time: "Night", minutes: 40 }
];

const metadataMap = {
  "japji-sahib": {
    author: "Guru Nanak Dev Ji",
    ang: "Ang 1",
    structure: "38 Pauris, 2 Saloks"
  },
  "jaap-sahib": {
    author: "Guru Gobind Singh Ji",
    ang: "Dasam Granth Ang 1",
    structure: "199 Verses"
  },
  "tav-prasad-savaiye": {
    author: "Guru Gobind Singh Ji",
    ang: "Dasam Granth Ang 13",
    structure: "10 Savaiye"
  },
  "benti-chaupai-sahib": {
    author: "Guru Gobind Singh Ji",
    ang: "Dasam Granth Ang 1386",
    structure: "29 Verses"
  },
  "anand-sahib": {
    author: "Guru Amar Das Ji",
    ang: "Ang 917",
    structure: "40 Pauris"
  },
  "rehras-sahib": {
    author: "Multiple Gurus",
    ang: "Ang 8",
    structure: "Compilation"
  },
  "kirtan-sohila": {
    author: "Multiple Gurus",
    ang: "Ang 12",
    structure: "5 Shabads"
  },
  "sukhmani-sahib": {
    author: "Guru Arjan Dev Ji",
    ang: "Ang 262",
    structure: "24 Ashtpadis, 24 Saloks"
  },
  "asa-di-vaar": {
    author: "Guru Nanak Dev Ji",
    ang: "Ang 462",
    structure: "24 Pauris, 59 Saloks"
  },
  "ardas": {
    author: "Traditional",
    ang: "Sikh Rehat Maryada",
    structure: "Supplication"
  },
  "shabad-hazare": {
    author: "Guru Nanak Dev Ji & Guru Arjan Dev Ji",
    ang: "Ang 96",
    structure: "7 Shabads"
  },
  "salok-mahalla-9": {
    author: "Guru Tegh Bahadur Ji",
    ang: "Ang 1426",
    structure: "57 Saloks"
  },
  "barah-maha": {
    author: "Guru Arjan Dev Ji",
    ang: "Ang 133",
    structure: "14 Stanzas"
  },
  "dakhni-oankar": {
    author: "Guru Nanak Dev Ji",
    ang: "Ang 929",
    structure: "54 Pauris"
  },
  "sidha-gosht": {
    author: "Guru Nanak Dev Ji",
    ang: "Ang 938",
    structure: "73 Pauris"
  },
  "basant-ki-vaar": {
    author: "Guru Arjan Dev Ji",
    ang: "Ang 1193",
    structure: "3 Pauris"
  },
  "ramkali-ki-vaar": {
    author: "Guru Amar Das Ji",
    ang: "Ang 947",
    structure: "21 Pauris"
  },
  "aarti": {
    author: "Guru Nanak Dev Ji & Bhagats",
    ang: "Ang 663",
    structure: "5 Shabads"
  },
  "chandi-di-vaar": {
    author: "Guru Gobind Singh Ji",
    ang: "Dasam Granth Ang 119",
    structure: "55 Pauris"
  },
  "ragmala": {
    author: "Sri Guru Granth Sahib Ji",
    ang: "Ang 1429",
    structure: "Salok Mahalla 9, Mundavni, Ragmala"
  }
};

const CONSONANTS = "ਕਖਗਘਙਚਛਜਝਞਟਠਡਢਣਤਥਦਧਨਪਫਬਭਮਯਰਲਵੜਸਹਸ਼ਖ਼ਗ਼ਜ਼ਫ਼ਲ਼";
const INDEPENDENT_VOWELS = "ਅਆਇਈਉਊਏਐਓਔੳੲ";
const VOWEL_SIGNS = "ਾਿੀੁੂੇੈੋੌ";
const NASALIZATION_MARKS = "ਂੰਁ";
const LABIAL_CONSONANTS = "ਪਫਬਭਮ";

function getWordAt(text, i) {
  let start = i;
  while (start > 0 && !" \t\r\n.,;:!?()।॥\"'-".includes(text[start - 1])) {
    start--;
  }
  let end = i;
  while (end < text.length - 1 && !" \t\r\n.,;:!?()।॥\"'-".includes(text[end + 1])) {
    end++;
  }
  return text.slice(start, end + 1);
}

function gurmukhiToHindi(text) {
  if (!text) return "";
  
  const map = {
    // Independent Vowels
    'ਅ': 'अ', 'ਆ': 'आ', 'ਇ': 'इ', 'ਈ': 'ई', 'ਉ': 'उ', 'ਊ': 'ऊ', 'ਏ': 'ए', 'ਐ': 'ऐ', 'ਓ': 'ओ', 'ਔ': 'औ',
    'ੳ': 'उ', 'ੲ': 'इ',
    
    // Consonants
    'ਕ': 'क', 'ਖ': 'ख', 'ਗ': 'ग', 'ਘ': 'घ', 'ਙ': 'ङ',
    'ਚ': 'च', 'ਛ': 'छ', 'ਜ': 'ज', 'ਝ': 'झ', 'ਞ': 'ञ',
    'ਟ': 'ट', 'ਠ': 'ठ', 'ਡ': 'ड', 'ਢ': 'ढ', 'ਣ': 'ण',
    'ਤ': 'त', 'ਥ': 'थ', 'ਦ': 'द', 'ਧ': 'ध', 'ਨ': 'न',
    'ਪ': 'प', 'ਫ': 'फ', 'ਬ': 'ब', 'ਭ': 'भ', 'ਮ': 'म',
    'ਯ': 'य', 'ਰ': 'र', 'ਲ': 'ल', 'ਵ': 'व', 'ੜ': 'ड़',
    'ਸ': 'स', 'ਹ': 'ह',
    'ਸ਼': 'श', 'ਖ਼': 'ख़', 'ਗ਼': 'ग़', 'ਜ਼': 'ज़', 'ਫ਼': 'फ़', 'ਲ਼': 'ळ',
    
    // Vowel Signs
    'ਾ': 'ा', 'ਿ': 'ि', 'ੀ': 'ी', 'ੁ': 'ु', 'ੂ': 'ू',
    'ੇ': 'े', 'ੈ': 'ै', 'ੋ': 'ो', 'ੌ': 'ौ',
    
    // Nasalization & Specialized marks
    'ਂ': 'ं', 'ੰ': 'ं', 'ਁ': 'ँ', 'ਃ': 'ः',
    'ੑ': '्ह', 'ੵ': '्य',
    'ੴ': 'एकओंकार',
    
    // Numbers & Punctuation
    '੦': '०', '੧': '१', '੨': '२', '੩': '३', '੪': '४', '੫': '५', '੬': '६', '੭': '७', '੮': '८', '੯': '९',
    '।': '।', '॥': '॥'
  };

  let result = "";
  for (let i = 0; i < text.length; i++) {
    const char = text[i];
    if (char === 'ੱ') {
      let nextIdx = i + 1;
      while (nextIdx < text.length && (text[nextIdx] === 'ਿ' || text[nextIdx] === 'ੁ')) {
        nextIdx++;
      }
      if (nextIdx < text.length) {
        const nextChar = text[nextIdx];
        const devNext = map[nextChar] || nextChar;
        result += devNext + '्';
      }
      continue;
    }
    if (char === '੍') {
      result += '्';
      continue;
    }
    result += map[char] || char;
  }

  return result
    .replaceAll('अा', 'आ')
    .replaceAll('अै', 'ऐ')
    .replaceAll('अौ', 'औ')
    .replaceAll('इि', 'इ')
    .replaceAll('इी', 'ई')
    .replaceAll('इे', 'ए')
    .replaceAll('इै', 'ऐ')
    .replaceAll('उु', 'उ')
    .replaceAll('उू', 'ऊ')
    .replaceAll('उो', 'ओ');
}

function gurmukhiToRoman(text) {
  if (!text) return "";

  const map = {
    // Independent Vowels
    'ਅ': 'a', 'ਆ': 'aa', 'ਇ': 'i', 'ਈ': 'ee', 'ਉ': 'u', 'ਊ': 'oo', 'ਏ': 'e', 'ਐ': 'ai', 'ਓ': 'o', 'ਔ': 'au',
    'ੳ': 'u', 'ੲ': 'i',

    // Consonants
    'ਕ': 'k', 'ਖ': 'kh', 'ਗ': 'g', 'ਘ': 'gh', 'ਙ': 'ng',
    'ਚ': 'ch', 'ਛ': 'chh', 'ਜ': 'j', 'ਝ': 'jh', 'ਞ': 'n',
    'ਟ': 't', 'ਠ': 'th', 'ਡ': 'd', 'ਢ': 'dh', 'ਣ': 'n',
    'ਤ': 't', 'ਥ': 'th', 'ਦ': 'd', 'ਧ': 'dh', 'ਨ': 'n',
    'ਪ': 'p', 'ਫ': 'ph', 'ਬ': 'b', 'ਭ': 'bh', 'ਮ': 'm',
    'ਯ': 'y', 'ਰ': 'r', 'ਲ': 'l', 'ਵ': 'v', 'ੜ': 'r',
    'ਸ': 's', 'ਹ': 'h',
    'ਸ਼': 'sh', 'ਖ਼': 'kh', 'ਗ਼': 'g', 'ਜ਼': 'z', 'ਫ਼': 'f', 'ਲ਼': 'l',

    // Vowel Signs
    'ਾ': 'aa', 'ਿ': 'i', 'ੀ': 'ee', 'ੁ': 'u', 'ੂ': 'oo',
    'ੇ': 'e', 'ੈ': 'ai', 'ੋ': 'o', 'ੌ': 'au',

    // Nasalization & Specialized marks
    'ਂ': 'n', 'ੰ': 'n', 'ਁ': 'n', 'ਃ': 'h',
    'ੑ': 'h', 'ੵ': 'y',
    'ੴ': 'ik oankar',

    // Numbers & Punctuation
    '੦': '0', '੧': '1', '੨': '2', '੩': '3', '੪': '4', '੫': '5', '੬': '6', '੭': '7', '੮': '8', '੯': '9',
    '।': '|', '॥': '||'
  };

  let result = "";
  for (let i = 0; i < text.length; i++) {
    const char = text[i];

    // Omit silent final 'ੁ' and 'ਿ' for words with length > 2
    if (char === 'ੁ' || char === 'ਿ') {
      const isWordEnd = (i + 1 >= text.length) || " \t\r\n.,;:!?()।॥\"'-".includes(text[i + 1]);
      if (isWordEnd) {
        const word = getWordAt(text, i);
        if (word.length > 2) {
          continue;
        }
      }
    }

    if (char === 'ੱ') {
      let nextIdx = i + 1;
      while (nextIdx < text.length && (text[nextIdx] === 'ਿ' || text[nextIdx] === 'ੁ')) {
        nextIdx++;
      }
      if (nextIdx < text.length) {
        const nextChar = text[nextIdx];
        const romNext = map[nextChar] || nextChar;
        if (romNext.length > 0) {
          result += romNext[0];
        }
      }
      continue;
    }

    if (char === '੍') {
      continue;
    }

    // Nasalization assimilation rule: map tippi/bindi to 'm' before labial consonants
    if (char === 'ੰ' || char === 'ਂ') {
      let nextIdx = i + 1;
      while (nextIdx < text.length && (text[nextIdx] === 'ਿ' || text[nextIdx] === 'ੁ' || text[nextIdx] === '੍')) {
        nextIdx++;
      }
      if (nextIdx < text.length && LABIAL_CONSONANTS.includes(text[nextIdx])) {
        result += "m";
        continue;
      }
    }

    const rom = map[char] || char;
    result += rom;

    // Implicit 'a' rule
    if (CONSONANTS.includes(char)) {
      const nextChar = i + 1 < text.length ? text[i + 1] : "";
      
      // Determine if nextChar calls for an implicit 'a'
      const isNextConsonantOrVowelOrNasal = nextChar && (
        CONSONANTS.includes(nextChar) || 
        INDEPENDENT_VOWELS.includes(nextChar) || 
        NASALIZATION_MARKS.includes(nextChar)
      );

      if (isNextConsonantOrVowelOrNasal) {
        // Schwa deletion lookahead: suppress implicit 'a' if nextChar is followed by a vowel sign
        // UNLESS the consonant is part of a subjoined cluster (preceded by '੍')
        const charAfterNext = i + 2 < text.length ? text[i + 2] : "";
        let isNextFollowedByVowel = charAfterNext && VOWEL_SIGNS.includes(charAfterNext);
        
        // If the vowel following nextChar is a final silent 'ੁ' or 'ਿ', it will be omitted,
        // so it does not count as a pronounced vowel for schwa suppression purposes.
        if (isNextFollowedByVowel && (charAfterNext === 'ੁ' || charAfterNext === 'ਿ')) {
          const nextCharIndex = i + 2;
          const isWordEnd = (nextCharIndex + 1 >= text.length) || " \t\r\n.,;:!?()।॥\"'-".includes(text[nextCharIndex + 1]);
          if (isWordEnd) {
            const word = getWordAt(text, nextCharIndex);
            if (word.length > 2) {
              isNextFollowedByVowel = false;
            }
          }
        }

        const isSubjoined = i > 0 && text[i - 1] === '੍';
        
        if (isNextFollowedByVowel && i > 0 && !isSubjoined) {
          // Suppress 'a'
        } else {
          result += "a";
        }
      } else {
        // Standalone single-letter consonant word gets implicit 'a'
        const isPrecededByBoundary = i === 0 || " \t\r\n.,;:!?()।॥\"'-".includes(text[i - 1]);
        const isFollowedByBoundary = i + 1 === text.length || " \t\r\n.,;:!?()।॥\"'-".includes(text[i + 1]);
        if (isPrecededByBoundary && isFollowedByBoundary) {
          result += "a";
        }
      }
    }
  }

  return result
    .replaceAll('aaa', 'aa')
    .replaceAll('aii', 'i')
    .replaceAll('auu', 'u')
    .replaceAll('mmr', 'mr') // clean double m in amrit
    .replace(/\s+/g, ' ')
    .trim();
}

function sqliteJson(sql) {
  const output = execFileSync("sqlite3", ["-json", db, sql], { encoding: "utf8", maxBuffer: 1024 * 1024 * 80 });
  return JSON.parse(output || "[]");
}

function sqlString(value) {
  return `'${String(value).replaceAll("'", "''")}'`;
}

const normalBanis = banis.filter(b => b.dbId !== "BSKV" && b.dbId !== "RKKV");

const normalRows = sqliteJson(`
  select
     bl.bani_id as baniId,
     bl.line_id as lineId,
     bl.section_order as sectionOrder,
     bl.line_order as lineOrder,
     al.asset_id as assetId,
     al.type as type,
     al.data as data,
     json_extract(al.additional, '$.language') as language,
     al.priority as priority
  from bani_lines bl
  left join asset_lines al on al.line_id = bl.line_id
  where bl.bani_id in (${normalBanis.map((bani) => sqlString(bani.dbId)).join(",")})
    and (
      al.type = 'primary'
      or (al.type = 'translation' and json_extract(al.additional, '$.language') in ('en', 'pa'))
      or (al.type = 'note' and json_extract(al.additional, '$.language') = 'pa')
    )
  order by bl.bani_id, bl.section_order, bl.line_order, al.priority
`);

const bskvRows = sqliteJson(`
  select
     'BSKV' as baniId,
     l.id as lineId,
     1 as sectionOrder,
     l.line_group_order as lineOrder,
     al.asset_id as assetId,
     al.type as type,
     al.data as data,
     json_extract(al.additional, '$.language') as language,
     al.priority as priority
  from lines l
  left join asset_lines al on al.line_id = l.id
  where l.line_group_id = 'QMT'
    and (
      al.type = 'primary'
      or (al.type = 'translation' and json_extract(al.additional, '$.language') in ('en', 'pa'))
      or (al.type = 'note' and json_extract(al.additional, '$.language') = 'pa')
    )
  order by l.line_group_order, al.priority
`);

const rkkvRows = sqliteJson(`
  select
     'RKKV' as baniId,
     l.id as lineId,
     1 as sectionOrder,
     dense_rank() over (order by cast(json_extract(al_page.additional, '$.page') as integer), cast(json_extract(al_page.additional, '$.line') as integer), l.id) as lineOrder,
     al.asset_id as assetId,
     al.type as type,
     al.data as data,
     json_extract(al.additional, '$.language') as language,
     al.priority as priority
  from lines l
  join line_groups lg on l.line_group_id = lg.id
  join sections sec on lg.section_id = sec.id
  join asset_lines al on al.line_id = l.id
  join asset_lines al_page on al_page.line_id = l.id and al_page.type = 'primary'
  where sec.source_id = 'SGGS'
    and cast(json_extract(al_page.additional, '$.page') as integer) >= 947
    and cast(json_extract(al_page.additional, '$.page') as integer) <= 956
    and (
      al.type = 'primary'
      or (al.type = 'translation' and json_extract(al.additional, '$.language') in ('en', 'pa'))
      or (al.type = 'note' and json_extract(al.additional, '$.language') = 'pa')
    )
  order by cast(json_extract(al_page.additional, '$.page') as integer), cast(json_extract(al_page.additional, '$.line') as integer), al.priority
`);

const allRows = [...normalRows, ...bskvRows, ...rkkvRows];

function choose(current, candidate, score) {
  if (!candidate?.data) return current;
  if (!current || score(candidate) < score(current)) return candidate;
  return current;
}

const rowsByBani = new Map();
for (const row of allRows) {
  if (!rowsByBani.has(row.baniId)) rowsByBani.set(row.baniId, new Map());
  const lineMap = rowsByBani.get(row.baniId);
  if (!lineMap.has(row.lineId)) {
    lineMap.set(row.lineId, {
      lineId: row.lineId,
      sectionOrder: row.sectionOrder,
      lineOrder: row.lineOrder,
      primary: null,
      english: null,
      punjabi: null,
      note: null
    });
  }
  const line = lineMap.get(row.lineId);
  if (row.type === "primary") {
    line.primary = choose(line.primary, row, (item) => item.assetId === "SSA2" ? 0 : 10 + item.priority);
  } else if (row.type === "translation" && row.language === "en") {
    line.english = choose(line.english, row, (item) => item.assetId === "DSSK" ? 0 : item.assetId === "SBMS" ? 1 : 10 + item.priority);
  } else if (row.type === "translation" && row.language === "pa") {
    line.punjabi = choose(line.punjabi, row, (item) => item.assetId === "PSST" ? 0 : item.assetId === "SBMS" ? 1 : 10 + item.priority);
  } else if (row.type === "note" && row.language === "pa") {
    line.note = choose(line.note, row, (item) => item.priority);
  }
}

function lineRows(dbId) {
  return Array.from(rowsByBani.get(dbId)?.values() ?? [])
    .sort((a, b) => a.sectionOrder - b.sectionOrder || a.lineOrder - b.lineOrder)
    .map((line) => ({
      lineId: line.lineId,
      lineOrder: line.lineOrder,
      gurmukhi: line.primary?.data ?? "",
      english: line.english?.data ?? "",
      punjabi: line.punjabi?.data ?? "",
      note: line.note?.data ?? ""
    }));
}

function buildBani(config) {
  const rows = lineRows(config.dbId);
  const duration = config.audio?.durationMs ?? 0;
  const step = rows.length > 0 && duration > 0 ? Math.floor(duration / rows.length) : 0;
  const meta = metadataMap[config.id] || { author: "", ang: "", structure: "" };
  
  return {
    id: config.id,
    dbId: config.dbId,
    title: config.title,
    time: config.time,
    estimatedMinutes: config.minutes,
    source: {
      text: "ShabadOS Database 5.0.0-next.0, generated from local SQLite package.",
      audio: config.audio ? config.audio.attribution : "Audio source not wired yet.",
      licenseNote: "Gurbani text and supporting translations are from the ShabadOS database package. Review ShabadOS content/source licenses before release."
    },
    audio: config.audio ?? null,
    author: meta.author,
    ang: meta.ang,
    structure: meta.structure,
    lines: rows.map((row, index) => ({
      id: `${config.id}-${index + 1}`,
      sourceLineId: row.lineId,
      order: row.lineOrder,
      gurmukhi: row.gurmukhi,
      transliteration: gurmukhiToRoman(row.gurmukhi),
      english: row.english,
      hindi: gurmukhiToHindi(row.gurmukhi),
      punjabi: row.punjabi,
      wordMeaning: row.note,
      startsAtMs: step > 0 ? index * step : 0,
      endsAtMs: step > 0 ? (index + 1) * step : 0
    }))
  };
}

function kt(value) {
  return JSON.stringify(value).replaceAll("$", "\\$");
}

function ktFunctionName(value) {
  return value
    .split(/[^a-zA-Z0-9]+/)
    .filter(Boolean)
    .map((part, index) => {
      const lower = part.toLowerCase();
      const titled = lower.charAt(0).toUpperCase() + lower.slice(1);
      return index === 0 ? lower : titled;
    })
    .join("");
}

function toKotlinLine(line) {
  return `BaniLine(id = ${kt(line.id)}, gurmukhi = ${kt(line.gurmukhi)}, transliteration = ${kt(line.transliteration)}, translation = ${kt(line.english)}, hindi = ${kt(line.hindi)}, punjabi = ${kt(line.punjabi)}, wordMeaning = ${kt(line.wordMeaning)}, startsAtMs = ${line.startsAtMs}L, endsAtMs = ${line.endsAtMs}L)`;
}

function toKotlinBani(bani) {
  const functionName = `generated${ktFunctionName(bani.id).replace(/^./, (char) => char.toUpperCase())}`;
  const audio = bani.audio
    ? `AudioTrack(id = ${kt(bani.audio.id)}, title = ${kt(bani.audio.title)}, artist = ${kt(bani.audio.artist)}, durationMs = ${bani.audio.durationMs}L, license = ${kt(bani.audio.license)}, attribution = ${kt(bani.audio.attribution)}, remoteUrl = ${kt(bani.audio.remoteUrl)})`
    : "null";
  return `fun ${functionName}(): Bani = Bani(
    id = ${kt(bani.id)},
    title = ${kt(bani.title)},
    time = BaniTime.${bani.time},
    estimatedMinutes = ${bani.estimatedMinutes},
    audio = ${audio},
    source = ContentSource(text = ${kt(bani.source.text)}, audio = ${kt(bani.source.audio)}, licenseNote = ${kt(bani.source.licenseNote)}),
    author = ${kt(bani.author)},
    ang = ${kt(bani.ang)},
    structure = ${kt(bani.structure)},
    lines = ${functionName}Lines()
)
${toKotlinLineChunks(functionName, bani.lines)}`;
}

function toKotlinLineChunks(functionName, lines) {
  const chunkSize = 32;
  const chunks = [];
  for (let index = 0; index < lines.length; index += chunkSize) {
    chunks.push(lines.slice(index, index + chunkSize));
  }
  const combiner = `fun ${functionName}Lines(): List<BaniLine> = buildList {
${chunks.map((_, index) => `    addAll(${functionName}Lines${index}())`).join("\n")}
}`;
  const chunkFunctions = chunks.map((chunk, index) => `private fun ${functionName}Lines${index}(): List<BaniLine> = listOf(
${chunk.map((line) => `    ${toKotlinLine(line)}`).join(",\n")}
)`).join("\n\n");
  return `${combiner}\n\n${chunkFunctions}`;
}

mkdirSync(jsonOut, { recursive: true });
mkdirSync(dirname(kotlinOut), { recursive: true });

const generated = banis.map(buildBani);
for (const bani of generated) {
  writeFileSync(join(jsonOut, `${bani.id}.json`), `${JSON.stringify(bani, null, 2)}\n`);
}

const ktDir = dirname(kotlinOut);
for (const bani of generated) {
  const functionName = `generated${ktFunctionName(bani.id).replace(/^./, (char) => char.toUpperCase())}`;
  const fileName = `${functionName}.kt`;
  const fileContent = `package org.audiocular.nitnem.data.generated

import org.audiocular.nitnem.models.AudioTrack
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.models.BaniTime
import org.audiocular.nitnem.models.ContentSource

${toKotlinBani(bani)}
`;
  writeFileSync(join(ktDir, fileName), fileContent);
}

const catalogContent = `package org.audiocular.nitnem.data.generated

import org.audiocular.nitnem.models.Bani

object GeneratedBaniCatalog {
    val banis: List<Bani> by lazy {
        listOf(
${generated.map((bani) => `            generated${ktFunctionName(bani.id).replace(/^./, (char) => char.toUpperCase())}()`).join(",\n")}
        )
    }
}
`;

writeFileSync(kotlinOut, catalogContent);
console.log(`Generated ${generated.length} banis and ${generated.reduce((sum, bani) => sum + bani.lines.length, 0)} lines.`);
