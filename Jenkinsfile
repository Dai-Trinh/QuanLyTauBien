#!/usr/bin/env groovy

def buildNumber = env.BUILD_NUMBER as int
if (buildNumber > 1) milestone(buildNumber - 1)
milestone(buildNumber)

node {
    stage('checkout') {
        checkout scm
    }
    stage('check java') {
        bat "java -version"
    }

    stage('clean') {
        bat "./mvnw -ntp clean -P-webapp"
    }

     //stage('packaging') {
    //     bat "./mvnw -ntp verify -P-webapp -Pdev -DskipTests -Dcheckstyle.skip"
    //     archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
    // }
    stage('run') {
        bat "./mvnw -Dmaven.test.skip"
    }
}
