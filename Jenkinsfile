pipeline {
    environment {
        JAVA_TOOL_OPTIONS = '-Duser.home=/var/jenkins_home'
    }
    agent {
        docker {
            image 'maven:3.6.1-jdk-8-alpine'
            args '-e MAVEN_CONFIG=/var/jenkins_home/.m2'
        }
    }
    stages {
        stage('Preparation') {
            steps {
                git url: 'https://github.com/rlsutton1/jpacontainer.git', credentialsId: 'bitbucket'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B -Dmaven.test.skip=true -DskipTests -f jpacontainer-addon/pom.xml clean package'
            }
        }
        stage('Deliver') {
            steps {
                sh 'mvn jar:jar install:install help:evaluate -Dexpression=project.name -f jpacontainer-addon/pom.xml'
            }
        }
    }
    post {
        always {
            emailext attachLog: true,
            to: '$DEFAULT_RECIPIENTS',
            subject: '$DEFAULT_SUBJECT',
            body: '$DEFAULT_CONTENT'
        }
    }
}
