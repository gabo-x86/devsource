def url_repo = "https://github.com/gabo-x86/keycloak-repository.git"
pipeline{
   agent
   {
      label 'jenkins_slave'
   }
    tools{
        maven 'maven-396'
        jdk 'jdk21'
    }
    parameters{
         string defaultValue: 'main', description: 'Active branch', name: 'BRANCH', trim: false
         choice (name: 'SCAN_GRYPE', choices: ['YES','NO'], description: 'Grype scanner')
    }
    environment{
       VAR='NUEVO'
    }
    stages{
        stage("create build name"){
            steps{           
                script{
                   currentBuild.displayName= "service_back-"+ currentBuild.number
                }
            }
        }
        stage("Limpiar"){
            steps{
                cleanWs()
            }
        }
        stage("download proyect"){
            steps{
                git credentialsId: 'git_credentials', branch: "${BRANCH}", url: "${url_repo}"
                echo "proyecto descargado"
            }
        }
        stage('build proyect')
        {
            steps{
                sh "pwd"
                sh "ls -la"
                // sh "mvn clean compile package -Dmaven.test.skip=true -U"
                // sh "mv am-core-web-service/target/*.jar am-core-web-service/target/app.jar"
                // stash includes: 'am-core-web-service/target/app.jar', name: 'backartifact'
                // archiveArtifacts artifacts: 'am-core-web-service/target/app.jar', onlyIfSuccessful: true
                // sh "cp am-core-web-service/target/app.jar /tmp/"
            }
        }
        // stage("Test vulnerability")
        // {
        //    when {equals expected: 'YES', actual: SCAN_GRYPE} 
        //     steps{
        //        sh "/grype /tmp/app.jar > informe-scan.txt"
        //        archiveArtifacts artifacts: 'informe-scan.txt', onlyIfSuccessful: true
        //     }
        // }
        // stage('sonarqube analysis'){
        //       when {equals expected: 'YES', actual: SCAN_GRYPE}
        //     steps{
        //        script{
        //            sh "pwd"
		// 				writeFile encoding: 'UTF-8', file: 'sonar-project.properties', text: """sonar.projectKey=academy
		// 				sonar.projectName=academy
		// 				sonar.projectVersion=academy
		// 				sonar.sourceEncoding=UTF-8
		// 				sonar.sources=am-core-web-service/src/main/
		// 				sonar.java.binaries=am-core-web-service/target/
		// 				sonar.java.libraries=am-core-web-service/target/classes
		// 				sonar.language=java
		// 				sonar.scm.provider=git
		// 				"""
        //                 // Sonar Disabled due to we don't have a sonar in tools account yet
		// 				withSonarQubeEnv('Sonar_CI') {
		// 				     def scannerHome = tool 'Sonar_CI'
		// 				     sh "${tool("Sonar_CI")}/bin/sonar-scanner -X"
		// 				}
               
                   
        //        }
        
        //     }
        
        // }
        // stage('Image push artifactory')
        // {
        //     agent {label 'node_deploy'}
        //     steps{
        //         script{
        //             unstash 'backartifact'
        //             sh "rm /data/publish/app.jar | true"
        //             sh "cp am-core-web-service/target/app.jar /data/publish/"
        //             sh "docker rmi 192.168.137.10:8082/v2/repository/docker/back-prueba:latest | true; cd /data/publish/ ; docker build -t 192.168.137.10:8082/v2/repository/docker/back-prueba:latest ."
        //             sh "docker push 192.168.137.10:8082/v2/repository/docker/back-prueba:latest "
        //         }
        //     }
        // }
        
    }
}