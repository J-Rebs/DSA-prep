# Custom Agent Rules for DSA-prep

## Verification of UI and Web Documentation Updates
- Before pushing any changes to HTML, CSS, or documentation sites, always verify the rendered page in a browser subagent or run a syntax/parse validation locally.
- Never commit or push HTML/JavaScript edits without confirming that all templates, scripts, and layout blocks are syntactically valid, properly closed/nested, and visually reasonable.
- Whenever completing a problem or feature, immediately update all corresponding problem status fields (`🟢 Solved`), pattern progress badges (`🔄 In Progress` / `🟢 Mastered`), and problem counts in `index.html` to keep the dashboard 100% in sync with the codebase.
