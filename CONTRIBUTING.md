# FormSense Team Git Workflow

## Branches

- **main** — stable, demo-ready code only
- **dev** — where completed team features are combined
- **feature/<short-name>** — one branch per task or feature

Examples:

- `feature/home-screen`
- `feature/camera-preview`
- `feature/squat-rep-counting`
- `feature/session-history`

## Starting Work

Always update `dev` before starting a feature:

```bash
git checkout dev
git pull
git checkout -b feature/your-feature
```

## Saving Work

```bash
git add .
git commit -m "Describe what you changed"
git push -u origin feature/your-feature
```

Then open a pull request from your feature branch into `dev`.

## Team Rules

1. Do not push unfinished work directly to `main`.
2. Create a separate branch for each major feature.
3. Pull the latest `dev` before beginning new work.
4. Keep commits small enough that teammates can understand them.
5. Test your feature before requesting a merge.
6. Use pull requests so another teammate can quickly review major changes.
7. Never commit passwords, API keys, signing keys, or `local.properties`.
8. Merge `dev` into `main` only for stable milestones or demos.

## Android Studio

Android Studio can handle most Git operations from the **Git** menu, but the same workflow can also be done from the built-in terminal.
