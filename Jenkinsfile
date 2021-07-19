pipeline {
    agent any

    environment {
        workingDir = "."
        JAVA_HOME = "/opt/graalvm/graalvm-ce-java11-20.2.0"
        GRAALVM_HOME = "/opt/graalvm/graalvm-ce-java11-20.2.0"
        DOCKER_REGISTRY = "srv-docker05.ntx.cisbox.com:5000"
    }

    parameters {
        booleanParam(name: 'BUILD_NATIVE_CONTAINER', defaultValue: false, description: '<h5>Build a <b>native</b> Container </h5>')
    }

    stages {
        stage('configure application') {
            steps {
                dir("$workingDir") {
                    echo "SECRET_ID: ${env.SECRET_ID}"
                    echo "BUILD_NATIVE_CONTAINER: ${env.BUILD_NATIVE_CONTAINER}"
                }
            }
        }

        stage('build-native') {
            when { expression { return params.BUILD_NATIVE_CONTAINER } }

            steps {
                dir("$workingDir") {
                    sh "pwd"
                    sh "ls -al ."
                    sh "chmod 777 ./mvnw"
                    sh "./mvnw clean package -Pnative " +
                        " -Dquarkus.jib.environment-variables.version=$BUILD_TAG" +
                        " -Dquarkus.native.container-build=true" +
                        " -Dquarkus.container-image.push=true" +
                        " -Dquarkus.container-image.registry=$DOCKER_REGISTRY" +
                        " -Dquarkus.container-image.insecure=true"
                }
            }
        }

        stage('build') {
            when { not { expression { return params.BUILD_NATIVE_CONTAINER } } }

            steps {
            	sh "pwd"
                sh "ls -al ."
                sh "chmod 777 ./mvnw"
                dir("$workingDir") {
                    sh "./mvnw clean package" +
                        " -Dquarkus.jib.environment-variables.version=$BUILD_TAG" +
                        " -Dquarkus.container-image.push=true" +
                        " -Dquarkus.container-image.registry=$DOCKER_REGISTRY" +
                        " -Dquarkus.container-image.insecure=true"
                }
            }
        }
    }
}