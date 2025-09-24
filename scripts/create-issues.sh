#!/usr/bin/env bash
set -euo pipefail

# --- CONFIGURE THESE ---
OWNER="kthurman59"
REPO="order-management-system"
PROJECT_NUMBER=7            # the '7' from the URL you gave
COLUMN_NAME="To Do"        # column where issues should land (case-sensitive)
# -----------------------

# Prereqs
if ! command -v gh >/dev/null 2>&1; then
  echo "gh (GitHub CLI) not found. Install it and run 'gh auth login' first."; exit 1
fi
if ! gh auth status >/dev/null 2>&1; then
  echo "You are not authenticated with gh. Run: gh auth login"; exit 1
fi
if ! command -v jq >/dev/null 2>&1; then
  echo "jq not found. Install it (e.g. 'sudo apt install jq' or 'brew install jq')"; exit 1
fi

echo "Using repo: $OWNER/$REPO"
echo "Looking up project number $PROJECT_NUMBER for user $OWNER..."

# get the project id for the user project with number == PROJECT_NUMBER
PROJECT_ID=$(gh api /users/"$OWNER"/projects -H "Accept: application/vnd.github.inertia-preview+json" \
  | jq -r ".[] | select(.number==$PROJECT_NUMBER) | .id")

if [ -z "$PROJECT_ID" ] || [ "$PROJECT_ID" = "null" ]; then
  echo "ERROR: Could not find project number $PROJECT_NUMBER for user $OWNER."
  echo "List available projects with: gh api /users/$OWNER/projects -H 'Accept: application/vnd.github.inertia-preview+json' | jq '.'"
  exit 1
fi
echo "Found project id: $PROJECT_ID"

# get columns for this project
COLUMNS_JSON=$(gh api /projects/"$PROJECT_ID"/columns -H "Accept: application/vnd.github.inertia-preview+json")
echo "Project columns:"
echo "$COLUMNS_JSON" | jq -r '.[] | " - id: \(.id)  name: \(.name)"'

COLUMN_ID=$(echo "$COLUMNS_JSON" | jq -r '.[] | select(.name=="'"$COLUMN_NAME"'") | .id')

if [ -z "$COLUMN_ID" ] || [ "$COLUMN_ID" = "null" ]; then
  echo "ERROR: Column named '$COLUMN_NAME' not found. Available columns:"
  echo "$COLUMNS_JSON" | jq -r '.[] | .name'
  exit 1
fi
echo "Using column id: $COLUMN_ID (name: $COLUMN_NAME)"

# Issues data (title, body, labels)
TITLES=(
  "Set up Docker Compose for local development"
  "Bootstrap Order Service with Spring Boot"
  "Integrate Order Service with PostgreSQL"
  "Enable Kafka producer/consumer in Order Service"
  "Add CI/CD pipeline with GitHub Actions"
  "Add developer onboarding docs (README, OPERATIONS.md)"
)

BODIES=(
  "Add PostgreSQL and Kafka with docker-compose, update README with run instructions.\n\nAcceptance Criteria:\n- docker-compose up starts Postgres + Kafka + app\n- README doc added"
  "Initialize a Spring Boot project with REST endpoints, config files, and Actuator enabled.\n\nAcceptance Criteria:\n- skeleton project in services/order-service\n- mvn spring-boot:run starts the app"
  "Add JPA entities, repositories, and connect to Postgres. Provide schema migrations via Flyway.\n\nAcceptance Criteria:\n- Flyway migration added\n- Local DB connection works"
  "Configure KafkaTemplate and @KafkaListener. Add sample producer/consumer endpoints.\n\nAcceptance Criteria:\n- OrderPlaced events published and consumed"
  "Build JAR, run tests, build Docker image, push to GHCR. Use GitOps for deployment.\n\nAcceptance Criteria:\n- CI workflow on PRs and main\n- Docker image pushed on successful build"
  "Write README with setup steps, system overview, and usage instructions. Add OPERATIONS.md for runbooks.\n\nAcceptance Criteria:\n- README has setup instructions\n- OPERATIONS.md file present"
)

LABELS=(
  "infra"
  "feature"
  "feature"
  "feature"
  "infra"
  "docs"
)

# loop and create issues, then add to project column
for idx in "${!TITLES[@]}"; do
  title="${TITLES[$idx]}"
  body="${BODIES[$idx]}"
  labels="${LABELS[$idx]}"

  echo "Creating issue: '$title' ..."
  # create issue and capture number
  ISSUE_NUMBER=$(gh issue create --repo "$OWNER/$REPO" --title "$title" --body $'\n'"$body" --label "$labels" --json number -q .number)
  echo " -> Created issue #$ISSUE_NUMBER"

  # fetch the internal DB id for the issue (required by Projects API)
  ISSUE_DB_ID=$(gh api repos/"$OWNER"/"$REPO"/issues/"$ISSUE_NUMBER" -q .id)
  echo " -> Issue DB id: $ISSUE_DB_ID"

  # add a project card to the target column referencing this issue
  gh api -X POST /projects/columns/"$COLUMN_ID"/cards -H "Accept: application/vnd.github.inertia-preview+json" \
    -f "content_id=$ISSUE_DB_ID" -f "content_type=Issue" >/dev/null

  echo " -> Added issue #$ISSUE_NUMBER to project column '$COLUMN_NAME'."
done

echo "All done."

