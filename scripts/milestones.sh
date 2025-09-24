#!/bin/bash

REPO="kthurman59/order-management-system"

MILESTONES=("Week 1 - Foundations" "Week 2 - Foundations" "Week 3-6 - Productionization" "Week 7-9 - Senior Signals" "Week 10-12 - Job Packaging")

for title in "${MILESTONES[@]}"; do
  gh api -X POST repos/$REPO/milestones \
    -F title="$title"
done


