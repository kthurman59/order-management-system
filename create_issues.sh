#!/usr/bin/env bash
set -euo pipefail

# Requirements: gh and jq
command -v gh >/dev/null 2>&1 || { echo "Install GitHub CLI gh"; exit 1; }
command -v jq >/dev/null 2>&1 || { echo "Install jq"; exit 1; }

: "${REPO:?Set REPO to owner/repo, e.g. export REPO=kthurman59/order-management-system}"

JSON="${1:-oms_issues.json}"
[ -f "$JSON" ] || { echo "JSON file not found: $JSON"; exit 1; }

echo "Using repo: $REPO"
echo "Using file: $JSON"

echo "Loading existing labels"
existing_labels_json="$(gh label list -R "$REPO" --limit 1000 --json name || echo '[]')"

echo "Creating labels"
jq -r '.labels[]' "$JSON" | while read -r label; do
  if echo "$existing_labels_json" | jq -e --arg n "$label" '.[] | select(.name == $n)' >/dev/null; then
    echo "Label exists $label"
  else
    if gh label create "$label" -R "$REPO" >/dev/null 2>&1; then
      echo "Created label $label"
      existing_labels_json="$(gh label list -R "$REPO" --limit 1000 --json name || echo '[]')"
    else
      echo "Label create failed or exists $label"
    fi
  fi
done

echo "Loading existing milestones"
existing_milestones_json="$(
  gh api "repos/$REPO/milestones" --paginate 2>/dev/null | jq -s 'add // []'
)"

echo "Creating milestones"
jq -c '.milestones[]' "$JSON" | while read -r ms; do
  title=$(echo "$ms" | jq -r '.title')
  desc=$(echo "$ms" | jq -r '.description // ""')

  if echo "$existing_milestones_json" | jq -e --arg t "$title" '.[] | select(.title == $t)' >/dev/null; then
    echo "Milestone exists $title"
  else
    gh api -X POST "repos/$REPO/milestones" -f title="$title" -f description="$desc" -f state=open >/dev/null
    echo "Created milestone $title"
    existing_milestones_json="$(
      gh api "repos/$REPO/milestones" --paginate 2>/dev/null | jq -s 'add // []'
    )"
  fi
done

echo "Loading existing issues"
existing_issues_json="$(gh issue list -R "$REPO" --state all --limit 1000 --json title || echo '[]')"

echo "Creating issues"
jq -c '.issues[]' "$JSON" | while read -r issue; do
  title=$(echo "$issue" | jq -r '.title')
  body=$(echo "$issue" | jq -r '.body // ""')
  milestone=$(echo "$issue" | jq -r '.milestone // empty')

  mapfile -t label_list < <(echo "$issue" | jq -r '.labels[]?')
  labels_args=()
  if [ "${#label_list[@]}" -gt 0 ]; then
    for l in "${label_list[@]}"; do
      labels_args+=(--label "$l")
    done
  fi

  if echo "$existing_issues_json" | jq -e --arg t "$title" '.[] | select(.title == $t)' >/dev/null; then
    echo "Issue exists $title"
    continue
  fi

  if [ -n "${milestone:-}" ]; then
    gh issue create -R "$REPO" --title "$title" --body "$body" "${labels_args[@]}" --milestone "$milestone" >/dev/null
  else
    gh issue create -R "$REPO" --title "$title" --body "$body" "${labels_args[@]}" >/dev/null
  fi
  echo "Created issue $title"
done

echo "Done"

