def url_repo = "https://github.com/gabo-x86/keycloak-repository.git"
pipeline{
    agent any
    parameters{
        string defaultValue: 'main', description: 'Active branch', name: 'BRANCH', trim: false
        choice (name: 'SCAN_GRYPE', choices: ['NO','YES'], description: 'Grype scanner')
        choice (name: 'SCAN_SONARQUBE', choices: ['NO','YES'], description: 'SonarQube scanner')
    }
    environment{
       DOCKERHUB_CREDENTIALS = credentials('docker_hub_key')
       DOCKER_IMAGE = 'gabox86/keycloak-image'
       DOCKER_TAG = 'latest'
    }
    stages{
        stage("Create Build Name"){
            steps{           
                script{
                   currentBuild.displayName= "keycloak-service"+ currentBuild.number
                }
            }
        }
        stage("Limpiar"){
            steps{
                cleanWs()
            }
        }
        stage("Download Project"){
            steps{
                git credentialsId: 'git_credentials', branch: "${BRANCH}", url: "${url_repo}"
                echo "Project downloaded"
            }
        }
        stage('Build Project')
        {
            when {equals expected: 'YES', actual: SCAN_GRYPE} 
            steps{
                sh 'tar -czvf keycloak.tar.gz ./*'
                archiveArtifacts artifacts: 'keycloak.tar.gz', onlyIfSuccessful: true
                sh 'cp keycloak.tar.gz /tmp/'
            }
        }
        stage("Test vulnerability")
        {
           when {equals expected: 'YES', actual: SCAN_GRYPE} 
            steps{
               sh "curl -sSfL https://raw.githubusercontent.com/anchore/grype/main/install.sh | sh -s -- -b"
               sh "ls"
               sh "/grype /tmp/keycloak.tar.gz > vulnerability-scan.txt"
               archiveArtifacts artifacts: 'vulnerability-scan.txt', onlyIfSuccessful: true
            }
        }

        stage('Image push artifactory') {
            steps {
                script {

                    sh 'curl -O https://download.docker.com/linux/static/stable/x86_64/docker-26.1.0.tgz'
                    sh 'tar -xvf docker-26.1.0.tgz'
                    sh './docker/docker build -t ${DOCKER_IMAGE}:latest .'
                    echo 'Build finished'
                }
            }
        }
        
    }
}



