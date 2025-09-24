#!/usr/bin/env bash
set -euo pipefail

OWNER="kthurman59"
REPO="order-management-system"

# make sure gh is available and authenticated
if ! command -v gh >/dev/null 2>&1; then
  echo "gh CLI not found. Install it first (https://cli.github.com/)."
  exit 1
fi
if ! gh auth status >/dev/null 2>&1; then
  echo "Not authenticated. Run: gh auth login"
  exit 1
fi

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

echo "Creating issues in $OWNER/$REPO ..."

for i in "${!TITLES[@]}"; do
  title="${TITLES[$i]}"
  body="${BODIES[$i]}"
  labels="${LABELS[$i]}"

  echo
  echo "Creating: $title"
  # create issue and capture the returned URL (works even on older gh versions)
  ISSUE_URL=$(gh issue create --repo "$OWNER/$REPO" --title "$title" --body "$body" --label "$labels")
  # extract number
  ISSUE_NUMBER="${ISSUE_URL##*/}"
  echo " -> Created $ISSUE_URL"
  echo " -> Quick open in browser: gh issue view $ISSUE_NUMBER --repo $OWNER/$REPO --web"
done

echo
echo "Done. To add each issue to your Project board, run the 'gh issue view ... --web' links above and use the 'Projects' sidebar in the issue page to add it to your project/column."

