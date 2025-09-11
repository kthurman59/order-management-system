```mermaid
flowchart TD
    A[Implement OrderService CRUD API] --> B[Integration tests with Testcontainers]
    B --> C[Refactor layered architecture]
    A --> D[Set up GitHub Actions CI/CD pipeline]
    D --> E[Fix Docker build caching issue]
    A --> F[Draft OPERATIONS.md]
