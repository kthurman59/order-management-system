# order-management-system
flowchart LR
    Dev[Developer\n(Code + PRs)]
    GitHub[GitHub Repo\n(Code + Issues + Projects)]
    CI[GitHub Actions\nBuild + Test + Sonar + Docker]
    Registry[(Container Registry\nGHCR / ECR / GCR)]
    InfraRepo[Infra Repo\nHelm / Kustomize Manifests]
    FluxCD[FluxCD\nGitOps Operator]
    Cluster[Kubernetes Cluster\nCloud: EKS / GKE / AKS]
    Obs[Observability Stack\nPrometheus + Grafana + Tracing]

    Dev -->|push PR / commit| GitHub
    GitHub -->|trigger pipeline| CI
    CI -->|push Docker image| Registry
    CI -->|update manifests\n(new image tag)| InfraRepo
    InfraRepo -->|watched by| FluxCD
    FluxCD -->|sync + apply manifests| Cluster
    Cluster -->|export metrics/logs/traces| Obs
