pipeline {
    agent any

    environment {
        GIT_REPO = 'https://github.com/bhavy0949/Red-Bus.git'
        PATH = "/opt/homebrew/bin:/usr/local/bin:${env.PATH}"
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        POSTGRES_PASSWORD      = credentials('postgres-password')
        JWT_SECRET             = credentials('jwt-secret')
        ADMIN_PASSWORD         = credentials('admin-password')
        ELASTIC_PASSWORD       = credentials('elastic-password')
        KIBANA_PASSWORD        = credentials('kibana-password')
        LOGSTASH_PASSWORD      = credentials('logstash-password')
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo '========== Cloning Repository =========='
                git branch: 'main', url: "${GIT_REPO}"
            }
        }

        stage('Build & Push Services') {
            parallel {
                stage('Frontend')           { steps { build job: 'frontend',           wait: true } }
                stage('API Gateway')        { steps { build job: 'api-gateway',        wait: true } }
                stage('Auth Service')       { steps { build job: 'auth-service',       wait: true } }
                stage('Member Service')     { steps { build job: 'member-service',     wait: true } }
                stage('Security Service')   { steps { build job: 'security-service',   wait: true } }
                stage('Expedition Service') { steps { build job: 'expedition-service', wait: true } }
                stage('Payment Service')    { steps { build job: 'payment-service',    wait: true } }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo '========== Deploying to Kubernetes =========='
                script {
                    def gitTag = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    echo "Updating manifests to use version: ${gitTag}"

                    sh "sed -i '' 's/:latest/:${gitTag}/g' k8s/*.yaml"
                    sh """
                        kubectl apply -f k8s/namespace.yaml --validate=false
                        kubectl apply -f k8s/configmap.yaml --validate=false

                        # Docker Hub pull secret
                        kubectl create secret docker-registry docker-credentials -n redbus \\
                            --docker-server=https://index.docker.io/v1/ \\
                            --docker-username=\$DOCKER_HUB_CREDENTIALS_USR \\
                            --docker-password=\$DOCKER_HUB_CREDENTIALS_PSW \\
                            --docker-email=redbus@example.com \\
                            --dry-run=client -o yaml | kubectl apply -f -

                        # App secrets — injected from Jenkins Credentials, never stored in git
                        kubectl create secret generic redbus-secrets -n redbus \\
                            --from-literal=POSTGRES_PASSWORD=\$POSTGRES_PASSWORD \\
                            --from-literal=DB_PASSWORD=\$POSTGRES_PASSWORD \\
                            --from-literal=JWT_SECRET=\$JWT_SECRET \\
                            --from-literal=ADMIN_PASSWORD=\$ADMIN_PASSWORD \\
                            --from-literal=ADMIN_EMAIL=redbus@example.com \\
                            --from-literal=DOCKER_HUB_USERNAME=\$DOCKER_HUB_CREDENTIALS_USR \\
                            --from-literal=DOCKER_HUB_PASSWORD=\$DOCKER_HUB_CREDENTIALS_PSW \\
                            --dry-run=client -o yaml | kubectl apply -f -

                        # ELK secrets
                        kubectl create secret generic elk-credentials -n redbus \\
                            --from-literal=ELASTIC_PASSWORD=\$ELASTIC_PASSWORD \\
                            --from-literal=KIBANA_PASSWORD=\$KIBANA_PASSWORD \\
                            --from-literal=LOGSTASH_PASSWORD=\$LOGSTASH_PASSWORD \\
                            --dry-run=client -o yaml | kubectl apply -f -

                        kubectl apply -f k8s/vault.yaml --validate=false
                        kubectl apply -f k8s/postgres.yaml --validate=false
                        kubectl apply -f k8s/pgadmin.yaml --validate=false
                        kubectl apply -f k8s/auth-service.yaml --validate=false
                        kubectl apply -f k8s/api-gateway.yaml --validate=false
                        kubectl apply -f k8s/member-service.yaml --validate=false
                        kubectl apply -f k8s/security-service.yaml --validate=false
                        kubectl apply -f k8s/expedition-service.yaml --validate=false
                        kubectl apply -f k8s/payment-service.yaml --validate=false
                        kubectl apply -f k8s/frontend.yaml --validate=false
                        kubectl apply -f k8s/ingress.yaml --validate=false
                        kubectl apply -f k8s/elk-stack.yaml --validate=false
                        kubectl apply -f k8s/hpa.yaml --validate=false
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                echo '========== Verifying Deployment =========='
                sh '''
                    sleep 30
                    kubectl get pods -n redbus
                    kubectl get svc -n redbus
                '''
            }
        }
    }

    post {
        always {
            echo '========== Pipeline Execution Completed =========='
            cleanWs()
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
