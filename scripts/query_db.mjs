import { execFileSync } from "node:child_process";

const db = "node_modules/@shabados/database/dist/master.sqlite";

function sqliteJson(sql) {
  const output = execFileSync("sqlite3", ["-json", db, sql], { encoding: "utf8", maxBuffer: 1024 * 1024 * 80 });
  return JSON.parse(output || "[]");
}

try {
  const rows = sqliteJson(`
    SELECT
      'RKKV' as baniId,
      l.id as lineId,
      1 as sectionOrder,
      al.asset_id as assetId,
      al.type as type,
      al.data as data,
      json_extract(al.additional, '$.language') as language,
      al.priority as priority
    FROM lines l
    JOIN line_groups lg ON l.line_group_id = lg.id
    JOIN sections sec ON lg.section_id = sec.id
    JOIN asset_lines al ON al.line_id = l.id
    JOIN asset_lines al_page ON al_page.line_id = l.id AND al_page.type = 'primary'
    WHERE sec.source_id = 'SGGS'
      AND CAST(json_extract(al_page.additional, '$.page') AS INTEGER) >= 947
      AND CAST(json_extract(al_page.additional, '$.page') AS INTEGER) <= 956
      AND (
        al.type = 'primary'
        OR (al.type = 'translation' AND json_extract(al.additional, '$.language') in ('en', 'pa'))
        OR (al.type = 'note' AND json_extract(al.additional, '$.language') = 'pa')
      )
  `);
  
  console.log("Total translation & primary rows found:", rows.length);
  // Find distinct lineIds
  const lineIds = new Set(rows.map(r => r.lineId));
  console.log("Total unique lines:", lineIds.size);
} catch (e) {
  console.error("General error:", e);
}
