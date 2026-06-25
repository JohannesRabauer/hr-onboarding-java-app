// Extension: i18n-editor
// Compare, review, and edit i18n message properties side by side

import { createServer } from "node:http";
import { readFile, writeFile, readdir } from "node:fs/promises";
import { join } from "node:path";
import { joinSession, createCanvas } from "@github/copilot-sdk/extension";

const servers = new Map();

// --- Data helpers ---

function getResourcesDir(workspacePath) {
    return join(workspacePath, "src", "main", "resources");
}

async function discoverLocales(resourcesDir) {
    const files = await readdir(resourcesDir);
    const locales = [];
    for (const f of files) {
        const m = f.match(/^messages(_([a-z]{2}))?.properties$/);
        if (m) {
            locales.push({ locale: m[2] || "en", file: f });
        }
    }
    locales.sort((a, b) => (a.locale === "en" ? -1 : b.locale === "en" ? 1 : a.locale.localeCompare(b.locale)));
    return locales;
}

function parseProperties(text) {
    const entries = [];
    for (const line of text.split(/\r?\n/)) {
        const trimmed = line.trim();
        if (!trimmed || trimmed.startsWith("#")) continue;
        const idx = trimmed.indexOf("=");
        if (idx === -1) continue;
        entries.push({ key: trimmed.slice(0, idx), value: trimmed.slice(idx + 1) });
    }
    return entries;
}

function serializeProperties(entries, locale) {
    const labels = { en: "English messages (default)", de: "German messages", pt: "Portuguese messages" };
    let out = `# ${labels[locale] || locale + " messages"}\n`;
    let lastGroup = "";
    for (const { key, value } of entries) {
        const group = key.split(".").slice(0, 1).join(".");
        if (group !== lastGroup && lastGroup !== "") out += "\n";
        lastGroup = group;
        out += `${key}=${value}\n`;
    }
    return out;
}

async function loadAllMessages(resourcesDir) {
    const locales = await discoverLocales(resourcesDir);
    const result = {};
    const allKeys = new Set();
    for (const { locale, file } of locales) {
        const text = await readFile(join(resourcesDir, file), "utf-8");
        const entries = parseProperties(text);
        result[locale] = Object.fromEntries(entries.map((e) => [e.key, e.value]));
        entries.forEach((e) => allKeys.add(e.key));
    }
    return { locales: locales.map((l) => l.locale), messages: result, keys: [...allKeys] };
}

// --- SSE support ---

const sseClients = new Map(); // instanceId → Set<res>

function broadcastUpdate(instanceId) {
    const clients = sseClients.get(instanceId);
    if (!clients) return;
    for (const res of clients) {
        res.write(`data: reload\n\n`);
    }
}

// --- HTML renderer ---

function renderHtml(data) {
    const { locales, messages, keys } = data;
    const localeLabels = { en: "English", de: "Deutsch", pt: "Português" };

    const rows = keys.map((key) => {
        const cells = locales.map((loc) => {
            const val = messages[loc]?.[key] || "";
            const missing = !messages[loc]?.[key];
            return `<td class="cell ${missing ? "missing" : ""}" data-locale="${loc}" data-key="${key}">
                <textarea rows="1" data-locale="${loc}" data-key="${key}">${escapeHtml(val)}</textarea>
            </td>`;
        }).join("");
        return `<tr><td class="key-cell">${escapeHtml(key)}</td>${cells}</tr>`;
    }).join("");

    const headerCols = locales.map((l) => `<th>${localeLabels[l] || l}</th>`).join("");

    return `<!doctype html>
<html>
<head>
<meta charset="utf-8" />
<title>i18n Editor</title>
<style>
* { box-sizing: border-box; }
body {
    margin: 0; padding: 12px;
    background: var(--background-color-default, #ffffff);
    color: var(--text-color-default, #1f2328);
    font-family: var(--font-sans, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif);
    font-size: var(--text-body-medium, 14px);
    line-height: var(--leading-body-medium, 20px);
}
h1 {
    font-size: var(--text-title-large, 22px);
    font-weight: var(--font-weight-semibold, 600);
    margin: 0 0 4px 0;
}
.subtitle { color: var(--text-color-muted, #656d76); margin: 0 0 12px 0; }
.toolbar {
    display: flex; gap: 8px; margin-bottom: 12px; align-items: center;
}
.toolbar input {
    flex: 1; padding: 6px 10px; border: 1px solid var(--border-color-default, #d0d7de);
    border-radius: 6px; font-size: 13px;
    background: var(--background-color-default, #fff);
    color: var(--text-color-default, #1f2328);
}
.toolbar button, .save-btn {
    padding: 6px 14px; border: none; border-radius: 6px; cursor: pointer;
    font-size: 13px; font-weight: 500;
    background: var(--true-color-blue, #0969da); color: #fff;
}
.toolbar button:hover, .save-btn:hover { opacity: 0.85; }
.status { font-size: 12px; color: var(--text-color-muted, #656d76); margin-left: auto; }
.status.saved { color: var(--true-color-green, #1a7f37); }
.status.unsaved { color: var(--true-color-red, #cf222e); }
table { width: 100%; border-collapse: collapse; }
th, td { border: 1px solid var(--border-color-default, #d0d7de); padding: 4px 6px; text-align: left; vertical-align: top; }
th { background: var(--background-color-default, #f6f8fa); font-size: 12px; font-weight: 600; position: sticky; top: 0; z-index: 1; }
.key-cell { font-family: var(--font-mono, monospace); font-size: 12px; white-space: nowrap; background: var(--background-color-default, #f6f8fa); min-width: 160px; }
.cell { padding: 2px; }
.cell.missing { background: var(--true-color-red-muted, #ffebe9); }
textarea {
    width: 100%; border: 1px solid transparent; padding: 4px; font-size: 13px;
    font-family: var(--font-sans, -apple-system, sans-serif);
    resize: vertical; min-height: 28px; border-radius: 4px;
    background: transparent; color: var(--text-color-default, #1f2328);
}
textarea:focus { border-color: var(--true-color-blue, #0969da); outline: none; background: var(--background-color-default, #fff); }
.filter-info { font-size: 12px; color: var(--text-color-muted, #656d76); margin-bottom: 8px; }
.badge {
    display: inline-block; padding: 2px 6px; border-radius: 10px; font-size: 11px;
    background: var(--true-color-red-muted, #ffebe9); color: var(--true-color-red, #cf222e);
    margin-left: 8px;
}
</style>
</head>
<body>
<h1>i18n Editor</h1>
<p class="subtitle">Compare and edit translations across locales</p>
<div class="toolbar">
    <input type="text" id="filter" placeholder="Filter keys (e.g. auth.login)..." />
    <label><input type="checkbox" id="showMissing" /> Missing only</label>
    <span id="missingBadge" class="badge" style="display:none"></span>
    <button class="save-btn" onclick="save()">💾 Save All</button>
    <span id="status" class="status"></span>
</div>
<div style="overflow: auto; max-height: calc(100vh - 140px);">
<table>
<thead><tr><th>Key</th>${headerCols}</tr></thead>
<tbody id="tbody">${rows}</tbody>
</table>
</div>
<script>
const locales = ${JSON.stringify(locales)};
let dirty = false;

// Filter
const filterInput = document.getElementById("filter");
const showMissing = document.getElementById("showMissing");
const tbody = document.getElementById("tbody");

function applyFilter() {
    const q = filterInput.value.toLowerCase();
    const missingOnly = showMissing.checked;
    const rows = tbody.querySelectorAll("tr");
    let missingCount = 0;
    rows.forEach(row => {
        const key = row.querySelector(".key-cell").textContent.toLowerCase();
        const hasMissing = row.querySelector(".missing") !== null;
        if (hasMissing) missingCount++;
        const matchFilter = !q || key.includes(q);
        const matchMissing = !missingOnly || hasMissing;
        row.style.display = (matchFilter && matchMissing) ? "" : "none";
    });
    const badge = document.getElementById("missingBadge");
    if (missingCount > 0) { badge.style.display = ""; badge.textContent = missingCount + " missing"; }
    else { badge.style.display = "none"; }
}
filterInput.addEventListener("input", applyFilter);
showMissing.addEventListener("change", applyFilter);
applyFilter();

// Track changes
document.querySelectorAll("textarea").forEach(ta => {
    ta.addEventListener("input", () => {
        dirty = true;
        document.getElementById("status").textContent = "Unsaved changes";
        document.getElementById("status").className = "status unsaved";
        // Update missing class
        const cell = ta.closest("td");
        if (ta.value.trim()) cell.classList.remove("missing");
        else cell.classList.add("missing");
    });
    // Auto-resize
    ta.addEventListener("input", function() {
        this.style.height = "auto";
        this.style.height = this.scrollHeight + "px";
    });
});

// Save
async function save() {
    const data = {};
    locales.forEach(l => { data[l] = {}; });
    document.querySelectorAll("textarea").forEach(ta => {
        const loc = ta.dataset.locale;
        const key = ta.dataset.key;
        if (ta.value.trim()) data[loc][key] = ta.value;
    });
    const res = await fetch("/save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (res.ok) {
        dirty = false;
        document.getElementById("status").textContent = "✓ Saved";
        document.getElementById("status").className = "status saved";
        setTimeout(() => { document.getElementById("status").textContent = ""; }, 3000);
    } else {
        document.getElementById("status").textContent = "Save failed!";
    }
}

// SSE for live reload
const evtSource = new EventSource("/events");
evtSource.onmessage = (e) => { if (e.data === "reload" && !dirty) location.reload(); };
</script>
</body>
</html>`;
}

function escapeHtml(s) {
    return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

// --- Server ---

async function startServer(instanceId, workspacePath) {
    const resourcesDir = getResourcesDir(workspacePath);
    let data = await loadAllMessages(resourcesDir);

    const server = createServer(async (req, res) => {
        if (req.url === "/events") {
            res.writeHead(200, {
                "Content-Type": "text/event-stream",
                "Cache-Control": "no-cache",
                "Connection": "keep-alive",
            });
            res.write("data: connected\n\n");
            if (!sseClients.has(instanceId)) sseClients.set(instanceId, new Set());
            sseClients.get(instanceId).add(res);
            req.on("close", () => { sseClients.get(instanceId)?.delete(res); });
            return;
        }

        if (req.method === "POST" && req.url === "/save") {
            let body = "";
            for await (const chunk of req) body += chunk;
            const updates = JSON.parse(body);
            const locales = await discoverLocales(resourcesDir);
            for (const { locale, file } of locales) {
                if (!updates[locale]) continue;
                const entries = data.keys
                    .filter((k) => updates[locale][k] !== undefined)
                    .map((k) => ({ key: k, value: updates[locale][k] }));
                const content = serializeProperties(entries, locale);
                await writeFile(join(resourcesDir, file), content, "utf-8");
            }
            data = await loadAllMessages(resourcesDir);
            res.writeHead(200, { "Content-Type": "application/json" });
            res.end(JSON.stringify({ ok: true }));
            return;
        }

        if (req.url === "/data") {
            data = await loadAllMessages(resourcesDir);
            res.writeHead(200, { "Content-Type": "application/json" });
            res.end(JSON.stringify(data));
            return;
        }

        // Default: serve HTML
        data = await loadAllMessages(resourcesDir);
        res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
        res.end(renderHtml(data));
    });

    await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
    const address = server.address();
    const port = typeof address === "object" && address ? address.port : 0;
    return { server, url: `http://127.0.0.1:${port}/` };
}

// --- Resolve actual repo root ---
// workspacePath may point to session state; detect the repo by finding
// the .github/extensions dir relative to the extension file itself.
import { fileURLToPath } from "node:url";
import { dirname } from "node:path";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
// extension lives at <repo>/.github/extensions/i18n-editor/extension.mjs
const repoRoot = join(__dirname, "..", "..", "..");

// --- Canvas ---

const session = await joinSession({
    canvases: [
        createCanvas({
            id: "i18n-editor",
            displayName: "i18n Editor",
            description: "Compare, review, and edit i18n translation properties side by side across locales.",
            actions: [
                {
                    name: "reload",
                    description: "Reload translations from disk and refresh the canvas",
                    handler: async (ctx) => {
                        broadcastUpdate(ctx.instanceId);
                        return { ok: true };
                    },
                },
                {
                    name: "get_translations",
                    description: "Get current translations data as JSON",
                    handler: async (ctx) => {
                        const resourcesDir = getResourcesDir(repoRoot);
                        return await loadAllMessages(resourcesDir);
                    },
                },
                {
                    name: "update_translation",
                    description: "Update a specific translation key for a locale",
                    inputSchema: {
                        type: "object",
                        properties: {
                            locale: { type: "string", description: "Locale code (en, de, pt)" },
                            key: { type: "string", description: "Message key (e.g. auth.login.title)" },
                            value: { type: "string", description: "New translation value" },
                        },
                        required: ["locale", "key", "value"],
                    },
                    handler: async (ctx) => {
                        const { locale, key, value } = ctx.input;
                        const resourcesDir = getResourcesDir(repoRoot);
                        const locales = await discoverLocales(resourcesDir);
                        const localeInfo = locales.find((l) => l.locale === locale);
                        if (!localeInfo) return { error: `Unknown locale: ${locale}` };

                        const filePath = join(resourcesDir, localeInfo.file);
                        const text = await readFile(filePath, "utf-8");
                        const entries = parseProperties(text);
                        const existing = entries.find((e) => e.key === key);
                        if (existing) existing.value = value;
                        else entries.push({ key, value });

                        await writeFile(filePath, serializeProperties(entries, locale), "utf-8");
                        broadcastUpdate(ctx.instanceId);
                        return { ok: true, locale, key, value };
                    },
                },
            ],
            open: async (ctx) => {
                let entry = servers.get(ctx.instanceId);
                if (!entry) {
                    entry = await startServer(ctx.instanceId, repoRoot);
                    servers.set(ctx.instanceId, entry);
                }
                return { title: "i18n Editor", url: entry.url };
            },
            onClose: async (ctx) => {
                const entry = servers.get(ctx.instanceId);
                if (entry) {
                    servers.delete(ctx.instanceId);
                    sseClients.delete(ctx.instanceId);
                    await new Promise((resolve) => entry.server.close(() => resolve()));
                }
            },
        }),
    ],
});
