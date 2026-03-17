#!/bin/bash
# Usage: ./bump-version.sh [major|minor|patch]
# Bumps version in build.gradle.kts, commits, and tags

FILE="app/build.gradle.kts"
CURRENT=$(grep 'versionName' "$FILE" | head -1 | sed 's/.*"\(.*\)".*/\1/')
CODE=$(grep 'versionCode' "$FILE" | head -1 | sed 's/[^0-9]//g')

IFS='.' read -r major minor patch <<< "$CURRENT"

case "${1:-patch}" in
    major) major=$((major + 1)); minor=0; patch=0 ;;
    minor) minor=$((minor + 1)); patch=0 ;;
    patch) patch=$((patch + 1)) ;;
esac

NEW="$major.$minor.$patch"
NEW_CODE=$((CODE + 1))

sed -i "s/versionCode = $CODE/versionCode = $NEW_CODE/" "$FILE"
sed -i "s/versionName = \"$CURRENT\"/versionName = \"$NEW\"/" "$FILE"

echo "Version: $CURRENT → $NEW (code $CODE → $NEW_CODE)"
