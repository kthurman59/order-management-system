#!/usr/bin/env bash
set -euo pipefail

# --- Project Info ---
OWNER="kthurman59"
REPO="order-management-system"

# --- Helper Function ---
create_issue() {
  gh issue create \
    --repo "$OWNER/$REPO" \
    --title "$1" \
    --body "$2" \
    --label "$3"
}

echo "📋 Creating Day 2 GitHub issues for Order Management System..."

# --- Issues ---

create_issue \
  "Add Customer entity and JPA repository" \
  "Create \`Customer\` entity in \`com.example.oms.entity\` and \`CustomerRepository\` in \`com.example.oms.repository\`.  
  Verify H2 auto-generates the table and application starts cleanly." \
  "backend,entity"

create_issue \
  "Implement CustomerController with CRUD endpoints" \
  "Add REST controller at \`/api/customers\` to list, create, get, and delete customers.  
  Use the injected \`CustomerRepository\`.  
  Confirm endpoints return valid JSON using IntelliJ HTTP Client." \
  "api,controller"

create_issue \
  "Test /api/customers endpoints manually" \
  "Use IntelliJ HTTP Client or curl to POST and GET sample customers.  
  Verify H2 reflects the inserts." \
  "testing"

create_issue \
  "Optional: Add CustomerControllerTest integration test" \
  "Write simple \`@SpringBootTest\` to ensure repository and controller load correctly.  
  Use assertThat(repository.findAll()).isNotEmpty()." \
  "testing"

create_issue \
  "Update README with run instructions for IntelliJ" \
  "Add clear steps for building and running OMS in IntelliJ,  
  including H2 console access and API testing." \
  "docs"

echo "✅ All issues created successfully!"

